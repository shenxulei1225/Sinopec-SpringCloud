package cn.cheers.x.bpm.framework.flowable.core.candidate.strategy.dept;

import cn.cheers.x.framework.common.util.string.StrUtils;
import cn.cheers.x.bpm.framework.flowable.core.candidate.BpmTaskCandidateStrategy;
import cn.cheers.x.bpm.framework.flowable.core.enums.BpmTaskCandidateStrategyEnum;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 部门的成员 {@link BpmTaskCandidateStrategy} 实现类
 *
 * @author kyle
 */
@Component
public class BpmTaskCandidateDeptMemberStrategy implements BpmTaskCandidateStrategy {

    @Resource
    private DepartmentDirectoryApi departmentDirectoryApi;

    @Override
    public BpmTaskCandidateStrategyEnum getStrategy() {
        return BpmTaskCandidateStrategyEnum.DEPT_MEMBER;
    }

    @Override
    public void validateParam(String param) {
        Set<Long> deptIds = StrUtils.splitToLongSet(param);
        deptIds.forEach(deptId ->
                departmentDirectoryApi.validateDepartmentEnabled(deptId).getCheckedData());
    }

    @Override
    public Set<Long> calculateUsers(String param) {
        Set<Long> deptIds = StrUtils.splitToLongSet(param);
        Set<Long> userAccountIds = new LinkedHashSet<>();
        departmentDirectoryApi.listUserAccountIdsByDepartmentIds(
                        java.util.List.copyOf(deptIds)).getCheckedData()
                .values().forEach(userAccountIds::addAll);
        return userAccountIds;
    }

}