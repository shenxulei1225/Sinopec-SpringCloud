package cn.cheers.x.maintenance.controller.admin.vo.calendar;
import cn.cheers.x.framework.common.pojo.PageParam;
import lombok.Data; import lombok.EqualsAndHashCode;
import java.time.LocalDate;
@Data @EqualsAndHashCode(callSuper = true)
public class CalendarPageReqVO extends PageParam {
    private Long handbookId; private Long assetId; private String status;
    private LocalDate from; private LocalDate to;
}
