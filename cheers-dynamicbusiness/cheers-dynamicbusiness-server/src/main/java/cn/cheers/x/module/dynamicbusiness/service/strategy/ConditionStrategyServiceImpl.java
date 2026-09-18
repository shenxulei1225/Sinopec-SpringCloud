package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategyRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.strategy.ConditionStrategyMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 条件策略写入与可见查询。
 * <p>权威在本表。禁止列表为空时再偷偷插一条预置。
 * <p>平台预置行所有租户可见，但不能被租户删掉或改成自己的。
 */
@Service
public class ConditionStrategyServiceImpl implements ConditionStrategyService {

    @Resource
    private ConditionStrategyMapper conditionStrategyMapper;

    @Override
    public List<ConditionStrategyRespVO> listVisible() {
        Long tenantId = currentTenantId();
        List<ConditionStrategyDO> rows = TenantUtils.executeIgnore(
                () -> conditionStrategyMapper.selectVisibleAll(tenantId));
        return rows.stream().map(this::toResp).toList();
    }

    @Override
    public List<ConditionStrategyDO> listVisibleEnabled(String eventType) {
        Long tenantId = currentTenantId();
        return TenantUtils.executeIgnore(
                () -> conditionStrategyMapper.selectVisibleEnabled(eventType, tenantId));
    }

    @Override
    public Long save(ConditionStrategySaveReqVO req) {
        if (req == null || !StringUtils.hasText(req.getName()) || !StringUtils.hasText(req.getEventType())
                || !StringUtils.hasText(req.getActionCode())) {
            throw new ServiceException(400, "策略名称、何时发生、做哪件事都不能空");
        }
        String eventType = req.getEventType().trim();
        if (!StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED.equals(eventType)
                && !StrategyTriggerEventDTO.EVENT_EXECUTION_START.equals(eventType)) {
            throw new ServiceException(400, "只能选「采集结果到了」或「人点了开始」");
        }
        if (!RegisteredActions.isKnown(req.getActionCode().trim())) {
            throw new ServiceException(400, "只能勾已登记的动作");
        }
        String conditionJson = ConditionStrategyCodec.encodeCondition(req);
        String actionParamsJson = ConditionStrategyCodec.encodeActionParams(req);
        ConditionStrategyDO row;
        if (req.getId() == null) {
            row = new ConditionStrategyDO();
            row.setPlatform(false);
            row.setTenantId(currentTenantId());
        } else {
            row = requireOwnRow(req.getId());
        }
        row.setName(req.getName().trim());
        row.setEnabled(Boolean.TRUE.equals(req.getEnabled()));
        row.setEventType(req.getEventType().trim());
        row.setConditionJson(conditionJson);
        row.setActionCode(req.getActionCode().trim());
        row.setActionParamsJson(actionParamsJson);
        row.setPriority(req.getPriority() == null ? 100 : req.getPriority());
        if (row.getId() == null) {
            conditionStrategyMapper.insert(row);
        } else {
            conditionStrategyMapper.updateById(row);
        }
        return row.getId();
    }

    @Override
    public void delete(Long id) {
        ConditionStrategyDO row = requireOwnRow(id);
        conditionStrategyMapper.deleteById(row.getId());
    }

    @Override
    public Map<String, String> actionCatalog() {
        return RegisteredActions.catalog();
    }

    private ConditionStrategyDO requireOwnRow(Long id) {
        if (id == null) {
            throw new ServiceException(400, "要改哪一条策略");
        }
        ConditionStrategyDO row = conditionStrategyMapper.selectById(id);
        if (row == null) {
            throw new ServiceException(404, "没有这条策略");
        }
        if (Boolean.TRUE.equals(row.getPlatform())) {
            throw new ServiceException(400, "平台预置策略不能改、不能删；要换做法请另建一条");
        }
        if (!Objects.equals(row.getTenantId(), currentTenantId())) {
            throw new ServiceException(403, "不能改别人租户的策略");
        }
        return row;
    }

    private ConditionStrategyRespVO toResp(ConditionStrategyDO row) {
        ConditionStrategyRespVO vo = new ConditionStrategyRespVO();
        vo.setId(row.getId());
        vo.setName(row.getName());
        vo.setEnabled(Boolean.TRUE.equals(row.getEnabled()));
        vo.setPlatform(Boolean.TRUE.equals(row.getPlatform()));
        vo.setEventType(row.getEventType());
        vo.setEventTypeName(ConditionStrategyCodec.eventTypeName(row.getEventType()));
        vo.setActionCode(row.getActionCode());
        vo.setActionName(RegisteredActions.displayName(row.getActionCode()));
        vo.setPriority(row.getPriority());
        ConditionStrategyCodec.fillFromStored(vo, row.getConditionJson(), row.getActionParamsJson());
        return vo;
    }

    private static Long currentTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
