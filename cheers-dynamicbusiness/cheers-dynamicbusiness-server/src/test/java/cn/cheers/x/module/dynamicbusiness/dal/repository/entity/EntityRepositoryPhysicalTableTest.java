package cn.cheers.x.module.dynamicbusiness.dal.repository.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityRepositoryPhysicalTableTest {

    @Mock
    private EntityMapper entityMapper;
    @Mock
    private EntityTableNameHandler entityTableNameHandler;
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private EntityDedicatedColumnService entityDedicatedColumnService;

    private EntityRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        repository = new EntityRepositoryImpl(
                entityMapper, entityTableNameHandler, jdbcTemplate, entityDedicatedColumnService);
    }

    @Test
    void requireExistingUsesHandlerTableAndDoesNotInventName() {
        when(entityTableNameHandler.resolvePhysicalTableName("task")).thenReturn("ent_task_t1");
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("ent_task_t1"))).thenReturn(1);

        assertEquals("ent_task_t1", repository.requireExistingPhysicalTable("task"));
        verify(entityTableNameHandler).resolvePhysicalTableName("task");
    }

    @Test
    void requireExistingFailsWhenTableMissing() {
        when(entityTableNameHandler.resolvePhysicalTableName("task")).thenReturn("ent_task_t1");
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("ent_task_t1"))).thenReturn(0);

        ServiceException ex = assertThrows(
                ServiceException.class, () -> repository.requireExistingPhysicalTable("task"));
        assertEquals(400, ex.getCode());
    }
}
