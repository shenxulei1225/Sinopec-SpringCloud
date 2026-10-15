package cn.cheers.x.module.dynamicbusiness.service.execution;

import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class PendingExecutionPhysicalDeleterTest {

    @AfterEach
    void clearTenant() {
        TenantContextHolder.clear();
    }

    @Test
    void delete_usesPhysicalDeleteForRecordStepsAndRelations() {
        TenantContextHolder.setTenantId(1L);
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        PendingExecutionPhysicalDeleter deleter = new PendingExecutionPhysicalDeleter(jdbc);

        deleter.delete(55L, List.of(101L, 102L));

        verify(jdbc).update(startsWith("DELETE FROM dynamic_entity_relation_t1"), any(Object[].class));
        verify(jdbc).update(startsWith("DELETE FROM dynamic_entity_category_relation_t1"), any(Object[].class));
        verify(jdbc).update(startsWith("DELETE FROM ent_task_execution_step_t1"), any(Object[].class));
        verify(jdbc).update(startsWith("DELETE FROM ent_task_excution_record_t1"), any(Object[].class));
        verify(jdbc, times(4)).update(startsWith("DELETE FROM"), any(Object[].class));
    }
}
