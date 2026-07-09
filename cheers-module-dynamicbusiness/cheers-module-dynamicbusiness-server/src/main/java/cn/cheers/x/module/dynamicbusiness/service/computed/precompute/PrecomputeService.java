package cn.cheers.x.module.dynamicbusiness.service.computed.precompute;

import java.util.List;

/**
 * 预计算服务接口
 * 
 * 提供计算字段的异步预计算功能，包括：
 * - 异步触发预计算
 * - 批量预计算
 * - 数据变化触发重算
 * - 预计算任务管理
 * 
 * @author yudao
 */
public interface PrecomputeService {

    // ========== 异步预计算 ==========

    /**
     * 异步触发单个实体的预计算
     * 
     * @param entityId 实体 ID
     */
    void triggerPrecomputeAsync(Long entityId);

    /**
     * 异步触发单个实体指定字段的预计算
     * 
     * @param modelId Model ID
     * @param fieldCode 字段编码
     * @param entityId 实体 ID
     */
    void triggerFieldPrecomputeAsync(Long modelId, String fieldCode, Long entityId);

    /**
     * 异步批量触发预计算
     * 
     * @param modelId Model ID
     * @param entityIds 实体 ID 列表
     */
    void triggerBatchPrecomputeAsync(Long modelId, List<Long> entityIds);

    // ========== 数据变化触发 ==========

    /**
     * 实体创建后触发预计算
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     */
    void onEntityCreated(Long modelId, Long entityId);

    /**
     * 实体更新后触发预计算
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     * @param changedFields 变化的字段列表
     */
    void onEntityUpdated(Long modelId, Long entityId, List<String> changedFields);

    /**
     * 实体删除后触发相关预计算
     * 
     * @param modelId Model ID
     * @param entityId 实体 ID
     */
    void onEntityDeleted(Long modelId, Long entityId);

    /**
     * 关联数据变化后触发预计算
     * 用于聚合统计字段的重算
     * 
     * @param targetEntityType 目标业务类型
     * @param targetModelCode 目标 Model 编码
     * @param targetEntityId 目标实体 ID
     */
    void onRelatedDataChanged(String targetEntityType, String targetModelCode, Long targetEntityId);

    // ========== 预计算任务管理 ==========

    /**
     * 获取待处理的预计算任务数量
     * 
     * @return 待处理任务数量
     */
    int getPendingTaskCount();

    /**
     * 清空所有待处理的预计算任务
     * 
     * @return 清空的任务数量
     */
    int clearPendingTasks();

    /**
     * 重新计算所有预计算字段
     * 用于系统维护或数据修复
     * 
     * @param modelId Model ID（可选，为 null 时重算所有）
     * @return 重算的字段数量
     */
    int recomputeAll(Long modelId);

    // ========== 预计算统计 ==========

    /**
     * 获取预计算统计信息
     * 
     * @return 预计算统计
     */
    PrecomputeStatistics getStatistics();

    /**
     * 处理待计算的预计算任务
     * 
     * 此方法供 XXL-Job 定时任务调用。
     * 租户级任务：每个租户可以独立配置执行周期。
     * 
     * @see cn.cheers.x.module.dynamicbusiness.job.PrecomputeJob
     */
    void processPendingPrecomputes();

    /**
     * 预计算统计信息
     */
    record PrecomputeStatistics(
            long totalTasks,
            long completedTasks,
            long failedTasks,
            long pendingTasks,
            double avgExecutionTimeMs
    ) {
        /**
         * 创建空统计
         */
        public static PrecomputeStatistics empty() {
            return new PrecomputeStatistics(0, 0, 0, 0, 0.0);
        }
    }
}
