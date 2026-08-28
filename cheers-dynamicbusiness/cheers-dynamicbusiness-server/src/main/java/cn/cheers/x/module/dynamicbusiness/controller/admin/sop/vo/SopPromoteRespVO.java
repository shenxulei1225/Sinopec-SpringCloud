package cn.cheers.x.module.dynamicbusiness.controller.admin.sop.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "SOP 实例升格为新模板 Response")
@Data
public class SopPromoteRespVO {

    @Schema(description = "新 SOP 模板实体 id")
    private Long newTemplateId;
}
