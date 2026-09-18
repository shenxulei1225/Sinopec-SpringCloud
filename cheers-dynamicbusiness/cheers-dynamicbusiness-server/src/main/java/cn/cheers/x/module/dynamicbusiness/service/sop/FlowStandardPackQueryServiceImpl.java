package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.FlowStandardPackRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.FlowScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.FlowItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.FlowScopeRuleMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SOP 标准包查询实现。
 *
 * <p><b>权威</b>：V103 {@code dynamic_sop_scope_rule}/{@code dynamic_sop_item_pack}。</p>
 * <p><b>禁止</b>：读路径补检查项行；改写 SOP 动作树来表达标准项包。</p>
 */
@Service
public class FlowStandardPackQueryServiceImpl implements FlowStandardPackQueryService {

    private static final String INSPECTION_ITEM_ENTITY_TYPE_CODE = "inspection_item";

    @Resource
    private EntityService entityService;

    @Resource
    private FlowScopeRuleMapper flowScopeRuleMapper;

    @Resource
    private FlowItemPackMapper flowItemPackMapper;

    @Override
    public FlowStandardPackRespVO getStandardPack(long flowId) {
        FlowStandardPackSupport.requireFlow(entityService, flowId);

        List<FlowScopeRuleDO> scopeRows = flowScopeRuleMapper.selectByFlowId(flowId);
        List<FlowItemPackDO> itemRows = flowItemPackMapper.selectByFlowId(flowId);

        FlowStandardPackRespVO out = new FlowStandardPackRespVO();
        out.setFlowId(flowId);
        out.setScopeRules(toScopeRules(scopeRows));
        out.setItemPack(toItemPackRows(itemRows));
        return out;
    }

    private List<FlowStandardPackRespVO.ScopeRule> toScopeRules(List<FlowScopeRuleDO> rows) {
        List<FlowStandardPackRespVO.ScopeRule> out = new ArrayList<>(rows.size());
        for (FlowScopeRuleDO row : rows) {
            FlowStandardPackRespVO.ScopeRule dto = new FlowStandardPackRespVO.ScopeRule();
            dto.setId(row.getId());
            dto.setScopeType(row.getScopeType());
            dto.setTargetId(row.getTargetId());
            dto.setSortNo(row.getSortNo());
            dto.setNote(row.getNote());
            out.add(dto);
        }
        return out;
    }

    private List<FlowStandardPackRespVO.ItemPackRow> toItemPackRows(List<FlowItemPackDO> rows) {
        Map<Long, String> inspectionItemNameById = resolveInspectionItemNames(rows);
        List<FlowStandardPackRespVO.ItemPackRow> out = new ArrayList<>(rows.size());
        for (FlowItemPackDO row : rows) {
            FlowStandardPackRespVO.ItemPackRow dto = new FlowStandardPackRespVO.ItemPackRow();
            dto.setId(row.getId());
            dto.setInspectionItemId(row.getInspectionItemId());
            dto.setInspectionItemName(inspectionItemNameById.get(row.getInspectionItemId()));
            dto.setRequired(row.getRequired());
            dto.setSortNo(row.getSortNo());
            dto.setNote(row.getNote());
            out.add(dto);
        }
        return out;
    }

    private Map<Long, String> resolveInspectionItemNames(List<FlowItemPackDO> rows) {
        Map<Long, String> out = new LinkedHashMap<>();
        for (FlowItemPackDO row : rows) {
            Long itemId = row.getInspectionItemId();
            if (itemId == null || out.containsKey(itemId)) {
                continue;
            }
            EntityRespVO entity = entityService.get(itemId, INSPECTION_ITEM_ENTITY_TYPE_CODE);
            out.put(itemId, entity != null ? entity.getName() : null);
        }
        return out;
    }
}
