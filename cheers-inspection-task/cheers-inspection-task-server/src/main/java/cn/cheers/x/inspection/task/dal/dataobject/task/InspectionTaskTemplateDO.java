package cn.cheers.x.inspection.task.dal.dataobject.task;

import cn.cheers.x.framework.mybatis.core.dataobject.BaseDO;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 巡检任务模板 DO。
 */
@TableName(value = "inspection_task_template", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class InspectionTaskTemplateDO extends BaseDO {

    @TableId
    private Long id;

    private Long parentId;

    private Long categoryId;

    private String templateCode;

    private String templateName;

    private Boolean enabled;

    private String remark;

    @TableField(typeHandler = JacksonTypeHandler.class)
    private InspectionContent inspectionContent;

    private Long defaultSchedulePolicyId;
}
