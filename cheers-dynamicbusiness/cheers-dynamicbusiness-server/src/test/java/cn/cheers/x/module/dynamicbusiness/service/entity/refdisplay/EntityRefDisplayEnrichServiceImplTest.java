package cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * 详情「上级分区」：编号留着，名称补上。
 */
@ExtendWith(MockitoExtension.class)
class EntityRefDisplayEnrichServiceImplTest {

    @Mock
    private EntityCoreService entityCoreService;

    @Test
    void enrich_keepsParentIdAndAddsName() {
        EntityRefDisplayEnrichServiceImpl service = new EntityRefDisplayEnrichServiceImpl(entityCoreService);
        EntityRespVO row = new EntityRespVO();
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", "zone");
        base.put("parentId", 11L);
        base.put("name", "电力专用舱");
        row.setBaseFields(base);

        EntityDO parent = new EntityDO();
        parent.setId(11L);
        parent.setName("3号防火区");
        when(entityCoreService.listByIds(anyList(), eq("zone"))).thenReturn(List.of(parent));

        service.enrich(List.of(row));

        assertEquals(11L, row.getParentId());
        Map<String, Object> parentRef = assertInstanceOf(Map.class, row.getBaseFields().get("parentId"));
        assertEquals("zone", parentRef.get("entityTypeCode"));
        assertEquals(11L, ((Number) parentRef.get("id")).longValue());
        assertEquals("3号防火区", parentRef.get("name"));
    }

    @Test
    void enrich_keepsBareParentIdWhenNameMissing() {
        EntityRefDisplayEnrichServiceImpl service = new EntityRefDisplayEnrichServiceImpl(entityCoreService);
        EntityRespVO row = new EntityRespVO();
        Map<String, Object> base = new LinkedHashMap<>();
        base.put("entityTypeCode", "zone");
        base.put("parentId", 11L);
        row.setBaseFields(base);
        when(entityCoreService.listByIds(anyList(), eq("zone"))).thenReturn(List.of());

        service.enrich(List.of(row));

        assertEquals(11L, row.getParentId());
        assertEquals(11L, row.getBaseFields().get("parentId"));
    }
}
