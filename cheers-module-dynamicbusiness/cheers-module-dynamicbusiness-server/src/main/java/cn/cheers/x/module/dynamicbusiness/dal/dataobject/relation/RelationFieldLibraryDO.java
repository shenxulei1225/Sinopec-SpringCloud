package cn.cheers.x.module.dynamicbusiness.dal.dataobject.relation;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 关联字段库 DO
 * 
 * 业务含义：全局的关联字段定义库，存储"字段名称 → 关联目标"的映射关系。
 * 使用松散引用（存储编码，不使用外键约束），支持预定义尚未创建的目标。
 * 
 * 例如：
 * - 安全负责人 → 人员管理/员工
 * - 操作员 → 人员管理/员工
 * - 关联设备 → 设备管理/设备
 * 
 * @author yudao
 */
@TableName("dynamic_relation_field_library")
@KeySequence("dynamic_relation_field_library_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelationFieldLibraryDO extends TenantBaseDO {

    /**
     * 字段ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 字段名称
     * 
     * 如"安全负责人"、"操作员"、"关联设备"
     */
    private String fieldName;

    /**
     * 字段编码
     * 
     * 如"safety_manager"、"operator"、"related_device"
     * 格式：小写字母开头，只包含小写字母、数字和下划线
     */
    private String fieldCode;

    /**
     * 关联业务类型编码（松散引用）
     *
     * 存储 EntityType 的编码，不使用外键约束
     * 如"personnel"、"equipment"
     */
    private String refEntityType;

    /**
     * 关联目标 Model 编码（历史字段，已弃用）
     *
     * 已改为按业务级引用，不再区分模型级目标。
     * 该字段仅用于兼容旧数据结构，不落库。
     */
    @Deprecated
    @TableField(exist = false)
    private String targetModelCode;

    /**
     * 展示字段编码
     * 
     * 指定关联展示时使用的字段编码
     * NULL 表示使用目标业务类型的默认名称字段
     */
    private String displayFieldCode;

    /**
     * 是否启用约束器
     */
    private Boolean constraintEnabled;

    /**
     * 约束器类型
     * 
     * NONE / ROLE_DEPT / EQUIPMENT_CATEGORY
     */
    private String constraintType;

    /**
     * 字段说明
     */
    private String description;

    /**
     * 使用次数
     * 
     * 记录该字段被 Model 选用的次数
     */
    private Integer usageCount;

    /**
     * 是否系统预置
     * 
     * 系统预置字段不可删除
     */
    private Boolean isSystem;
}
