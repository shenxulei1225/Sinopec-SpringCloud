package cn.cheers.x.bpm.framework.flowable.core.candidate.strategy.dept;

import cn.cheers.x.framework.common.util.string.StrUtils;
import cn.cheers.x.bpm.framework.flowable.core.candidate.BpmTaskCandidateStrategy;
import cn.cheers.x.bpm.framework.flowable.core.enums.BpmTaskCandidateStrategyEnum;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static cn.cheers.x.framework.common.util.collection.CollectionUtils.convertSet;

/**
 * 部门的负责人 {@link BpmTaskCandidateStrategy} 实现类
 *
 * @author kyle
 */
@Component
public class BpmTaskCandidateDeptLeaderStrategy implements BpmTaskCandidateStrategy {

    @Resource
    private DepartmentDirectoryApi departmentDirectoryApi;

    @Override
    public BpmTaskCandidateStrategyEnum getStrategy() {
        return BpmTaskCandidateStrategyEnum.DEPT_LEADER;
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
        List<DepartmentDTO> depts = departmentDirectoryApi.listDepartmentsByIds(
                List.copyOf(deptIds)).getCheckedData();
        return convertSet(depts, DepartmentDTO::getLeaderUserId);
    }

}