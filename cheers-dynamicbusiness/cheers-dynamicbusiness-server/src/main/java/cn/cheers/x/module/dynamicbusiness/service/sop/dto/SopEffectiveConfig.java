package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * merge 后的生效配置。
 */
@Data
public class SopEffectiveConfig {

    private List<SopStepTemplateRef> steps;
    private Map<String, Object> params = new LinkedHashMap<>();
}
