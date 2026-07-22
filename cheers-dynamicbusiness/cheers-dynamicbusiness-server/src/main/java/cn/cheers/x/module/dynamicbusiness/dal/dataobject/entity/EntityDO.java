package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.cheers.x.framework.mybatis.core.type.JsonbMapTypeHandler;
import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.Map;

/**
 * 实体 DO
 *
 * @author 基础服务模块
 */
@TableName(value = "dynamic_entity", autoResultMap = true)
@KeySequence("dynamic_entity_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityDO extends TenantBaseDO {

    /**
     * 实体ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码
     */
    private String entityTypeCode;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 实体名称
     */
    private String name;

    /**
     * 业务编码（专用表 ent_* 常见 NOT NULL；GENERIC 的 dynamic_entity 无此列，空值不参与 INSERT/UPDATE）。
     */
    @TableField(insertStrategy = FieldStrategy.NOT_EMPTY, updateStrategy = FieldStrategy.NOT_EMPTY)
    private String code;

    /**
     * 父实体ID
     */
    private Long parentId;

    /**
     * 树形路径，用于快速查询子孙节点
     * 格式：,父ID,祖父ID,...
     */
    private String treePath;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态（1-启用，0-禁用）
     */
    private Integer status;

    /**
     * 自定义字段（键为字段编码或字段 ID，持久化在 PostgreSQL JSONB 列）。
     */
    @TableField(typeHandler = JsonbMapTypeHandler.class)
    private Map<String, Object> customFields;
}