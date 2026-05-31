package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 扩展字段索引 DO
 * 
 * 业务含义：存储 Entity 扩展字段的索引数据，用于支持扩展字段的范围查询和排序。
 * 只有标记为 is_searchable=true 的字段才会同步到此表。
 * 
 * 索引策略：
 * - 字符串类型：存储到 value_string
 * - 数值类型：存储到 value_number
 * - 日期类型：存储到 value_date
 * - 日期时间类型：存储到 value_datetime
 * - 布尔类型：存储到 value_boolean
 * 
 * @author yudao
 */
@TableName("dynamic_entity_field_index")
@KeySequence("dynamic_entity_field_index_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntityFieldIndexDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 字段编码
     */
    private String fieldCode;

    /**
     * 字符串值
     * 用于 STRING、SELECT、ENTITY_REF 等类型
     */
    private String valueString;

    /**
     * 数值
     * 用于 INTEGER、DECIMAL 类型
     */
    private BigDecimal valueNumber;

    /**
     * 日期值
     * 用于 DATE 类型
     */
    private LocalDate valueDate;

    /**
     * 日期时间值
     * 用于 DATETIME 类型
     */
    private LocalDateTime valueDatetime;

    /**
     * 布尔值
     * 用于 BOOLEAN 类型
     */
    private Boolean valueBoolean;
}
