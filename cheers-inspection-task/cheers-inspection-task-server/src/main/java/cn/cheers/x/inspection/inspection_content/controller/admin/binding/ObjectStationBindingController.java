package cn.cheers.x.inspection.inspection_content.controller.admin.binding;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.binding.ObjectStationBindingReplaceReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.binding.ObjectStationBindingRespVO;
import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingQueryService;
import cn.cheers.x.inspection.inspection_content.service.binding.ObjectStationBindingService;
import cn.cheers.x.inspection.inspection_content.service.binding.model.ObjectStationBindingView;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 对象↔停靠点绑定台账 Controller。
 */
@Tag(name = "管理后台 - 对象停靠点绑定")
@RestController
@RequestMapping("/inspection/object-station-bindings")
@Validated
public class ObjectStationBindingController {

    @Resource
    private ObjectStationBindingService objectStationBindingService;

    @Resource
    private ObjectStationBindingQueryService objectStationBindingQueryService;

    @PutMapping("/replace")
    @Operation(summary = "全量替换某对象的停靠点绑定")
    public CommonResult<Boolean> replaceBindings(@Valid @RequestBody ObjectStationBindingReplaceReqVO reqVO) {
        objectStationBindingService.replaceBindings(
                reqVO.getFacilityId(),
                reqVO.getObjectId(),
                reqVO.getStationNodeIds(),
                reqVO.getWorkMinutesPerStop());
        return success(true);
    }

    @GetMapping("/list-by-object")
    @Operation(summary = "查询某对象的停靠点绑定")
    public CommonResult<List<ObjectStationBindingRespVO>> listByObject(
            @RequestParam("facilityId") @Parameter(description = "设施 ID", required = true) Long facilityId,
            @RequestParam("objectId") @Parameter(description = "对象 ID", required = true) Long objectId) {
        List<ObjectStationBindingView> views = objectStationBindingQueryService.listByObjectId(facilityId, objectId);
        return success(BeanUtils.toBean(views, ObjectStationBindingRespVO.class));
    }
}
