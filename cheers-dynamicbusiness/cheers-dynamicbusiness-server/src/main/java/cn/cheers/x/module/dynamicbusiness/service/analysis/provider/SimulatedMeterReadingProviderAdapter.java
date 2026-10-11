package cn.cheers.x.module.dynamicbusiness.service.analysis.provider;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.analysis.AnalysisCapabilityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.analysis.AnalysisProviderDO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 本地联调使用的仪表读数模拟适配器。
 *
 * <p>负责立即返回一条带 SIMULATED 标记的统一结果，让巡检任务验证智能分析交付链。
 * 不读取媒体，不调用跑冒滴漏算法，也不得作为生产服务接入。</p>
 */
@Component
@RequiredArgsConstructor
public class SimulatedMeterReadingProviderAdapter implements AnalysisProviderAdapter {

    public static final String ADAPTER_TYPE = "SIMULATED_METER_READING";
    private static final String PROVIDER_CAPABILITY_CODE = "SIMULATED_METER_READING_V1";

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(String adapterType) {
        return ADAPTER_TYPE.equals(adapterType);
    }

    @Override
    public String configurationError(AnalysisProviderDO provider) {
        try {
            Map<String, Object> config = readConfig(provider);
            if (!Boolean.TRUE.equals(config.get("demoOnly"))) {
                return "仪表读数模拟接入必须明确配置 demoOnly=true";
            }
            if (!StringUtils.hasText(text(config.get("simulatedValue")))) {
                return "仪表读数模拟接入缺少模拟读数";
            }
            new BigDecimal(text(config.get("simulatedValue")));
            return null;
        } catch (NumberFormatException ex) {
            return "仪表读数模拟值必须是数字";
        } catch (Exception ex) {
            return "仪表读数模拟接入配置不是合法 JSON";
        }
    }

    @Override
    public ProviderSubmitResult submit(
            AnalysisProviderDO provider,
            AnalysisCapabilityDO capability,
            ProviderSubmitRequest request) {
        String configurationError = configurationError(provider);
        if (configurationError != null) {
            return ProviderSubmitResult.rejected(configurationError);
        }
        if (!PROVIDER_CAPABILITY_CODE.equals(capability.getProviderCapabilityCode())) {
            return ProviderSubmitResult.rejected("仪表读数模拟接入只接受已登记的模拟能力");
        }
        try {
            Map<String, Object> config = readConfig(provider);
            BigDecimal value = new BigDecimal(text(config.get("simulatedValue")));
            String unit = text(config.get("unit"));
            Map<String, Object> reading = new LinkedHashMap<>();
            reading.put("value", value);
            reading.put("unit", unit);
            reading.put("source", "SIMULATED");

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("verdict", "NORMAL");
            result.put("message", "本地联调：模拟仪表读数 " + value.toPlainString() + unit);
            result.put("confidence", BigDecimal.ONE);
            result.put("detections", List.of());
            result.put("readings", List.of(reading));
            result.put("simulated", true);
            return ProviderSubmitResult.completed("SIM-" + request.jobNo(), result);
        } catch (Exception ex) {
            return ProviderSubmitResult.rejected("生成仪表读数模拟结果失败");
        }
    }

    private Map<String, Object> readConfig(AnalysisProviderDO provider) throws Exception {
        if (!StringUtils.hasText(provider.getConfigJson())) {
            return Map.of();
        }
        return objectMapper.readValue(provider.getConfigJson(), new TypeReference<>() {
        });
    }

    private static String text(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }
}
