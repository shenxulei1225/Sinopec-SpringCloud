package cn.cheers.x.inspection.task.service.execution.impl;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.opcode.TransportOpcode;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityWriteReqDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备故障上报写成一条异常记录，并记到对上的那台设备的「异常记录」引用里。
 * <p>设备分类不是分类即实体，这条引用不会转成分类关系。
 * 点设备看异常，靠引用同步写出的设备与异常之间的实体关系。
 * <p>不负责：改任务执行账、拆报文、巡检检查项不合格的写入、把异常挂到设备分类。
 * <p>禁止：对不上设备还建一条没挂设备的异常；同一条报文再写一条；协议不合格也记；
 * 在异常行上再写一条指向设备的引用。
 */
@Service
@RequiredArgsConstructor
public class DeviceCollectionAnomalyRecorder {

    public static final String ANOMALY_TYPE = "anomaly";
    public static final String MODEL_CODE = "exec_device_anomaly";
    public static final String EQUIPMENT_TYPE = "equipment";
    /** 设备型号上的「异常记录」多选引用（字段库 6025，目标是异常记录）。 */
    public static final String ANOMALY_REF_FIELD = "F-1104009eed0b4b18b80eb561d29848cc";
    static final String DEVICE_CODE = "device_code";
    static final String CODE = "code";

    private final EntityRpcApi entityRpcApi;

    /**
     * 协议合格的设备故障上报才记。已经有同一条报文的异常则不再写。
     * 设备编号对不上台账，或对上多台，直接报错，不建空挂的异常。
     */
    public void record(CollectionSample sample) {
        if (sample == null || !sample.isProtocolQualified()) {
            return;
        }
        if (!isFaultReport(sample)) {
            return;
        }
        String anomalyCode = anomalyCode(sample);
        if (alreadyRecorded(anomalyCode)) {
            return;
        }
        long equipmentId = resolveEquipmentId(sample.deviceId());
        EntityWriteReqDTO write = new EntityWriteReqDTO();
        write.setEntityTypeCode(ANOMALY_TYPE);
        write.setModelCode(MODEL_CODE);
        write.setName("设备故障 " + sample.deviceId().trim());
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(CODE, anomalyCode);
        write.setFields(fields);
        CommonResult<Long> created = entityRpcApi.createEntity(write);
        if (created == null || !created.isSuccess() || created.getData() == null) {
            throw new ServiceException(
                    created != null ? created.getCode() : 500,
                    created != null && StringUtils.hasText(created.getMsg())
                            ? created.getMsg()
                            : "设备故障写入异常记录失败");
        }
        attachToEquipment(equipmentId, created.getData());
    }

    /**
     * 把刚写下的异常追加到这台设备的「异常记录」引用。
     * 设备保存时，引用同步维护设备—异常的实体关系；设备分类不是分类即实体，不会据此建分类关系。
     */
    private void attachToEquipment(long equipmentId, long anomalyId) {
        CommonResult<EntityRespDTO> loaded = entityRpcApi.getEntity(equipmentId, EQUIPMENT_TYPE);
        if (loaded == null || !loaded.isSuccess() || loaded.getData() == null) {
            throw new ServiceException(
                    loaded != null ? loaded.getCode() : 500,
                    loaded != null && StringUtils.hasText(loaded.getMsg())
                            ? loaded.getMsg()
                            : "读设备台账失败");
        }
        List<Map<String, Object>> refs = currentAnomalyRefs(loaded.getData());
        Map<String, Object> added = anomalyRef(anomalyId);
        if (!containsRef(refs, anomalyId)) {
            refs.add(added);
        }
        EntityWriteReqDTO update = new EntityWriteReqDTO();
        update.setId(equipmentId);
        update.setEntityTypeCode(EQUIPMENT_TYPE);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(ANOMALY_REF_FIELD, refs);
        update.setFields(fields);
        CommonResult<Boolean> saved = entityRpcApi.updateEntityFields(update);
        if (saved == null || !saved.isSuccess()) {
            throw new ServiceException(
                    saved != null ? saved.getCode() : 500,
                    saved != null && StringUtils.hasText(saved.getMsg())
                            ? saved.getMsg()
                            : "把异常记到设备上失败");
        }
    }

    static boolean isFaultReport(CollectionSample sample) {
        try {
            return InspectionDeviceUplinkServiceImpl.readOpcode(sample) == TransportOpcode.FAULT_REPORT.code();
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * 同一条上报只记一次。有消息编号用消息编号，没有则用设备编号加收到时间。
     */
    static String anomalyCode(CollectionSample sample) {
        if (StringUtils.hasText(sample.msgId())) {
            return "device-fault-" + sample.msgId().trim();
        }
        return "device-fault-" + sample.deviceId().trim() + "-" + sample.receivedAtEpochMs();
    }

    static long singleEquipmentId(String deviceId, List<EntityRespDTO> rows) {
        if (!StringUtils.hasText(deviceId)) {
            throw new ServiceException(400, "采集样本缺少设备编号");
        }
        String wanted = deviceId.trim();
        Long matched = null;
        if (rows != null) {
            for (EntityRespDTO row : rows) {
                if (row == null || row.getId() == null || !matchesDevice(row, wanted)) {
                    continue;
                }
                if (matched != null) {
                    throw new ServiceException(400, "多台设备对上同一编号：" + wanted);
                }
                matched = row.getId();
            }
        }
        if (matched == null) {
            throw new ServiceException(400, "采集设备对不上设备台账：" + wanted);
        }
        return matched;
    }

    private boolean alreadyRecorded(String anomalyCode) {
        CommonResult<EntityRespDTO> existing = entityRpcApi.getEntityByCode(anomalyCode, ANOMALY_TYPE);
        if (existing == null || !existing.isSuccess()) {
            throw new ServiceException(
                    existing != null ? existing.getCode() : 500,
                    existing != null && StringUtils.hasText(existing.getMsg())
                            ? existing.getMsg()
                            : "查询已有异常记录失败");
        }
        return existing.getData() != null && existing.getData().getId() != null;
    }

    private long resolveEquipmentId(String deviceId) {
        CommonResult<List<EntityRespDTO>> listed = entityRpcApi.listEntities(EQUIPMENT_TYPE, null);
        if (listed == null || !listed.isSuccess()) {
            throw new ServiceException(
                    listed != null ? listed.getCode() : 500,
                    listed != null && StringUtils.hasText(listed.getMsg())
                            ? listed.getMsg()
                            : "读设备台账失败");
        }
        return singleEquipmentId(deviceId, listed.getData());
    }

    private static boolean matchesDevice(EntityRespDTO row, String deviceId) {
        String deviceCode = text(bag(row).get(DEVICE_CODE));
        String code = text(bag(row).get(CODE));
        return deviceId.equals(deviceCode) || deviceId.equals(code);
    }

    private static Map<String, Object> bag(EntityRespDTO row) {
        Map<String, Object> bag = new LinkedHashMap<>();
        if (row.getBaseFields() != null) {
            bag.putAll(row.getBaseFields());
        }
        if (row.getCustomFields() != null) {
            bag.putAll(row.getCustomFields());
        }
        return bag;
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static Map<String, Object> anomalyRef(long anomalyId) {
        Map<String, Object> ref = new LinkedHashMap<>(2);
        ref.put("entityTypeCode", ANOMALY_TYPE);
        ref.put("id", anomalyId);
        return ref;
    }

    private static List<Map<String, Object>> currentAnomalyRefs(EntityRespDTO equipment) {
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
}
