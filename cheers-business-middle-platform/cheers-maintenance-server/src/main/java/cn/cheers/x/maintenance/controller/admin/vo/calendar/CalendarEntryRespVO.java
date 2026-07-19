package cn.cheers.x.maintenance.controller.admin.vo.calendar;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class CalendarEntryRespVO {
    private Long id; private Long handbookId; private Long assetId; private String scope;
    private LocalDate plannedDate; private String status; private String runtimeJobId;
    private List<Long> workOrderIds; private String lastError;
    private LocalDateTime createTime;
}
