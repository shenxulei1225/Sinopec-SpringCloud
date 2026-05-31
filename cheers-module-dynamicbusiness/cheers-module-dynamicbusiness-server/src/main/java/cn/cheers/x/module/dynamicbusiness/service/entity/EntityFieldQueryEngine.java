package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;

import java.util.List;
import java.util.Set;

/**
 * 实体字段查询引擎（EVA 统一入口）。
 */
public interface EntityFieldQueryEngine {

    /** 在候选范围内按关键词搜索实体ID。 */
    Set<Long> searchEntityIdsByKeyword(String businessTypeCode, String keyword, List<Long> candidateEntityIds);

    /** 在候选范围内按结构化筛选条件搜索实体ID（仅非 relation 字段）。 */
    Set<Long> filterEntityIdsByFilters(String businessTypeCode, List<FieldFilterReqVO> filters, List<Long> candidateEntityIds);

    /** 在候选范围内执行关键词 + 结构化筛选。 */
    Set<Long> searchAndFilterEntityIds(String businessTypeCode, String keyword, List<FieldFilterReqVO> filters, List<Long> candidateEntityIds);
}
