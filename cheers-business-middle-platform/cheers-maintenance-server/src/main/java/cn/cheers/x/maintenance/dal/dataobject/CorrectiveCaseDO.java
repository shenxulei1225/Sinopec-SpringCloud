package cn.cheers.x.maintenance.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@TableName("mm_corrective_case")
@KeySequence("mm_corrective_case_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectiveCaseDO extends BaseDO {
    @TableId private Long id;
    private String caseNo;
    private String title;
    private String description;
    private Long assetId;
    private String assetTypeCode;
    private String priority;
    private String status;
    private Long fieldStandardId;
    private Long workOrderId;
    private String processInstanceKey;
    private Long tenantId;
}
