package cn.cheers.x.module.platform.routing.planner;

import java.util.List;

/**
 * 停靠点访问顺序策略：在给定 pairwise 代价矩阵下重排 stopIds。
 */
public interface StopOrderStrategy {

    List<String> order(List<String> stopIds, CostMatrix matrix, GraphView view);

}
