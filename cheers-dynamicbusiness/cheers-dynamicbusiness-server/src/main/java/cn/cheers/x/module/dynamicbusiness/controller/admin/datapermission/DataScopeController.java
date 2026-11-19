package cn.cheers.x.module.dynamicbusiness.controller.admin.datapermission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.MapStation;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PipelineSegmentNode;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ScopeRecord;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 登录人自己的数据范围。只需登录，返回的就是本人范围。
 */
@Tag(name = "管理后台 - 我的数据范围")
@RestController
@RequestMapping("/dynamicbusiness/data-scope")
public class DataScopeController {

    @Resource
    private DataScopeQueryService dataScopeQueryService;

    @GetMapping("/my-facilities")
    @Operation(summary = "右上角可选的站：本人有效授权分配覆盖到的启用场站")
    public CommonResult<List<ScopeRecord>> myFacilities() {
        return success(dataScopeQueryService.listMyFacilities());
    }

    @GetMapping("/my-pipeline-segments")
    @Operation(summary = "数据页左边的管线树：本人任职覆盖到的、挂在管线下面的启用记录，带上级编号")
    public CommonResult<List<PipelineSegmentNode>> myPipelineSegments() {
        return success(dataScopeQueryService.listMyPipelineSegments());
    }

    @GetMapping("/map-stations")
    @Operation(summary = "应急资源地图用的可见场站：编号、名称、经纬度、所属管线段")
    public CommonResult<List<MapStation>> mapStations() {
        return success(dataScopeQueryService.listMapStations());
    }
}
