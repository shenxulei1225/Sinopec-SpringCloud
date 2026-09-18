package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EntityRpcTaskStepTreeCatalogTest {

    @Test
    void requireStepTree_readsByTaskIdOnly() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.getEntity(eq(1L), eq("task")))
                .thenReturn(CommonResult.success(taskEntity()));
        EntityRpcTaskStepTreeCatalog catalog = new EntityRpcTaskStepTreeCatalog(api, new ObjectMapper());

        List<TaskStepNode> nodes = catalog.requireStepTree(1L);

        assertEquals(1, nodes.size());
        assertEquals(11L, nodes.get(0).refId());
        verify(api, never()).getEntityByCode(any(), eq("task"));
    }

    @Test
    void requireStepTree_missing_throws() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.getEntity(eq(1L), eq("task")))
                .thenReturn(CommonResult.success(null));
        EntityRpcTaskStepTreeCatalog catalog = new EntityRpcTaskStepTreeCatalog(api, new ObjectMapper());

        ServiceException ex = assertThrows(ServiceException.class, () -> catalog.requireStepTree(1L));
        assertTrue(ex.getMessage().contains("没有执行步骤图"));
        verify(api, never()).getEntityByCode(any(), eq("task"));
    }

    @Test
    void loadHostPack_missingEquipment_throws() {
        EntityRpcApi api = mock(EntityRpcApi.class);
        when(api.getEntity(eq(99L), eq("equipment")))
                .thenReturn(CommonResult.success(null));
        EntityRpcTaskStepTreeCatalog catalog = new EntityRpcTaskStepTreeCatalog(api, new ObjectMapper());

        ServiceException ex = assertThrows(ServiceException.class, () -> catalog.loadHostPack(99L));
        assertTrue(ex.getMessage().contains("被检查设备"));
    }

    private static EntityRespDTO taskEntity() {
        EntityRespDTO dto = new EntityRespDTO();
        dto.setId(1L);
        dto.setBaseFields(Map.of(
                "step_tree_json", Map.of(
                        "version", 1,
                        "nodes", List.of(Map.of(
                                "nodeKey", "n-move",
                                "order", 1,
                                "hangTypeCode", "action",
                                "refId", 11,
                                "title", "到达指定位置"
                        ))
                )
        ));
        return dto;
    }
}
