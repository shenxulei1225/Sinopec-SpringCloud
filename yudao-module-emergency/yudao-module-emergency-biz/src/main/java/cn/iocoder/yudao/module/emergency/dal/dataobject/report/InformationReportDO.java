package cn.iocoder.yudao.module.emergency.dal.dataobject.report;

import cn.iocoder.yudao.module.emergency.dal.dataobject.EmergencyBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 信息报送流程 DO
 *
 * @author 芋道源码
 */
@TableName("emergency_information_report")
@KeySequence("emergency_information_report_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationReportDO extends EmergencyBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;

    /**
     * 关联事件ID
     */
    private Long eventId;

    /**
     * 报送类型（RECEIVE接报/PRESENT呈报/REPORT上报）
     */
    private String reportType;

    /**
     * 报送对象
     */
    private String reportTarget;

    /**
     * 报送内容
     */
    private String reportContent;

    /**
     * 报送状态（PENDING待报送/SUBMITTED已报送/CONFIRMED已确认）
     */
    private String status;

    /**
     * 接报时间（BR-013：记录完整的报送流程）
     */
    private LocalDateTime receiveTime;

    /**
     * 呈报时间（BR-013：记录完整的报送流程）
     */
    private LocalDateTime presentTime;

    /**
     * 上报时间（BR-013：记录完整的报送流程）
     */
    private LocalDateTime reportTime;

    /**
     * 确认时间
     */
    private LocalDateTime confirmTime;

    /**
     * 确认人
     */
    private String confirmPerson;

    /**
     * 确认备注
     */
    private String confirmRemark;

    /**
     * 时限要求（分钟）
     */
    private Integer timeLimit;

    /**
     * 是否超时
     */
    private Boolean timeout;
}




