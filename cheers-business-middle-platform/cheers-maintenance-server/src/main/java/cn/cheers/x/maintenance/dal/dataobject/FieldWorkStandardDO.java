package cn.cheers.x.maintenance.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName(value = "mm_field_work_standard", autoResultMap = true)
@KeySequence("mm_field_work_standard_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldWorkStandardDO extends BaseDO {
    @TableId
    private Long id;
    private String code;
    private String name;
    private Integer versionNo;
    private String scope;
    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String stepsJson;
    private Integer status;
    private Long tenantId;
}
