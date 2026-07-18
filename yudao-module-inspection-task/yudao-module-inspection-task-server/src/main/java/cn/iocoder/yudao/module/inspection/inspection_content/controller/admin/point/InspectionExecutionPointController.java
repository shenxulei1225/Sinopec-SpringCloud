package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.point;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointPageReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointRespVO;
import cn.iocoder.yudao.module.inspection.inspection_content.convert.InspectionExecutionPointConvert;
import cn.iocoder.yudao.module.inspection.inspection_content.service.point.InspectionExecutionPointQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 执行点位 Controller。
 */
@Tag(name = "管理后台 - 执行点位")
@RestController
@RequestMapping("/inspection-model/point")
@Validated
public class InspectionExecutionPointController {

    @Resource
    private InspectionExecutionPointQueryService inspectionExecutionPointQueryService;

    @GetMapping("/{pointId}")
    @Operation(summary = "获取执行点位详情")
    @Parameter(name = "pointId", description = "执行点位 ID", required = true, example = "1")
    public CommonResult<InspectionExecutionPointRespVO> getPoint(@PathVariable("pointId") Long pointId) {
        return success(InspectionExecutionPointConvert.convertRespVO(inspectionExecutionPointQueryService.getPointView(pointId)));
    }

    @GetMapping("/search")
    @Operation(summary = "执行点位查询")
    public CommonResult<List<InspectionExecutionPointRespVO>> searchPoint(InspectionExecutionPointPageReqVO reqVO) {
        return success(inspectionExecutionPointQueryService.getPointViewList(reqVO).stream()
                .map(InspectionExecutionPointConvert::convertRespVO)
                .toList());
    }
}
