package cn.cheers.x.module.dynamicbusiness.api.analysis.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 提交一次图片或录像分析。
 *
 * <p>调用方只选择平台分析能力并提供媒体引用。厂商地址、算法编号和鉴权信息不进入本契约。</p>
 */
@Data
public class AnalysisJobSubmitReqDTO {

    @NotNull
    private Long analysisCapabilityId;

    @NotBlank
    private String inputType;

    @NotBlank
    private String inputRef;

    private Map<String, Object> parameters = new LinkedHashMap<>();

    private String businessRefType;

    private String businessRefId;
}
