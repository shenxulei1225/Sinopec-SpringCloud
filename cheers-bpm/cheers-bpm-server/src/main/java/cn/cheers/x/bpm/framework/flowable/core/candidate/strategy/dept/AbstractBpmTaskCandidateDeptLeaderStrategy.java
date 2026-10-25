package cn.cheers.x.bpm.framework.flowable.core.candidate.strategy.dept;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.cheers.x.bpm.framework.flowable.core.candidate.BpmTaskCandidateStrategy;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import jakarta.annotation.Resource;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 部门的负责人 {@link BpmTaskCandidateStrategy} 抽象类
 *
 * @author jason
 */
public abstract class AbstractBpmTaskCandidateDeptLeaderStrategy implements BpmTaskCandidateStrategy {

    @Resource
    protected DepartmentDirectoryApi departmentDirectoryApi;
    @Resource
    protected UserAccountApi userAccountApi;

    /**
     * 获得指定层级的部门负责人，只有第 level 的负责人
     *
     * @param dept 指定部门
     * @param level 第几级
     * @return 部门负责人的编号
     */
    protected Long getAssignLevelDeptLeaderId(DepartmentDTO dept, Integer level) {
        Assert.isTrue(level > 0, "level 必须大于 0");
        if (dept == null) {
            return null;
        }
        DepartmentDTO currentDept = dept;
        for (int i = 1; i < level; i++) {
            if (currentDept.getParentId() == null) {
                break;
            }
            DepartmentDTO parentDept = departmentDirectoryApi.getDepartment(currentDept.getParentId()).getCheckedData();
            currentDept = parentDept;
        }
        return currentDept.getLeaderUserId();
    }

    /**
     * 获得连续层级的部门负责人，包含 [1, level] 的负责人
     *
     * @param deptIds 指定部门编号数组
     * @param level 最大层级
     * @return 连续部门负责人 Id
     */
    protected Set<Long> getMultiLevelDeptLeaderIds(List<Long> deptIds, Integer level) {
        Assert.isTrue(level > 0, "level 必须大于 0");
        if (CollUtil.isEmpty(deptIds)) {
            return new HashSet<>();
        }
        Set<Long> deptLeaderIds = new LinkedHashSet<>();
        Set<Long> currentDepartmentIds = new LinkedHashSet<>(deptIds);
        for (int i = 0; i < level && !currentDepartmentIds.isEmpty(); i++) {
            // 同一层级一次读取。禁止按部门逐条远程查询动态目录。
            List<DepartmentDTO> departments = departmentDirectoryApi
                    .listDepartmentsByIds(List.copyOf(currentDepartmentIds)).getCheckedData();
            Set<Long> parentDepartmentIds = new LinkedHashSet<>();
            for (DepartmentDTO dept : departments) {
                if (dept.getLeaderUserId() != null) {
                    deptLeaderIds.add(dept.getLeaderUserId());
                }
                if (dept.getParentId() != null) {
                    parentDepartmentIds.add(dept.getParentId());
                }
            }
            currentDepartmentIds = parentDepartmentIds;
        }
        return deptLeaderIds;
    }

    /**
     * 获取发起人的部门
     *
     * @param startUserId 发起人 Id
     */
    protected DepartmentDTO getStartUserDept(Long startUserId) {
        UserAccountDTO startUser = userAccountApi.getUserAccount(startUserId).getCheckedData();
        if (startUser.getDepartmentId() == null) { // 找不到部门
            return null;
        }
        return departmentDirectoryApi.getDepartment(startUser.getDepartmentId()).getCheckedData();
    }

}
