package cn.cheers.x.device.protocolgateway.instructiondispatch;



import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;

import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;

import cn.cheers.x.framework.common.pojo.CommonResult;

import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;



import java.util.LinkedHashMap;

import java.util.List;

import java.util.Map;



import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;



class DispatchParamResolverTest {



    private static final ObjectMapper MAPPER = new ObjectMapper();



    @Test

    void resolve_keepsStepParamsAndFillsStreamUrl() {

        DispatchParamResolver resolver = DispatchParamResolver.noop();

        DeviceMissionPlan plan = new DeviceMissionPlan(

                "dji-dock",

                "dock-1",

                "task-1",

                "task-1",

                List.of(new DispatchAction(1L, Map.of("gimbalPitchRotateAngle", -45), "orientedShoot")),

                8001L,

                null,

                "rtsp://ingest/example");

        Map<String, Object> bag = resolver.resolve(plan, plan.actions().get(0), List.of());



        assertEquals(-45, bag.get("gimbalPitchRotateAngle"));

        assertEquals("rtsp://ingest/example", bag.get("stream_url"));

    }



    @Test

    void resolve_fillsExecutorMountSlotFromHost() {

        EntityRpcApi rpc = mock(EntityRpcApi.class);

        long executorId = 8001L;

        long cameraId = 9002L;



        when(rpc.getEntity(eq(executorId), eq("equipment"))).thenReturn(CommonResult.success(hostEntity(executorId, cameraId)));

        when(rpc.getEntity(eq(cameraId), eq("equipment"))).thenReturn(CommonResult.success(gimbalCameraEntity(cameraId)));



        DispatchParamResolver resolver = new DispatchParamResolver(rpc, MAPPER, new ExecutorMountLoader(rpc, MAPPER));

        DeviceMissionPlan plan = new DeviceMissionPlan(

                "dji-dock",

                "dock-1",

                "task-1",

                "task-1",

                List.of(new DispatchAction(1L, Map.of(), "orientedShoot")),

                executorId,

                null,

                null);

        List<SlotMapping> slots = List.of(

                new SlotMapping("payloadPositionIndex", "", "request.index", "EXECUTOR_MOUNT"));

        Map<String, Object> bag = resolver.resolve(plan, plan.actions().get(0), slots);



        assertEquals(2, bag.get("payloadPositionIndex"));

    }



    @Test

    void resolve_fillsSystemPayloadIndexFromStringMountAndCameraName() {

        EntityRpcApi rpc = mock(EntityRpcApi.class);

        long executorId = 900158L;

        long gimbalId = 900294L;

        long bodyId = 900293L;

        EntityRespDTO host = new EntityRespDTO();

        host.setId(executorId);

        host.setCustomFields(Map.of(

                "modelId", 12L,

                "payload_mounts", List.of(

                        Map.of("mountPoint", "0", "equipmentId", gimbalId),

                        Map.of("mountPoint", "7", "equipmentId", bodyId))));

        when(rpc.getEntity(eq(executorId), eq("equipment"))).thenReturn(CommonResult.success(host));

        when(rpc.getEntity(eq(gimbalId), eq("equipment")))

                .thenReturn(CommonResult.success(namedCamera(gimbalId, "相机 · 云台相机 M30 双光相机", 1895L)));

        when(rpc.getEntity(eq(bodyId), eq("equipment")))

                .thenReturn(CommonResult.success(namedCamera(bodyId, "相机 · Matrice 30 / 30T 机身相机", 1917L)));



        DispatchParamResolver resolver = new DispatchParamResolver(rpc, MAPPER, new ExecutorMountLoader(rpc, MAPPER));

        DeviceMissionPlan plan = new DeviceMissionPlan(

                "dji-pilot",

                "pilot-1",

                "task-1",

                "task-1",

                List.of(new DispatchAction(1L, Map.of(), "orientedShoot")),

                executorId,

                null,

                null);

        Map<String, Object> bag = resolver.resolve(

                plan,

                plan.actions().get(0),

                List.of(new SlotMapping("payloadPositionIndex", "", "params.payloadPositionIndex", "SYSTEM")));



        assertEquals(0, bag.get("payloadPositionIndex"));

    }



    private static EntityRespDTO namedCamera(long cameraId, String name, long modelId) {

        EntityRespDTO dto = new EntityRespDTO();

        dto.setId(cameraId);

        Map<String, Object> base = new LinkedHashMap<>();

        base.put("name", name);

        base.put("modelId", modelId);

        dto.setBaseFields(base);

        return dto;

    }



    private static EntityRespDTO hostEntity(long hostId, long cameraId) {

        EntityRespDTO dto = new EntityRespDTO();

        dto.setId(hostId);

        Map<String, Object> custom = new LinkedHashMap<>();

        custom.put("modelId", 12L);

        custom.put("payload_mounts", List.of(Map.of("mountPoint", 2, "equipmentId", cameraId)));

        dto.setCustomFields(custom);

        return dto;

    }



    private static EntityRespDTO gimbalCameraEntity(long cameraId) {

        EntityRespDTO dto = new EntityRespDTO();

        dto.setId(cameraId);

        dto.setModelName("云台相机M30");

        Map<String, Object> custom = new LinkedHashMap<>();

        custom.put("modelId", 99L);

        custom.put("dji_main_type", "1");

        custom.put("dji_sub_type", "0");

        dto.setCustomFields(custom);

        return dto;

    }

}

