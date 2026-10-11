package cn.cheers.x.inspection.task.controller.admin.execution.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 画面上这一下飞行操作。起飞和返航的数不从按钮带来。
 */
@Data
public class LiveFlightCommandReqVO {

    @NotBlank(message = "要说明这一下是起飞还是返航")
    private String command;
}
