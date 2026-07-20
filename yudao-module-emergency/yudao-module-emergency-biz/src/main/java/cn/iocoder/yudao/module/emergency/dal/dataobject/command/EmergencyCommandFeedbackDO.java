package cn.iocoder.yudao.module.emergency.dal.dataobject.command;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.util.List;
import java.util.Map;

/**
 * 应急指令反馈
 */
@TableName("emergency_command_feedback")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyCommandFeedbackDO extends TenantBaseDO {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 指令ID
     */
    private Long commandId;

    /**
     * 执行人ID
     */
    private Long executorId;

    /**
     * 反馈状态
     */
    private String status;

    /**
     * 进度百分比
     */
    private Integer progress;

    /**
     * 反馈内容
     */
    private String feedback;

    /**
     * 附件信息（JSON）
     */
    private List<Map<String, Object>> attachments;

    /**
     * 反馈类型
     */
    private String feedbackType;
}
