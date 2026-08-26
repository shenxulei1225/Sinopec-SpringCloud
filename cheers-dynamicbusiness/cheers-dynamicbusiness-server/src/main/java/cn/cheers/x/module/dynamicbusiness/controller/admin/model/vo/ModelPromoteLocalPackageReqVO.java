package cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

@Schema(description = "管理后台 - 本地型号包晋升请求")
@Data
public class ModelPromoteLocalPackageReqVO {

    @Schema(description = "本地型号编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "modelId 不能为空")
    private Long modelId;

    @Schema(description = "本地字段编码到既有公司字段编码的合并映射；未映射字段直接晋升")
    private Map<String, String> fieldMergeMap;
}
