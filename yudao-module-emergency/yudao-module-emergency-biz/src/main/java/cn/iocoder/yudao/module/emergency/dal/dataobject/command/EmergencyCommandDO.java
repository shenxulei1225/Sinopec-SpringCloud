package cn.iocoder.yudao.module.emergency.dal.dataobject.command;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.emergency.framework.mybatis.PostgreSQLJsonbTypeHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 应急指令
 */
@TableName("emergency_command")
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmergencyCommandDO extends TenantBaseDO {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 指令编号（唯一）
     */
    private String commandNo;

    /**
     * 指令标题
     */
    private String title;

    /**
     * 指令内容
     */
    private String content;

    /**
     * 指令类型（引用数据字典：emergency_command_type）
     */
    private String commandType;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 截止时间
     */
    private LocalDateTime deadline;

    /**
     * 指令状态
     */
    private String status;

    /**
     * 执行阶段
     */
    private String stage;

    /**
     * 关联事件ID
     */
    private Long eventId;

    /**
     * 关联响应ID
     */
    private Long responseId;

    /**
     * 模板ID
     */
    private Long templateId;

    /**
     * 附件信息（JSON数组）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private List<Map<String, Object>> attachments;

    /**
     * 发布信息（JSONB格式，包含：发布时间、发布人等扩展信息）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> issueInfo;

    /**
     * 执行信息（JSONB格式，包含：执行状态、执行人、完成时间等）
     */
    @TableField(typeHandler = PostgreSQLJsonbTypeHandler.class)
    private Map<String, Object> executionInfo;

    /**
     * 关联自定义配置表单ID（可选，仅创建时设置，更新时不允许修改）
     * 用于用户使用自定义表单上报数据
     * 当设置时必须验证表单配置ID的有效性（存在且未删除）
     */
    private Long formId;
}
