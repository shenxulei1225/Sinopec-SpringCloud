package cn.cheers.x.intelligentanalysis.controller.admin.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 保存平台分析能力。
 *
 * <p>providerCapabilityCode 是服务接入内部使用的厂商编号。动作只引用保存后生成的能力 id。</p>
 */
@Data
public class AnalysisCapabilitySaveReqVO {

    private Long id;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Long providerId;

    @NotBlank
    private String providerCapabilityCode;

    @NotBlank
    private String capabilityVersion;

    private List<String> supportedMedia = new ArrayList<>();

    private Map<String, Object> parameterSchema = new LinkedHashMap<>();

    private Map<String, Object> resultSchema = new LinkedHashMap<>();

    private Boolean enabled;
}
