package cn.cheers.x.module.dynamicbusiness.controller.admin.reference.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "引用候选项")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReferenceCandidateRespVO {

    @Schema(description = "候选值 ID", example = "1")
    private String id;

    @Schema(description = "显示名称", example = "张三")
    private String label;

    @Schema(description = "扩展数据 JSON")
    private String extra;
}