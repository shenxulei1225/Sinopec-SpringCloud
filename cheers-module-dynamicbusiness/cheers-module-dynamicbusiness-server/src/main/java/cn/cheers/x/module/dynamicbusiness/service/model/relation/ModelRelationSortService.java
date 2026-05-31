package cn.cheers.x.module.dynamicbusiness.service.model.relation;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import org.springframework.stereotype.Service;

@Service
public class ModelRelationSortService {

    public static final int SORT_STEP = 1000;

    /**
     * 稀疏排序插值：
     * - 两侧都有值：取中位
     * - 仅左侧：左侧 + step
     * - 仅右侧：右侧 - step
     * - 都没有：step
     * - 间隙不足（<=1）：返回 null，交给调用方触发 rebalance
     */
    public Integer computeSparseSort(Integer prevSort, Integer nextSort) {
        if (prevSort == null && nextSort == null) return SORT_STEP;
        if (prevSort == null) return nextSort - SORT_STEP;
        if (nextSort == null) return prevSort + SORT_STEP;
        if (nextSort - prevSort <= 1) return null;
        return prevSort + (nextSort - prevSort) / 2;
    }

    public int safeSort(Integer sort) {
        return sort == null ? 0 : sort;
    }

    public int rebalanceSortByIndex(int index) {
        if (index < 0) {
            throw new ServiceException(400, "重排索引非法");
        }
        return (index + 1) * SORT_STEP;
    }
}
