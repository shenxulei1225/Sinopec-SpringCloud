package cn.cheers.x.inspection.task.service.execution.live;

import cn.cheers.x.device.protocolgateway.api.DjiCloudAircraftStatusApi;
import cn.cheers.x.device.protocolgateway.api.dto.AircraftFlightStatusDTO;
import cn.cheers.x.device.protocolgateway.api.protocol.ProtocolCodes;
import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 实时画面上要看的飞机状态：用这次账记下的网关，向协议网关要那一架飞机的最新一帧。
 * <p>不负责发负载操作，不回读摄像机台账，不用设备编码冒充飞机序列号。
 * <p>禁止：网关下有多架时挑一架；没报到就填 0。
 */
@Service
public class PatrolLiveAircraftStatusService {

    static final String RECORD_TYPE = "task_record_patrol";

    private final EntityRpcApi entityRpcApi;
    private final DjiCloudAircraftStatusApi aircraftStatusApi;

    public PatrolLiveAircraftStatusService(EntityRpcApi entityRpcApi, DjiCloudAircraftStatusApi aircraftStatusApi) {
        this.entityRpcApi = entityRpcApi;
        this.aircraftStatusApi = aircraftStatusApi;
    }

    public AircraftFlightStatusDTO load(Long recordId) {
        if (recordId == null || recordId <= 0) {
            return AircraftFlightStatusDTO.gap("还没有正在执行的巡检，不显示飞机状态", null);
        }
        EntityRespDTO record = requireData(entityRpcApi.getEntity(recordId, RECORD_TYPE), "这次巡检账不存在");
        Map<String, Object> fields = record.getCustomFields() == null ? Map.of() : record.getCustomFields();
        Map<String, Object> snapshot = snapshotOf(fields.get("standard_snapshot"));
        String protocol = text(snapshot.get("protocolVersion"));
        String gatewaySn = text(snapshot.get("logicalDeviceId"));
        if (!ProtocolCodes.isDjiCloud(protocol)) {
            return AircraftFlightStatusDTO.gap("这次执行不是大疆机场或大疆 Pilot，没有这路飞机状态", null);
        }
        if (!StringUtils.hasText(gatewaySn)) {
            return AircraftFlightStatusDTO.gap("这次执行没有记下网关，不能看飞机状态", null);
        }
        CommonResult<AircraftFlightStatusDTO> rpc = aircraftStatusApi.status(gatewaySn);
        if (rpc == null || !rpc.isSuccess() || rpc.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(
                    rpc != null && StringUtils.hasText(rpc.getMsg()) ? rpc.getMsg() : "飞机状态没有读到");
        }
        return rpc.getData();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> snapshotOf(Object raw) {
        if (raw instanceof Map<?, ?> map) {
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> copy.put(String.valueOf(key), value));
            return copy;
        }
        return Map.of();
    }

    private static String text(Object raw) {
        return raw == null ? "" : String.valueOf(raw).trim();
    }

    private static EntityRespDTO requireData(CommonResult<EntityRespDTO> result, String missing) {
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw ServiceExceptionUtil.invalidParamException(missing);
        }
        return result.getData();
    }
}
