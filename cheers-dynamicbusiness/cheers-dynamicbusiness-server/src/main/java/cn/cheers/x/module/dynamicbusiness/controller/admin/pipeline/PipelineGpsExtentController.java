package cn.cheers.x.module.dynamicbusiness.controller.admin.pipeline;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.pipeline.PipelineGpsExtent;
import cn.cheers.x.module.dynamicbusiness.service.pipeline.PipelineGpsExtentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管廊实时监控读取管线 GPS 外框。
 *
 * 管什么：按当前这座站返回已保存 GPS 的西、南、东、北。
 * 不管什么：不写坐标，不决定镜头怎么摆。
 * 禁止：没有 GPS 时返回一个写死的范围。
 */
@Tag(name = "管理后台 - 管线 GPS 外框")
@RestController
@RequestMapping("/dynamicbusiness/business/pipeline-gps-extent")
public class PipelineGpsExtentController {

    @Resource
    private PipelineGpsExtentQueryService pipelineGpsExtentQueryService;

    @GetMapping
    @Operation(summary = "这座站管线 GPS 的外框。没有点时 data 为空。")
    @PreAuthorize("@pageAccess.allow('live-monitor', 'view')")
    public CommonResult<PipelineGpsExtent> extent(@RequestParam("facilityId") Long facilityId) {
        return success(pipelineGpsExtentQueryService.extentOfFacility(facilityId));
    }
}
