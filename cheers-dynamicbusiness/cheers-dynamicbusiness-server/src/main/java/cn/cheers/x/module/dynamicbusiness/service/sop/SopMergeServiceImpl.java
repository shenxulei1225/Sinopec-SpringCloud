package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopEffectiveConfig;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepOverride;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepTemplateRef;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * SOP merge 实现。
 *
 * <p><b>权威</b>：生效配置 = merge(模板默认步骤/参数, 步骤差量, 参数差量)。</p>
 * <p><b>禁止</b>：静默补参、解析业务巡检点 / 停靠站、跨实例共享参数。</p>
 */
@Service
public class SopMergeServiceImpl implements SopMergeService {

    @Override
    public SopMergeResult merge(SopTemplateSnapshot template,
                                SopStepOverride stepOverride,
                                Map<String, Object> paramOverride) {
        if (template == null) {
            return SopMergeResult.failure(List.of("MISSING_TEMPLATE"));
        }
        List<SopStepTemplateRef> steps = resolveEffectiveSteps(template, stepOverride);
        Map<String, Object> params = mergeParams(template, paramOverride);
        List<String> gapCodes = validateRequiredParams(steps, params);
        if (!gapCodes.isEmpty()) {
            return SopMergeResult.failure(gapCodes);
        }
        SopEffectiveConfig effective = new SopEffectiveConfig();
        effective.setSteps(steps);
        effective.setParams(params);
        return SopMergeResult.success(effective);
    }

    private List<SopStepTemplateRef> resolveEffectiveSteps(SopTemplateSnapshot template,
                                                           SopStepOverride stepOverride) {
        List<SopStepTemplateRef> source;
        if (stepOverride != null
                && stepOverride.getReplaceSteps() != null
                && !stepOverride.getReplaceSteps().isEmpty()) {
            source = stepOverride.getReplaceSteps();
        } else {
            source = template.getDefaultSteps() != null ? template.getDefaultSteps() : List.of();
        }
        List<SopStepTemplateRef> result = new ArrayList<>(source.size());
        for (int i = 0; i < source.size(); i++) {
            SopStepTemplateRef step = source.get(i);
            SopStepTemplateRef copy = new SopStepTemplateRef();
            copy.setStepTemplateId(step.getStepTemplateId());
            copy.setOrder(step.getOrder() != null ? step.getOrder() : i + 1);
            copy.setParamSlots(step.getParamSlots() != null
                    ? new ArrayList<>(step.getParamSlots())
                    : new ArrayList<>());
            result.add(copy);
        }
        return result;
    }

    private Map<String, Object> mergeParams(SopTemplateSnapshot template,
                                            Map<String, Object> paramOverride) {
        Map<String, Object> merged = new LinkedHashMap<>();
        if (template.getDefaultParams() != null) {
            merged.putAll(template.getDefaultParams());
        }
        if (paramOverride != null) {
            merged.putAll(paramOverride);
        }
        return merged;
    }

    private List<String> validateRequiredParams(List<SopStepTemplateRef> steps,
                                                Map<String, Object> params) {
        Set<String> slots = new LinkedHashSet<>();
        for (SopStepTemplateRef step : steps) {
            if (step.getParamSlots() == null) {
                continue;
            }
            slots.addAll(step.getParamSlots());
        }
        List<String> gapCodes = new ArrayList<>();
        for (String slot : slots) {
            if (!isParamValuePresent(params.get(slot))) {
                gapCodes.add("MISSING_PARAM:" + slot);
            }
        }
        return gapCodes;
    }

    private boolean isParamValuePresent(Object value) {
        if (value == null) {
            return false;
        }
        if (value instanceof String s) {
            return !s.trim().isEmpty();
        }
        return true;
    }
}
