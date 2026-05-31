package cn.cheers.x.module.dynamicbusiness.service.capability;

/**
 * 实例能力注册表重建服务
 */
public interface CapabilityRegistryRebuildService {

    void rebuildModelListCapability(String businessTypeCode);

    void rebuildEntityCapability(Long modelId);

    void rebuildAfterModelFieldChange(Long modelId);

    void rebuildAllForBusinessType(String businessTypeCode);

    /**
     * 重建单个 System 固定资源能力（instanceKey = system:{resourceCode}）。
     */
    void rebuildSystemCapability(String resourceCode);

    /**
     * 重建 {@link SystemCapabilityCatalog} 中全部 System 资源能力。
     */
    void rebuildAllSystemCapabilities();

    /**
     * 重建全部业务类型下的 dynamic-model / dynamic-entity 能力（模型字段变更后用于刷新 CRUD schema）。
     */
    void rebuildAllDynamicCapabilities();
}
