package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopScopeRuleMapper;
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
public class SopStandardPackQueryServiceImpl implements SopStandardPackQueryService {

    private static final String INSPECTION_ITEM_ENTITY_TYPE_CODE = "inspection_item";

    @Resource
    private EntityService entityService;

    @Resource
    private SopScopeRuleMapper sopScopeRuleMapper;

    @Resource
    private SopItemPackMapper sopItemPackMapper;

    @Override
    public SopStandardPackRespVO getStandardPack(long sopId) {
        SopStandardPackSupport.requireSop(entityService, sopId);

        List<SopScopeRuleDO> scopeRows = sopScopeRuleMapper.selectBySopId(sopId);
        List<SopItemPackDO> itemRows = sopItemPackMapper.selectBySopId(sopId);

        SopStandardPackRespVO out = new SopStandardPackRespVO();
        out.setSopId(sopId);
        out.setScopeRules(toScopeRules(scopeRows));
        out.setItemPack(toItemPackRows(itemRows));
        return out;
    }

    private List<SopStandardPackRespVO.ScopeRule> toScopeRules(List<SopScopeRuleDO> rows) {
        List<SopStandardPackRespVO.ScopeRule> out = new ArrayList<>(rows.size());
        for (SopScopeRuleDO row : rows) {
            SopStandardPackRespVO.ScopeRule dto = new SopStandardPackRespVO.ScopeRule();
            dto.setId(row.getId());
            dto.setScopeType(row.getScopeType());
            dto.setTargetId(row.getTargetId());
            dto.setSortNo(row.getSortNo());
            dto.setNote(row.getNote());
            out.add(dto);
        }
        return out;
    }

    private List<SopStandardPackRespVO.ItemPackRow> toItemPackRows(List<SopItemPackDO> rows) {
        Map<Long, String> inspectionItemNameById = resolveInspectionItemNames(rows);
        List<SopStandardPackRespVO.ItemPackRow> out = new ArrayList<>(rows.size());
        for (SopItemPackDO row : rows) {
            SopStandardPackRespVO.ItemPackRow dto = new SopStandardPackRespVO.ItemPackRow();
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

    private Map<Long, String> resolveInspectionItemNames(List<SopItemPackDO> rows) {
        Map<Long, String> out = new LinkedHashMap<>();
        for (SopItemPackDO row : rows) {
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
