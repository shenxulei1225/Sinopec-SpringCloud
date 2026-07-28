package cn.cheers.x.module.dynamicbusiness.service.entity.categoryviaref;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryEntityLinkService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityRelationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Category-via-Ref 编排单元测试（Mockito，不连库）。
 */
@ExtendWith(MockitoExtension.class)
class CategoryViaRefQueryServiceTest {

    @Mock
    private CategoryViaRefQueryPathRegistry pathRegistry;
    @Mock
    private CategoryService categoryService;
    @Mock
    private EntityCategoryRelationService entityCategoryRelationService;
    @Mock
    private CategoryEntityLinkService categoryEntityLinkService;
    @Mock
    private EntityRelationService entityRelationService;

    @InjectMocks
    private CategoryViaRefQueryService service;

    @Test
    void equipmentCategory_reverseToTask_viaRelationField() {
        CategoryViaRefQueryPath path = new CategoryViaRefQueryPath(
                CategoryViaRefQueryPathRegistry.TASK_VIA_EQUIPMENT_CATEGORY,
                "equipment",
                TargetEntityResolveMode.CATEGORY_ENTITY_RELATION,
                "equipment",
                "FLD-TSK-016",
                "task");
        when(pathRegistry.require(CategoryViaRefQueryPathRegistry.TASK_VIA_EQUIPMENT_CATEGORY))
                .thenReturn(path);
        when(categoryService.getAllCategoryIdsIncludingChildrenBatch(List.of(10L), "equipment"))
                .thenReturn(Map.of(10L, List.of(10L, 11L)));
        when(entityCategoryRelationService.listEntityIdsByCategoryIdsOnly(
                List.of(10L, 11L), "equipment"))
                .thenReturn(List.of(100L, 101L));
        when(entityRelationService.listSubjectEntityIdsByRefFieldAndTargetIds(
                eq("FLD-TSK-016"), eq("task"), eq(List.of(100L, 101L))))
                .thenReturn(List.of(9001L));

        assertEquals(List.of(9001L),
                service.listSubjectEntityIds(CategoryViaRefQueryPathRegistry.TASK_VIA_EQUIPMENT_CATEGORY, List.of(10L)));
    }

    @Test
    void regionCategory_reverseToFacility_viaCategoryEntityLink() {
        CategoryViaRefQueryPath path = new CategoryViaRefQueryPath(
                CategoryViaRefQueryPathRegistry.FACILITY_VIA_REGION_CATEGORY,
                "region",
                TargetEntityResolveMode.CATEGORY_ENTITY_LINK,
                "region",
                "FLD-BASE-facility-REF_REGION",
                "facility");
        when(pathRegistry.require(CategoryViaRefQueryPathRegistry.FACILITY_VIA_REGION_CATEGORY))
                .thenReturn(path);
        when(categoryService.getAllCategoryIdsIncludingChildrenBatch(List.of(20L), "region"))
                .thenReturn(Map.of(20L, List.of(20L, 21L)));
        CategoryEntityLinkDO link = CategoryEntityLinkDO.builder()
                .categoryId(20L)
                .entityId(200L)
                .entityTypeCode("region")
                .build();
        when(categoryEntityLinkService.getLinksByCategoryIds(List.of(20L, 21L)))
                .thenReturn(List.of(link));
        when(entityRelationService.listSubjectEntityIdsByRefFieldAndTargetIds(
                eq("FLD-BASE-facility-REF_REGION"), eq("facility"), eq(List.of(200L))))
                .thenReturn(List.of(3001L));

        assertEquals(List.of(3001L),
                service.listSubjectEntityIds(CategoryViaRefQueryPathRegistry.FACILITY_VIA_REGION_CATEGORY, List.of(20L)));
    }

    @Test
    void unknownPath_rejected() {
        when(pathRegistry.require("nope")).thenThrow(new ServiceException(400, "未知反查路径: nope"));
        assertThrows(ServiceException.class, () -> service.listSubjectEntityIds("nope", List.of(1L)));
    }

    @Test
    void assertDimensionCategoryTypeMatches_rejectsMismatch() {
        CategoryViaRefQueryPath path = new CategoryViaRefQueryPath(
                CategoryViaRefQueryPathRegistry.TASK_VIA_EQUIPMENT_CATEGORY,
                "equipment",
                TargetEntityResolveMode.CATEGORY_ENTITY_RELATION,
                "equipment",
                "FLD-TSK-016",
                "task");
        when(pathRegistry.require(CategoryViaRefQueryPathRegistry.TASK_VIA_EQUIPMENT_CATEGORY))
                .thenReturn(path);
        assertThrows(ServiceException.class, () ->
                service.assertDimensionCategoryTypeMatches(
                        CategoryViaRefQueryPathRegistry.TASK_VIA_EQUIPMENT_CATEGORY, "region"));
    }
}
