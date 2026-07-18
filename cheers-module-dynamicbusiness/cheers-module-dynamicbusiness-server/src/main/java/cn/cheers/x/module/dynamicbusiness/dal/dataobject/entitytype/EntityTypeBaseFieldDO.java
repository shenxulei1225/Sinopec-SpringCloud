package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 业务类型固定列字段定义 DO
 * 
 * 业务含义:存储每个业务类型的固定列字段配置
 * - 固定列字段自动继承到该业务类型下的所有 Model
 * - 用户创建 Model 时无需手动添加这些字段
 * - 固定列字段存储在专用表的物理列中,支持索引和约束
 * 
 * 与扩展字段的区别:
 * - 固定列字段(BASE):存储在专用表的固定列中,由系统定义,不可删除
 * - 扩展字段(CUSTOM):存储在 custom_fields JSON 中,由用户添加,可删除
 * 
 * @author yudao
 */
@TableName("dynamic_entity_type_base_field")
@KeySequence("dynamic_entity_type_base_field_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityTypeBaseFieldDO extends TenantBaseDO {

    /**
     * 字段ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 业务类型编码
     * 
     * 关联 dynamic_entity_type_config.entity_type_code
     * 例如:equipment, maintenance_task
     */
    private String entityTypeCode;

    /**
     * 字段编码（与字段库 dynamic_field.code 一致）
     */
    private String fieldCode;

    /**
     * 字段库字段 ID（权威关联）
     */
    private Long libraryFieldId;

    /**
     * 字段显示名称
     * 
     * 用于界面展示
     * 例如:设备编码、区域/位置、制造商、健康评分
     */
    private String fieldName;

    /**
     * 数据类型
     * 
     * 支持的类型:
     * - TEXT: 文本类型
     * - NUMBER: 数字类型
     * - DATE: 日期类型(不含时间)
     * - DATETIME: 日期时间类型
     * - BOOLEAN: 布尔类型
     * - ENUM: 枚举类型
     * - REFERENCE: 引用类型(关联其他模型)
     */
    private String dataType;

    /**
     * 是否必填
     */
    private Boolean required;

    /**
     * 是否可搜索（列表）
     */
    private Boolean isSearchable;

    /**
     * 是否可筛选（列表）
     */
    private Boolean isFilterable;

    /**
     * 是否可排序（列表）
     */
    private Boolean isSortable;

    /**
     * 默认值
     * 
     * 字符串格式,根据数据类型进行解析
     */
    private String defaultValue;

    /**
     * 字段描述
     */
    private String description;

    /**
     * 类型配置(JSON格式)
     * 
     * 根据数据类型存储不同的配置:
     * - NUMBER: {"precision": 10, "scale": 2, "min": 0, "max": 100}
     * - ENUM: {"options": [{"value": "NORMAL", "label": "正常"}, ...]}
     * - REFERENCE: {"refModel": "area", "refField": "id", "refDisplayField": "name"}
     */
    private String typeConfig;

    /**
     * 排序顺序
     * 
     * 用于控制字段在界面上的显示顺序
     */
    private Integer sortOrder;

    /**
     * 状态(1=启用,0=禁用)
     */
    private Integer status;

    // ========== 便捷方法 ==========

    /**
     * 判断是否为文本类型
     */
    public boolean isTextType() {
        return "TEXT".equals(this.dataType);
    }

    /**
     * 判断是否为数字类型
     */
    public boolean isNumberType() {
        return "NUMBER".equals(this.dataType);
    }

    /**
     * 判断是否为日期类型
     */
    public boolean isDateType() {
        return "DATE".equals(this.dataType);
    }

    /**
     * 判断是否为日期时间类型
     */
    public boolean isDateTimeType() {
        return "DATETIME".equals(this.dataType);
    }

    /**
     * 判断是否为布尔类型
     */
    public boolean isBooleanType() {
        return "BOOLEAN".equals(this.dataType);
    }

    /**
     * 判断是否为枚举类型
     */
    public boolean isEnumType() {
        return "ENUM".equals(this.dataType);
    }

    /**
     * 判断是否为引用类型
     */
    public boolean isReferenceType() {
        return "REFERENCE".equals(this.dataType);
    }

    /**
     * 判断是否启用
     */
    public boolean isEnabled() {
        return Integer.valueOf(1).equals(this.status);
    }
}
