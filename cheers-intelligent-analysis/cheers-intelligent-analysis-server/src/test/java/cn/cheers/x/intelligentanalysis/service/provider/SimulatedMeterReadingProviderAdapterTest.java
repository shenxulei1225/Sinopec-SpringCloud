package cn.cheers.x.intelligentanalysis.service.provider;

import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatedMeterReadingProviderAdapterTest {

    private final SimulatedMeterReadingProviderAdapter adapter =
            new SimulatedMeterReadingProviderAdapter(new ObjectMapper());

    @Test
    void submit_returnsExplicitlyMarkedCompletedResult() {
        AnalysisProviderDO provider = provider(
                "{\"demoOnly\":true,\"simulatedValue\":42.0,\"unit\":\"MPa\"}");
        AnalysisCapabilityDO capability = new AnalysisCapabilityDO();
        capability.setProviderCapabilityCode("SIMULATED_METER_READING_V1");

        AnalysisProviderAdapter.ProviderSubmitResult result = adapter.submit(
                provider,
                capability,
                new AnalysisProviderAdapter.ProviderSubmitRequest(
                        "AJ-1", "IMAGE", "https://media.example.com/a.jpg", Map.of(), "1:step"));

        assertTrue(result.accepted());
        assertEquals("NORMAL", result.normalizedResult().get("verdict"));
        assertEquals(true, result.normalizedResult().get("simulated"));
    }

    @Test
    void configurationRequiresExplicitDemoBoundaryAndNumericValue() {
        assertEquals(
                "仪表读数模拟接入必须明确配置 demoOnly=true",
                adapter.configurationError(provider("{\"simulatedValue\":42}")));
        assertEquals(
                "仪表读数模拟值必须是数字",
                adapter.configurationError(provider(
                        "{\"demoOnly\":true,\"simulatedValue\":\"not-a-number\"}")));
        assertNull(adapter.configurationError(provider(
                "{\"demoOnly\":true,\"simulatedValue\":42}")));
    }

    private static AnalysisProviderDO provider(String configJson) {
        AnalysisProviderDO provider = new AnalysisProviderDO();
        provider.setConfigJson(configJson);
        return provider;
    }
}
