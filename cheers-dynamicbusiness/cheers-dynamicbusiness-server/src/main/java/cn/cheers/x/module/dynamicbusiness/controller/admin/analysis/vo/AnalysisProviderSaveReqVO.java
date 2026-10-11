package cn.cheers.x.module.dynamicbusiness.controller.admin.analysis.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 保存智能分析服务接入。
 *
 * <p>credentialRef 是密钥引用。接口不接收明文密码或令牌。</p>
 */
@Data
public class AnalysisProviderSaveReqVO {

    private Long id;

    @NotBlank
    private String code;

    @NotBlank
    private String name;

    @NotBlank
    private String adapterType;

    private String baseUrl;

    private String credentialRef;

    private Map<String, Object> config = new LinkedHashMap<>();

    private Boolean enabled;
}
