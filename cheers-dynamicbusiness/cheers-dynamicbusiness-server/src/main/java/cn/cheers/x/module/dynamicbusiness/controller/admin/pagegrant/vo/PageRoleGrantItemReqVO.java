package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Schema(description = "一个页面上勾选的动作")
@Data
public class PageRoleGrantItemReqVO {

    @Schema(description = "页面编码", example = "inspection-task")
    @NotBlank(message = "请选择页面")
    private String pageCode;

    @Schema(description = "这个页面上已勾选的动作")
    private List<String> actions;
}
