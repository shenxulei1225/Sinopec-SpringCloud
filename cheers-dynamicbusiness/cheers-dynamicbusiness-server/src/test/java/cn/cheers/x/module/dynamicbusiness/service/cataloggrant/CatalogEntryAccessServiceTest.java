package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytypescope.EntityTypeScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CatalogEntryAccessServiceTest {

    @InjectMocks
    private CatalogEntryAccessService service;

    @Mock
    private EntityTypeMapper entityTypeMapper;
    @Mock
    private EntityTypeScopeMapper entityTypeScopeMapper;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private EntityRepository entityRepository;

    @BeforeEach
    void catalogs() {
        type("task", "NATIVE", null, null);
        type("task_patrol", "DOMAIN", "task", "巡检");
        type("task_maintenance", "DOMAIN", "task", "维修");
        type("equipment", "NATIVE", null, null);
        type("patrol_equipment", "SCOPE", "equipment", null);
        type("inspection_item", "NATIVE", null, null);
    }

    @Test
    void storageCodeOnDomainPage_isThePage_andDomainIsForced() {
        CatalogEntryAccessService.QueryBinding binding = service.bindQuery("task_patrol", "task", null);
        assertEquals("task", binding.entityTypeCode());
        assertEquals("巡检", binding.domain());
        assertNull(service.otherCatalogCode("task_patrol", "task"));
    }

    @Test
    void otherDomainOnDomainPage_isRejected() {
        assertThrows(ServiceException.class, () -> service.bindQuery("task_patrol", "task", "维修"));
    }

    @Test
    void storageCodeOnScopePage_queriesTheScopeEntry() {
        CatalogEntryAccessService.QueryBinding binding = service.bindQuery("patrol_equipment", "equipment", null);
        assertEquals("patrol_equipment", binding.entityTypeCode());
    }

    @Test
    void anotherCatalogColumn_needsThatCatalog() {
        assertEquals("equipment", service.otherCatalogCode("inspection_item", "equipment"));
        assertEquals("task_maintenance", service.otherCatalogCode("task_patrol", "task_maintenance"));
        CatalogEntryAccessService.QueryBinding binding = service.bindQuery("inspection_item", "equipment", null);
        assertEquals("equipment", binding.entityTypeCode());
    }

    @Test
    void unknownCatalog_isRejected() {
        assertThrows(ServiceException.class, () -> service.otherCatalogCode("inspection_item", "nope"));
        assertThrows(ServiceException.class, () -> service.otherCatalogCode(null, "equipment"));
    }

    @Test
    void recordOfOtherDomain_isRejectedOnDomainPage() {
        when(entityRepository.findByIds(anyList(), eq("task"))).thenReturn(List.of(entity(7L, "维修")));
        assertThrows(ServiceException.class, () -> service.assertRecordsInTarget("task_patrol", "task", List.of(7L)));
    }

    @Test
    void recordOfPageDomain_passes() {
        when(entityRepository.findByIds(anyList(), eq("task"))).thenReturn(List.of(entity(7L, "巡检")));
        assertDoesNotThrow(() -> service.assertRecordsInTarget("task_patrol", "task", List.of(7L)));
    }

    @Test
    void missingRecord_isRejected() {
        when(entityRepository.findByIds(anyList(), eq("task"))).thenReturn(List.of());
        assertThrows(ServiceException.class, () -> service.assertRecordsInTarget("task_patrol", "task", List.of(7L)));
    }

    @Test
    void nonMember_isRejectedOnScopePage() {
        EntityTypeScopeDO member = new EntityTypeScopeDO();
        member.setEntityId(1L);
        when(entityTypeScopeMapper.selectByCodeAndEntityIds(eq("patrol_equipment"), anyList()))
                .thenReturn(List.of(member));
        assertDoesNotThrow(() -> service.assertRecordsInTarget("patrol_equipment", "equipment", List.of(1L)));
        assertThrows(ServiceException.class,
                () -> service.assertRecordsInTarget("patrol_equipment", "equipment", List.of(1L, 2L)));
    }

    @Test
    void modelOfOtherDomain_isRejectedOnDomainPage() {
        when(modelMapper.selectByIds(anyList())).thenReturn(List.of(model(5L, "task", "维修")));
        assertThrows(ServiceException.class, () -> service.assertModelInTarget("task_patrol", "task", 5L));
    }

    @Test
    void modelOfPageDomain_passes() {
        when(modelMapper.selectByIds(anyList())).thenReturn(List.of(model(5L, "task", "巡检")));
        assertDoesNotThrow(() -> service.assertModelInTarget("task_patrol", "task", 5L));
    }

    @Test
    void modelOfOtherStorageType_isRejected() {
        when(modelMapper.selectByIds(anyList())).thenReturn(List.of(model(5L, "equipment", null)));
        assertThrows(ServiceException.class,
                () -> service.assertModelsInTarget("inspection_item", null, List.of(5L)));
    }

    @Test
    void missingModel_isRejected() {
        when(modelMapper.selectByIds(anyList())).thenReturn(List.of(model(5L, "task", "巡检")));
        assertThrows(ServiceException.class,
                () -> service.assertModelsInTarget("task_patrol", "task", List.of(5L, 6L)));
    }

    @Test
    void newModelWithoutPageDomain_isRejectedOnDomainPage() {
        assertThrows(ServiceException.class, () -> service.assertNewModelInTarget("task_patrol", "task", null));
        assertDoesNotThrow(() -> service.assertNewModelInTarget("task_patrol", "task", "巡检"));
    }

    @Test
    void modelQueryOnDomainPage_forcesPageDomain() {
        CatalogEntryAccessService.QueryBinding binding = service.bindModelQuery("task_patrol", "task", null);
        assertEquals("task", binding.entityTypeCode());
        assertEquals("巡检", binding.domain());
        assertThrows(ServiceException.class, () -> service.bindModelQuery("task_patrol", "task", "维修"));
    }

    @Test
    void modelQueryOnScopePage_usesStorageType() {
        CatalogEntryAccessService.QueryBinding binding = service.bindModelQuery("patrol_equipment", "equipment", null);
        assertEquals("equipment", binding.entityTypeCode());
        assertNull(binding.domain());
    }

    @Test
    void retainRecords_keepsOnlyPageDomainInOrder() {
        when(entityRepository.findByIds(anyList(), eq("task")))
                .thenReturn(List.of(entity(9L, "巡检"), entity(7L, "维修"), entity(3L, "巡检")));
        assertEquals(List.of(3L, 9L), service.retainRecordsInTarget("task_patrol", "task", List.of(3L, 7L, 9L)));
    }

    @Test
    void retainRecords_keepsOnlyMembersOnScopePage() {
        EntityTypeScopeDO member = new EntityTypeScopeDO();
        member.setEntityId(2L);
        when(entityTypeScopeMapper.selectByCodeAndEntityIds(eq("patrol_equipment"), anyList()))
                .thenReturn(List.of(member));
        assertEquals(List.of(2L), service.retainRecordsInTarget("patrol_equipment", "equipment", List.of(1L, 2L)));
    }

    @Test
    void wholeTypeTree_isRejectedForScopeAndNeedsModelForDomain() {
        assertThrows(ServiceException.class,
                () -> service.assertWholeTypeTreeAllowed("patrol_equipment", "equipment", null));
        assertThrows(ServiceException.class,
                () -> service.assertWholeTypeTreeAllowed("task_patrol", "task", null));
        assertDoesNotThrow(() -> service.assertWholeTypeTreeAllowed("equipment", "equipment", null));
    }

    private void type(String code, String entryKind, String base, String domain) {
        EntityTypeDO row = new EntityTypeDO();
        row.setCode(code);
        row.setEntryKind(entryKind);
        row.setBaseEntityTypeCode(base);
        row.setDomain(domain);
        when(entityTypeMapper.selectByCode(code)).thenReturn(row);
    }

    private static ModelDO model(Long id, String typeCode, String domain) {
        ModelDO row = new ModelDO();
        row.setId(id);
        row.setEntityTypeCode(typeCode);
        row.setDomain(domain);
        return row;
    }

    private static EntityDO entity(Long id, String domain) {
        EntityDO row = new EntityDO();
        row.setId(id);
        row.setDomain(domain);
        return row;
    }
}
