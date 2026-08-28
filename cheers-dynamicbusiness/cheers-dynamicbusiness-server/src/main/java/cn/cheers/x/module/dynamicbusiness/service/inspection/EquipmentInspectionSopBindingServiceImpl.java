package cn.cheers.x.module.dynamicbusiness.service.inspection;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopBindingRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopCreateInstanceReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.EquipmentInspectionSopUpsertReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.inspection.EquipmentInspectionSopBindingDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.inspection.EquipmentInspectionSopBindingMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.sop.SopMergeService;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopMergeResult;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopStepOverride;
import cn.cheers.x.module.dynamicbusiness.service.sop.dto.SopTemplateSnapshot;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备检查绑定实现。
 *
 * <p><b>权威</b>：V75 绑定表 + SOP 实例行；一键（设备×检查项×手段）一条实例。</p>
 * <p><b>禁止</b>：多设备共用同一实例；createInstance 复用已有实例行；读路径补绑定。</p>
 */
@Service
public class EquipmentInspectionSopBindingServiceImpl implements EquipmentInspectionSopBindingService {

    @Resource
    private EquipmentInspectionSopBindingMapper bindingMapper;

    @Resource
    private EntityService entityService;

    @Resource
    private SopMergeService sopMergeService;

    @Override
    public EquipmentInspectionSopBindingRespVO getBinding(
            long equipmentId, long inspectionItemId, String executionMeans) {
        String means = InspectionItemSopMethodServiceImpl.normalizeMeans(executionMeans);
        EquipmentInspectionSopBindingDO row =
                bindingMapper.selectByIdentity(equipmentId, inspectionItemId, means);
        return row == null ? null : toResp(row);
    }

    @Override
    public List<EquipmentInspectionSopBindingRespVO> listByEquipmentAndItem(
            long equipmentId, long inspectionItemId) {
        List<EquipmentInspectionSopBindingDO> rows =
                bindingMapper.selectByEquipmentAndItem(equipmentId, inspectionItemId);
        List<EquipmentInspectionSopBindingRespVO> out = new ArrayList<>(rows.size());
        for (EquipmentInspectionSopBindingDO row : rows) {
            out.add(toResp(row));
        }
        return out;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertBinding(EquipmentInspectionSopUpsertReqVO req) {
        String means = InspectionItemSopMethodServiceImpl.normalizeMeans(req.getExecutionMeans());
        requireSopInstance(req.getSopInstanceId());
        assertInstanceNotBoundToOtherEquipment(req.getSopInstanceId(), req.getEquipmentId());

        EquipmentInspectionSopBindingDO existing = bindingMapper.selectByIdentity(
                req.getEquipmentId(), req.getInspectionItemId(), means);
        try {
            if (existing != null) {
                existing.setSopInstanceId(req.getSopInstanceId());
                bindingMapper.updateById(existing);
                return;
            }
            EquipmentInspectionSopBindingDO created = EquipmentInspectionSopBindingDO.builder()
                    .equipmentId(req.getEquipmentId())
                    .inspectionItemId(req.getInspectionItemId())
                    .executionMeans(means)
                    .sopInstanceId(req.getSopInstanceId())
                    .build();
            bindingMapper.insert(created);
        } catch (DataIntegrityViolationException ex) {
            throw new ServiceException(400, "设备检查绑定唯一约束冲突（同设备同检查项同手段已存在）");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public long createInstanceFromTemplate(EquipmentInspectionSopCreateInstanceReqVO req) {
        String means = InspectionItemSopMethodServiceImpl.normalizeMeans(req.getExecutionMeans());
        EntityRespVO template = requireSopTemplate(req.getSopTemplateId());
        Map<String, Object> templateBase = emptyIfNull(template.getBaseFields());

        SopStepOverride stepOverride = parseStepOverride(req.getStepOverride());
        Map<String, Object> paramOverride = parseParamMap(req.getParamOverride());
        SopTemplateSnapshot snapshot = toTemplateSnapshot(templateBase);
        SopMergeResult merged = sopMergeService.merge(snapshot, stepOverride, paramOverride);
        if (!merged.isOk()) {
            throw new ServiceException(400, "无法创建实例：merge 缺口 " + merged.getGapCodes());
        }

        String name = StringUtils.hasText(req.getName())
                ? req.getName().trim()
                : template.getName() + " · 设备" + req.getEquipmentId();

        EntityCreateReqVO createReq = new EntityCreateReqVO();
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", SopFieldCodes.ENTITY_TYPE_CODE);
        baseFields.put("modelId", template.getModelId());
        baseFields.put("name", name);
        baseFields.put("status", 1);
        baseFields.put(SopFieldCodes.IS_TEMPLATE, false);
        baseFields.put(SopFieldCodes.SOP_TEMPLATE_ID, req.getSopTemplateId());
        baseFields.put(SopFieldCodes.STEP_OVERRIDE_JSON,
                stepOverride != null ? JSON.toJSONString(stepOverride) : null);
        baseFields.put(SopFieldCodes.PARAM_OVERRIDE_JSON,
                paramOverride != null && !paramOverride.isEmpty()
                        ? JSON.toJSONString(paramOverride) : null);
        baseFields.put(SopFieldCodes.DEFAULT_STEPS_JSON, "[]");
        baseFields.put(SopFieldCodes.DEFAULT_PARAMS_JSON, "{}");
        baseFields.put(SopFieldCodes.EXECUTION_MEANS, means);
        Object kind = templateBase.get(SopFieldCodes.PROCEDURE_KIND);
        if (kind != null) {
            baseFields.put(SopFieldCodes.PROCEDURE_KIND, kind);
        }
        baseFields.put(SopFieldCodes.VERSION_NO, 1);
        baseFields.put(SopFieldCodes.PUBLISH_STATUS, "DRAFT");
        baseFields.put(SopFieldCodes.STEPS_JSON, "[]");
        createReq.setBaseFields(baseFields);

        Long instanceId = entityService.create(createReq);

        EquipmentInspectionSopUpsertReqVO upsert = new EquipmentInspectionSopUpsertReqVO();
        upsert.setEquipmentId(req.getEquipmentId());
        upsert.setInspectionItemId(req.getInspectionItemId());
        upsert.setExecutionMeans(means);
        upsert.setSopInstanceId(instanceId);
        upsertBinding(upsert);
        return instanceId;
    }

    private void assertInstanceNotBoundToOtherEquipment(Long sopInstanceId, Long equipmentId) {
        List<EquipmentInspectionSopBindingDO> rows = bindingMapper.selectBySopInstanceId(sopInstanceId);
        for (EquipmentInspectionSopBindingDO row : rows) {
            if (!equipmentId.equals(row.getEquipmentId())) {
                throw new ServiceException(400,
                        "SOP 实例已被其它设备占用，禁止多设备共用同一可写实例：" + sopInstanceId);
            }
        }
    }

    private void requireSopInstance(Long sopInstanceId) {
        EntityRespVO sop = entityService.get(sopInstanceId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (sop == null || sop.getId() == null) {
            throw new ServiceException(404, "SOP 实例不存在：" + sopInstanceId);
        }
        Map<String, Object> base = emptyIfNull(sop.getBaseFields());
        if (readBool(base.get(SopFieldCodes.IS_TEMPLATE))) {
            throw new ServiceException(400, "绑定必须指向 SOP 实例（is_template=false），不能绑模板");
        }
    }

    private EntityRespVO requireSopTemplate(Long sopTemplateId) {
        EntityRespVO sop = entityService.get(sopTemplateId, SopFieldCodes.ENTITY_TYPE_CODE);
        if (sop == null || sop.getId() == null) {
            throw new ServiceException(404, "SOP 模板不存在：" + sopTemplateId);
        }
        Map<String, Object> base = emptyIfNull(sop.getBaseFields());
        if (!readBool(base.get(SopFieldCodes.IS_TEMPLATE))) {
            throw new ServiceException(400, "sopTemplateId 必须指向 SOP 模板行");
        }
        return sop;
    }

    private EquipmentInspectionSopBindingRespVO toResp(EquipmentInspectionSopBindingDO row) {
        EquipmentInspectionSopBindingRespVO vo = new EquipmentInspectionSopBindingRespVO();
        vo.setId(row.getId());
        vo.setEquipmentId(row.getEquipmentId());
        vo.setInspectionItemId(row.getInspectionItemId());
        vo.setExecutionMeans(row.getExecutionMeans());
        vo.setSopInstanceId(row.getSopInstanceId());
        return vo;
    }

    private SopTemplateSnapshot toTemplateSnapshot(Map<String, Object> base) {
        SopTemplateSnapshot snapshot = new SopTemplateSnapshot();
        snapshot.setDefaultSteps(JSON.parseObject(
                JSON.toJSONString(parseJsonValue(base.get(SopFieldCodes.DEFAULT_STEPS_JSON), List.of())),
                new TypeReference<>() {
                }));
        snapshot.setDefaultParams(parseParamMap(base.get(SopFieldCodes.DEFAULT_PARAMS_JSON)));
        return snapshot;
    }

    private SopStepOverride parseStepOverride(Object raw) {
        if (raw == null) {
            return null;
        }
        if (raw instanceof SopStepOverride o) {
            return o;
        }
        if (raw instanceof String s && StringUtils.hasText(s)) {
            return JSON.parseObject(s, SopStepOverride.class);
        }
        return JSON.parseObject(JSON.toJSONString(raw), SopStepOverride.class);
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
            Map<String, Object> parsed = JSON.parseObject(s, new TypeReference<>() {
            });
            return parsed != null ? parsed : new LinkedHashMap<>();
        }
        Map<String, Object> parsed = JSON.parseObject(JSON.toJSONString(raw), new TypeReference<>() {
        });
        return parsed != null ? parsed : new LinkedHashMap<>();
    }

    private Object parseJsonValue(Object raw, Object fallback) {
        if (raw == null) {
            return fallback;
        }
        if (raw instanceof String s) {
            if (!StringUtils.hasText(s)) {
                return fallback;
            }
            return JSON.parse(s);
        }
        return raw;
    }

    private Map<String, Object> emptyIfNull(Map<String, Object> map) {
        return map != null ? map : Map.of();
    }

    private static boolean readBool(Object v) {
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
}
