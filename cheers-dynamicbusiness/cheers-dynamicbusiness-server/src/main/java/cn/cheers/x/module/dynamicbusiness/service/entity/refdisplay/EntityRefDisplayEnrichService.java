package cn.cheers.x.module.dynamicbusiness.service.entity.refdisplay;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityRespVO;

import java.util.List;

/**
 * 实体列表查询增强：为 REF / REF_Multi 契约对象批量补展示字段 {@code name}。
 */
public interface EntityRefDisplayEnrichService {

    /**
     * 扫描 {@code baseFields} / {@code customFields} 中的
     * {@code {entityTypeCode,id}}（及数组），按类型批量查名称并写入 {@code name}。
     */
    void enrich(List<EntityRespVO> entities);
}
