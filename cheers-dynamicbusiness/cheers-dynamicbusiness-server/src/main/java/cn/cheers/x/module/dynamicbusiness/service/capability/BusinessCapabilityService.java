package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.BusinessCapabilityFullRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.BusinessCapabilitySummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.CapabilityComponentProjectionRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.ModelCrudFormDefinitionRespVO;

import java.util.List;

/**
 * 业务能力服务（Capability Domain Service）。
 *
 * <p>能力域约束（与定稿一致）：</p>
 * <ul>
 *   <li>能力索引统一使用 entityTypeCode；</li>
 *   <li>投影读取使用 entityTypeCode + componentCode；</li>
 *   <li>模型 CRUD 表单定义读取使用 entityTypeCode + modelId；</li>
 *   <li>不接受 dataSourceKey 等组件侧历史复合键。</li>
 * </ul>
 */
public interface BusinessCapabilityService {

    /**
     * 返回已注册业务能力摘要列表（供前端下拉选择业务数据来源）。
     *
     * @param businessCategory 可选：dynamic / system；为空则返回全部
     */
    List<BusinessCapabilitySummaryRespVO> listCapabilitySummaries(String businessCategory);

    /**
     * 按业务类型编码读取能力全集。
     */
    BusinessCapabilityFullRespVO getCapabilityFull(String entityTypeCode);

    /**
     * 按业务类型编码 + 组件编码 + 数据种类读取组件能力投影。
     *
     * @param dataKind model / entity；system 固定 entity
     */
    CapabilityComponentProjectionRespVO getProjection(
            String entityTypeCode, String componentCode, String dataKind);

    /**
     * 按业务类型编码 + 模型编号读取模型 CRUD 表单定义。
     */
    ModelCrudFormDefinitionRespVO getModelCrudFormDefinition(String entityTypeCode, Long modelId);

    /**
     * 重建单个动态业务类型能力（全集 + 投影 + 表单定义）。
     */
    void rebuildByEntityTypeCode(String entityTypeCode);

    /**
     * 重建全部已注册系统业务能力。
     */
    /**
     * 重建当前租户上下文下的全部系统业务能力（全集 + 各组件投影）。
     *
     * <p>interim：与 dynamic 相同，依赖 {@code TenantContextHolder} 的 {@code tenant_id}；
     * 启动 bootstrap 会对每个租户 {@code TenantUtils.execute(tenantId, ...)} 各执行一次。</p>
     */
    void rebuildAllSystemCapabilities();

    /**
     * 重建单个系统业务能力（全集 + 投影）。
     */
    void rebuildSystemCapability(String entityTypeCode);

    /**
     * 按模型触发重建（先解析模型所属 entityTypeCode，再重建该业务类型）。
     */
    void rebuildByModelId(Long modelId);

    /**
     * 模型字段分配或规则变更后，重建该模型的 CRUD 表单定义（写路径触发）。
     */
    void refreshModelCrudFormDefinition(Long modelId);

    /**
     * 业务类型基础字段（固定列）变更后，重建该类型能力全集与组件投影。
     * 不批量重写各型号 CRUD 表单；表单在打开时按最新固定列/分配重建。
     */
    void refreshAfterEntityTypeFieldDefinitionChanged(String entityTypeCode);
}
