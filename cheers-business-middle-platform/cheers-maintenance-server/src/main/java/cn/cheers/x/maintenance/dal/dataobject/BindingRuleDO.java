package cn.cheers.x.maintenance.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("mm_binding_rule")
@KeySequence("mm_binding_rule_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindingRuleDO extends BaseDO {
    @TableId
    private Long id;
    private String code;
    private String name;
    private String scope;
    private Long assetId;
    private String assetTypeCode;
    private String frequencyCode;
    private Long handbookId;
    private Long fieldStandardId;
    private String orchestrationTemplateCode;
    private Integer priority;
    private Integer status;
    private Long tenantId;
}
