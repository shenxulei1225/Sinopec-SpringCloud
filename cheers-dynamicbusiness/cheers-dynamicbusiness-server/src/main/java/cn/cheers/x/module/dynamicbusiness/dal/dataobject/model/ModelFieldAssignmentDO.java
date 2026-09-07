package cn.cheers.x.module.dynamicbusiness.dal.dataobject.model;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.module.dynamicbusiness.enums.model.FieldSourceEnum;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 模型字段 DO
 *
 * 业务含义：将 Model（业务模型/品类）与 Field（字段）进行多对多关联，
 * 用于定义某个 Model 有哪些字段、字段类型与校验规则。
 *
 * @author yudao
 */
@TableName("dynamic_model_field_assignment")
@KeySequence("dynamic_model_field_assignment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelFieldAssignmentDO extends TenantBaseDO {

    /**
     * 分配ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 模型编码（迁移幂等键，对应 {@link ModelDO#getCode()}）
     */
    private String modelCode;

    /**
     * 字段ID
     */
    private Long fieldId;

    /**
     * 字段编码（迁移幂等键，对应字段池 {@code dynamic_field.code}）
     */
    private String fieldCode;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否可查询
     */
    private Boolean isSearchable;


    /**
     * 是否可筛选
     */
    private Boolean isFilterable;

    /**
     * 是否可排序
     */
    private Boolean isSortable;

    /**
     * 默认值
     */
    private String defaultValue;

    /**
     * 业务规则（JSON格式）
     */
    private String validationRules;

    /**
     * 排序值
     */
    private Integer sort;

    /**
     * 字段分组 ID
     *
     * 关联 dynamic_model_field_group 表
     */
    private Long fieldGroupId;

    // ========== 字段来源标识 ==========

    /**
     * 字段来源
     *
     * @see FieldSourceEnum
     */
    private String fieldSource;

    // ========== 关联字段支持 ==========

    /**
     * 关联字段库ID
     */
    private Long refLibraryId;

    /**
     * 关联的 Model 关联 ID
     */
    private Long modelRelationId;

    /**
     * 目标业务类型（关联字段兜底信息）
     */
    private String targetEntityType;


}
