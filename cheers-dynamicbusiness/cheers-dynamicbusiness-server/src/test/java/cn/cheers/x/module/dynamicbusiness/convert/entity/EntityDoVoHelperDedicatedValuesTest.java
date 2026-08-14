package cn.cheers.x.module.dynamicbusiness.convert.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 列表 VO：从 dedicatedBaseFieldValues 组装，不走按行 merge。
 */
@ExtendWith(MockitoExtension.class)
class EntityDoVoHelperDedicatedValuesTest {

    @Mock
    private EntityDedicatedColumnService dedicatedColumnService;

    @Test
    void toRespVOList_appliesDedicatedValues_withoutMerge() {
        EntityDO entity = new EntityDO();
        entity.setId(1L);
        entity.setEntityTypeCode("equipment");
        entity.setName("泵-1");
        Map<String, Object> dedicated = new LinkedHashMap<>();
        dedicated.put("zone_id", 101L);
        entity.setDedicatedBaseFieldValues(dedicated);

        when(dedicatedColumnService.loadEnabledBaseFieldMeta("equipment")).thenReturn(Map.of());
        when(dedicatedColumnService.loadRefTargetsForBaseFieldMeta(any())).thenReturn(Map.of());

        List<EntityRespVO> list = EntityDoVoHelper.toRespVOList(
                List.of(entity), null, dedicatedColumnService);

        assertEquals(1, list.size());
        assertNotNull(list.get(0).getBaseFields());
        verify(dedicatedColumnService).applyDedicatedBaseFieldValues(eq(entity), any(), any(), any());
        verify(dedicatedColumnService, never()).mergePhysicalColumnsIntoBaseFields(any(), any());
    }

    @Test
    void toLightRespVOList_appliesDedicatedValues_withTypeCache() {
        EntityDO entity = new EntityDO();
        entity.setId(2L);
        entity.setEntityTypeCode("inspection_item");
        entity.setName("检查项-1");
        Map<String, Object> dedicated = new LinkedHashMap<>();
        dedicated.put("zone_id", 202L);
        entity.setDedicatedBaseFieldValues(dedicated);

        when(dedicatedColumnService.loadEnabledBaseFieldMeta("inspection_item")).thenReturn(Map.of());
        when(dedicatedColumnService.loadRefTargetsForBaseFieldMeta(any())).thenReturn(Map.of());

        List<EntityRespVO> list = EntityDoVoHelper.toLightRespVOList(
                List.of(entity), dedicatedColumnService);

        assertEquals(1, list.size());
        assertNotNull(list.get(0).getBaseFields());
        verify(dedicatedColumnService).applyDedicatedBaseFieldValues(eq(entity), any(), any(), any());
        verify(dedicatedColumnService, never()).mergePhysicalColumnsIntoBaseFields(any(), any());
    }
}
