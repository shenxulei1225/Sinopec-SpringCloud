package cn.cheers.x.inspection.task.service.query.model;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务模板视图。
 */
@Data
public class InspectionTaskTemplateView {

    private Long id;

    private Long parentId;

    private Long categoryId;

    private String templateCode;

    private String templateName;

    private Boolean enabled;

    private String remark;

    private String defaultSchedulePolicyName;

    private Integer objectCount;

    private Integer itemCount;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
