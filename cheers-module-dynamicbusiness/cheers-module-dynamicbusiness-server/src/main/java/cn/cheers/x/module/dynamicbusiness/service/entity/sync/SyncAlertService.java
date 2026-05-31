package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

/**
 * 同步告警服务接口
 * 
 * <p>负责处理 Entity 同步失败的告警通知。</p>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-037: 连续失败超过 10 次触发告警</li>
 *   <li>FR-043: 同步失败记录到 dynamic_entity_sync_fail_log 表</li>
 * </ul>
 * 
 * <h3>告警级别</h3>
 * <ul>
 *   <li>WARNING: 连续失败 10-20 次</li>
 *   <li>ERROR: 连续失败 20-50 次</li>
 *   <li>CRITICAL: 连续失败超过 50 次</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
public interface SyncAlertService {

    /**
     * 告警级别常量
     */
    String ALERT_LEVEL_WARNING = "WARNING";
    String ALERT_LEVEL_ERROR = "ERROR";
    String ALERT_LEVEL_CRITICAL = "CRITICAL";

    /**
     * 告警阈值常量
     */
    int THRESHOLD_WARNING = 10;
    int THRESHOLD_ERROR = 20;
    int THRESHOLD_CRITICAL = 50;

    /**
     * 发送同步失败告警
     * 
     * @param entityId Entity ID
     * @param modelId Model ID
     * @param failCount 连续失败次数
     * @param lastError 最后一次错误信息
     */
    void sendSyncFailureAlert(Long entityId, Long modelId, long failCount, String lastError);

    /**
     * 发送批量同步失败告警
     * 
     * @param modelId Model ID
     * @param failCount 失败数量
     * @param totalCount 总数量
     */
    void sendBatchSyncFailureAlert(Long modelId, int failCount, int totalCount);

    /**
     * 发送索引重建失败告警
     * 
     * @param modelId Model ID
     * @param errorMessage 错误信息
     */
    void sendIndexRebuildFailureAlert(Long modelId, String errorMessage);

    /**
     * 检查是否应该发送告警（防重复）
     * 
     * <p>在指定时间窗口内，同一 Entity 只发送一次告警。</p>
     * 
     * @param entityId Entity ID
     * @return 是否应该发送告警
     */
    boolean shouldSendAlert(Long entityId);

    /**
     * 获取告警级别
     * 
     * @param failCount 连续失败次数
     * @return 告警级别
     */
    String getAlertLevel(long failCount);

    /**
     * 获取告警统计信息
     * 
     * @return 告警统计
     */
    AlertStatistics getAlertStatistics();

    /**
     * 告警统计信息
     */
    class AlertStatistics {
        private long totalAlerts;
        private long warningCount;
        private long errorCount;
        private long criticalCount;
        private long pendingFailLogs;

        public AlertStatistics() {}

        public AlertStatistics(long totalAlerts, long warningCount, long errorCount, 
                               long criticalCount, long pendingFailLogs) {
            this.totalAlerts = totalAlerts;
            this.warningCount = warningCount;
            this.errorCount = errorCount;
            this.criticalCount = criticalCount;
            this.pendingFailLogs = pendingFailLogs;
        }

        public long getTotalAlerts() { return totalAlerts; }
        public void setTotalAlerts(long totalAlerts) { this.totalAlerts = totalAlerts; }
        public long getWarningCount() { return warningCount; }
        public void setWarningCount(long warningCount) { this.warningCount = warningCount; }
        public long getErrorCount() { return errorCount; }
        public void setErrorCount(long errorCount) { this.errorCount = errorCount; }
        public long getCriticalCount() { return criticalCount; }
        public void setCriticalCount(long criticalCount) { this.criticalCount = criticalCount; }
        public long getPendingFailLogs() { return pendingFailLogs; }
        public void setPendingFailLogs(long pendingFailLogs) { this.pendingFailLogs = pendingFailLogs; }
    }
}
