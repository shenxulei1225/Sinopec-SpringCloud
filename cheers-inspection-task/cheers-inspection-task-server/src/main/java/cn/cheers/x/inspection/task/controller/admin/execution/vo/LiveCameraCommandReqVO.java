package cn.cheers.x.inspection.task.controller.admin.execution.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * 画面上这一下负载操作。数由画面控件带来，这里不补默认值。
 */
@Data
public class LiveCameraCommandReqVO {

    @NotBlank(message = "要说明这一下是哪条操作")
    private String actionCode;

    @NotNull(message = "这一下还没有数")
    private Map<String, Object> params;
}
