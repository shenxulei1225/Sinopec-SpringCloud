package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo.SopStandardPackUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopItemPackDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.sop.SopScopeRuleDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopItemPackMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.sop.SopScopeRuleMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SOP 标准包命令实现。
 *
 * <p><b>权威</b>：标准包范围/项包仅写 V103 新表，避免双权威。</p>
 * <p><b>禁止</b>：把“该查什么”写回旧 method binding；写入时静默补不存在检查项。</p>
 */
@Service
public class SopStandardPackCommandServiceImpl implements SopStandardPackCommandService {

    private static final String INSPECTION_ITEM_ENTITY_TYPE_CODE = "inspection_item";

    @Resource
    private EntityService entityService;

    @Resource
    private SopScopeRuleMapper sopScopeRuleMapper;

    @Resource
    private SopItemPackMapper sopItemPackMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveStandardPack(long sopId, SopStandardPackUpsertReqVO reqVO) {
        SopStandardPackSupport.requireSop(entityService, sopId);
        List<SopScopeRuleDO> scopeRows = normalizeScopeRows(sopId, reqVO.getScopeRules());
        List<SopItemPackDO> itemRows = normalizeItemRows(sopId, reqVO.getItemPack());

        sopScopeRuleMapper.deleteBySopId(sopId);
        sopItemPackMapper.deleteBySopId(sopId);
        for (SopScopeRuleDO row : scopeRows) {
            sopScopeRuleMapper.insert(row);
        }
        for (SopItemPackDO row : itemRows) {
            sopItemPackMapper.insert(row);
        }
    }

    private List<SopScopeRuleDO> normalizeScopeRows(
            long sopId, List<SopStandardPackUpsertReqVO.ScopeRuleUpsert> scopeRules) {
        List<SopStandardPackUpsertReqVO.ScopeRuleUpsert> source =
                scopeRules != null ? scopeRules : List.of();
        Map<String, SopScopeRuleDO> dedup = new LinkedHashMap<>();
        int index = 0;
        for (SopStandardPackUpsertReqVO.ScopeRuleUpsert input : source) {
            if (input == null) {
                continue;
            }
            String scopeType = SopStandardPackSupport.normalizeScopeType(input.getScopeType());
            Long targetId = input.getTargetId();
            if (targetId == null || targetId <= 0) {
                throw new ServiceException(400, "scopeRules.targetId 必须大于 0");
            }
            String key = scopeType + ":" + targetId;
            SopScopeRuleDO row = SopScopeRuleDO.builder()
                    .sopId(sopId)
                    .scopeType(scopeType)
                    .targetId(targetId)
                    .sortNo(normalizeSortNo(input.getSortNo(), index))
                    .note(normalizeNote(input.getNote()))
                    .build();
            dedup.put(key, row);
            index++;
        }
        return new ArrayList<>(dedup.values());
    }

    private List<SopItemPackDO> normalizeItemRows(
            long sopId, List<SopStandardPackUpsertReqVO.ItemPackRowUpsert> itemPack) {
        List<SopStandardPackUpsertReqVO.ItemPackRowUpsert> source =
                itemPack != null ? itemPack : List.of();
        Map<Long, SopItemPackDO> dedup = new LinkedHashMap<>();
        int index = 0;
        for (SopStandardPackUpsertReqVO.ItemPackRowUpsert input : source) {
            if (input == null) {
                continue;
            }
            Long inspectionItemId = input.getInspectionItemId();
            if (inspectionItemId == null || inspectionItemId <= 0) {
                throw new ServiceException(400, "itemPack.inspectionItemId 必须大于 0");
            }
            requireInspectionItem(inspectionItemId);
            SopItemPackDO row = SopItemPackDO.builder()
                    .sopId(sopId)
                    .inspectionItemId(inspectionItemId)
                    .required(input.getRequired() == null || input.getRequired())
                    .sortNo(normalizeSortNo(input.getSortNo(), index))
                    .note(normalizeNote(input.getNote()))
                    .build();
            dedup.put(inspectionItemId, row);
            index++;
        }
        return new ArrayList<>(dedup.values());
    }

    private void requireInspectionItem(Long inspectionItemId) {
        EntityRespVO row = entityService.get(inspectionItemId, INSPECTION_ITEM_ENTITY_TYPE_CODE);
        if (row == null || row.getId() == null) {
            throw new ServiceException(400, "标准检查项不存在：" + inspectionItemId);
        }
    }

    private Integer normalizeSortNo(Integer sortNo, int index) {
        if (sortNo != null && sortNo >= 0) {
            return sortNo;
        }
        return (index + 1) * 10;
    }

    private String normalizeNote(String note) {
        if (!StringUtils.hasText(note)) {
            return null;
        }
        String text = note.trim();
        if (text.length() <= 500) {
            return text;
        }
        return text.substring(0, 500);
    }
}
