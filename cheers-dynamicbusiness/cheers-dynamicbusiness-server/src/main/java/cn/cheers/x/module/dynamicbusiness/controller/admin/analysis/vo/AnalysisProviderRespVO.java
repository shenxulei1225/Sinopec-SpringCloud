package cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 智能分析服务接入展示。
 */
@Data
public class AnalysisProviderRespVO {

    private Long id;

    private String code;

    private String name;

    private String adapterType;

    private String baseUrl;

    private String credentialRef;

    private Map<String, Object> config = new LinkedHashMap<>();

    private Boolean enabled;
}
