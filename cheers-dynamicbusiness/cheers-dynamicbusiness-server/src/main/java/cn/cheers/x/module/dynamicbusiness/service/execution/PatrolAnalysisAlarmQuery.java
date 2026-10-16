package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntitySceneQueryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.AnalysisAlarmItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.AnalysisAlarmListRespVO;
import cn.cheers.x.module.dynamicbusiness.enums.entity.EntityQueryScene;
import cn.cheers.x.module.dynamicbusiness.framework.facility.FacilityOwningFieldCodes;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 跨任务列出已经记入异常记录的分析告警。
 * <p>只列当前这一座场站。读的是异常记录。正常结果不在这里。不另建告警表，不读取照片文件。
 * <p>不负责：调用分析算法、改异常记录、把几座场站混进同一份列表。
 */
@Service
public class PatrolAnalysisAlarmQuery {

    private static final int PAGE_SIZE = 200;
    private static final int MAX_PAGES = 10;

    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;

    /**
     * 按记入时间从新到旧。必须指定场站。超过 {@link #MAX_PAGES} 页时 truncated 为 true。
     */
    public AnalysisAlarmListRespVO list(Long facilityId) {
        if (facilityId == null || facilityId <= 0) {
            throw new ServiceException(400, "站场级列表须指定所属场站");
        }
        Long modelId = entityRpcFacadeService.getModelIdByCode(PatrolAnalysisAlarmRecorder.MODEL_CODE);
        List<EntityRespVO> rows = new ArrayList<>();
        boolean truncated = false;
        for (int pageNo = 1; pageNo <= MAX_PAGES; pageNo++) {
            List<EntityRespVO> page = loadPage(modelId, facilityId, pageNo);
            rows.addAll(page);
            if (page.size() < PAGE_SIZE) {
                break;
            }
            if (pageNo == MAX_PAGES) {
                truncated = true;
            }
        }
        Map<Long, String> facilityNames = new HashMap<>();
        List<AnalysisAlarmItemRespVO> items = new ArrayList<>();
        for (EntityRespVO row : rows) {
            if (!codeOf(row).startsWith(PatrolAnalysisAlarmRecorder.ALARM_CODE_PREFIX)) {
                continue;
            }
            items.add(toItem(row, facilityNames));
        }
        items.sort(Comparator.comparing(AnalysisAlarmItemRespVO::getOccurredAt,
                Comparator.nullsLast(Comparator.reverseOrder())));
        AnalysisAlarmListRespVO result = new AnalysisAlarmListRespVO();
        result.setList(items);
        result.setTruncated(truncated);
        return result;
    }

    private List<EntityRespVO> loadPage(Long modelId, Long facilityId, int pageNo) {
        FieldFilterReqVO facility = new FieldFilterReqVO();
        facility.setFieldCode(FacilityOwningFieldCodes.FIELD_CODE);
        facility.setOp("EQ");
        facility.setValue(facilityId);
        EntitySceneQueryRespVO queried = entityService.queryEntities(
                EntityQueryScene.ENTITIES_BY_MODEL,
                "PAGE",
                "FULL",
                null,
                PatrolAnalysisAlarmRecorder.ANOMALY_TYPE,
                List.of(modelId),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                pageNo,
                PAGE_SIZE,
                null,
                null,
                List.of(facility),
                null,
                null,
                null,
                null,
                null);
        PageResult<EntityRespVO> page = queried == null ? null : queried.getPage();
        if (page == null || page.getList() == null) {
            return List.of();
        }
        return page.getList();
    }

    private AnalysisAlarmItemRespVO toItem(EntityRespVO row, Map<Long, String> facilityNames) {
        Map<String, Object> facts = row.getCustomFields() == null ? Map.of() : row.getCustomFields();
        AnalysisAlarmItemRespVO item = new AnalysisAlarmItemRespVO();
        item.setId(row.getId());
        item.setCapabilityName(text(facts.get(PatrolAnalysisAlarmRecorder.FACT_CAPABILITY_NAME)));
        String note = text(facts.get(PatrolAnalysisAlarmRecorder.FACT_NOTE));
        item.setNote(StringUtils.hasText(note) ? note : text(row.getName()));
        item.setPhotoUrl(text(facts.get(PatrolAnalysisAlarmRecorder.FACT_PHOTO_URL)));
        item.setDetections(facts.get(PatrolAnalysisAlarmRecorder.FACT_DETECTIONS));
        Long facilityId = FacilityOwningFieldCodes.extractId(
                row.getBaseFields() == null ? null : row.getBaseFields().get(FacilityOwningFieldCodes.FIELD_CODE));
        item.setFacilityId(facilityId);
        item.setFacilityName(facilityName(facilityId, facilityNames));
        item.setTaskId(longOrNull(facts.get(PatrolAnalysisAlarmRecorder.FACT_TASK_ID)));
        item.setTaskName(text(facts.get(PatrolAnalysisAlarmRecorder.FACT_TASK_NAME)));
        item.setExecutionRecordId(longOrNull(facts.get(PatrolAnalysisAlarmRecorder.FACT_EXECUTION_RECORD_ID)));
        item.setStepCode(text(facts.get(PatrolAnalysisAlarmRecorder.FACT_STEP_CODE)));
        item.setStepTitle(text(facts.get(PatrolAnalysisAlarmRecorder.FACT_STEP_TITLE)));
        item.setOccurredAt(formatTime(row.getCreateTime()));
        return item;
    }

    private String facilityName(Long facilityId, Map<Long, String> cache) {
        if (facilityId == null) {
            return "";
        }
        if (cache.containsKey(facilityId)) {
            return cache.get(facilityId);
        }
        EntityRespVO facility = entityService.get(facilityId, FacilityOwningFieldCodes.TARGET_ENTITY_TYPE);
        String name = facility == null ? "" : text(facility.getName());
        cache.put(facilityId, name);
        return name;
    }

    private static String codeOf(EntityRespVO row) {
        if (row.getBaseFields() == null) {
            return "";
        }
        return text(row.getBaseFields().get("code"));
    }

    private static String formatTime(LocalDateTime time) {
        return time == null ? "" : time.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private static Long longOrNull(Object raw) {
        if (raw instanceof Number number) {
            long value = number.longValue();
            return value > 0 ? value : null;
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
