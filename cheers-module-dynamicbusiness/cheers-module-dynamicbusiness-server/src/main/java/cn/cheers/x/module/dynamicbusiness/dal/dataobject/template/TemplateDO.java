package cn.cheers.x.module.dynamicbusiness.dal.dataobject.template;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 字段模板 DO
 * 
 * 业务含义：字段组合的预设模板，作为创建 Model 的起点。
 * Template 定义一组常用字段组合，用户创建 Model 时可选择一个 Template，
 * 系统将 Template 的字段**复制**到新 Model 中。
 * 
 * Template 与 Model 是"复制"关系而非"继承"关系：
 * - 创建 Model 时，Template 的字段被复制到 Model
 * - 之后 Model 与 Template 完全独立
 * - 修改 Template 不影响已创建的 Model
 * - 修改 Model 也不影响 Template
 * 
 * @author yudao
 */
@TableName("dynamic_template")
@KeySequence("dynamic_template_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDO extends TenantBaseDO {

    /**
     * 模板ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 模板编码（全局唯一，系统自动生成）
     */
    private String code;

    /**
     * 模板名称
     * 系统内必须唯一
     */
    private String name;

    /**
     * 业务类型编码（用于区分不同业务领域）
     * 例如：equipment（设备管理）、task（任务管理）
     */
    private String entityTypeCode;

    /**
     * 模板描述
     */
    private String description;

    /**
     * 模板状态（1-启用，0-禁用）
     */
    private Integer status;

    /**
     * 是否为系统预设模板
     * 
     * true: 系统预设模板，不允许删除
     * false: 用户自定义模板，可以删除
     */
    private Boolean isSystem;

    // ========== 辅助方法 ==========

    /**
     * 判断是否为系统预设模板
     */
    public boolean isSystemTemplate() {
        return Boolean.TRUE.equals(this.isSystem);
    }

    /**
     * 判断是否可以删除
     * 
     * 系统预设模板不能删除
     */
    public boolean canDelete() {
        return !isSystemTemplate();
    }
}
