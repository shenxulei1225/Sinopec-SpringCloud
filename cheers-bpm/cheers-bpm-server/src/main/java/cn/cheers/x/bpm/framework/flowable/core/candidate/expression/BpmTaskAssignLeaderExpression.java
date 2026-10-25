package cn.cheers.x.bpm.framework.flowable.core.candidate.expression;

import cn.cheers.x.framework.common.util.number.NumberUtils;
import cn.cheers.x.bpm.service.task.BpmProcessInstanceService;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import jakarta.annotation.Resource;
import java.util.Set;

import static cn.cheers.x.framework.common.util.collection.SetUtils.asSet;
import static java.util.Collections.emptySet;

/**
 * 分配给发起人的 Leader 审批的 Expression 流程表达式
 * 目前 Leader 的定义是，发起人所在部门的 Leader
 *
 * 
 */
@Component
@Deprecated // 仅仅是表达式的示例，建议使用 BpmTaskCandidateStartUserDeptLeaderStrategy 替代
public class BpmTaskAssignLeaderExpression {

    @Resource
    private UserAccountApi userAccountApi;
    @Resource
    private DepartmentDirectoryApi departmentDirectoryApi;

    @Resource
    private BpmProcessInstanceService processInstanceService;

    /**
     * 计算审批的候选人
     *
     * @param execution 流程执行实体
     * @param level 指定级别
     * @return 指定级别的领导
     */
    public Set<Long> calculateUsers(DelegateExecution execution, int level) {
        Assert.isTrue(level > 0, "level 必须大于 0");
        // 获得发起人
        ProcessInstance processInstance = processInstanceService.getProcessInstance(execution.getProcessInstanceId());
        Long startUserId = NumberUtils.parseLong(processInstance.getStartUserId());
        // 获得对应 leve 的部门
        DepartmentDTO dept = null;
        for (int i = 0; i < level; i++) {
            // 获得 level 对应的部门
            if (dept == null) {
                dept = getStartUserDept(startUserId);
                if (dept == null) { // 找不到发起人的部门，所以无法使用该规则
                    return emptySet();
                }
            } else {
                if (dept.getParentId() == null) {
                    break;
                }
                DepartmentDTO parentDept = departmentDirectoryApi.getDepartment(dept.getParentId()).getCheckedData();
                dept = parentDept;
            }
        }
        return dept.getLeaderUserId() != null ? asSet(dept.getLeaderUserId()) : emptySet();
    }

    private DepartmentDTO getStartUserDept(Long startUserId) {
        UserAccountDTO startUser = userAccountApi.getUserAccount(startUserId).getCheckedData();
        if (startUser.getDepartmentId() == null) { // 找不到部门，所以无法使用该规则
            return null;
        }
        return departmentDirectoryApi.getDepartment(startUser.getDepartmentId()).getCheckedData();
    }

}
