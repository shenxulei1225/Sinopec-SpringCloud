package cn.cheers.x.inspection.inspection_content.controller.admin.point;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointItemPageReqVO;
import cn.cheers.x.inspection.inspection_content.controller.admin.vo.point.InspectionExecutionPointItemRespVO;
import cn.cheers.x.inspection.inspection_content.convert.InspectionExecutionPointItemConvert;
import cn.cheers.x.inspection.inspection_content.service.point.InspectionExecutionPointItemQueryService;
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
 * 点位检查项 Controller。
 */
@Tag(name = "管理后台 - 点位检查项")
@RestController
@RequestMapping("/inspection-model/point-item")
@Validated
public class InspectionExecutionPointItemController {

    @Resource
    private InspectionExecutionPointItemQueryService inspectionExecutionPointItemQueryService;

    @GetMapping("/{id}")
    @Operation(summary = "获取点位检查项详情")
    @Parameter(name = "id", description = "点位检查项关联 ID", required = true, example = "1")
    public CommonResult<InspectionExecutionPointItemRespVO> getPointItem(@PathVariable("id") Long id) {
        return success(InspectionExecutionPointItemConvert.convertRespVO(inspectionExecutionPointItemQueryService.getPointItemView(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "点位检查项查询")
    public CommonResult<List<InspectionExecutionPointItemRespVO>> searchPointItem(InspectionExecutionPointItemPageReqVO reqVO) {
        return success(inspectionExecutionPointItemQueryService.getPointItemViewList(reqVO).stream()
                .map(InspectionExecutionPointItemConvert::convertRespVO)
                .toList());
    }
}
