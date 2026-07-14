package cn.cheers.x.module.platform.topology.exchange;

import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import cn.cheers.x.module.platform.contract.enums.NetworkKind;
import cn.cheers.x.module.platform.exchange.dto.GeoJsonImportResultDTO;
import com.fasterxml.jackson.databind.JsonNode;

public interface GeoJsonExchangeService {

    JsonNode exportDraft(Long facilityId, NetworkKind networkKind);

    JsonNode exportByNetworkRef(String networkRef);

    GeoJsonImportResultDTO importGeoJson(Long facilityId, NetworkKind networkKind, JsonNode geoJson);
}
