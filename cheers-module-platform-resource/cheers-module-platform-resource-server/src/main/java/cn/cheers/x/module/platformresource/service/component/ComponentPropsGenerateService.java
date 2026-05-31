package cn.cheers.x.module.platformresource.service.component;

import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentPropsGenerateFromContractReqVO;

import java.util.Map;

public interface ComponentPropsGenerateService {

    /**
     * 根据 dataSourceKey 拉取契约并生成 Props JSON（不落库）。
     */
    Map<String, Object> previewPropsFromContract(ComponentPropsGenerateFromContractReqVO reqVO);

    /**
     * 根据契约生成并保存为 Props 模板，返回 propsId。
     */
    Long generateTemplateFromContract(ComponentPropsGenerateFromContractReqVO reqVO);

    /**
     * 为全部 system:* 能力生成/更新 Props 模板（list + 支持树的 tree，幂等）。
     */
    int seedAllSystemPropsTemplates();

    /**
     * 为注册表中全部 dynamic-model:* / dynamic-entity:* 生成 list 模板；契约含 tree 端点时另生成 tree 模板。
     */
    int seedAllDynamicPropsTemplates();

    /**
     * system + dynamic 一并种子（幂等）。
     */
    int seedAllCapabilityPropsTemplates();
}
