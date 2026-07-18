package cn.cheers.x.module.platform.topology.exchange;

import cn.cheers.x.module.platform.contract.dto.network.PathEdgeDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNodeDTO;
import cn.cheers.x.module.platform.contract.dto.topology.TopologyPointDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.contract.enums.NetworkLayer;
import cn.cheers.x.module.platform.contract.enums.NodeType;
import cn.cheers.x.module.platform.exchange.dto.GeoJsonImportResultDTO;
import cn.cheers.x.module.platform.topology.service.PathNetworkService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.module.platform.topology.enums.ErrorCodeConstants.GEOJSON_INVALID;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Service
public class GeoJsonExchangeServiceImpl implements GeoJsonExchangeService {

    private static final String FEATURE_COLLECTION = "FeatureCollection";
    private static final String FEATURE = "Feature";
    private static final String POINT = "Point";
    private static final String LINE_STRING = "LineString";
    private static final String PROP_TYPE = "type";
    private static final String TYPE_NODE = "node";
    private static final String TYPE_EDGE = "edge";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private PathNetworkService pathNetworkService;

    @Override
    public JsonNode exportDraft(Long facilityId, NetworkKind networkKind) {
        PathNetworkDTO network = pathNetworkService.getDraft(facilityId, networkKind);
        return toFeatureCollection(network);
    }

    @Override
    public JsonNode exportByNetworkRef(String networkRef) {
        PathNetworkDTO network = pathNetworkService.getNetwork(networkRef);
        return toFeatureCollection(network);
    }

    @Override
    public GeoJsonImportResultDTO importGeoJson(Long facilityId, NetworkKind networkKind, JsonNode geoJson) {
        PathNetworkDTO draft = fromFeatureCollection(geoJson, facilityId, networkKind);
        PathNetworkDTO saved = pathNetworkService.saveDraft(draft);
        return GeoJsonImportResultDTO.builder()
                .networkRef(saved.getNetworkRef())
                .nodeCount(size(saved.getNodes()))
                .edgeCount(size(saved.getEdges()))
                .build();
    }

    JsonNode toFeatureCollection(PathNetworkDTO network) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("type", FEATURE_COLLECTION);
        ArrayNode features = root.putArray("features");
        if (network.getNodes() != null) {
            for (PathNodeDTO node : network.getNodes()) {
                if (node != null) {
                    features.add(nodeFeature(node));
                }
            }
        }
        if (network.getEdges() != null) {
            for (PathEdgeDTO edge : network.getEdges()) {
                if (edge != null) {
                    features.add(edgeFeature(edge));
                }
            }
        }
        return root;
    }

    PathNetworkDTO fromFeatureCollection(JsonNode geoJson, Long facilityId, NetworkKind networkKind) {
        JsonNode root = parseRoot(geoJson);
        if (!FEATURE_COLLECTION.equals(textValue(root.get("type")))) {
            throw exception(GEOJSON_INVALID);
        }
        JsonNode features = root.get("features");
        if (features == null || !features.isArray()) {
            throw exception(GEOJSON_INVALID);
        }
        List<PathNodeDTO> nodes = new ArrayList<>();
        List<PathEdgeDTO> edges = new ArrayList<>();
        for (JsonNode feature : features) {
            if (feature == null || !feature.isObject()) {
                continue;
            }
            JsonNode properties = feature.get("properties");
            if (properties == null || !properties.isObject()) {
                throw exception(GEOJSON_INVALID);
            }
            String featureType = textValue(properties.get(PROP_TYPE));
            if (TYPE_NODE.equals(featureType)) {
                nodes.add(parseNodeFeature(feature, properties));
            } else if (TYPE_EDGE.equals(featureType)) {
                edges.add(parseEdgeFeature(feature, properties));
            }
        }
        return PathNetworkDTO.builder()
                .facilityId(facilityId)
                .networkKind(networkKind)
                .nodes(nodes)
                .edges(edges)
                .build();
    }

    private ObjectNode nodeFeature(PathNodeDTO node) {
        ObjectNode feature = objectMapper.createObjectNode();
        feature.put("type", FEATURE);
        ObjectNode properties = feature.putObject("properties");
        properties.put(PROP_TYPE, TYPE_NODE);
        properties.put("nodeId", node.getNodeId());
        if (node.getNodeType() != null) {
            properties.put("nodeType", node.getNodeType().name());
        }
        if (node.getLayer() != null) {
            properties.put("layer", node.getLayer().name());
        }
        if (node.getZoneId() != null) {
            properties.put("zoneId", node.getZoneId());
        }
        if (StringUtils.hasText(node.getDisplayName())) {
            properties.put("displayName", node.getDisplayName());
        }
        if (node.getPosition() != null) {
            feature.set("geometry", pointGeometry(node.getPosition()));
        }
        return feature;
    }

    private ObjectNode edgeFeature(PathEdgeDTO edge) {
        ObjectNode feature = objectMapper.createObjectNode();
        feature.put("type", FEATURE);
        ObjectNode properties = feature.putObject("properties");
        properties.put(PROP_TYPE, TYPE_EDGE);
        properties.put("edgeId", edge.getEdgeId());
        properties.put("fromNodeId", edge.getFromNodeId());
        properties.put("toNodeId", edge.getToNodeId());
        if (edge.getLayer() != null) {
            properties.put("layer", edge.getLayer().name());
        }
        if (edge.getWaypoints() != null && !edge.getWaypoints().isEmpty()) {
            feature.set("geometry", lineStringGeometry(edge.getWaypoints()));
        }
        return feature;
    }

    private ObjectNode pointGeometry(TopologyPointDTO position) {
        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", POINT);
        ArrayNode coordinates = geometry.putArray("coordinates");
        coordinates.add(position.getX() != null ? position.getX() : 0.0);
        coordinates.add(position.getY() != null ? position.getY() : 0.0);
        if (position.getZ() != null) {
            coordinates.add(position.getZ());
        }
        return geometry;
    }

    private ObjectNode lineStringGeometry(List<TopologyPointDTO> waypoints) {
        ObjectNode geometry = objectMapper.createObjectNode();
        geometry.put("type", LINE_STRING);
        ArrayNode coordinates = geometry.putArray("coordinates");
        for (TopologyPointDTO waypoint : waypoints) {
            ArrayNode point = coordinates.addArray();
            point.add(waypoint.getX() != null ? waypoint.getX() : 0.0);
            point.add(waypoint.getY() != null ? waypoint.getY() : 0.0);
            if (waypoint.getZ() != null) {
                point.add(waypoint.getZ());
            }
        }
        return geometry;
    }

    private PathNodeDTO parseNodeFeature(JsonNode feature, JsonNode properties) {
        String nodeId = requiredText(properties, "nodeId");
        PathNodeDTO.PathNodeDTOBuilder builder = PathNodeDTO.builder()
                .nodeId(nodeId)
                .nodeType(parseEnum(properties.get("nodeType"), NodeType.class))
                .layer(parseEnum(properties.get("layer"), NetworkLayer.class))
                .zoneId(longValue(properties.get("zoneId")))
                .displayName(textValue(properties.get("displayName")));
        JsonNode geometry = feature.get("geometry");
        if (geometry != null && geometry.isObject()) {
            builder.position(parsePointGeometry(geometry));
        }
        return builder.build();
    }

    private PathEdgeDTO parseEdgeFeature(JsonNode feature, JsonNode properties) {
        PathEdgeDTO.PathEdgeDTOBuilder builder = PathEdgeDTO.builder()
                .edgeId(requiredText(properties, "edgeId"))
                .fromNodeId(requiredText(properties, "fromNodeId"))
                .toNodeId(requiredText(properties, "toNodeId"))
                .layer(parseEnum(properties.get("layer"), NetworkLayer.class));
        JsonNode geometry = feature.get("geometry");
        if (geometry != null && geometry.isObject()
                && LINE_STRING.equals(textValue(geometry.get("type")))) {
            builder.waypoints(parseLineStringGeometry(geometry));
        }
        return builder.build();
    }

    private TopologyPointDTO parsePointGeometry(JsonNode geometry) {
        if (!POINT.equals(textValue(geometry.get("type")))) {
            throw exception(GEOJSON_INVALID);
        }
        JsonNode coordinates = geometry.get("coordinates");
        if (coordinates == null || !coordinates.isArray() || coordinates.size() < 2) {
            throw exception(GEOJSON_INVALID);
        }
        TopologyPointDTO.TopologyPointDTOBuilder builder = TopologyPointDTO.builder()
                .x(coordinates.get(0).asDouble())
                .y(coordinates.get(1).asDouble());
        if (coordinates.size() >= 3) {
            builder.z(coordinates.get(2).asDouble());
        }
        return builder.build();
    }

    private List<TopologyPointDTO> parseLineStringGeometry(JsonNode geometry) {
        JsonNode coordinates = geometry.get("coordinates");
        if (coordinates == null || !coordinates.isArray()) {
            throw exception(GEOJSON_INVALID);
        }
        List<TopologyPointDTO> waypoints = new ArrayList<>();
        for (JsonNode point : coordinates) {
            if (point == null || !point.isArray() || point.size() < 2) {
                throw exception(GEOJSON_INVALID);
            }
            TopologyPointDTO.TopologyPointDTOBuilder builder = TopologyPointDTO.builder()
                    .x(point.get(0).asDouble())
                    .y(point.get(1).asDouble());
            if (point.size() >= 3) {
                builder.z(point.get(2).asDouble());
            }
            waypoints.add(builder.build());
        }
        return waypoints;
    }

    private JsonNode parseRoot(JsonNode geoJson) {
        if (geoJson == null) {
            throw exception(GEOJSON_INVALID);
        }
        if (geoJson.isTextual()) {
            try {
                return objectMapper.readTree(geoJson.asText());
            } catch (Exception ignored) {
                throw exception(GEOJSON_INVALID);
            }
        }
        if (geoJson.isObject()) {
            return geoJson;
        }
        throw exception(GEOJSON_INVALID);
    }

    private static String requiredText(JsonNode node, String field) {
        String value = textValue(node.get(field));
        if (!StringUtils.hasText(value)) {
            throw exception(GEOJSON_INVALID);
        }
        return value;
    }

    private static String textValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asText();
    }

    private static Long longValue(JsonNode node) {
        if (node == null || node.isNull()) {
            return null;
        }
        return node.asLong();
    }

    private static <E extends Enum<E>> E parseEnum(JsonNode node, Class<E> enumType) {
        String value = textValue(node);
        if (!StringUtils.hasText(value)) {
            return null;
        }
        try {
            return Enum.valueOf(enumType, value.toUpperCase());
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private static int size(List<?> values) {
        return values != null ? values.size() : 0;
    }
}
