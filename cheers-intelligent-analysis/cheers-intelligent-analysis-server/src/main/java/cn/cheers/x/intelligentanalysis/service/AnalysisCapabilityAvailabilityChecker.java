package cn.cheers.x.intelligentanalysis.service;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisCapabilityDO;
import cn.cheers.x.intelligentanalysis.dal.dataobject.AnalysisProviderDO;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisCapabilityMapper;
import cn.cheers.x.intelligentanalysis.dal.mysql.AnalysisProviderMapper;
import cn.cheers.x.intelligentanalysis.service.provider.AnalysisProviderAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 校验动作选中的智能分析能力当前是否可用。
 *
 * <p>只读取能力目录、服务接入和适配器配置，不依赖分析任务服务或任务执行回写。
 * 动作保存与分析任务提交可以复用本校验，禁止为校验能力可用性引入执行链循环依赖。</p>
 */
@Component
@RequiredArgsConstructor
public class AnalysisCapabilityAvailabilityChecker {

    private final AnalysisCapabilityMapper capabilityMapper;
    private final AnalysisProviderMapper providerMapper;
    private final List<AnalysisProviderAdapter> providerAdapters;

    public void assertAvailable(Long capabilityId) {
        if (capabilityId == null) {
            throw new ServiceException(400, "必须选择分析能力");
        }
        AnalysisCapabilityDO capability = capabilityMapper.selectById(capabilityId);
        if (capability == null) {
            throw new ServiceException(404, "没有这项分析能力");
        }
        AnalysisProviderDO provider = providerMapper.selectById(capability.getProviderId());
        if (!Boolean.TRUE.equals(capability.getEnabled())
                || provider == null
                || !Boolean.TRUE.equals(provider.getEnabled())
                || configurationError(provider) != null) {
            throw new ServiceException(400, "所选分析能力当前不可用");
        }
    }

    public String configurationError(AnalysisProviderDO provider) {
        if (provider == null) {
            return "服务接入不存在";
        }
        AnalysisProviderAdapter adapter = providerAdapters.stream()
                .filter(candidate -> candidate.supports(provider.getAdapterType()))
                .findFirst()
                .orElse(null);
        return adapter == null
                ? "没有对应的分析服务适配器"
                : adapter.configurationError(provider);
    }
}
