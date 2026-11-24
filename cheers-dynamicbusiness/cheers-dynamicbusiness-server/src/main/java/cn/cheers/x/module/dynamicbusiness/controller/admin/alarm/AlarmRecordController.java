package cn.cheers.x.module.dynamicbusiness.controller.admin.alarm;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.service.alarm.AlarmRecordAccess;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 告警页上的业务大类。
 * 只返回这个人有对应业务页面权限、并且分类树上已经有的大类。
 * 不负责列告警，也不在打开页面时补分类。
 */
@Tag(name = "管理后台 - 告警记录")
@RestController
@RequestMapping("/dynamicbusiness/alarm-records")
@Validated
public class AlarmRecordController {

    @Resource
    private AlarmRecordAccess alarmRecordAccess;

    @GetMapping("/tabs")
    @Operation(summary = "当前登录人能看的告警业务大类")
    @PreAuthorize("@catalogAccess.allow('alarm', 'query')")
    public CommonResult<List<AlarmRecordAccess.Tab>> tabs() {
        return success(alarmRecordAccess.visibleTabs());
    }
}
