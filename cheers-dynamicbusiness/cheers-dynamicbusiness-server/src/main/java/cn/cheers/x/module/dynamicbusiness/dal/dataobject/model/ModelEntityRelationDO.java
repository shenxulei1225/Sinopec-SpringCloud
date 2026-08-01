package cn.cheers.x.module.dynamicbusiness.dal.dataobject.model;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 型号—实体多对多关联 DO。
 *
 * <p>与分类—实体同构：跨类型挂靠（如设备型号 ↔ 检查内容）。本类型实体归属仍用实体表 {@code model_id}，
 * 不写本表。</p>
 *
 * <p>运行时物理表为 {@code dynamic_model_entity_relation_t{tenantId}}（基表仅模板）。</p>
 */
@TableName("dynamic_model_entity_relation")
@KeySequence("dynamic_model_entity_relation_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelEntityRelationDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 型号 ID */
    private Long modelId;

    /** 型号所属实际存储类型（如 equipment） */
    private String modelEntityTypeCode;

    /** 挂靠实体 ID */
    private Long entityId;

    /** 挂靠实体实际存储类型（如 inspection_item） */
    private String entityTypeCode;

    /** 实体业务域镜像；不参与唯一键 */
    private String domain;

    /** 同一型号上下文内排序 */
    private Integer sort;
}
