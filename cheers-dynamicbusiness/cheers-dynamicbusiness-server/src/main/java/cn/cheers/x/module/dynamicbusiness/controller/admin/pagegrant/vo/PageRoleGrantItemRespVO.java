package cn.cheers.x.module.dynamicbusiness.controller.admin.pagegrant.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Schema(description = "一个页面上已保存的动作")
@Data
public class PageRoleGrantItemRespVO {

    @Schema(description = "页面编码")
    private String pageCode;

    @Schema(description = "已勾选动作")
    private List<String> actions;
}
