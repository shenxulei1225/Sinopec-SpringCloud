package cn.cheers.x.inspection.task.controller.admin.schedulepolicy;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskSchedulePolicyRespVO;
import cn.cheers.x.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePolicyDO;
import cn.cheers.x.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePolicyMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

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
    @Operation(summary = "获取启用的排期策略简单列表")
    public CommonResult<List<InspectionTaskSchedulePolicyRespVO>> getSchedulePolicySimpleList() {
        List<InspectionTaskSchedulePolicyDO> policies = schedulePolicyMapper.selectByEnabled(true);
        return success(BeanUtils.toBean(policies, InspectionTaskSchedulePolicyRespVO.class));
    }
}
