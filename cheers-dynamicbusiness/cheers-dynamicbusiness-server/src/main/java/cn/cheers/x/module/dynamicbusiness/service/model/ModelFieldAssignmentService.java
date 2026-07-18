package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldBatchAssignReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFilterFieldMetaRespVO;
import java.util.List;

/**
 * 模型字段分配 Service 接口
 * 
 * 支持三种字段类型：
 * - 固定列字段（BASE）：来自业务类型配置，自动继承
 * - 扩展字段（CUSTOM）：用户通过 ModelFieldAssignment 添加
 * - 关联字段（RELATION）：引用其他业务实体（FR-BDA-030~034）
 * 
 * @author yudao
 */
public interface ModelFieldAssignmentService {

    /**
     * 为模型分配单个字段
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @param required 是否必填
     * @param isSearchable 是否可查询（如果为 null，则使用字段定义中的默认值）
     * @param isSortable 是否可排序（如果为 null，则使用字段定义中的默认值）
     * @param defaultValue 默认值
     * @param validationRules 业务规则
     */
    void assignFieldToModel(Long modelId, Long fieldId, Boolean required, Boolean isSearchable, Boolean isFilterable, Boolean isSortable, String defaultValue, String validationRules);

    /**
     * 为模型分配字段（支持批量）
     *
     * @param reqVO 字段分配请求
     */
    int assignFieldsToModel(ModelFieldBatchAssignReqVO reqVO);

    /**
     * 解除模型与字段的关联
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     */
    void unassignFieldFromModel(Long modelId, Long fieldId);

    /**
     * 批量解除模型与字段的关联
     *
     * @param modelId 模型ID
     * @param fieldIds 字段ID列表
     */
    int batchUnassignFieldsFromModel(Long modelId, List<Long> fieldIds);

    /**
     * 获取模型已分配的字段列表（包含字段定义和业务规则）
     *
     * @param modelId 模型ID
     * @return 字段分配列表
     */
    List<ModelFieldAssignmentRespVO> getModelFields(Long modelId);


    // ========== 关联字段（模型侧通过自定义创建 + 普通字段分配；不依赖关联字段库）==========

    /**
     * 创建自定义关联字段
     * 
     * @param reqVO 创建请求
     * @return 字段分配ID
     */
    Long createCustomRelationField(cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.CustomRelationFieldCreateReqVO reqVO);

    /**
     * 获取模型的关联字段列表（ENTITY_REF / ENTITY_REF_MULTI）
     *
     * @param modelId 模型ID
     * @return 关联字段列表
     */
    java.util.List<ModelFieldAssignmentRespVO> getModelRelationFields(Long modelId);

    // ========== 业务关联流程优化：级联删除支持 ==========

    /**
     * 根据 Model 关联 ID 删除字段分配
     * 
     * 用于级联删除：当删除 ModelRelation 时，同时删除相关的字段分配记录。
     * 
     * @param modelRelationId Model 关联 ID
     * @return 删除的记录数
     */
    int deleteByModelRelationId(Long modelRelationId);

    // ========== 业务关联流程优化：数据修复支持（需求 7.5）==========

    /**
     * 根据字段编码查找字段
     * 
     * @param fieldCode 字段编码
     * @return 字段定义，不存在返回 null
     */
    cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO findFieldByCode(String fieldCode);

    /**
     * 根据模型ID和字段ID查找字段分配
     * 
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @return 字段分配，不存在返回 null
     */
    cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO findAssignmentByModelIdAndFieldId(
            Long modelId, Long fieldId);

    /**
     * 为已存在的字段创建 ModelFieldAssignment
     * 
     * 用于数据修复：当字段存在但 ModelFieldAssignment 缺失时，创建新的分配记录。
     * 
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @param modelRelationId Model 关联 ID
     * @return 新创建的字段分配ID
     */
    Long createAssignmentForExistingField(Long modelId, Long fieldId, Long modelRelationId);

    /**
     * 更新字段分配的关联信息
     * 
     * 用于数据修复：当 ModelFieldAssignment 存在但关联信息不完整时，更新关联信息。
     * 
     * @param assignmentId 字段分配ID
     * @param modelRelationId Model 关联 ID
     */
    void updateAssignmentRelationInfo(Long assignmentId, Long modelRelationId);

    // ========== 批量查询接口（性能优化）==========

    /**
     * 批量获取多个模型已分配的字段列表
     * 
     * 用于前端一次性获取多个模型的字段信息，减少 N+1 查询问题。
     * 
     * @param modelIds 模型ID列表
     * @return Map<模型ID, 字段分配列表>
     */
    java.util.Map<Long, List<ModelFieldAssignmentRespVO>> getModelFieldsBatch(List<Long> modelIds);

    /**
     * 获取模型可用筛选字段元信息（用于数据管理页面动态渲染筛选项）。
     *
     * @param modelId 模型ID
     * @return 可筛选字段元信息列表
     */
    java.util.List<ModelFilterFieldMetaRespVO> getModelFilterFieldMeta(Long modelId);

    // ========== 批量分配关联字段（性能优化）==========

    /**
     * 批量分配关联字段到多个Model（性能优化）
     * 
     * 用于视图创建时自动为多个Model添加关联字段，使用批量操作减少数据库交互次数。
     * 
     * @param modelIds Model ID列表
     * @param fieldId 关联字段ID
     * @param targetEntityTypeCode 目标业务类型编码（用于日志记录）
     * @return 成功分配的Model数量
     */
    int batchAssignAssociationFieldToModels(List<Long> modelIds, Long fieldId, String targetEntityTypeCode);

    /**
     * 智能分配关联字段到Model（根据Model数量选择处理方式）
     * 
     * 性能优化策略：
     * - ≤10个Model：同步批量处理
     * - >10个Model：同步批量处理（短期方案，后续可改为异步）
     * 
     * @param modelIds Model ID列表
     * @param fieldId 关联字段ID
     * @param targetEntityTypeCode 目标业务类型编码
     * @return 成功分配的Model数量
     */
    int smartAssignAssociationFieldToModels(List<Long> modelIds, Long fieldId, String targetEntityTypeCode);
}

