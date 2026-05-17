package cn.iocoder.yudao.module.alarm.dal.dataobject;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 告警附件 DO
 *
 * @author 告警管理模块
 */
@TableName("alarm_attachment")
@KeySequence("alarm_attachment_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlarmAttachmentDO extends BaseDO {

    /**
     * 附件ID
     */
    @TableId
    private Long id;

    /**
     * 告警ID
     */
    private Long alarmId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件类型：IMAGE/VIDEO
     */
    private String fileType;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 租户ID
     */
    private Long tenantId;

}
