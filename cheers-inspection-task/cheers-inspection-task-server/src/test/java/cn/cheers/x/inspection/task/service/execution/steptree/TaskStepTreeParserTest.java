package cn.cheers.x.inspection.task.service.execution.steptree;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaskStepTreeParserTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void parse_readsStepNodesInOrder() {
        Map<String, Object> raw = Map.of(
                "version", 1,
                "nodes", List.of(
                        Map.of("nodeKey", "n-photo", "order", 2, "hangTypeCode", "action",
                                "refId", 20, "title", "拍照"),
                        Map.of("nodeKey", "n-move", "order", 1, "hangTypeCode", "action",
                                "refCode", "act-arrive", "refId", 10, "title", "到达指定位置")
                )
        );

        List<TaskStepNode> nodes = TaskStepTreeParser.parse(raw, mapper);

        assertEquals(2, nodes.size());
        assertEquals("n-move", nodes.get(0).nodeKey());
        assertEquals(1, nodes.get(0).order());
        assertEquals("n-photo", nodes.get(1).nodeKey());
    }

    @Test
    void parse_arrayOrEmpty_isEmptyTree() {
        assertTrue(TaskStepTreeParser.parse(List.of(), mapper).isEmpty());
        assertTrue(TaskStepTreeParser.parse("[]", mapper).isEmpty());
        assertTrue(TaskStepTreeParser.parse(null, mapper).isEmpty());
        assertTrue(TaskStepTreeParser.parse("not-json", mapper).isEmpty());
    }
}
