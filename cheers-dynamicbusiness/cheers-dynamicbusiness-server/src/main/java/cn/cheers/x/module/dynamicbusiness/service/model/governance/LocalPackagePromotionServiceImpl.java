package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.service.model.core.ModelCoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地型号包晋升实现。
 *
 * <p>型号、字段定义和字段分配在同一事务内改写，型号最后落为公司规格。
 * 本实现不提供仅翻转型号状态的方法，也不在读取时补齐未完成的晋升。</p>
 */
@Service
@RequiredArgsConstructor
public class LocalPackagePromotionServiceImpl implements LocalPackagePromotionService {

    private static final String GOVERNANCE_LOCAL = "LOCAL";
    private static final String GOVERNANCE_COMPANY = "COMPANY";

    private final ModelCoreService modelCoreService;
    private final ModelFieldAssignmentMapper assignmentMapper;
    private final FieldMapper fieldMapper;
    private final MasterDataCapabilityChecker capabilityChecker;

    /**
     * 按“校验权限与型号 → 收集本地字段 → 晋升或合并字段 → 晋升型号”的固定顺序执行。
     *
     * <p>合并时只把当前型号的分配改指向既有公司字段；原本地字段定义保持 LOCAL 且不停用，
     * 因为它可能仍被其它本地型号使用。本方法不删除或静默改写其它型号的分配。</p>
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void promoteLocalPackage(Long modelId, Map<String, String> fieldMergeMap) {
        if (!capabilityChecker.canPromotePackage()) {
            throw new ServiceException(403, "无权晋升本地型号包");
        }
        ModelDO model = requireLocalModel(modelId);
        Map<String, String> mergeMap = normalizeMergeMap(fieldMergeMap);

        List<ModelFieldAssignmentDO> assignments = assignmentMapper.selectByModelId(modelId);
        Map<String, LocalFieldAssignment> localFields = collectLocalFields(assignments);
        validateMergeTargets(mergeMap, localFields);

        for (LocalFieldAssignment local : localFields.values()) {
            String companyFieldCode = mergeMap.get(local.field().getCode());
            if (companyFieldCode == null) {
                promoteField(local.field());
            } else {
                repointAssignment(local.assignment(), requireCompanyField(companyFieldCode));
            }
        }

        ModelDO modelUpdate = new ModelDO();
        modelUpdate.setId(model.getId());
        modelUpdate.setGovernanceStatus(GOVERNANCE_COMPANY);
        modelUpdate.setOriginFacilityId(null);
        modelCoreService.update(modelUpdate);
    }

    private ModelDO requireLocalModel(Long modelId) {
        if (modelId == null) {
            throw new ServiceException(400, "modelId 不能为空");
        }
        ModelDO model = modelCoreService.get(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }
        if (!GOVERNANCE_LOCAL.equals(model.getGovernanceStatus())) {
            throw new ServiceException(400, "只有本地型号可以晋升");
        }
        return model;
    }

    private Map<String, String> normalizeMergeMap(Map<String, String> fieldMergeMap) {
        if (fieldMergeMap == null || fieldMergeMap.isEmpty()) {
            return Map.of();
        }
        Map<String, String> normalized = new LinkedHashMap<>();
        fieldMergeMap.forEach((localCode, companyCode) -> {
            if (!StringUtils.hasText(localCode) || !StringUtils.hasText(companyCode)) {
                throw new ServiceException(400, "字段合并映射中的字段编码不能为空");
            }
            normalized.put(localCode.trim(), companyCode.trim());
        });
        return normalized;
    }

    private Map<String, LocalFieldAssignment> collectLocalFields(
            List<ModelFieldAssignmentDO> assignments) {
        Map<String, LocalFieldAssignment> localFields = new LinkedHashMap<>();
        for (ModelFieldAssignmentDO assignment : assignments == null ? List.<ModelFieldAssignmentDO>of() : assignments) {
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null) {
                throw new ServiceException(400, "模型字段引用了不存在的字段");
            }
            if (!GOVERNANCE_LOCAL.equals(field.getGovernanceStatus())) {
                continue;
            }
            LocalFieldAssignment previous =
                    localFields.putIfAbsent(field.getCode(), new LocalFieldAssignment(assignment, field));
            if (previous != null && !previous.field().getId().equals(field.getId())) {
                throw new ServiceException(400, "型号存在重复的本地字段编码：" + field.getCode());
            }
        }
        return localFields;
    }

    private void validateMergeTargets(
            Map<String, String> mergeMap, Map<String, LocalFieldAssignment> localFields) {
        for (String localFieldCode : mergeMap.keySet()) {
            if (!localFields.containsKey(localFieldCode)) {
                throw new ServiceException(400, "合并映射字段不属于该型号的本地字段：" + localFieldCode);
            }
            requireCompanyField(mergeMap.get(localFieldCode));
        }
    }

    private FieldDO requireCompanyField(String fieldCode) {
        FieldDO target = fieldMapper.selectByCode(fieldCode);
        if (target == null) {
            throw new ServiceException(404, "目标公司字段不存在：" + fieldCode);
        }
        if (!GOVERNANCE_COMPANY.equals(target.getGovernanceStatus())) {
            throw new ServiceException(400, "合并目标必须是公司字段：" + fieldCode);
        }
        return target;
    }

    private void promoteField(FieldDO field) {
        FieldDO update = new FieldDO();
        update.setId(field.getId());
        update.setGovernanceStatus(GOVERNANCE_COMPANY);
        update.setOriginFacilityId(null);
        fieldMapper.updateById(update);
    }

    private void repointAssignment(ModelFieldAssignmentDO assignment, FieldDO companyField) {
        ModelFieldAssignmentDO update = new ModelFieldAssignmentDO();
        update.setId(assignment.getId());
        update.setFieldId(companyField.getId());
        update.setFieldCode(companyField.getCode());
        assignmentMapper.updateById(update);
    }

    private record LocalFieldAssignment(
            ModelFieldAssignmentDO assignment, FieldDO field) {
    }
}
