package cn.cheers.x.maintenance.dal.dataobject;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.framework.mybatis.core.type.JsonbStringTypeHandler;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDate;

@TableName(value = "mm_calendar_entry", autoResultMap = true)
@KeySequence("mm_calendar_entry_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEntryDO extends BaseDO {
    @TableId private Long id;
    private Long handbookId;
    private Long assetId;
    private String scope;
    private LocalDate plannedDate;
    private String status;
    private String runtimeJobId;
    @TableField(typeHandler = JsonbStringTypeHandler.class)
    private String workOrderIds;
    private String lastError;
    private Long tenantId;
}
