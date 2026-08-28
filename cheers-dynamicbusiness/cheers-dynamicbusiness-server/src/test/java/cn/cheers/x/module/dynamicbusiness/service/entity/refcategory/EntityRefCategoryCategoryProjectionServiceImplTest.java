package cn.cheers.x.module.dynamicbusiness.service.entity.refcategory;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryModeSupport;
import cn.cheers.x.module.dynamicbusiness.service.category.relation.CategoryCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.model.relation.ModelCategoryRelationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityRefCategoryCategoryProjectionServiceImplTest {

    @Mock
    private CategoryTypeMapper categoryTypeMapper;
    @Mock
    private ModelCategoryRelationService modelCategoryRelationService;
    @Mock
    private CategoryCategoryRelationService categoryCategoryRelationService;
    @Mock
    private EntityTypeScopeResolver entityTypeScopeResolver;

    private EntityRefCategoryCategoryProjectionServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new EntityRefCategoryCategoryProjectionServiceImpl(
                categoryTypeMapper,
                modelCategoryRelationService,
                categoryCategoryRelationService,
                entityTypeScopeResolver);
    }

    @Test
    void sync_combo3_upsertsCategoryCategory() {
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(categoryTypeMapper.selectByCategoryTypeCode("region")).thenReturn(advancedType("region"));
        when(categoryTypeMapper.selectByCategoryTypeCode("equipment")).thenReturn(simpleType("equipment"));
        when(modelCategoryRelationService.listCategoryIdsByModelId(50L, "equipment"))
                .thenReturn(List.of(200L, 201L));

        service.syncCategoryCategoryOnRefAssociate(50L, "equipment", 100L, "region");

        verify(categoryCategoryRelationService).associate(100L, 200L, "region", "equipment");
        verify(categoryCategoryRelationService).associate(100L, 201L, "region", "equipment");
    }

    @Test
    void sync_skipsWhenBothAdvanced() {
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("facility")).thenReturn("facility");
        when(categoryTypeMapper.selectByCategoryTypeCode("region")).thenReturn(advancedType("region"));
        when(categoryTypeMapper.selectByCategoryTypeCode("facility")).thenReturn(advancedType("facility"));

        service.syncCategoryCategoryOnRefAssociate(10L, "facility", 100L, "region");

        verify(categoryCategoryRelationService, never()).associate(eq(100L), eq(10L), eq("region"), eq("facility"));
        verify(modelCategoryRelationService, never()).listCategoryIdsByModelId(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void sync_skipsWhenNoModelCategories() {
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("equipment")).thenReturn("equipment");
        when(categoryTypeMapper.selectByCategoryTypeCode("region")).thenReturn(advancedType("region"));
        when(categoryTypeMapper.selectByCategoryTypeCode("equipment")).thenReturn(simpleType("equipment"));
        when(modelCategoryRelationService.listCategoryIdsByModelId(50L, "equipment")).thenReturn(List.of());

        service.syncCategoryCategoryOnRefAssociate(50L, "equipment", 100L, "region");

        verify(categoryCategoryRelationService, never()).associate(
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.anyLong(),
                org.mockito.ArgumentMatchers.any(),
                org.mockito.ArgumentMatchers.any());
    }

    private static CategoryTypeDO advancedType(String code) {
        CategoryTypeDO type = new CategoryTypeDO();
        type.setCategoryTypeCode(code);
        type.setCategoryMode(CategoryModeSupport.ADVANCED);
        return type;
    }

    private static CategoryTypeDO simpleType(String code) {
        CategoryTypeDO type = new CategoryTypeDO();
        type.setCategoryTypeCode(code);
        type.setCategoryMode(CategoryModeSupport.SIMPLE);
        return type;
    }
}
