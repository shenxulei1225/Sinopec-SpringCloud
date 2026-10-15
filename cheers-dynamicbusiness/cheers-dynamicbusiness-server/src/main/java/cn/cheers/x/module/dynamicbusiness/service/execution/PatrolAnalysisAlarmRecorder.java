package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 智能分析步骤写成告警时，另记一条巡检异常记录，并挂到这次任务的执行设备上。
 * <p>点设备才能在异常记录里看到它。同一条分析任务只记一次。
 * <p>不负责：设备故障上报、改步骤上的分析结果、在路线回放上画告警。
 * <p>禁止：对不上唯一执行设备还建一条没挂设备的异常；没有分析任务编号就编一个编号。
 */
@Service
public class PatrolAnalysisAlarmRecorder {

    static final String ANOMALY_TYPE = "anomaly";
    static final String MODEL_CODE = "exec_patrol_issue";
    static final String EQUIPMENT_TYPE = "equipment";
    static final String TASK_TYPE = "task";
    /** 任务草稿。executionDeviceBinding 记下这次执行设备。 */
    static final String TASK_DRAFT_FIELD = "FLD-TSK-033";
    /** 设备上的「异常记录」多选引用。 */
    static final String ANOMALY_REF_FIELD = "F-1104009eed0b4b18b80eb561d29848cc";

    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;

    static boolean isAlarm(Map<String, Object> payload) {
        return payload != null && "告警".equals(text(payload.get("analysisResult")));
    }

    /**
     * 从任务草稿的执行设备绑定里取出唯一一台执行设备。
     * 没有，或多于一台，直接报错。
     */
    static long requireSingleExecutionEquipmentId(Object binding) {
        List<Long> ids = new ArrayList<>();
        collectEquipmentIds(binding, ids);
        List<Long> unique = ids.stream().distinct().toList();
        if (unique.size() != 1) {
            throw new ServiceException(400, unique.isEmpty()
                    ? "这次任务没有写下执行设备，分析告警不能记入异常记录"
                    : "这次任务有多台执行设备，分析告警不知道记到哪一台");
        }
        return unique.get(0);
    }

    /**
     * 步骤结果已经写成告警之后调用。缺分析任务编号或执行设备则报错，不建空挂异常。
     */
    public void record(EntityRespVO executionRecord, String inspectionItemTitle, Map<String, Object> payload) {
        String jobNo = text(payload.get("analysisJobNo"));
        if (!StringUtils.hasText(jobNo)) {
            throw new ServiceException(400, "分析告警没有分析任务编号，不能记入异常记录");
        }
        String anomalyCode = "analysis-alarm-" + jobNo;
        if (alreadyRecorded(anomalyCode)) {
            return;
        }
        long equipmentId = requireSingleExecutionEquipmentId(executionDeviceBinding(executionRecord));
        EntityCreateReqVO create = new EntityCreateReqVO();
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", ANOMALY_TYPE);
        base.put("modelId", entityRpcFacadeService.getModelIdByCode(MODEL_CODE));
        base.put("name", anomalyName(inspectionItemTitle, payload));
        base.put("code", anomalyCode);
        base.put("status", 1);
        Long facilityId = facilityOf(executionRecord);
        if (facilityId != null) {
            base.put(FacilityOwningFieldCodes.FIELD_CODE, FacilityOwningFieldCodes.toApiRef(facilityId));
        }
        create.setBaseFields(base);
        Long anomalyId = entityService.create(create);
        if (anomalyId == null || anomalyId <= 0) {
            throw new ServiceException(500, "分析告警写入异常记录失败");
        }
        attachToEquipment(equipmentId, anomalyId);
    }

    private Object executionDeviceBinding(EntityRespVO executionRecord) {
        Long taskId = longOf(bag(executionRecord).get("task_id"));
        if (taskId == null) {
            throw new ServiceException(400, "这次执行记录没有任务，分析告警不能记入异常记录");
        }
        EntityRespVO task = entityService.get(taskId, TASK_TYPE);
        if (task == null) {
            throw new ServiceException(404, "任务不存在：" + taskId);
        }
        Object draft = bag(task).get(TASK_DRAFT_FIELD);
        if (draft instanceof String text && StringUtils.hasText(text)) {
            draft = JSON.parse(text);
        }
        if (!(draft instanceof Map<?, ?> map)) {
            return null;
        }
        return map.get("executionDeviceBinding");
    }

    private String anomalyName(String inspectionItemTitle, Map<String, Object> payload) {
        String display = text(payload.get("display"));
        if (!StringUtils.hasText(display)) {
            display = text(payload.get("message"));
        }
        if (!StringUtils.hasText(display)) {
            display = "告警";
        }
        return StringUtils.hasText(inspectionItemTitle) ? inspectionItemTitle + "：" + display : display;
    }

    private boolean alreadyRecorded(String anomalyCode) {
        EntityRespDTO existing = entityRpcFacadeService.getByCode(anomalyCode, ANOMALY_TYPE);
        return existing != null && existing.getId() != null;
    }

    private void attachToEquipment(long equipmentId, long anomalyId) {
        EntityRespVO equipment = entityService.get(equipmentId, EQUIPMENT_TYPE);
        if (equipment == null || equipment.getId() == null) {
            throw new ServiceException(404, "执行设备不存在：" + equipmentId);
        }
        List<Map<String, Object>> refs = currentAnomalyRefs(equipment);
        if (!containsRef(refs, anomalyId)) {
            Map<String, Object> added = new LinkedHashMap<>(2);
            added.put("entityTypeCode", ANOMALY_TYPE);
            added.put("id", anomalyId);
            refs.add(added);
        }
        EntityWriteReqDTO update = new EntityWriteReqDTO();
        update.setId(equipmentId);
        update.setEntityTypeCode(EQUIPMENT_TYPE);
        update.setFields(Map.of(ANOMALY_REF_FIELD, refs));
        entityRpcFacadeService.updateFields(update);
    }

    private static void collectEquipmentIds(Object raw, List<Long> ids) {
        if (!(raw instanceof Map<?, ?> map)) {
            return;
        }
        Long direct = longOf(map.get("equipmentId"));
        if (direct != null && direct > 0) {
            ids.add(direct);
            return;
        }
        for (Object value : map.values()) {
            collectEquipmentIds(value, ids);
        }
    }

    private static List<Map<String, Object>> currentAnomalyRefs(EntityRespVO equipment) {
        Object raw = bag(equipment).get(ANOMALY_REF_FIELD);
        List<Map<String, Object>> refs = new ArrayList<>();
        if (raw instanceof List<?> list) {
            for (Object item : list) {
                if (item instanceof Map<?, ?> map) {
                    refs.add(copyRef(map));
                }
            }
        } else if (raw instanceof Map<?, ?> map) {
            refs.add(copyRef(map));
        }
        return refs;
    }

    private static Map<String, Object> copyRef(Map<?, ?> map) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", map.get("entityTypeCode"));
        ref.put("id", map.get("id"));
        return ref;
    }

    private static boolean containsRef(List<Map<String, Object>> refs, long anomalyId) {
        for (Map<String, Object> ref : refs) {
            if (ref.get("id") instanceof Number number && number.longValue() == anomalyId) {
                return true;
            }
        }
        return false;
    }

    private static Long facilityOf(EntityRespVO record) {
        return FacilityOwningFieldCodes.extractId(bag(record).get(FacilityOwningFieldCodes.FIELD_CODE));
    }

    private static Map<String, Object> bag(EntityRespVO row) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (row.getBaseFields() != null) {
            bag.putAll(row.getBaseFields());
        }
        if (row.getCustomFields() != null) {
            bag.putAll(row.getCustomFields());
        }
        return bag;
    }

    private static Long longOf(Object raw) {
        if (raw instanceof Number number) {
            long value = number.longValue();
            return value > 0 ? value : null;
        }
        if (raw instanceof Map<?, ?> map) {
            return longOf(map.get("id"));
        }
        if (raw instanceof String text && StringUtils.hasText(text)) {
            try {
                long value = Long.parseLong(text.trim());
                return value > 0 ? value : null;
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }
}
