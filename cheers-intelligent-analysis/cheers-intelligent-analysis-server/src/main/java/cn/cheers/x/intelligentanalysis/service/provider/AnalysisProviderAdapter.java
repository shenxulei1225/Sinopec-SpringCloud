package cn.cheers.x.intelligentanalysis.service.provider;

import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;

import java.util.Map;

/**
 * 厂商分析服务适配边界。
 *
 * <p>能力目录把稳定平台能力解析为厂商配置后才进入这里。适配器不得读取动作名称或设备协议。</p>
 */
public interface AnalysisProviderAdapter {

    boolean supports(String adapterType);

    /**
     * 检查该接入是否具备受理任务所需的全部配置。
     *
     * @return 配置完整时返回 null，否则返回管理员可处理的缺口说明
     */
    String configurationError(AnalysisProviderDO provider);

    ProviderSubmitResult submit(
            AnalysisProviderDO provider,
            AnalysisCapabilityDO capability,
            ProviderSubmitRequest request);

    record ProviderSubmitRequest(
            String jobNo,
            String inputType,
            String inputRef,
            Map<String, Object> parameters,
            String businessRefId
    ) {
    }

    /**
     * 服务受理结果。
     *
     * <p>异步服务只返回 providerJobId，之后由回调交付结果。同步服务可以同时返回
     * normalizedResult，由分析任务服务在本次提交内完成任务和业务交付。</p>
     */
    record ProviderSubmitResult(
            boolean accepted,
            String providerJobId,
            String failureMessage,
            Map<String, Object> normalizedResult) {

        public static ProviderSubmitResult accepted(String providerJobId) {
            return new ProviderSubmitResult(true, providerJobId, null, null);
        }

        public static ProviderSubmitResult completed(
                String providerJobId, Map<String, Object> normalizedResult) {
            return new ProviderSubmitResult(true, providerJobId, null, Map.copyOf(normalizedResult));
        }

        public static ProviderSubmitResult rejected(String message) {
            return new ProviderSubmitResult(false, null, message, null);
        }
    }
}
