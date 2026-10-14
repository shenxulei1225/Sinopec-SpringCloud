package cn.cheers.x.device.protocolgateway.datacollection;

import cn.cheers.x.device.protocolgateway.api.channel.AccessChannelCodes;
import cn.cheers.x.device.protocolgateway.api.datacollection.CollectionSample;
import cn.cheers.x.device.protocolgateway.api.datacollection.ProtocolQualifyStatus;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 按字段说明核必填和类型；多出来的键不罚。
 */
class FieldDescriptionQualifierTest {

    @Test
    void requiredAndTypesMatch_isQualified() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of(
                        "opcode", 500202,
                        "msgId", "u1",
                        "packages", Map.of("opcode", 200301, "sequence", 3)
                )),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("opcode", "integer", "指令编码", true),
                        new FieldDescriptionRow("msgId", "string", "消息编号", true),
                        new FieldDescriptionRow("packages", "object", "结果", true),
                        new FieldDescriptionRow("packages.opcode", "integer", "内层动作码", true),
                        new FieldDescriptionRow("packages.sequence", "integer", "序号", true)
                )));

        assertEquals(ProtocolQualifyStatus.QUALIFIED, sample.protocolQualify());
        assertTrue(sample.qualifyErrors().isEmpty());
    }

    @Test
    void extraKeys_doNotFail() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of("opcode", 500202, "msgId", "u1", "extra", "x")),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("opcode", "integer", "指令编码", true),
                        new FieldDescriptionRow("msgId", "string", "消息编号", true)
                )));
        assertEquals(ProtocolQualifyStatus.QUALIFIED, sample.protocolQualify());
    }

    @Test
    void missingRequired_isUnqualified() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of("opcode", 500202)),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("opcode", "integer", "指令编码", true),
                        new FieldDescriptionRow("msgId", "string", "消息编号", true)
                )));
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertTrue(sample.qualifyErrors().get(0).contains("消息编号"));
    }

    @Test
    void wrongType_isUnqualified() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of("opcode", "500202")),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("opcode", "integer", "指令编码", true)
                )));
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertTrue(sample.qualifyErrors().get(0).contains("类型不对"));
    }

    @Test
    void getPackageResponse_packagesArray_isQualified() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of(
                        "opcode", 500301,
                        "msgId", "m1",
                        "code", 200,
                        "data", Map.of(
                                "templateId", "T1",
                                "lineId", "L1",
                                "packages", List.of(
                                        Map.of("sequence", 0, "opcode", 200102, "request", Map.of("pointId", "p1"))
                                )
                        )
                )),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("opcode", "integer", "操作码", true),
                        new FieldDescriptionRow("data.templateId", "string", "任务模板编号", true),
                        new FieldDescriptionRow("data.lineId", "string", "指令报文 id", true),
                        new FieldDescriptionRow("data.packages", "array", "有序动作集合", true),
                        new FieldDescriptionRow("data.packages[].sequence", "integer", "动作序号", true),
                        new FieldDescriptionRow("data.packages[].opcode", "integer", "内层动作码", true)
                )));
        assertEquals(ProtocolQualifyStatus.QUALIFIED, sample.protocolQualify());
    }

    @Test
    void getPackageResponse_packagesAsString_isUnqualified() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of(
                        "opcode", 500301,
                        "code", 200,
                        "data", Map.of("templateId", "T1", "lineId", "L1", "packages", "[]")
                )),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("data.packages", "array", "有序动作集合", true)
                )));
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertTrue(sample.qualifyErrors().get(0).contains("类型不对"));
    }

    @Test
    void arrayPath_checksEachItem() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of(
                        "photos", List.of(
                                Map.of("type", "jpg"),
                                Map.of("type", 1)
                        )
                )),
                CatalogLookup.found(List.of(
                        new FieldDescriptionRow("photos", "array", "照片", true),
                        new FieldDescriptionRow("photos[].type", "string", "格式", true)
                )));
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertTrue(sample.qualifyErrors().stream().anyMatch(item -> item.contains("格式")));
    }

    @Test
    void missingCatalog_isUnqualified() {
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                sample(Map.of("opcode", 500202)),
                CatalogLookup.failed("找不到该指令的字段说明"));
        assertEquals(ProtocolQualifyStatus.UNQUALIFIED, sample.protocolQualify());
        assertEquals("找不到该指令的字段说明", sample.qualifyErrors().get(0));
    }

    @Test
    void alreadyUnqualified_keepsOriginalReason() {
        CollectionSample parsed = new CollectionSample(
                AccessChannelCodes.INSPECTION, "d1", "500202",
                ProtocolQualifyStatus.UNQUALIFIED, List.of("上报正文不是对象"),
                Map.of(), null, "u1", 1L);
        CollectionSample sample = FieldDescriptionQualifier.qualify(
                parsed, CatalogLookup.found(List.of(
                        new FieldDescriptionRow("opcode", "integer", "指令编码", true))));
        assertEquals(List.of("上报正文不是对象"), sample.qualifyErrors());
    }

    private static CollectionSample sample(Map<String, Object> fields) {
        return new CollectionSample(
                AccessChannelCodes.INSPECTION, "d1", "500202",
                ProtocolQualifyStatus.UNCHECKED, List.of(),
                new LinkedHashMap<>(fields), 9L, "u1", 1L);
    }
}
