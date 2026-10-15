package cn.cheers.x.module.dynamicbusiness.service.entity.relation;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeResolver;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import cn.cheers.x.module.dynamicbusiness.service.entity.refcategory.EntityCategoryRefWritebackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EntityCategoryRelationUnlinkTest {

    @Mock
    private EntityCategoryRelationMapper relationMapper;
    @Mock
    private EntityTypeScopeResolver entityTypeScopeResolver;
    @Mock
    private EntityCategoryRefWritebackService entityCategoryRefWritebackService;
    @Mock
    private EntityCoreService entityCoreService;
    @InjectMocks
    private EntityCategoryRelationServiceImpl service;

    @Test
    void unlinkWhenNotLinkedDoesNotInsertARow() {
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("inspection_item")).thenReturn("inspection_item");
        when(entityTypeScopeResolver.resolve("inspection_item")).thenReturn(null);
        when(relationMapper.selectByEntityIdsAndCategoryIds(anyList(), anyList(), eq("inspection_item")))
                .thenReturn(List.of());

        service.disassociate(2574L, 2104558L, "inspection_item");

        verify(relationMapper, never()).insertBatchRelations(any());
        verify(relationMapper, never()).deleteByCategoryIdAndEntityIds(any(), any(), any());
    }

    @Test
    void unlinkWhenLinkedDeletesThatRow() {
        when(entityTypeScopeResolver.resolveStorageEntityTypeCode("inspection_item")).thenReturn("inspection_item");
        when(entityTypeScopeResolver.resolve("inspection_item")).thenReturn(null);
        when(relationMapper.selectByEntityIdsAndCategoryIds(anyList(), anyList(), eq("inspection_item")))
                .thenReturn(List.of(EntityCategoryRelationDO.builder()
                        .entityId(2574L)
                        .categoryId(2104558L)
                        .entityTypeCode("inspection_item")
                        .build()));
        when(entityCoreService.listByIds(anyList(), eq("inspection_item"))).thenReturn(List.of());

        service.disassociate(2574L, 2104558L, "inspection_item");

        verify(relationMapper).deleteByCategoryIdAndEntityIds(eq(2104558L), anyList(), eq("inspection_item"));
        verify(relationMapper, never()).insertBatchRelations(any());
    }
}
