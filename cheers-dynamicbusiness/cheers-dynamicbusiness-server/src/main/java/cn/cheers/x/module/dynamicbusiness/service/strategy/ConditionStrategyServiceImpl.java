package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.api.strategy.dto.StrategyTriggerEventDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategyRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.ConditionStrategySaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo.StrategyDeviceOptionVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.strategy.ConditionStrategyDeviceDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.strategy.ConditionStrategyDeviceMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.strategy.ConditionStrategyMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 条件策略写入与可见查询。
 * <p>权威在本表。禁止列表为空时再偷偷插一条预置。
 * <p>平台预置行所有租户可见，但不能被租户删掉或改成自己的。
 */
@Service
public class ConditionStrategyServiceImpl implements ConditionStrategyService {

    /** 设备台账的目录注册编码。关联里存的是这类实体的编号。 */
    private static final String EQUIPMENT_TYPE = "equipment";
    private static final int EQUIPMENT_PAGE_SIZE = 200;
    private static final int EQUIPMENT_PAGE_LIMIT = 10;

    @Resource
    private ConditionStrategyMapper conditionStrategyMapper;

    @Resource
    private ConditionStrategyDeviceMapper conditionStrategyDeviceMapper;

    @Resource
    private EntityRepository entityRepository;

    @Resource
    private StrategyRuleVersion strategyRuleVersion;

    @Override
    public List<ConditionStrategyRespVO> listVisible() {
        Long tenantId = currentTenantId();
        List<ConditionStrategyDO> rows = TenantUtils.executeIgnore(
                () -> conditionStrategyMapper.selectVisibleAll(tenantId));
        Map<Long, List<Long>> bindings = bindingsOf(rows.stream().map(ConditionStrategyDO::getId).toList());
        return rows.stream().map(row -> toResp(row, bindings)).toList();
    }

    @Override
    public List<ConditionStrategyDO> listVisibleEnabled(String eventType) {
        Long tenantId = currentTenantId();
        return TenantUtils.executeIgnore(
                () -> conditionStrategyMapper.selectVisibleEnabled(eventType, tenantId));
    }

    @Override
    public List<PointProcessingRule> listPointRules() {
        List<PointProcessingRule> rules = new ArrayList<>();
        for (ConditionStrategyDO row : listVisibleEnabled(StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED)) {
            PointProcessingRule rule = PointRuleReader.read(row);
            if (rule != null) {
                rules.add(rule);
            }
        }
        return rules;
    }

    @Override
    public Map<Long, Set<Long>> boundDeviceIds() {
        Long tenantId = currentTenantId();
        List<ConditionStrategyDeviceDO> rows = TenantUtils.executeIgnore(() -> conditionStrategyDeviceMapper.selectList(
                new LambdaQueryWrapper<ConditionStrategyDeviceDO>()
                        .eq(ConditionStrategyDeviceDO::getTenantId, tenantId)));
        Map<Long, Set<Long>> bound = new HashMap<>();
        for (ConditionStrategyDeviceDO row : rows) {
            if (row.getStrategyId() == null || row.getDeviceId() == null) {
                continue;
            }
            bound.computeIfAbsent(row.getStrategyId(), key -> new HashSet<>()).add(row.getDeviceId());
        }
        return bound;
    }

    @Override
    public List<StrategyDeviceOptionVO> listEquipmentOptions() {
        List<StrategyDeviceOptionVO> options = new ArrayList<>();
        long total = Long.MAX_VALUE;
        for (int pageNo = 1; pageNo <= EQUIPMENT_PAGE_LIMIT && options.size() < total; pageNo++) {
            EntityRepository.EntityQuery query = EntityRepository.EntityQuery.builder()
                    .entityTypeCode(EQUIPMENT_TYPE)
                    .pageNo(pageNo)
                    .pageSize(EQUIPMENT_PAGE_SIZE)
                    .build();
            PageResult<EntityDO> page = entityRepository.findPage(query);
            List<EntityDO> list = page.getList() == null ? List.of() : page.getList();
            if (list.isEmpty()) {
                break;
            }
            total = page.getTotal() == null ? list.size() : page.getTotal();
            for (EntityDO row : list) {
                if (row.getId() == null) {
                    continue;
                }
                StrategyDeviceOptionVO option = new StrategyDeviceOptionVO();
                option.setId(row.getId());
                option.setName(row.getName());
                options.add(option);
            }
        }
        return options;
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
        boolean pointAlarm = StrategyTriggerEventDTO.EVENT_COLLECTION_RECEIVED.equals(row.getEventType())
                && RegisteredActions.RAISE_ALARM.equals(row.getActionCode());
        replaceDevices(row.getId(), row.getTenantId(), pointAlarm ? req.getDeviceIds() : List.of());
        strategyRuleVersion.bump();
        return row.getId();
    }

    @Override
    public void delete(Long id) {
        ConditionStrategyDO row = requireOwnRow(id);
        replaceDevices(row.getId(), row.getTenantId(), List.of());
        conditionStrategyMapper.deleteById(row.getId());
        strategyRuleVersion.bump();
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

    /**
     * 用这次提交的设备名单换掉这条策略原来挂的设备。
     * 空名单就是没挂。不保留旧名单，也不当成全部设备。
     */
    private void replaceDevices(Long strategyId, Long tenantId, List<Long> deviceIds) {
        conditionStrategyDeviceMapper.delete(new LambdaQueryWrapper<ConditionStrategyDeviceDO>()
                .eq(ConditionStrategyDeviceDO::getStrategyId, strategyId));
        if (deviceIds == null || deviceIds.isEmpty()) {
            return;
        }
        Set<Long> distinct = new LinkedHashSet<>();
        for (Long deviceId : deviceIds) {
            if (deviceId != null) {
                distinct.add(deviceId);
            }
        }
        for (Long deviceId : distinct) {
            ConditionStrategyDeviceDO link = new ConditionStrategyDeviceDO();
            link.setStrategyId(strategyId);
            link.setDeviceId(deviceId);
            link.setTenantId(tenantId);
            conditionStrategyDeviceMapper.insert(link);
        }
    }

    private Map<Long, List<Long>> bindingsOf(List<Long> strategyIds) {
        List<Long> ids = strategyIds.stream().filter(Objects::nonNull).toList();
        if (ids.isEmpty()) {
            return Map.of();
        }
        List<ConditionStrategyDeviceDO> rows = conditionStrategyDeviceMapper.selectList(
                new LambdaQueryWrapper<ConditionStrategyDeviceDO>()
                        .in(ConditionStrategyDeviceDO::getStrategyId, ids));
        Map<Long, List<Long>> bindings = new HashMap<>();
        for (ConditionStrategyDeviceDO row : rows) {
            if (row.getStrategyId() == null || row.getDeviceId() == null) {
                continue;
            }
            bindings.computeIfAbsent(row.getStrategyId(), key -> new ArrayList<>()).add(row.getDeviceId());
        }
        return bindings;
    }

    private ConditionStrategyRespVO toResp(ConditionStrategyDO row, Map<Long, List<Long>> bindings) {
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
        vo.setDeviceIds(bindings.getOrDefault(row.getId(), List.of()));
        return vo;
    }

    private static Long currentTenantId() {
        return Objects.requireNonNullElse(TenantContextHolder.getRequiredTenantId(), 0L);
    }
}
