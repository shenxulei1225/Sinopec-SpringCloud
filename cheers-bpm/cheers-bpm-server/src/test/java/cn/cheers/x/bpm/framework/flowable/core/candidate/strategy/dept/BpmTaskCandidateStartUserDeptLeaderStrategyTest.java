package cn.cheers.x.bpm.framework.flowable.core.candidate.strategy.dept;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.test.core.ut.BaseMockitoUnitTest;
import cn.cheers.x.bpm.service.task.BpmProcessInstanceService;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import cn.cheers.x.module.dynamicbusiness.api.directory.UserAccountApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import org.assertj.core.util.Sets;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.runtime.ProcessInstance;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.stubbing.Answer;

import java.util.Set;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.framework.test.core.util.RandomUtils.randomPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BpmTaskCandidateStartUserDeptLeaderStrategyTest extends BaseMockitoUnitTest {

    @InjectMocks
    private BpmTaskCandidateStartUserDeptLeaderStrategy strategy;

    @Mock
    private BpmProcessInstanceService processInstanceService;

    @Mock
    private UserAccountApi userAccountApi;
    @Mock
    private DepartmentDirectoryApi departmentDirectoryApi;

    @Test
    public void testCalculateUsersByTask() {
        // 准备参数
        String param = "2";
        // mock 方法（获得流程发起人）
        Long startUserId = 1L;
        ProcessInstance processInstance = mock(ProcessInstance.class);
        DelegateExecution execution = mock(DelegateExecution.class);
        when(processInstanceService.getProcessInstance(eq(execution.getProcessInstanceId()))).thenReturn(processInstance);
        when(processInstance.getStartUserId()).thenReturn(startUserId.toString());
        // mock 方法（获取发起人的部门负责人）
        mockGetStartUserDeptLeader(startUserId);

        // 调用
        Set<Long> userIds = strategy.calculateUsersByTask(execution, param);
        // 断言
        assertEquals(Sets.newLinkedHashSet(1001L), userIds);
    }

    @Test
    public void testGetStartUserDeptLeader() {
        // 准备参数
        String param = "2";
        // mock 方法
        Long startUserId = 1L;
        mockGetStartUserDeptLeader(startUserId);

        // 调用
        Set<Long> userIds = strategy.calculateUsersByActivity(null, null, param,
                startUserId, null, null);
        // 断言
        assertEquals(Sets.newLinkedHashSet(1001L), userIds);
    }

    private void mockGetStartUserDeptLeader(Long startUserId) {
        when(userAccountApi.getUserAccount(eq(startUserId))).thenReturn(
                success(randomPojo(UserAccountDTO.class, o -> o.setId(startUserId).setDepartmentId(10L))));
        when(departmentDirectoryApi.getDepartment(any())).thenAnswer((Answer< CommonResult<DepartmentDTO>>) invocationOnMock -> {
            Long deptId = invocationOnMock.getArgument(0);
            return success(randomPojo(DepartmentDTO.class, o -> o.setId(deptId).setParentId(deptId * 100).setLeaderUserId(deptId + 1)));
        });
    }

}
