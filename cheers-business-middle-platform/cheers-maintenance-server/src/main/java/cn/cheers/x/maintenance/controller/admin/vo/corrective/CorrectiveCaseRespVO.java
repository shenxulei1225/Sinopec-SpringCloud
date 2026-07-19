package cn.cheers.x.maintenance.controller.admin.vo.corrective;
import lombok.Data;
import java.time.LocalDateTime;
@Data
public class CorrectiveCaseRespVO {
    private Long id; private String caseNo; private String title; private String description;
    private Long assetId; private String assetTypeCode; private String priority; private String status;
    private Long fieldStandardId; private Long workOrderId; private String processInstanceKey;
    private LocalDateTime createTime;
}
