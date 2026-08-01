package cn.cheers.x.module.dynamicbusiness.service.entity;

import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;

import java.util.List;
import java.util.Set;

/**
 * 实体字段查询引擎（EVA 统一入口）。
 */
public interface EntityFieldQueryEngine {

    /** 在候选范围内按关键词搜索实体ID。 */
    Set<Long> searchEntityIdsByKeyword(String entityTypeCode, String keyword, List<Long> candidateEntityIds);

    /**
     * 在候选范围内按关键词搜索；{@code searchFieldCodes} 非空时只在这些字段上匹配（多列 OR）。
     * 空则与无范围重载一致（name + 可搜索索引字段）。
     */
    default Set<Long> searchEntityIdsByKeyword(String entityTypeCode, String keyword,
                                               List<Long> candidateEntityIds, List<String> searchFieldCodes) {
        return searchEntityIdsByKeyword(entityTypeCode, keyword, candidateEntityIds);
    }

    /** 在候选范围内按结构化筛选条件搜索实体ID（仅非 relation 字段）。 */
    Set<Long> filterEntityIdsByFilters(String entityTypeCode, List<FieldFilterReqVO> filters, List<Long> candidateEntityIds);

    /** 在候选范围内执行关键词 + 结构化筛选。 */
    Set<Long> searchAndFilterEntityIds(String entityTypeCode, String keyword, List<FieldFilterReqVO> filters, List<Long> candidateEntityIds);

    /** 同 {@link #searchAndFilterEntityIds}，可限定关键词搜索字段。 */
    default Set<Long> searchAndFilterEntityIds(String entityTypeCode, String keyword,
                                               List<FieldFilterReqVO> filters, List<Long> candidateEntityIds,
                                               List<String> searchFieldCodes) {
        return searchAndFilterEntityIds(entityTypeCode, keyword, filters, candidateEntityIds);
    }
}
