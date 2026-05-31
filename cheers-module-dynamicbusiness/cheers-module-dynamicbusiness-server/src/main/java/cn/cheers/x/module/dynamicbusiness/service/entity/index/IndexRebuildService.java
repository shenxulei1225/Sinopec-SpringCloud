package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import java.util.List;
import java.util.function.Consumer;

/**
 * 索引重建服务接口
 *
 * <p>负责管理扩展字段索引的重建，包括：</p>
 * <ul>
 *   <li>全量索引重建</li>
 *   <li>按 Model 重建索引</li>
 *   <li>进度回调</li>
 * </ul>
 *
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-038: 系统必须支持全量索引重建功能</li>
 *   <li>FR-047: 系统必须支持初始化部署时的全量索引创建</li>
 *   <li>FR-048: 系统必须支持字段配置变更时的增量索引创建</li>
 *   <li>FR-049: 系统必须支持数据修复场景的索引重建</li>
 *   <li>FR-050: 系统必须在索引重建时显示进度信息</li>
 * </ul>
 *
 * @author 扩展字段查询服务
 */
public interface IndexRebuildService {

    /**
     * 重建指定 Model 的索引
     *
     * <p>异步执行，将指定 Model 下所有 Entity 的可查询字段同步到索引表。</p>
     *
     * @param modelId Model ID
     * @param progressCallback 进度回调（可为 null）
     */
    void rebuildIndex(Long modelId, Consumer<RebuildProgress> progressCallback);

    /**
     * 重建指定 Model 的索引（通过 Model 编码）
     *
     * @param modelCode Model 编码
     * @param progressCallback 进度回调（可为 null）
     */
    void rebuildIndexByModelCode(String modelCode, Consumer<RebuildProgress> progressCallback);

    /**
     * 重建所有 Model 的索引
     *
     * <p>异步执行，重建系统中所有 Model 的索引。</p>
     *
     * @param progressCallback 进度回调（可为 null）
     */
    void rebuildAllIndexes(Consumer<RebuildProgress> progressCallback);

    /**
     * 获取当前重建任务的进度
     *
     * @param modelId Model ID（null 表示全量重建）
     * @return 重建进度，如果没有正在进行的任务则返回 null
     */
    RebuildProgress getCurrentProgress(Long modelId);

    /**
     * 取消正在进行的重建任务
     *
     * @param modelId Model ID（null 表示全量重建）
     * @return 是否成功取消
     */
    boolean cancelRebuild(Long modelId);

    /**
     * 检查是否有正在进行的重建任务
     *
     * @param modelId Model ID（null 表示检查全量重建）
     * @return 是否有正在进行的任务
     */
    boolean isRebuilding(Long modelId);

    // ==================== 补同步功能 ====================

    /**
     * 按 Entity ID 补同步（需要提供 businessTypeCode）
     *
     * <p>重新同步指定 Entity 的索引数据。通过 businessTypeCode 路由到正确的存储策略。</p>
     *
     * @param entityId Entity ID
     * @param businessTypeCode 业务类型编码（必填，用于路由到正确的存储策略）
     * @return 是否同步成功
     */
    boolean resyncByEntityId(Long entityId, String businessTypeCode);

    /**
     * 按 Entity ID 列表批量补同步（需要提供 businessTypeCode）
     *
     * <p>批量重新同步指定 Entity 的索引数据。所有 Entity 必须属于同一业务类型。</p>
     *
     * @param entityIds Entity ID 列表
     * @param businessTypeCode 业务类型编码（必填，用于路由到正确的存储策略）
     * @return 成功同步的数量
     */
    int resyncByEntityIds(List<Long> entityIds, String businessTypeCode);

    /**
     * 按时间范围补同步失败的记录
     *
     * <p>查询指定时间范围内的失败日志，并重新同步。</p>
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功同步的数量
     */
    int resyncFailedByTimeRange(java.time.LocalDateTime startTime, java.time.LocalDateTime endTime);

    /**
     * 补同步所有待处理的失败记录
     *
     * @param limit 最大处理数量
     * @return 成功同步的数量
     */
    int resyncPendingFailLogs(int limit);

    /**
     * 获取失败日志统计信息
     *
     * @return 统计信息
     */
    ResyncStatistics getResyncStatistics();

    /**
     * 补同步统计信息
     */
    class ResyncStatistics {
        /**
         * 待处理数量
         */
        private long pendingCount;

        /**
         * 重试中数量
         */
        private long retryingCount;

        /**
         * 失败数量
         */
        private long failedCount;

        /**
         * 成功数量
         */
        private long successCount;

        public ResyncStatistics() {
        }

        public ResyncStatistics(long pendingCount, long retryingCount, long failedCount, long successCount) {
            this.pendingCount = pendingCount;
            this.retryingCount = retryingCount;
            this.failedCount = failedCount;
            this.successCount = successCount;
        }

        public long getPendingCount() {
            return pendingCount;
        }

        public void setPendingCount(long pendingCount) {
            this.pendingCount = pendingCount;
        }

        public long getRetryingCount() {
            return retryingCount;
        }

        public void setRetryingCount(long retryingCount) {
            this.retryingCount = retryingCount;
        }

        public long getFailedCount() {
            return failedCount;
        }

        public void setFailedCount(long failedCount) {
            this.failedCount = failedCount;
        }

        public long getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(long successCount) {
            this.successCount = successCount;
        }

        public long getTotalCount() {
            return pendingCount + retryingCount + failedCount + successCount;
        }
    }

    /**
     * 重建进度信息
     */
    class RebuildProgress {

        /**
         * 已处理数量
         */
        private long processed;

        /**
         * 总数量
         */
        private long total;

        /**
         * 状态：RUNNING, COMPLETED, FAILED, CANCELLED
         */
        private String status;

        /**
         * Model ID（null 表示全量重建）
         */
        private Long modelId;

        /**
         * 错误信息（失败时）
         */
        private String errorMessage;

        /**
         * 开始时间（毫秒时间戳）
         */
        private long startTime;

        /**
         * 结束时间（毫秒时间戳，未完成时为 0）
         */
        private long endTime;

        public RebuildProgress() {
        }

        public RebuildProgress(long processed, long total, String status) {
            this.processed = processed;
            this.total = total;
            this.status = status;
        }

        public RebuildProgress(long processed, long total, String status, Long modelId) {
            this.processed = processed;
            this.total = total;
            this.status = status;
            this.modelId = modelId;
        }

        /**
         * 获取完成百分比
         */
        public int getPercent() {
            return total == 0 ? 100 : (int) (processed * 100 / total);
        }

        /**
         * 获取预计剩余时间（秒）
         */
        public long getEstimatedRemainingSeconds() {
            if (processed == 0 || total == 0 || startTime == 0) {
                return -1;
            }
            long elapsed = System.currentTimeMillis() - startTime;
            double rate = (double) processed / elapsed;
            long remaining = total - processed;
            return rate > 0 ? (long) (remaining / rate / 1000) : -1;
        }

        // Getters and Setters

        public long getProcessed() {
            return processed;
        }

        public void setProcessed(long processed) {
            this.processed = processed;
        }

        public long getTotal() {
            return total;
        }

        public void setTotal(long total) {
            this.total = total;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getModelId() {
            return modelId;
        }

        public void setModelId(Long modelId) {
            this.modelId = modelId;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        // 状态常量
        public static final String STATUS_RUNNING = "RUNNING";
        public static final String STATUS_COMPLETED = "COMPLETED";
        public static final String STATUS_FAILED = "FAILED";
        public static final String STATUS_CANCELLED = "CANCELLED";
    }
}
