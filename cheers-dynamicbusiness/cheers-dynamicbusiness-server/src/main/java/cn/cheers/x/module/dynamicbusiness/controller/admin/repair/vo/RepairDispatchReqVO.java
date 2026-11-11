package cn.cheers.x.module.dynamicbusiness.controller.admin.repair.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RepairDispatchReqVO {

    @NotNull(message = "报修单不能为空")
    private Long id;

    @NotBlank(message = "业务域不能为空")
    private String scope;

    @NotBlank(message = "工单标题不能为空")
    @Size(max = 256, message = "工单标题长度不能超过256个字符")
    private String title;

    @NotBlank(message = "工作内容不能为空")
    @Size(max = 8000, message = "工作内容长度不能超过8000个字符")
    private String workContent;

    /** 派单的人不选流程。只有处理内容上已经挂了标准时才带来。 */
    private Long standardId;

    @NotNull(message = "班组不能为空")
    private Long teamId;

    private String teamName;

    private Long assetId;

    /** 派单人指定的人。空着表示先派到班组，由班组里的人认领。 */
    private Long assigneeUserId;

    private String assigneeName;

    /** 派单人写下的执行时间。写入工单的处理时刻。 */
    @NotBlank(message = "请填写执行时间")
    private String handlingTime;
}
