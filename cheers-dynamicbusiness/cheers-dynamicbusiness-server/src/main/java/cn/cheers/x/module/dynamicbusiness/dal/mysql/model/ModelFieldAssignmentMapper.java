package cn.cheers.x.module.dynamicbusiness.dal.mysql.model;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 模型字段分配 Mapper
 *
 * @author yudao
 */
@Mapper
public interface ModelFieldAssignmentMapper extends BaseMapperX<ModelFieldAssignmentDO> {

        /**
         * 根据模型ID查询字段分配列表（按 sort 排序，sort 为 null 的排在后面）
         */
        default List<ModelFieldAssignmentDO> selectByModelId(Long modelId) {
                return selectList(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelId, modelId)
                        .orderByAsc(ModelFieldAssignmentDO::getSort)
                        .orderByAsc(ModelFieldAssignmentDO::getId)); // sort 为 null 时按 id 排序
        }

        /**
         * 根据模型ID查询字段ID列表
         *
         * @param modelId 模型ID
         * @return 字段ID列表
         */
        default List<Long> selectFieldIdsByModelId(Long modelId) {
                return selectByModelId(modelId).stream()
                        .map(ModelFieldAssignmentDO::getFieldId)
                        .toList();
        }

        /**
         * 根据字段ID查询模型分配列表
         */
        default List<ModelFieldAssignmentDO> selectByFieldId(Long fieldId) {
                return selectList(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getFieldId, fieldId));
        }

        /**
         * 根据模型ID和字段ID查询分配关系
         */
        default ModelFieldAssignmentDO selectByModelIdAndFieldId(Long modelId, Long fieldId) {
                return selectOne(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelId, modelId)
                        .eq(ModelFieldAssignmentDO::getFieldId, fieldId));
        }

        /**
         * 根据模型编码与字段编码查询分配关系（迁移/seed 幂等）
         */
        default ModelFieldAssignmentDO selectByModelCodeAndFieldCode(String modelCode, String fieldCode) {
                return selectOne(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelCode, modelCode)
                        .eq(ModelFieldAssignmentDO::getFieldCode, fieldCode));
        }

        /**
         * 根据模型ID和字段ID查询分配关系（包含已删除的记录）
         * 用于在重新绑定时恢复已删除的记录
         * 使用原生 SQL 绕过 MyBatis Plus 的逻辑删除过滤
         */
        @Select("SELECT * FROM dynamic_model_field_assignment " +
                "WHERE model_id = #{modelId} AND field_id = #{fieldId} AND tenant_id = #{tenantId} " +
                "LIMIT 1")
        ModelFieldAssignmentDO selectByModelIdAndFieldIdWithDeleted(@Param("modelId") Long modelId, 
                                                                        @Param("fieldId") Long fieldId, 
                                                                        @Param("tenantId") Long tenantId);

        /**
         * 恢复已删除的记录并更新业务规则
         * 使用原生 SQL 绕过 MyBatis Plus 的逻辑删除过滤，直接更新已删除的记录
         */
        @Update("UPDATE dynamic_model_field_assignment " +
                "SET deleted = false, " +
                "    required = #{required}, " +
                "    default_value = #{defaultValue}, " +
                "    is_searchable = #{isSearchable}, " +
                "    is_sortable = #{isSortable}, " +
                "    validation_rules = #{validationRules}, " +
                "    sort = #{sort}, " +
                "    update_time = CURRENT_TIMESTAMP " +
                "WHERE id = #{id}")
        void restoreAndUpdateDeletedRecord(@Param("id") Long id,
                                        @Param("required") Boolean required,
                                        @Param("isSearchable") Boolean isSearchable,
                                        @Param("isSortable") Boolean isSortable,
                                        @Param("defaultValue") String defaultValue,
                                        @Param("validationRules") String validationRules,
                                        @Param("sort") Integer sort);

        /**
         * 根据模型ID删除所有字段分配
         */
        default void deleteByModelId(Long modelId) {
                delete(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelId, modelId));
        }

        /**
         * 根据字段ID删除所有模型分配
         */
        default void deleteByFieldId(Long fieldId) {
                delete(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getFieldId, fieldId));
        }

        /**
         * 统计模型下的字段数量
         */
        default Long selectCountByModelId(Long modelId) {
                return selectCount(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelId, modelId));
        }

        // ========== 关联字段支持（FR-BDA-030~034）==========
        /**
         * 根据关联字段库ID查询使用该字段的分配列表
         *
         * @param refLibraryId 关联字段库ID
         * @return 使用该关联字段的分配列表
         */
        default List<ModelFieldAssignmentDO> selectByRefLibraryId(Long refLibraryId) {
                return selectList(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getRefLibraryId, refLibraryId));
        }

        /**
         * 统计关联字段库的使用次数
         *
         * @param refLibraryId 关联字段库ID
         * @return 使用次数
         */
        default Long selectCountByRefLibraryId(Long refLibraryId) {
                return selectCount(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getRefLibraryId, refLibraryId));
        }

        /**
         * 根据模型ID和关联字段库ID查询分配关系
         *
         * @param modelId 模型ID
         * @param refLibraryId 关联字段库ID
         * @return 分配关系
         */
        default ModelFieldAssignmentDO selectByModelIdAndRefLibraryId(Long modelId, Long refLibraryId) {
                return selectOne(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelId, modelId)
                        .eq(ModelFieldAssignmentDO::getRefLibraryId, refLibraryId));
        }

        // ========== 业务关联流程优化：级联删除支持 ==========

        /**
         * 根据 Model 关联 ID 删除字段分配
         *
         * @param modelRelationId Model 关联 ID
         * @return 删除的记录数
         */
        default int deleteByModelRelationId(Long modelRelationId) {
                return delete(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelRelationId, modelRelationId));
        }

        /**
         * 根据 Model 关联 ID 查询字段分配列表
         *
         * @param modelRelationId Model 关联 ID
         * @return 字段分配列表
         */
        default List<ModelFieldAssignmentDO> selectByModelRelationId(Long modelRelationId) {
                return selectList(new LambdaQueryWrapperX<ModelFieldAssignmentDO>()
                        .eq(ModelFieldAssignmentDO::getModelRelationId, modelRelationId));
        }
}

