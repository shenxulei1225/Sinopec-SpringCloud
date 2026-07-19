package cn.cheers.x.maintenance.controller.admin.vo.calendar;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
@Data
public class CalendarExpandReqVO {
    @NotNull private Long handbookId;
    @NotNull private LocalDate from;
    @NotNull private LocalDate to;
    @NotEmpty private List<Long> assetIds;
}
