package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo.AnalysisAlarmListRespVO;
import cn.cheers.x.module.dynamicbusiness.service.execution.PatrolAnalysisAlarmQuery;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 分析告警汇总。读异常记录里已经写成告警的那些，不创建分析任务。
 */
@Tag(name = "管理后台 - 分析告警")
@RestController
@RequestMapping("/dynamicbusiness/analysis-alarms")
@Validated
public class PatrolAnalysisAlarmController {

    @Resource
    private PatrolAnalysisAlarmQuery patrolAnalysisAlarmQuery;

    @GetMapping
    @Operation(summary = "列出当前场站的分析告警")
    @PreAuthorize("@ss.hasPermission('system:entity:query')")
    public CommonResult<AnalysisAlarmListRespVO> list(@RequestParam("facilityId") Long facilityId) {
        return success(patrolAnalysisAlarmQuery.list(facilityId));
    }
}
