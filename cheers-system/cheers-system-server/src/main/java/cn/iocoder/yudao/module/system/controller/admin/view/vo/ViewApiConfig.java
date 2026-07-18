package cn.iocoder.yudao.module.system.controller.admin.view.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 视图 API 配置")
@Data
public class ViewApiConfig {

    @Schema(description = "创建接口路径", example = "/system/category/create")
    private String createEndpoint;

    @Schema(description = "更新接口路径", example = "/system/category/update")
    private String updateEndpoint;

    @Schema(description = "删除接口路径", example = "/system/category/delete")
    private String deleteEndpoint;

    @Schema(description = "拖拽接口路径", example = "/system/category/drag")
    private String dragEndpoint;

    @Schema(description = "搜索接口路径", example = "/system/category/search")
    private String searchEndpoint;

    @Schema(description = "详情接口路径", example = "/system/category/get")
    private String getEndpoint;
}
