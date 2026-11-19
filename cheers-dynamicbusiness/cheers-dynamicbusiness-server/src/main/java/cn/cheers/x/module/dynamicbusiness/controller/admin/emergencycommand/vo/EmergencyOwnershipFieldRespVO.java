package cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 接报弹窗上的一项归属。
 * 名称和是否必填来自当前租户对应急事件目录的归属声明，不写死场站或管线。
 */
@Data
public class EmergencyOwnershipFieldRespVO {

    @Schema(description = "归属字段编码。提交时放在 ownership 里")
    private String fieldCode;

    @Schema(description = "这一项在页面上的名称，取字段上配置的名字")
    private String label;

    @Schema(description = "选项来自哪一本目录")
    private String targetRegistryCode;

    @Schema(description = "保存时必须选一条")
    private Boolean required;
}
