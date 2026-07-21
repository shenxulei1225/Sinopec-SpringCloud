package cn.cheers.x.inspection.inspection_content.controller.admin.profile;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.profile.ObjectProfileUpsertReqVO;
import cn.cheers.x.inspection.inspection_content.service.profile.ObjectProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 对象巡检类型台账 Controller。
 */
@Tag(name = "管理后台 - 对象巡检类型台账")
@RestController
@RequestMapping("/inspection/object-profiles")
@Validated
public class ObjectProfileController {

    @Resource
    private ObjectProfileService objectProfileService;

    @PutMapping("/upsert")
    @Operation(summary = "创建或更新对象巡检类型")
    public CommonResult<Boolean> upsert(@Valid @RequestBody ObjectProfileUpsertReqVO reqVO) {
        objectProfileService.upsert(
                reqVO.getFacilityId(),
                reqVO.getObjectId(),
                reqVO.getInspectionType(),
                reqVO.getDefaultWorkMinutes());
        return success(true);
    }
}
