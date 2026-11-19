package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantPhysicalTableNames;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityRelationDeleteScopeTest {

    @Mock
    private EntityRelationMapper entityRelationMapper;
    @Mock
    private EntityTableNameHandler entityTableNameHandler;
    @InjectMocks
    private EntityRelationServiceImpl relationService;

    @Test
    void deleteAllRelationsByEntityOnlyTouchesItsOwnTable() {
        List<String> codes = List.of("organization");
        when(entityTableNameHandler.listCodesSharingPhysicalTable("organization")).thenReturn(codes);

        relationService.deleteAllRelationsByEntity(5L, "organization");

        verify(entityRelationMapper).deleteBySourceEntity(5L, codes);
        verify(entityRelationMapper).deleteByTargetEntity(5L, codes);
    }

    @Test
    void deleteAllRelationsByEntityRejectsMissingCatalog() {
        assertThatThrownBy(() -> relationService.deleteAllRelationsByEntity(5L, null))
                .isInstanceOf(ServiceException.class);
        verify(entityRelationMapper, never()).deleteBySourceEntity(anyLong(), any());
        verify(entityRelationMapper, never()).deleteByTargetEntity(anyLong(), any());
    }

    @Test
    void syncRelationsOnDeleteOnlyTouchesItsOwnTable() {
        EntityTableNameHandler handler = mock(EntityTableNameHandler.class);
        EntityRelationMapper mapper = mock(EntityRelationMapper.class);
        EntityRelationSyncServiceImpl syncService = new EntityRelationSyncServiceImpl(
                mapper, null, null, null, null, null, null, null, handler, null, null);
        when(handler.listCodesSharingPhysicalTable("organization")).thenReturn(List.of("organization"));

        syncService.syncRelationsOnDelete(5L, "organization");

        verify(mapper).deleteBySourceEntity(5L, List.of("organization"));
    }

    @Test
    void codesSharingTableFollowTheRegisteredTableNotTheBaseCatalog() {
        EntityTypeMapper typeMapper = mock(EntityTypeMapper.class);
        EntityTableNameHandler handler = new EntityTableNameHandler(typeMapper);
        EntityTypeDO subscription = dedicated("event_subscription", "ent_push_subscription", null);
        EntityTypeDO topic = dedicated("event_topic", "ent_push_topic", null);
        EntityTypeDO reuse = dedicated("subscription_reuse", "ent_push_subscription", "event_subscription");
        EntityTypeDO organization = dedicated("organization", "ent_organization", null);
        when(typeMapper.selectByCode("event_subscription")).thenReturn(subscription);
        when(typeMapper.selectList()).thenReturn(List.of(subscription, topic, reuse, organization));

        try (MockedStatic<TenantPhysicalTableNames> names = mockStatic(TenantPhysicalTableNames.class)) {
            names.when(() -> TenantPhysicalTableNames.ensureTenantSuffix(anyString()))
                    .thenAnswer(inv -> inv.getArgument(0) + "_t1");
            assertThat(handler.listCodesSharingPhysicalTable("event_subscription"))
                    .containsExactly("event_subscription", "subscription_reuse");
        }
    }

    private static EntityTypeDO dedicated(String code, String table, String base) {
        EntityTypeDO type = new EntityTypeDO();
        type.setCode(code);
        type.setStorageType("DEDICATED");
        type.setDedicatedTableName(table);
        type.setBaseEntityTypeCode(base);
        return type;
    }
}
