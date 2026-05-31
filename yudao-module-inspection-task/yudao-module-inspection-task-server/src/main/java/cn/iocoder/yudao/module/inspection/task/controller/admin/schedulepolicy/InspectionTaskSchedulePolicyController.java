package cn.iocoder.yudao.module.inspection.task.controller.admin.schedulepolicy;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskSchedulePolicyRespVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePolicyDO;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePolicyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 排期策略 Controller。
 */
@Tag(name = "管理后台 - 排期策略")
@RestController
@RequestMapping("/inspection-task/schedule-policy")
@Validated
public class InspectionTaskSchedulePolicyController {

    @Resource
    private InspectionTaskSchedulePolicyMapper schedulePolicyMapper;

    @GetMapping("/simple-list")
    @Operation(summary = "获取启用的排期策略简易列表")
    public CommonResult<List<InspectionTaskSchedulePolicyRespVO>> getSchedulePolicySimpleList() {
        List<InspectionTaskSchedulePolicyDO> policies = schedulePolicyMapper.selectByEnabled(true);
        return success(BeanUtils.toBean(policies, InspectionTaskSchedulePolicyRespVO.class));
    }
}
