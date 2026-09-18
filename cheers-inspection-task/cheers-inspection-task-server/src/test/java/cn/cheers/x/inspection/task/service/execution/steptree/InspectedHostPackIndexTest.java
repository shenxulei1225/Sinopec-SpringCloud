package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.inspection.task.model.task.InspectionContent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InspectedHostPackIndexTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void from_indexesInspectedEquipmentNotExecutor() {
        InspectionContent content = new InspectionContent();
        InspectionContent.ObjectContent object = new InspectionContent.ObjectContent();
        object.setObjectId(200L);
        InspectionContent.ItemContent item = new InspectionContent.ItemContent();
        item.setItemId(7L);
        object.setItems(List.of(item));
        content.setCustomObjects(List.of(object));
        AtomicLong loadedId = new AtomicLong();
        HostSopParamPack pack = HostSopParamPack.parse(Map.of(
                "version", 1,
                "entries", List.of(Map.of(
                        "subjectId", 7,
                        "paramsByNode", Map.of("n-photo", Map.of("shot_count", 3))
                ))
        ), mapper);

        InspectedHostPackIndex index = InspectedHostPackIndex.from(content, equipmentId -> {
            loadedId.set(equipmentId);
            return pack;
        });

        assertEquals(200L, loadedId.get());
        assertEquals(3, index.packForItem(7L).paramsFor(7L, "n-photo", "act-photo").get("shot_count"));
        assertTrue(index.packForItem(8L).paramsFor(8L, "n-photo", "act-photo").isEmpty());
    }

    @Test
    void from_missingSelection_throws() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> InspectedHostPackIndex.from(new InspectionContent(), id -> HostSopParamPack.empty()));
        assertTrue(ex.getMessage().contains("没有勾选被检查对象"));
    }
}
