package cn.cheers.x.module.dynamicbusiness.service.relation;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation.RelationFieldLibraryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.relation.RelationFieldLibraryMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

/**
 * 关联数据修复服务实现
 * 
 * 需求：5.1, 5.2, 5.3
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class RelationDataRepairServiceImpl implements RelationDataRepairService {

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private ModelRelationMapper modelRelationMapper;

    @Resource
    private RelationFieldLibraryMapper relationFieldLibraryMapper;

    @Resource
    private FieldMapper fieldMapper;

    @Resource
    private ModelMapper modelMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepairResult repairRelationFieldData() {
        log.info("[repairRelationFieldData][开始修复关联字段数据]");
        
        RepairResult result = new RepairResult();
        StringBuilder details = new StringBuilder();
        
        // 1. 查找所有 ENTITY_REF 类型的字段
        List<FieldDO> entityRefFields = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getType, FieldTypeEnum.ENTITY_REF.getCode()));
        
        log.info("[repairRelationFieldData][找到 {} 个 ENTITY_REF 类型字段]", entityRefFields.size());
        
        // 2. 遍历每个字段，查找其分配记录并修复
        for (FieldDO field : entityRefFields) {
            List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByFieldId(field.getId());
            
            for (ModelFieldAssignmentDO assignment : assignments) {
                try {
                    boolean repaired = repairSingleAssignment(assignment, field, details);
                    if (repaired) {
                        result.incrementSuccess();
                    } else {
                        result.incrementSkipped();
                    }
                } catch (Exception e) {
                    log.error("[repairRelationFieldData][修复失败: assignmentId={}, error={}]", 
                            assignment.getId(), e.getMessage(), e);
                    result.incrementFailed();
                    details.append(String.format("修复失败: assignmentId=%d, error=%s\n", 
                            assignment.getId(), e.getMessage()));
                }
            }
        }
        
        result.setDetails(details.toString());
        log.info("[repairRelationFieldData][修复完成: {}]", result);
        
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RepairResult repairRelationFieldDataByModelId(Long modelId) {
        log.info("[repairRelationFieldDataByModelId][开始修复模型 {} 的关联字段数据]", modelId);
        
        RepairResult result = new RepairResult();
        StringBuilder details = new StringBuilder();
        
        // 1. 获取模型的所有字段分配
        List<ModelFieldAssignmentDO> assignments = modelFieldAssignmentMapper.selectByModelId(modelId);
        
        // 2. 遍历每个分配，检查是否是 ENTITY_REF 类型并修复
        for (ModelFieldAssignmentDO assignment : assignments) {
            FieldDO field = fieldMapper.selectById(assignment.getFieldId());
            if (field == null || !FieldTypeEnum.ENTITY_REF.getCode().equals(field.getType())) {
                continue;
            }
            
            try {
                boolean repaired = repairSingleAssignment(assignment, field, details);
                if (repaired) {
                    result.incrementSuccess();
                } else {
                    result.incrementSkipped();
                }
            } catch (Exception e) {
                log.error("[repairRelationFieldDataByModelId][修复失败: assignmentId={}, error={}]", 
                        assignment.getId(), e.getMessage(), e);
                result.incrementFailed();
                details.append(String.format("修复失败: assignmentId=%d, error=%s\n", 
                        assignment.getId(), e.getMessage()));
            }
        }
        
        result.setDetails(details.toString());
        log.info("[repairRelationFieldDataByModelId][修复完成: modelId={}, {}]", modelId, result);
        
        return result;
    }


    /**
     * 修复单个字段分配记录
     * 
     * @param assignment 字段分配记录
     * @param field 字段定义
     * @param details 详细信息记录
     * @return 是否进行了修复
     */
    private boolean repairSingleAssignment(ModelFieldAssignmentDO assignment, FieldDO field, 
                                           StringBuilder details) {
        boolean needsUpdate = false;
        
        // 检查是否需要修复
        boolean missingModelRelationId = assignment.getModelRelationId() == null;

        if (!missingModelRelationId) {
            // 数据完整，跳过
            log.debug("[repairSingleAssignment][数据完整，跳过: assignmentId={}]", assignment.getId());
            return false;
        }

        log.info("[repairSingleAssignment][开始修复: assignmentId={}, fieldCode={}, " +
                "missingModelRelationId=true]",
                assignment.getId(), field.getCode());

        // 1. 尝试从 RelationFieldLibrary 推断（仅作日志记录，DO 已不再存储冗余字段）
        if (assignment.getRefLibraryId() != null) {
            RelationFieldLibraryDO libraryField = relationFieldLibraryMapper.selectById(assignment.getRefLibraryId());
            if (libraryField != null) {
                log.info("[repairSingleAssignment][检测到可从 RelationFieldLibrary 推断关联信息: assignmentId={}]",
                        assignment.getId());
            }
        }
        
        // 2. 尝试通过字段编码匹配 ModelRelation
        if (missingModelRelationId) {
            ModelRelationDO matchedRelation = matchModelRelation(assignment, field);

            if (matchedRelation != null) {
                assignment.setModelRelationId(matchedRelation.getId());
                needsUpdate = true;

                log.info("[repairSingleAssignment][回填 modelRelationId: " +
                        "assignmentId={}, modelRelationId={}]",
                        assignment.getId(), matchedRelation.getId());
                details.append(String.format("回填 modelRelationId: assignmentId=%d, modelRelationId=%d\n",
                        assignment.getId(), matchedRelation.getId()));
            } else {
                log.warn("[repairSingleAssignment][无法匹配 ModelRelation: " +
                        "assignmentId={}, fieldCode={}]",
                        assignment.getId(), field.getCode());
                details.append(String.format("无法匹配 ModelRelation: assignmentId=%d, fieldCode=%s\n",
                        assignment.getId(), field.getCode()));
            }
        }
        
        // 3. 更新数据库
        if (needsUpdate) {
            modelFieldAssignmentMapper.updateById(assignment);
            log.info("[repairSingleAssignment][修复完成: assignmentId={}]", assignment.getId());
            return true;
        }
        
        return false;
    }

    /**
     * 通过字段编码匹配 ModelRelation
     * 
     * 匹配规则：
     * 1. 字段编码格式为 "REF-{targetModelCode}_id"
     * 2. 根据 modelId 和解析出的 fieldCode 查找 ModelRelation
     * 
     * @param assignment 字段分配记录
     * @param field 字段定义
     * @return 匹配到的 ModelRelation，未找到返回 null
     */
    private ModelRelationDO matchModelRelation(ModelFieldAssignmentDO assignment, FieldDO field) {
        String fieldCode = field.getCode();
        
        // 1. 解析字段编码，提取关联字段编码
        // 字段编码格式：REF-{relationFieldCode}
        String relationFieldCode = null;
        if (RelationFieldCodes.isLibraryRefFieldCode(fieldCode)) {
            relationFieldCode = RelationFieldCodes.stripPrefixIfPresent(fieldCode);
        }
        
        if (relationFieldCode == null || relationFieldCode.isEmpty()) {
            log.debug("[matchModelRelation][无法解析字段编码: fieldCode={}]", fieldCode);
            return null;
        }
        
        // 2. 根据 modelId 获取源 Model
        ModelDO sourceModel = modelMapper.selectById(assignment.getModelId());
        if (sourceModel == null) {
            log.warn("[matchModelRelation][源 Model 不存在: modelId={}]", assignment.getModelId());
            return null;
        }
        
        // 3. 通过 fieldCode 查找 ModelRelation
        ModelRelationDO relation = modelRelationMapper.selectByFieldCode(
                sourceModel.getId(), relationFieldCode);
        
        if (relation != null) {
            log.debug("[matchModelRelation][通过 fieldCode 匹配成功: " +
                    "modelId={}, fieldCode={}, modelRelationId={}]",
                    assignment.getModelId(), relationFieldCode, relation.getId());
            return relation;
        }
        
        // 4. 尝试通过目标 Model 编码匹配
        // 字段编码格式可能是 "{targetModelCode}_id"
        String targetModelCode = relationFieldCode.replace("_id", "").replace("_", "-");
        List<ModelRelationDO> relations = modelRelationMapper.selectBySourceModelId(sourceModel.getId());
        
        for (ModelRelationDO rel : relations) {
            if (rel.getTargetModelCode() != null && 
                    (rel.getTargetModelCode().equalsIgnoreCase(targetModelCode) ||
                     rel.getTargetModelCode().replace("-", "_").equalsIgnoreCase(
                             relationFieldCode.replace("_id", "")))) {
                log.debug("[matchModelRelation][通过目标 Model 编码匹配成功: " +
                        "modelId={}, targetModelCode={}, modelRelationId={}]",
                        assignment.getModelId(), rel.getTargetModelCode(), rel.getId());
                return rel;
            }
        }
        
        log.debug("[matchModelRelation][未找到匹配的 ModelRelation: " +
                "modelId={}, fieldCode={}]",
                assignment.getModelId(), fieldCode);
        return null;
    }
}
