package cn.cheers.x.module.dynamicbusiness.service.reference.provider;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.reference.ReferenceProviderDO;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.REF_PROVIDER_NOT_SUPPORTED;

@Component
public class ReferenceProviderExecutorRegistry {

    private final List<ReferenceProviderExecutor> executors;

    public ReferenceProviderExecutorRegistry(List<ReferenceProviderExecutor> executors) {
        this.executors = executors;
    }

    public ReferenceProviderExecutor getRequiredExecutor(ReferenceProviderDO provider) {
        return executors.stream()
                .filter(executor -> executor.supports(provider))
                .findFirst()
                .orElseThrow(() -> ServiceExceptionUtil.exception(REF_PROVIDER_NOT_SUPPORTED, provider.getProviderCode()));
    }
}
