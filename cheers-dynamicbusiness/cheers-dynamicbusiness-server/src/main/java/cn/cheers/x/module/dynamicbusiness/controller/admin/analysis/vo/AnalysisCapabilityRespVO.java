package cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 平台分析能力展示。
 */
@Data
public class AnalysisCapabilityRespVO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private Long providerId;

    private String providerName;

    private String providerCapabilityCode;

    private String capabilityVersion;

    private List<String> supportedMedia = new ArrayList<>();

    private Map<String, Object> parameterSchema = new LinkedHashMap<>();

    private Map<String, Object> resultSchema = new LinkedHashMap<>();

    private Boolean enabled;

    private Boolean available;
}
