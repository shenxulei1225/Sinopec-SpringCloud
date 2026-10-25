package cn.cheers.x.bpm.framework.flowable.core.candidate.strategy.dept;

import cn.cheers.x.framework.test.core.ut.BaseMockitoUnitTest;
import cn.cheers.x.module.dynamicbusiness.api.directory.DepartmentDirectoryApi;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Set;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;
import static cn.cheers.x.framework.test.core.util.RandomUtils.randomPojo;
import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.when;

public class BpmTaskCandidateDeptLeaderStrategyTest extends BaseMockitoUnitTest {

    @InjectMocks
    private BpmTaskCandidateDeptLeaderStrategy strategy;

    @Mock
    private DepartmentDirectoryApi departmentDirectoryApi;

    @Test
    public void testCalculateUsers() {
        // 准备参数
        String param = "10,20";
        // mock 方法
        when(departmentDirectoryApi.listDepartmentsByIds(
                argThat(ids -> Set.copyOf(ids).equals(Set.of(10L, 20L))))).thenReturn(success(asList(
                randomPojo(DepartmentDTO.class, o -> o.setId(10L).setParentId(10L).setLeaderUserId(11L)),
                randomPojo(DepartmentDTO.class, o -> o.setId(20L).setParentId(20L).setLeaderUserId(21L)))));

        // 调用
        Set<Long> userIds = strategy.calculateUsers(param);
        // 断言结果
        assertEquals(Sets.newLinkedHashSet(11L, 21L), userIds);
    }

}
