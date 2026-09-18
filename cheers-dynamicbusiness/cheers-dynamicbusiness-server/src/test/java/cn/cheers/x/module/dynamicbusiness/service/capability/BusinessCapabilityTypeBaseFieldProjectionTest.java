package cn.cheers.x.module.dynamicbusiness.service.capability;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 类型基础字段进投影后，必须带 baseField=true，否则配置器展示列勾不到。
 */
class BusinessCapabilityTypeBaseFieldProjectionTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void missingKeyIsStale() {
        ArrayNode fields = mapper.createArrayNode();
        fields.add(field("name", true));
        assertTrue(BusinessCapabilityServiceImpl.isTypeBaseFieldProjectionStale(
                Set.of("command_direction"), fields));
    }

    @Test
    void keyPresentButBaseFieldFalseIsStale() {
        ArrayNode fields = mapper.createArrayNode();
        fields.add(field("command_direction", false));
        assertTrue(BusinessCapabilityServiceImpl.isTypeBaseFieldProjectionStale(
                Set.of("command_direction"), fields));
    }

    @Test
    void keyPresentWithoutBaseFieldFlagIsStale() {
        ObjectNode node = mapper.createObjectNode();
        node.put("fieldKey", "command_direction");
        ArrayNode fields = mapper.createArrayNode();
        fields.add(node);
        assertTrue(BusinessCapabilityServiceImpl.isTypeBaseFieldProjectionStale(
                Set.of("command_direction"), fields));
    }

    @Test
    void keyPresentAndBaseFieldTrueIsFresh() {
        ArrayNode fields = mapper.createArrayNode();
        fields.add(field("name", true));
        fields.add(field("command_direction", true));
        assertFalse(BusinessCapabilityServiceImpl.isTypeBaseFieldProjectionStale(
                new LinkedHashSet<>(Set.of("command_direction")), fields));
    }

    @Test
    void fieldsNotArrayIsStale() {
        assertTrue(BusinessCapabilityServiceImpl.isTypeBaseFieldProjectionStale(
                Set.of("command_direction"), mapper.createObjectNode()));
    }

    private ObjectNode field(String fieldKey, boolean baseField) {
        ObjectNode node = mapper.createObjectNode();
        node.put("fieldKey", fieldKey);
        node.put("baseField", baseField);
        return node;
    }
}
