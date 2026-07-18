package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.FieldRulesUpdateReqVO;

/**
 * 模型字段业务规则 Service 接口
 * 
 * 职责：管理字段在模型中的业务规则（必填、默认值、验证规则等）
 * 
 * 设计说明：
 * - 字段的业务规则存储在 ModelFieldAssignment 中
 * - 同一个字段在不同的模型中可能有不同的业务规则
 * - 此服务只负责业务规则的管理，不涉及字段分配和分组关联
 * 
 * @author yudao
 */
public interface ModelFieldRulesService {

    /**
     * 更新字段业务规则（必填、范围、默认值等）
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @param reqVO 规则更新请求
     */
    void updateFieldRules(Long modelId, Long fieldId, FieldRulesUpdateReqVO reqVO);

    /**
     * 获取字段业务规则
     *
     * @param modelId 模型ID
     * @param fieldId 字段ID
     * @return 业务规则
     */
    FieldRulesUpdateReqVO getFieldRules(Long modelId, Long fieldId);
}
