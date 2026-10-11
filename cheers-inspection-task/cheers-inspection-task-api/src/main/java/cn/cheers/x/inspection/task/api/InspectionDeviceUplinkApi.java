package cn.cheers.x.inspection.task.api;

import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.enums.ApiConstants;
import cn.cheers.x.inspection.task.api.dto.PatrolGeneratedTaskDownlinkDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 巡检接收网关已经核对过的上报样本。
 * <p>负责：把样本交给巡检，由策略写入这次任务执行记录。协议监控下发前，读出这台设备已生成任务的编号和动作清单。
 * <p>不负责：拆原始报文、判协议合格、翻译指令包。
 * <p>禁止：把收到上报写成检查项完成；没有到点的待执行时编一个任务编号。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 巡检上报写入执行记录")
public interface InspectionDeviceUplinkApi {

    String PREFIX = ApiConstants.PREFIX + "/device-uplink";

    @PostMapping(PREFIX + "/notify")
    @Operation(summary = "接收采集样本，按策略写入这次任务执行记录")
    CommonResult<Boolean> notify(@RequestBody CollectionSample sample);

    @GetMapping(PREFIX + "/generated-task")
    @Operation(summary = "这台设备当前要下发的已生成任务")
    CommonResult<PatrolGeneratedTaskDownlinkDTO> generatedTask(@RequestParam("deviceId") String deviceId);
}
