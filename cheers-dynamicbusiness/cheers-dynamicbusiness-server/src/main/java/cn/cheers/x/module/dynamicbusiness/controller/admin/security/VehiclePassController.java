package cn.cheers.x.module.dynamicbusiness.controller.admin.security;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.security.VehicleRecognitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 车辆管理的识别写入。
 *
 * 管什么：一次识别、改正车牌、确认放行、无牌放行。
 * 不管什么：名单和车道的表单。那些由车辆管理页直接写入对应的账。
 * 禁止：没登录就写过车。
 */
@Tag(name = "管理后台 - 车辆管理")
@RestController
@RequestMapping("/dynamicbusiness/security/vehicle-pass")
@Validated
public class VehiclePassController {

    private final VehicleRecognitionService recognitionService;

    public VehiclePassController(VehicleRecognitionService recognitionService) {
        this.recognitionService = recognitionService;
    }

    @PostMapping("/recognition")
    @Operation(summary = "录入一次车牌识别")
    @PreAuthorize("@catalogAccess.allow('vehicle_pass', 'update')")
    public CommonResult<VehicleRecognitionService.Outcome> recognition(@Valid @RequestBody RecognitionRequest request) {
        return success(recognitionService.recognize(
                request.facilityId(), request.laneId(), request.plate(), request.plateColor(),
                request.scenePhoto(), request.platePhoto(), request.uncertain()));
    }

    @PostMapping("/confirm")
    @Operation(summary = "门卫确认放行")
    @PreAuthorize("@catalogAccess.allow('vehicle_pass', 'update')")
    public CommonResult<VehicleRecognitionService.Outcome> confirm(@Valid @RequestBody ConfirmRequest request) {
        return success(recognitionService.confirm(request.passId(), request.reason()));
    }

    @PostMapping("/correct-plate")
    @Operation(summary = "改正这一条的车牌并重新比对")
    @PreAuthorize("@catalogAccess.allow('vehicle_pass', 'update')")
    public CommonResult<VehicleRecognitionService.Outcome> correctPlate(@Valid @RequestBody CorrectRequest request) {
        return success(recognitionService.correctPlate(request.passId(), request.plate()));
    }

    @PostMapping("/unplated")
    @Operation(summary = "无牌车辆放行")
    @PreAuthorize("@catalogAccess.allow('vehicle_pass', 'update')")
    public CommonResult<VehicleRecognitionService.Outcome> unplated(@Valid @RequestBody UnplatedRequest request) {
        return success(recognitionService.unplated(request.facilityId(), request.laneId()));
    }

    public record RecognitionRequest(
            @NotNull Long facilityId,
            @NotNull Long laneId,
            String plate,
            String plateColor,
            String scenePhoto,
            String platePhoto,
            boolean uncertain) {
    }

    public record ConfirmRequest(@NotNull Long passId, String reason) {
    }

    public record CorrectRequest(@NotNull Long passId, String plate) {
    }

    public record UnplatedRequest(@NotNull Long facilityId, @NotNull Long laneId) {
    }
}
