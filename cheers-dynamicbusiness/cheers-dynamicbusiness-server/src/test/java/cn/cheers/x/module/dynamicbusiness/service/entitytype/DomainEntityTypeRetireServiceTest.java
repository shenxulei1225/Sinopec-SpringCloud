package cn.cheers.x.module.dynamicbusiness.service.entitytype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessEntryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.business.BusinessMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmCatalogOrchestrationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.model.ModelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * DOMAIN 下线：型号归位、不删型号；注册项停用后逻辑删。
 */
@ExtendWith(MockitoExtension.class)
class DomainEntityTypeRetireServiceTest {

    @Mock
    private EntityTypeMapper entityTypeMapper;
    @Mock
    private ModelMapper modelMapper;
    @Mock
    private ModelService modelService;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CategoryService categoryService;
    @Mock
    private BusinessMapper businessMapper;
    @Mock
    private BusinessEntryMapper businessEntryMapper;
    @Mock
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;
    @Mock
    private DmDataTabColumnRelationMapper dmDataTabColumnRelationMapper;
    @Mock
    private DmCatalogOrchestrationMapper catalogOrchestrationMapper;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private DomainEntityTypeRetireService retireService;

    @Test
    void retire_undomainsMatchingModels_thenSoftDeletesRegistry() {
        EntityTypeDO domainType = new EntityTypeDO();
        domainType.setId(280L);
        domainType.setCode("task_patrol");
        domainType.setEntryKind(EntityTypeDO.ENTRY_KIND_DOMAIN);
        domainType.setBaseEntityTypeCode("task");
        domainType.setDomain("巡检");
        domainType.setDataLayoutId(99L);

        ModelDO inDomain = new ModelDO();
        inDomain.setId(532L);
        inDomain.setEntityTypeCode("task");
        inDomain.setDomain("巡检");

        ModelDO otherDomain = new ModelDO();
        otherDomain.setId(900L);
        otherDomain.setEntityTypeCode("task");
        otherDomain.setDomain("维修");

        when(modelMapper.selectList(any())).thenReturn(List.of(inDomain, otherDomain));
        when(categoryMapper.selectOne(any())).thenReturn(null);
        when(dmDataTabLayoutMapper.selectList(any())).thenReturn(List.of());
        when(dmDataTabLayoutMapper.selectListByLayoutId(99L)).thenReturn(List.of());
        when(dmDataTabColumnRelationMapper.selectList(any())).thenReturn(List.of());
        when(dmDataTabColumnRelationMapper.selectListByLayoutId(99L)).thenReturn(List.of());
        when(catalogOrchestrationMapper.selectByEntityTypeCode("task_patrol")).thenReturn(null);
        when(businessMapper.selectByCode("task_patrol")).thenReturn(null);
        when(jdbcTemplate.update(any(String.class), eq("task_patrol"))).thenReturn(0);

        retireService.retire(domainType);

        verify(modelService).changeDomain(532L, EntityTypeScopeContext.DOMAIN_FILTER_NONE);
        verify(modelService, never()).changeDomain(eq(900L), any());

        ArgumentCaptor<EntityTypeDO> patchCaptor = ArgumentCaptor.forClass(EntityTypeDO.class);
        verify(entityTypeMapper).updateById(patchCaptor.capture());
        assertEquals(EntityTypeDO.STATUS_INACTIVE, patchCaptor.getValue().getStatus());
        verify(entityTypeMapper).deleteById(280L);
    }
}
