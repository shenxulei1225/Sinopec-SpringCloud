package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 业务实体关联关系 DO
 *
 * 用于定义和管理业务实体之间的关联关系，支持：
 * - 一对一关系（ONE_TO_ONE）
 * - 一对多关系（ONE_TO_MANY）
 * - 多对多关系（MANY_TO_MANY）
 *
 * 关联关系是双向的，通过 sourceEntityId 和 targetEntityId 定义关联方向
 *
 * V1.0.33 扩展：新增字段支持高效的反向查询和统计
 * - fieldCode: 关联来源字段编码
 * - sourceModelCode/targetModelCode: Model 编码，用于按 Model 分组
 * - sourceEntityTypeCode/targetEntityTypeCode: 业务类型编码，用于跨业务类型查询
 */
@TableName("dynamic_entity_relation")
@KeySequence("dynamic_entity_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityRelationDO extends TenantBaseDO {

    /**
     * 关联关系ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 源实体ID
     */
    private Long sourceEntityId;

    /**
     * 目标实体ID
     */
    private Long targetEntityId;

    /**
     * 关联类型
     * ONE_TO_ONE - 一对一
     * ONE_TO_MANY - 一对多
     * MANY_TO_MANY - 多对多
     */
    private String relationType;

    /**
     * 关联名称（用于描述关联关系的业务含义）
     * 例如："所属设备"、"关联任务"、"父级实体"等
     */
    private String relationName;

    /**
     * 关联描述
     */
    private String description;

    /**
     * 关联属性（JSON格式，存储额外的关联属性）
     * 例如：{"priority": 1, "startDate": "2024-01-01"}
     */
    private String relationAttributes;

    /**
     * 状态（1-启用，0-禁用）
     */
    private Integer status;

    // =====================================================
    // V1.0.33 新增字段：支持高效的反向查询和统计
    // 需求：FR-BDA-074, FR-BDA-090
    // =====================================================

    /**
     * 关联来源字段编码
     * 标识是哪个字段产生的关联，用于按字段分组统计
     * 例如："safety_manager"、"device_id"
     */
    private String fieldCode;

    /**
     * 源 Model 编码
     * 用于按 Model 分组统计反向关联
     * 例如："production_task"、"maintenance_task"
     */
    private String sourceModelCode;

    /**
     * 目标 Model 编码
     * 用于反向查询时过滤
     * 例如："employee"、"device"
     */
    private String targetModelCode;

    /**
     * 源业务类型编码
     * 用于跨业务类型查询
     * 例如："task_management"、"device_management"
     */
    private String sourceEntityTypeCode;

    /**
     * 目标业务类型编码
     * 用于跨业务类型查询
     * 例如："personnel_management"、"device_management"
     */
    private String targetEntityTypeCode;
}
