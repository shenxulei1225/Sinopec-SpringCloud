package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 读 merge 用的 SOP 模板快照。
 */
@Data
public class SopTemplateSnapshot {

    private List<SopStepTemplateRef> defaultSteps;
    private Map<String, Object> defaultParams = new LinkedHashMap<>();
}
