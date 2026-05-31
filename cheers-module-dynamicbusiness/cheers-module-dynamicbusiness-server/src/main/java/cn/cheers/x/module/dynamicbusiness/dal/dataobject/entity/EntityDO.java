package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
    private String businessTypeCode;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 实体名称
     */
    private String name;

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
     * 自定义字段（JSON格式,存储在 PostgreSQL JSONB 列中）
     * 使用 StringJsonbTypeHandler 处理 JSONB ↔ String 的转换
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private String customFields;
}