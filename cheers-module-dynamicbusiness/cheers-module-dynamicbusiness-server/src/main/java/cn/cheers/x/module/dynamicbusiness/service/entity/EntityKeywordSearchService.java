package cn.cheers.x.module.dynamicbusiness.service.entity;

import java.util.List;
import java.util.Set;

/**
 * 实体关键词搜索服务。
 *
 * <p>用于在“候选实体集合”上执行关键词命中，返回命中的实体ID集合。</p>
 */
public interface EntityKeywordSearchService {

    /**
     * 在候选实体范围内执行关键词搜索。
     *
     * @param businessTypeCode 业务类型编码
     * @param keyword 关键词
     * @param candidateEntityIds 候选实体ID（用于范围收敛）
     * @return 命中的实体ID集合
     */
    Set<Long> searchMatchedEntityIds(String businessTypeCode, String keyword, List<Long> candidateEntityIds);
}
