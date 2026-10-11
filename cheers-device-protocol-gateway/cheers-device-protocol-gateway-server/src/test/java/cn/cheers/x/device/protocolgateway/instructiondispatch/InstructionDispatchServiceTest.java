package cn.cheers.x.device.protocolgateway.instructiondispatch;

import cn.cheers.x.device.protocolgateway.api.mission.DeviceMissionPlan;
import cn.cheers.x.device.protocolgateway.api.mission.DispatchAction;
import cn.cheers.x.device.protocolgateway.datacollection.CatalogLookup;
import cn.cheers.x.device.protocolgateway.datacollection.FieldDescriptionRow;
import cn.cheers.x.device.protocolgateway.datacollection.ProtocolInstructionCatalog;
import cn.cheers.x.device.protocolgateway.protocol.envelope.EnvelopeJsonCodec;
import cn.cheers.x.device.protocolgateway.service.MissionDownlink;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InstructionDispatchServiceTest {

    @Test
    void missingMapping_namesProtocolConfig() {
        InstructionDispatchService service = service(Optional.empty(), takeoffPacket());
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.translate(plan(new DispatchAction(11L, Map.of()))));
        assertTrue(ex.getMessage().contains("没有指令对照"));
        assertTrue(ex.getMessage().contains("机器人对接协议"));
    }

    @Test
    void duplicateMapping_refusesToPickTheFirst() {
        List<MappingRow> rows = List.of(
                new MappingRow(11L, "robot-ws", 9L, List.of()),
                new MappingRow(11L, "robot-ws", 10L, List.of()));
        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> EntityRpcProtocolMappingCatalog.selectUnique(rows, 11L, "robot-ws"));
        assertTrue(ex.getMessage().contains("多条指令对照"));
    }

    @Test
    void oneMappingPerVersion_selectsThatInstruction() {
        Optional<MappingRow> found = EntityRpcProtocolMappingCatalog.selectUnique(List.of(
                new MappingRow(11L, "robot-ws", 9L, List.of()),
                new MappingRow(11L, "uav-ws", 8L, List.of())), 11L, "robot-ws");
        assertTrue(found.isPresent());
        assertEquals(9L, found.get().instructionId());
    }

    @Test
    void emptySlotMapping_stillTranslates() {
        InstructionDispatchService service = service(
                Optional.of(new MappingRow(11L, "robot-ws", 9L, List.of())),
                takeoffPacket());
        MissionDownlink downlink = service.translate(plan(new DispatchAction(11L, Map.of())));
        assertEquals(1, downlink.pieces().size());
        assertTrue(downlink.pieces().get(0).commandPackage() != null);
    }

    @Test
    void commandWithoutRequest_goesOutAsItself() {
        InstructionDispatchService service = service(
                Optional.of(new MappingRow(53L, "robot-ws", 46L, List.of())),
                statusPacket());
        MissionDownlink downlink = service.translate(plan(new DispatchAction(53L, Map.of("action_duration", 1))));
        assertEquals(1, downlink.pieces().size());
        assertTrue(downlink.pieces().get(0).commandPackage() == null);
        MissionDownlink.DirectCommand direct = downlink.pieces().get(0).direct();
        assertEquals(500101, direct.opcode());
        assertTrue(direct.wireJson().contains("\"deviceId\":\"robot-1\""));
        assertTrue(direct.wireJson().contains("\"opcode\":500101"));
    }

    @Test
    void photoTakesPlaceFromTheMoveAlreadyFilled() {
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                if (actionId == 41L) {
                    return Optional.of(new MappingRow(41L, "uav-ws", 26L, List.of(
                            new SlotMapping("place", "FLD-PNT-002", "request.pointId"),
                            new SlotMapping("place", "FLD-LOC-014.longitude", "request.lng"),
                            new SlotMapping("place", "FLD-LOC-014.latitude", "request.lat"))));
                }
                if (actionId == 9L) {
                    return Optional.of(new MappingRow(9L, "uav-ws", 32L, List.of(
                            new SlotMapping("action_duration", "", "request.time"))));
                }
                return Optional.of(new MappingRow(12L, "uav-ws", 32L, List.of(
                        new SlotMapping("shot_count", "", "request.number"))));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                if (instructionId == 26L) {
                    return new InstructionTemplate(200102, movePacket());
                }
                return new InstructionTemplate(200301, photoPacket());
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        Map<String, Object> place = new LinkedHashMap<>();
        place.put("FLD-PNT-002", "GDXJD-1001-1001-001");
        Map<String, Object> loc = new LinkedHashMap<>();
        loc.put("longitude", "117.2382841369663");
        loc.put("latitude", "34.3035108957959");
        place.put("FLD-LOC-014", loc);
        DeviceMissionPlan plan = new DeviceMissionPlan("uav-ws", "uav-1", "1", "tpl", List.of(
                new DispatchAction(41L, Map.of("place", place)),
                new DispatchAction(9L, Map.of("action_duration", 2)),
                new DispatchAction(12L, Map.of("shot_count", "4"))));
        String packages = service.translate(plan).pieces().get(0).commandPackage().packagesJson();
        assertEquals(2, countOf(packages, "\"pointId\":\"GDXJD-1001-1001-001\""));
        assertEquals(2, countOf(packages, "\"lat\":\"34.3035108957959\""));
        assertEquals(2, countOf(packages, "\"lng\":\"117.2382841369663\""));
        assertTrue(packages.contains("\"number\":\"4\""));
        assertTrue(packages.contains("\"height\":\"\""));
    }

    @Test
    void robotMoveThenPhoto_fillsOnePackage() throws Exception {
        String placeSlot = "F-e49f76bdca3e4e1393e8e2f1cc0e7867";
        String parkingSlot = "F-2b8fa8bda0b94663979c112af85325dc";
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                if (actionId == 41L) {
                    return Optional.of(new MappingRow(41L, "robot-ws", 26L, List.of(
                            new SlotMapping(placeSlot, "FLD-PNT-002", "request.pointId", "USER"),
                            new SlotMapping(placeSlot, "FLD-LOC-014.latitude", "request.lat", "USER"),
                            new SlotMapping(placeSlot, "FLD-LOC-014.longitude", "request.lng", "USER"),
                            new SlotMapping(placeSlot, "FLD-LOC-014.height", "request.height", "USER"))));
                }
                return Optional.of(new MappingRow(12L, "robot-ws", 32L, List.of(
                        new SlotMapping("yaw", "", "request.angleLevel", "USER"),
                        new SlotMapping("pitch", "", "request.angleVertical", "USER"),
                        new SlotMapping("focal_length", "", "request.focuses", "USER"),
                        new SlotMapping("shot_count", "", "request.number", "USER"),
                        new SlotMapping(parkingSlot, "", "request.parking", "USER"),
                        new SlotMapping("action_duration", "", "request.time", "USER"))));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                if (instructionId == 26L) {
                    return new InstructionTemplate(200102, robotMovePacket());
                }
                return new InstructionTemplate(200301, robotPhotoPacket());
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        Map<String, Object> place = new LinkedHashMap<>();
        place.put("FLD-PNT-002", "16de65073e804a4898305c5886246467");
        Map<String, Object> loc = new LinkedHashMap<>();
        loc.put("latitude", "39.1277217676");
        loc.put("longitude", "117.0190597072");
        loc.put("height", "17.874");
        place.put("FLD-LOC-014", loc);
        Map<String, Object> photo = new LinkedHashMap<>();
        photo.put("yaw", "10");
        photo.put("pitch", "10");
        photo.put("focal_length", "2");
        photo.put("shot_count", "1");
        photo.put(parkingSlot, "true");
        photo.put("action_duration", "20");
        String packages = service.translate(new DeviceMissionPlan(
                "robot-ws", "robot-1", "88", "1001234", List.of(
                        new DispatchAction(41L, Map.of(placeSlot, place)),
                        new DispatchAction(12L, photo)))).pieces().get(0).commandPackage().packagesJson();
        JsonNode lines = new ObjectMapper().readTree(packages);
        assertEquals(2, lines.size());
        assertEquals(200102, lines.get(0).get("opcode").asInt());
        assertEquals(0, lines.get(0).get("sequence").asInt());
        assertEquals("16de65073e804a4898305c5886246467", lines.get(0).get("request").get("pointId").asText());
        assertEquals("39.1277217676", lines.get(0).get("request").get("lat").asText());
        assertEquals("117.0190597072", lines.get(0).get("request").get("lng").asText());
        assertEquals("17.874", lines.get(0).get("request").get("height").asText());
        assertEquals(200301, lines.get(1).get("opcode").asInt());
        assertEquals(1, lines.get(1).get("sequence").asInt());
        JsonNode shot = lines.get(1).get("request");
        assertEquals("16de65073e804a4898305c5886246467", shot.get("pointId").asText());
        assertEquals("39.1277217676", shot.get("lat").asText());
        assertEquals("117.0190597072", shot.get("lng").asText());
        assertEquals("", shot.get("height").asText());
        assertEquals("10", shot.get("angleLevel").asText());
        assertEquals("10", shot.get("angleVertical").asText());
        assertEquals("2", shot.get("focuses").asText());
        assertEquals("1", shot.get("number").asText());
        assertEquals("true", shot.get("parking").asText());
        assertEquals("20", shot.get("time").asText());
    }

    @Test
    void cloudReturnHome_isOfficialServiceNotGroundStationPackage() throws Exception {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("method", "return_home");
        outbound.put("data", new LinkedHashMap<>());
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return Optional.of(new MappingRow(11L, protocolVersion, 9L, List.of()));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                return new InstructionTemplate(0, outbound);
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        DeviceMissionPlan plan = new DeviceMissionPlan("dji-dock-1.16.1", "dock-sn-1", "88", "tpl",
                List.of(new DispatchAction(11L, Map.of())));
        MissionDownlink downlink = service.translate(plan);
        assertEquals(1, downlink.pieces().size());
        assertTrue(downlink.pieces().get(0).commandPackage() == null);
        MissionDownlink.DirectCommand direct = downlink.pieces().get(0).direct();
        assertEquals(0, direct.opcode());
        assertEquals("thing/product/dock-sn-1/services", direct.mqttTopic());
        assertTrue(direct.cloudService());
        com.fasterxml.jackson.databind.JsonNode wire = new ObjectMapper().readTree(direct.wireJson());
        assertEquals("return_home", wire.get("method").asText());
        assertTrue(wire.get("data").isObject());
        assertTrue(wire.get("tid").asText().length() > 0);
        assertTrue(wire.get("bid").asText().length() > 0);
        assertTrue(wire.get("timestamp").isNumber());
        assertTrue(!direct.wireJson().contains("500104"));
        assertTrue(!direct.wireJson().contains("\"opcode\""));
    }

    @Test
    void cloudCredentialReply_usesTheInstructionTopic() throws Exception {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("method", "storage_config_get");
        outbound.put("data", new LinkedHashMap<>());
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return Optional.of(new MappingRow(21L, protocolVersion, 19L, List.of()));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                return new InstructionTemplate(
                        0,
                        outbound,
                        "mqtt",
                        "thing/product/{gateway_sn}/requests_reply");
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        DeviceMissionPlan plan = new DeviceMissionPlan("dji-dock-1.16.1", "dock-sn-1", "88", "tpl",
                List.of(new DispatchAction(21L, Map.of())));
        MissionDownlink.DirectCommand direct = service.translate(plan).pieces().get(0).direct();
        assertEquals("thing/product/dock-sn-1/requests_reply", direct.mqttTopic());
        assertEquals("storage_config_get", new ObjectMapper().readTree(direct.wireJson()).get("method").asText());
    }

    @Test
    void httpsInstruction_isNotPublishedAsMqttService() {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("httpMethod", "GET");
        outbound.put("path", "/wayline/api/v1/workspaces/{workspace_id}/waylines");
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return Optional.of(new MappingRow(22L, protocolVersion, 20L, List.of()));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                return new InstructionTemplate(0, outbound, "https", "");
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        DeviceMissionPlan plan = new DeviceMissionPlan("dji-pilot-1.16.1", "rc-sn-1", "88", "tpl",
                List.of(new DispatchAction(22L, Map.of())));
        IllegalArgumentException error = org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class, () -> service.translate(plan));
        assertTrue(error.getMessage().contains("HTTPS"));
        assertTrue(error.getMessage().contains("services"));
    }

    @Test
    void statusThenMove_keepsPackageSequence() {
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                if (actionId == 53L) {
                    return Optional.of(new MappingRow(53L, "uav-ws", 6L, List.of()));
                }
                return Optional.of(new MappingRow(41L, "uav-ws", 26L, List.of()));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                if (instructionId == 6L) {
                    return new InstructionTemplate(500101, statusPacket());
                }
                return new InstructionTemplate(200102, takeoffPacket());
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        DeviceMissionPlan plan = new DeviceMissionPlan("uav-ws", "uav-1", "1", "tpl", List.of(
                new DispatchAction(53L, Map.of()),
                new DispatchAction(41L, Map.of())));
        MissionDownlink downlink = service.translate(plan);
        assertEquals(2, downlink.pieces().size());
        assertEquals(500101, downlink.pieces().get(0).direct().opcode());
        String packages = downlink.pieces().get(1).commandPackage().packagesJson();
        assertTrue(packages.contains("\"sequence\":1"));
    }

    @Test
    void djiWaylineActions_becomeOneFile_whileDongguStillNeedsAMapping() throws Exception {
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                if (actionId == 70L && "dji-dock-1.16.1".equals(protocolVersion)) {
                    return Optional.of(new MappingRow(70L, "dji-dock-1.16.1", 501L,
                            List.of(new SlotMapping("hoverTime", "", "params.hoverTime"))));
                }
                return Optional.empty();
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                Map<String, Object> params = new LinkedHashMap<>();
                params.put("hoverTime", "");
                Map<String, Object> outbound = new LinkedHashMap<>();
                outbound.put("actionActuatorFunc", "hover");
                outbound.put("params", params);
                return new InstructionTemplate(0, outbound);
            }
        };
        byte[][] written = new byte[1][];
        WaylineFileStore store = (executionId, kmz, fingerprint) -> {
            written[0] = kmz;
            return new WaylineFileStore.Stored(executionId + "-" + fingerprint, "http://dock.local/" + fingerprint + ".kmz", fingerprint);
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()), store);
        DispatchAction battery = new DispatchAction(64L, Map.of(), "action-c6d414f4f24c442eb5947dccdf802b37");
        DispatchAction point = new DispatchAction(65L, Map.of(
                "latitude", 39.12,
                "longitude", 117.01,
                "height", 30,
                "max_speed", 5,
                "security_takeoff_height", 20,
                "rth_altitude", 80,
                "finish_action", "goHome"
        ), "action-065340437bcb46788b7dc261fab81c63");
        DispatchAction hover = new DispatchAction(70L, Map.of("hoverTime", 3), "hover");
        MissionDownlink downlink = service.translate(new DeviceMissionPlan(
                "dji-dock-1.16.1", "dock-sn-1", "88", "tpl", List.of(battery, point, hover)));
        assertEquals(2, downlink.pieces().size());
        assertTrue(downlink.waylineFileId().startsWith("88-"));
        JsonNode prepare = new ObjectMapper().readTree(downlink.pieces().get(0).direct().wireJson());
        assertEquals("flighttask_prepare", prepare.get("method").asText());
        assertEquals(downlink.waylineFingerprint(), prepare.get("data").get("file").get("fingerprint").asText());
        assertTrue(prepare.get("data").get("file").get("url").asText().startsWith("http://dock.local/"));
        JsonNode execute = new ObjectMapper().readTree(downlink.pieces().get(1).direct().wireJson());
        assertEquals("flighttask_execute", execute.get("method").asText());
        String kmzText = zipText(written[0]);
        assertTrue(kmzText.contains("<wpml:hoverTime>3</wpml:hoverTime>"));
        assertTrue(!kmzText.contains("action_duration"));

        IllegalArgumentException donggu = assertThrows(IllegalArgumentException.class, () -> service.translate(
                new DeviceMissionPlan("uav-ws", "uav-1", "88", "tpl", List.of(point, hover))));
        assertTrue(donggu.getMessage().contains("没有指令对照"));
    }

    @Test
    void pilotWayline_requiresPilotLibraryInsteadOfDockFlightTaskMethods() {
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return Optional.empty();
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                throw new AssertionError("Pilot 航线不应进入机场指令填包");
            }
        };
        InstructionDispatchService service = new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()));
        DispatchAction point = new DispatchAction(65L, Map.of(
                "latitude", 39.12,
                "longitude", 117.01,
                "height", 30
        ), "action-065340437bcb46788b7dc261fab81c63");
        DispatchAction hover = new DispatchAction(70L, Map.of("hoverTime", 3), "hover");

        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> service.translate(
                new DeviceMissionPlan(
                        "dji-pilot-1.16.1", "pilot-sn-1", "88", "tpl", List.of(point, hover))));

        assertTrue(error.getMessage().contains("Pilot 航线库"));
        assertTrue(error.getMessage().contains("flighttask_prepare"));
    }

    @Test
    void waylinePoint_readsLongitudeFromSelectedLocationCoordinate() throws Exception {
        Map<String, Object> coordinate = new LinkedHashMap<>();
        coordinate.put("longitude", 117.2382841369663);
        coordinate.put("latitude", 39.12);
        coordinate.put("height", 30);
        Map<String, Object> place = new LinkedHashMap<>();
        place.put("id", 9001L);
        place.put("name", "停靠点");
        place.put("FLD-LOC-014", coordinate);
        Map<String, Object> params = new LinkedHashMap<>();
        params.put("F-e49f76bdca3e4e1393e8e2f1cc0e7867", place);
        params.put("max_speed", 5);
        params.put("security_takeoff_height", 20);
        params.put("rth_altitude", 80);
        params.put("finish_action", "goHome");
        WaylineKmzBuilder.Built built = WaylineKmzBuilder.build(List.of(
                new DispatchAction(65L, params, WaylineKmzBuilder.WAYPOINT_ANCHOR),
                new DispatchAction(70L, Map.of("hoverTime", 3), "hover")));
        assertTrue(zipText(built.kmz()).contains("117.2382841369663,39.12"));
    }

    private static String zipText(byte[] kmz) throws Exception {
        StringBuilder text = new StringBuilder();
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(kmz))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                text.append(entry.getName()).append('\n');
                text.append(new String(zip.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        return text.toString();
    }

    private static int countOf(String text, String piece) {
        int count = 0;
        int from = 0;
        while (true) {
            int at = text.indexOf(piece, from);
            if (at < 0) {
                return count;
            }
            count++;
            from = at + piece.length();
        }
    }

    @Test
    void optionalBlank_warnsAndKeepsSequence() {
        InstructionDispatchService service = moveService();
        MissionDownlink downlink = service.translate(plan(moveAction(true)));
        List<Map<String, Object>> packages = new EnvelopeJsonCodec(new ObjectMapper())
                .decodePackagesJson(downlink.pieces().get(0).commandPackage().packagesJson());

        assertEquals(0, ((Number) packages.get(0).get("sequence")).intValue());
        assertEquals(200102, ((Number) packages.get(0).get("opcode")).intValue());
        @SuppressWarnings("unchecked")
        Map<String, Object> request = (Map<String, Object>) packages.get(0).get("request");
        assertEquals("GDXJD-1", request.get("pointId"));
        assertEquals("", request.get("height"));
        assertTrue(downlink.notices().stream().anyMatch(line -> line.contains("没有值：地面高度")));
        assertTrue(downlink.notices().stream().anyMatch(line -> line.contains("没有值：朝向")));
        assertTrue(downlink.notices().stream().anyMatch(line -> line.contains("序号 0")));
    }

    @Test
    void requiredBlank_listsEveryGapAndDoesNotEmit() {
        InstructionDispatchService service = moveService();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.translate(plan(moveAction(false))));

        assertTrue(ex.getMessage().contains("缺必填：点位"));
        assertTrue(ex.getMessage().contains("没有值：地面高度"));
        assertTrue(ex.getMessage().contains("序号 0"));
    }

    private static InstructionDispatchService moveService() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", "");
        request.put("lat", "");
        request.put("lng", "");
        request.put("height", "");
        request.put("heading", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 200102);
        outbound.put("request", request);
        List<SlotMapping> slots = List.of(
                new SlotMapping("place", "FLD-PNT-002", "request.pointId"),
                new SlotMapping("place", "FLD-LOC-014.latitude", "request.lat"),
                new SlotMapping("place", "FLD-LOC-014.longitude", "request.lng"),
                new SlotMapping("place", "FLD-LOC-014.height", "request.height"));
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return Optional.of(new MappingRow(10L, "robot-ws", 20L, slots));
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                return new InstructionTemplate(200102, outbound);
            }
        };
        ProtocolInstructionCatalog book = (opcode, version) -> CatalogLookup.found(List.of(
                new FieldDescriptionRow("request.pointId", "string", "点位", true),
                new FieldDescriptionRow("request.lat", "string", "纬度", false),
                new FieldDescriptionRow("request.lng", "string", "经度", false),
                new FieldDescriptionRow("request.height", "string", "地面高度", false),
                new FieldDescriptionRow("request.heading", "string", "朝向", false)));
        return new InstructionDispatchService(
                catalog, new EnvelopeJsonCodec(new ObjectMapper()),
                (executionId, kmz, fingerprint) -> {
                    throw new IllegalStateException("航线文件没有可给机场下载的地址");
                },
                DispatchParamResolver.noop(),
                book);
    }

    private static DispatchAction moveAction(boolean withPoint) {
        Map<String, Object> place = new LinkedHashMap<>();
        if (withPoint) {
            place.put("FLD-PNT-002", "GDXJD-1");
            Map<String, Object> loc = new LinkedHashMap<>();
            loc.put("latitude", "34.3");
            loc.put("longitude", "117.2");
            place.put("FLD-LOC-014", loc);
        }
        return new DispatchAction(10L, Map.of("place", place), "act-robot-arrive");
    }

    private static InstructionDispatchService service(Optional<MappingRow> row, Map<String, Object> outbound) {
        ProtocolMappingCatalog catalog = new ProtocolMappingCatalog() {
            @Override
            public Optional<MappingRow> find(long actionId, String protocolVersion) {
                return row;
            }

            @Override
            public InstructionTemplate loadInstruction(long instructionId) {
                int opcode = outbound.get("opcode") instanceof Number number ? number.intValue() : 200101;
                return new InstructionTemplate(opcode, outbound);
            }
        };
        return new InstructionDispatchService(catalog, new EnvelopeJsonCodec(new ObjectMapper()));
    }

    private static DeviceMissionPlan plan(DispatchAction action) {
        return new DeviceMissionPlan("robot-ws", "robot-1", "88", "tpl", List.of(action));
    }

    private static Map<String, Object> takeoffPacket() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("lng", "");
        request.put("lat", "");
        request.put("height", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 200101);
        outbound.put("request", request);
        return outbound;
    }

    private static Map<String, Object> movePacket() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", "");
        request.put("lng", "");
        request.put("lat", "");
        request.put("height", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 200102);
        outbound.put("request", request);
        return outbound;
    }

    private static Map<String, Object> photoPacket() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", "");
        request.put("lng", "");
        request.put("lat", "");
        request.put("height", "");
        request.put("number", "");
        request.put("time", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("opcode", 200301);
        outbound.put("request", request);
        return outbound;
    }

    private static Map<String, Object> robotMovePacket() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", "");
        request.put("lat", "");
        request.put("lng", "");
        request.put("height", "");
        request.put("heading", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("sequence", 0);
        outbound.put("opcode", 200102);
        outbound.put("request", request);
        return outbound;
    }

    private static Map<String, Object> robotPhotoPacket() {
        Map<String, Object> request = new LinkedHashMap<>();
        request.put("pointId", "");
        request.put("lat", "");
        request.put("lng", "");
        request.put("height", "");
        request.put("heading", "");
        request.put("angleLevel", "");
        request.put("angleVertical", "");
        request.put("focuses", "");
        request.put("number", "");
        request.put("parking", "");
        request.put("time", "");
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("sequence", 0);
        outbound.put("opcode", 200301);
        outbound.put("request", request);
        return outbound;
    }

    private static Map<String, Object> statusPacket() {
        Map<String, Object> outbound = new LinkedHashMap<>();
        outbound.put("msgId", "");
        outbound.put("opcode", 500101);
        outbound.put("deviceId", "");
        return outbound;
    }
}
