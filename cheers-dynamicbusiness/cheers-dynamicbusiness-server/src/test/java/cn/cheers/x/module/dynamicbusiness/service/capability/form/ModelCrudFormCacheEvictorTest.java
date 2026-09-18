package cn.cheers.x.module.dynamicbusiness.service.capability.form;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ModelCrudFormCacheEvictorTest {

    @Test
    void evictsRegistryAndDomainCatalogsTogether() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        ModelCrudFormCacheEvictor.evictRegistryAndDomainCatalogs(jdbc, "task");

        ArgumentCaptor<String> sql = ArgumentCaptor.forClass(String.class);
        verify(jdbc).update(sql.capture(), eq("task"), eq(EntityTypeDO.ENTRY_KIND_DOMAIN), eq("task"));
        assertTrue(sql.getValue().contains("base_entity_type_code"));
    }

    @Test
    void skipsBlankCode() {
        JdbcTemplate jdbc = mock(JdbcTemplate.class);
        ModelCrudFormCacheEvictor.evictRegistryAndDomainCatalogs(jdbc, "  ");
        verify(jdbc, never()).update(anyString(), any(), any(), any());
    }
}
