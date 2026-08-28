package cn.cheers.x.module.dynamicbusiness.service.sop;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopEffectiveConfig;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepOverride;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepTemplateRef;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SOP 模板命令实现。
 *
 * <p><b>权威</b>：模板 default_* / 实例 override 在实体 baseFields；merge 走 {@link SopMergeService}。</p>
 * <p><b>禁止</b>：升格改原实例、自动改设备检查绑定、读路径静默补参。</p>
 */
@Service
public class SopTemplateCommandServiceImpl implements SopTemplateCommandService {

    @Resource
    private EntityService entityService;

    @Resource
    private SopMergeService sopMergeService;

    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;

    @Override
    public SopMergeResult getEffective(long sopId) {
        EntityRespVO row = requireSop(sopId);
        Map<String, Object> base = emptyIfNull(row.getBaseFields());
        if (isTemplateRow(base)) {
            return sopMergeService.merge(toTemplateSnapshot(base), null, null);
        }
        Long templateId = readLong(base.get(SopFieldCodes.SOP_TEMPLATE_ID));
        if (templateId == null) {
            return SopMergeResult.failure(List.of("MISSING_SOP_TEMPLATE_ID"));
        }
        EntityRespVO templateRow = requireSop(templateId);
        if (!isTemplateRow(emptyIfNull(templateRow.getBaseFields()))) {
            throw new ServiceException(400, "sop_template_id 必须指向 SOP 模板行");
        }
        SopTemplateSnapshot snapshot = toTemplateSnapshot(emptyIfNull(templateRow.getBaseFields()));
        SopStepOverride stepOverride = parseStepOverride(base.get(SopFieldCodes.STEP_OVERRIDE_JSON));
        Map<String, Object> paramOverride = parseParamMap(base.get(SopFieldCodes.PARAM_OVERRIDE_JSON));
        return sopMergeService.merge(snapshot, stepOverride, paramOverride);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long promoteInstanceToTemplate(long instanceId, String name, List<Long> categoryIds) {
        String trimmedName = name == null ? "" : name.trim();
        if (!StringUtils.hasText(trimmedName)) {
            throw new ServiceException(400, "升格模板名称不能为空");
        }
        if (categoryIds == null || categoryIds.isEmpty()) {
            throw new ServiceException(400, "升格须指定至少一个 SOP 分类");
        }

        EntityRespVO instance = requireSop(instanceId);
        Map<String, Object> instanceBase = emptyIfNull(instance.getBaseFields());
        if (isTemplateRow(instanceBase)) {
            throw new ServiceException(400, "只能对 SOP 实例升格，不能对模板升格");
        }

        SopMergeResult merged = getEffective(instanceId);
        if (!merged.isOk()) {
            throw new ServiceException(400, "升格失败：merge 存在缺口 " + merged.getGapCodes());
        }

        SopEffectiveConfig effective = merged.getEffective();
        EntityCreateReqVO createReq = new EntityCreateReqVO();
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", SopFieldCodes.ENTITY_TYPE_CODE);
        baseFields.put("modelId", instance.getModelId());
        baseFields.put("name", trimmedName);
        baseFields.put("status", 1);
        baseFields.put(SopFieldCodes.IS_TEMPLATE, true);
        baseFields.put(SopFieldCodes.SOP_TEMPLATE_ID, null);
        baseFields.put(SopFieldCodes.STEP_OVERRIDE_JSON, null);
        baseFields.put(SopFieldCodes.PARAM_OVERRIDE_JSON, null);
        baseFields.put(SopFieldCodes.DEFAULT_STEPS_JSON, JSON.toJSONString(effective.getSteps()));
        baseFields.put(SopFieldCodes.DEFAULT_PARAMS_JSON, JSON.toJSONString(effective.getParams()));
        baseFields.put(SopFieldCodes.VERSION_NO, 1);
        baseFields.put(SopFieldCodes.PUBLISH_STATUS, "DRAFT");
        baseFields.put(SopFieldCodes.STEPS_JSON, "[]");
        Object means = instanceBase.get(SopFieldCodes.EXECUTION_MEANS);
        if (means != null) {
            baseFields.put(SopFieldCodes.EXECUTION_MEANS, means);
        }
        Object kind = instanceBase.get(SopFieldCodes.PROCEDURE_KIND);
        if (kind != null) {
            baseFields.put(SopFieldCodes.PROCEDURE_KIND, kind);
        }
        createReq.setBaseFields(baseFields);

        Long newTemplateId = entityService.create(createReq);
        for (Long categoryId : categoryIds) {
            if (categoryId == null) {
                continue;
            }
            Object domain = instanceBase.get("domain");
            categoryEntityLinkService.linkCategoryToEntity(
                    categoryId,
                    newTemplateId,
                    instance.getModelId(),
                    SopFieldCodes.ENTITY_TYPE_CODE,
                    domain != null ? String.valueOf(domain) : null);
        }
        return newTemplateId;
    }

    private EntityRespVO requireSop(long sopId) {
        EntityRespVO row = entityService.get(sopId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (row == null || row.getId() == null) {
            throw new ServiceException(404, "SOP 不存在：" + sopId);
        }
        return row;
    }

    private boolean isTemplateRow(Map<String, Object> base) {
        Object v = base.get(SopFieldCodes.IS_TEMPLATE);
        if (v instanceof Boolean b) {
            return b;
        }
        if (v instanceof Number n) {
            return n.intValue() != 0;
        }
        if (v instanceof String s) {
            return "true".equalsIgnoreCase(s.trim()) || "1".equals(s.trim());
        }
        return false;
    }

    private SopTemplateSnapshot toTemplateSnapshot(Map<String, Object> base) {
        SopTemplateSnapshot snapshot = new SopTemplateSnapshot();
        snapshot.setDefaultSteps(parseStepList(base.get(SopFieldCodes.DEFAULT_STEPS_JSON)));
        snapshot.setDefaultParams(parseParamMap(base.get(SopFieldCodes.DEFAULT_PARAMS_JSON)));
        return snapshot;
    }

    private SopStepOverride parseStepOverride(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof SopStepOverride override) {
            return override;
        }
        if (raw instanceof Map<?, ?> map) {
            Object replace = map.get("replaceSteps");
            SopStepOverride override = new SopStepOverride();
            override.setReplaceSteps(parseStepList(replace));
            return override;
        }
        if (raw instanceof String s && StringUtils.hasText(s)) {
            return JSON.parseObject(s, SopStepOverride.class);
        }
        return JSON.parseObject(JSON.toJSONString(raw), SopStepOverride.class);
    }

    private List<SopStepTemplateRef> parseStepList(Object raw) {
        if (raw == null) {
            return new ArrayList<>();
        }
        if (raw instanceof List<?> list) {
            return JSON.parseObject(JSON.toJSONString(list), new TypeReference<List<SopStepTemplateRef>>() {
            });
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s) || "null".equalsIgnoreCase(s.trim())) {
                return new ArrayList<>();
            }
            return JSON.parseObject(s, new TypeReference<List<SopStepTemplateRef>>() {
            });
        }
        return JSON.parseObject(JSON.toJSONString(raw), new TypeReference<List<SopStepTemplateRef>>() {
        });
    }

    private Map<String, Object> parseParamMap(Object raw) {
        if (raw == null) {
            return new LinkedHashMap<>();
        }
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> out = new LinkedHashMap<>();
            for (Map.Entry<?, ?> e : map.entrySet()) {
                if (e.getKey() != null) {
                    out.put(String.valueOf(e.getKey()), e.getValue());
                }
            }
            return out;
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s) || "null".equalsIgnoreCase(s.trim())) {
                return new LinkedHashMap<>();
            }
            Map<String, Object> parsed = JSON.parseObject(s, new TypeReference<Map<String, Object>>() {
            });
            return parsed != null ? parsed : new LinkedHashMap<>();
        }
        Map<String, Object> parsed = JSON.parseObject(JSON.toJSONString(raw), new TypeReference<Map<String, Object>>() {
        });
        return parsed != null ? parsed : new LinkedHashMap<>();
    }

    private Long readLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number n) {
            return n.longValue();
        }
        if (value instanceof Map<?, ?> map) {
            Object id = map.get("id");
            if (id instanceof Number n) {
                return n.longValue();
            }
            if (id != null) {
                try {
                    return Long.parseLong(String.valueOf(id));
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        try {
            return Long.parseLong(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private Map<String, Object> emptyIfNull(Map<String, Object> map) {
        return map != null ? map : Map.of();
    }
}
