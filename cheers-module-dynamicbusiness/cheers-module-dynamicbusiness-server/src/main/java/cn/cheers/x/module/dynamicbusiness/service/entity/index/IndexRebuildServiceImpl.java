package cn.cheers.x.module.dynamicbusiness.service.entity.index;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityFieldIndexMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntitySyncFailLogMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.sync.EntitySyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * 索引重建服务实现
 *
 * <p>负责管理扩展字段索引的重建，支持全量重建、按 Model 重建、进度回调等功能。</p>
 *
 * <h3>重建策略</h3>
 * <ul>
 *   <li>分批处理：每批 100 条，避免内存溢出</li>
 *   <li>删除后插入：先删除旧索引，再插入新索引</li>
 *   <li>进度回调：支持实时进度通知</li>
 *   <li>可取消：支持取消正在进行的重建任务</li>
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
@Slf4j
@Service
@RequiredArgsConstructor
public class IndexRebuildServiceImpl implements IndexRebuildService {

    private final EntityRepository entityRepository;
    private final EntityFieldIndexMapper entityFieldIndexMapper;
    private final EntitySyncService entitySyncService;
    private final ModelMapper modelMapper;
    private final EntitySyncFailLogMapper entitySyncFailLogMapper;

    /**
     * 批处理大小
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 进度报告间隔（每处理多少条报告一次）
     */
    private static final int PROGRESS_REPORT_INTERVAL = 50;

    /**
     * 正在进行的重建任务进度
     * Key: modelId（null 表示全量重建，使用 -1L 作为 key）
     */
    private final Map<Long, RebuildProgress> progressMap = new ConcurrentHashMap<>();

    /**
     * 取消标志
     * Key: modelId（null 表示全量重建，使用 -1L 作为 key）
     */
    private final Map<Long, AtomicBoolean> cancelFlags = new ConcurrentHashMap<>();

    // ==================== 重建索引 ====================

    @Override
    @Async("entitySyncExecutor")
    public void rebuildIndex(Long modelId, Consumer<RebuildProgress> progressCallback) {
        if (modelId == null) {
            log.warn("Model ID 不能为空");
            return;
        }

        log.info("开始重建 Model 索引: modelId={}", modelId);

        // 初始化进度和取消标志
        RebuildProgress progress = new RebuildProgress(0, 0, RebuildProgress.STATUS_RUNNING, modelId);
        progress.setStartTime(System.currentTimeMillis());
        progressMap.put(modelId, progress);
        cancelFlags.put(modelId, new AtomicBoolean(false));

        try {
            // 1. 获取 Model 的业务类型
            ModelDO model = modelMapper.selectById(modelId);
            if (model == null) {
                log.warn("Model 不存在: modelId={}", modelId);
                failProgress(progress, "Model 不存在", progressCallback);
                return;
            }
            String entityTypeCode = model.getEntityTypeCode();

            // 2. 使用 Repository 获取实体列表
            List<EntityDO> allEntities = entityRepository.findByModelId(modelId, entityTypeCode);
            long total = allEntities.size();
            progress.setTotal(total);

            if (total == 0) {
                log.info("Model 下没有 Entity，跳过索引重建: modelId={}", modelId);
                completeProgress(progress, progressCallback);
                return;
            }

            // 4. 删除旧索引
            log.info("删除旧索引: modelId={}", modelId);
            entityFieldIndexMapper.deleteByModelId(modelId);

            // 5. 分批处理
            long processed = 0;
            int failCount = 0;

            for (EntityDO entity : allEntities) {
                // 检查是否取消
                if (cancelFlags.get(modelId).get()) {
                    log.info("索引重建被取消: modelId={}, processed={}", modelId, processed);
                    cancelProgress(progress, progressCallback);
                    return;
                }

                try {
                    entitySyncService.syncEntity(entity);
                    processed++;
                } catch (Exception e) {
                    failCount++;
                    log.error("同步 Entity 失败: entityId={}, error={}", entity.getId(), e.getMessage());
                }

                // 更新进度
                progress.setProcessed(processed);

                // 定期报告进度
                if (processed % PROGRESS_REPORT_INTERVAL == 0 && progressCallback != null) {
                    progressCallback.accept(progress);
                }
            }

            // 6. 完成
            log.info("索引重建完成: modelId={}, total={}, processed={}, failed={}", 
                    modelId, total, processed, failCount);
            completeProgress(progress, progressCallback);

        } catch (Exception e) {
            log.error("索引重建失败: modelId={}, error={}", modelId, e.getMessage(), e);
            failProgress(progress, e.getMessage(), progressCallback);
        } finally {
            // 清理取消标志
            cancelFlags.remove(modelId);
        }
    }

    @Override
    public void rebuildIndexByModelCode(String modelCode, Consumer<RebuildProgress> progressCallback) {
        if (modelCode == null || modelCode.isEmpty()) {
            log.warn("Model 编码不能为空");
            return;
        }

        ModelDO model = modelMapper.selectByCode(modelCode);
        if (model == null) {
            log.warn("Model 不存在: modelCode={}", modelCode);
            return;
        }

        rebuildIndex(model.getId(), progressCallback);
    }

    @Override
    @Async("entitySyncExecutor")
    public void rebuildAllIndexes(Consumer<RebuildProgress> progressCallback) {
        log.info("开始全量索引重建");

        // 使用 -1L 作为全量重建的 key
        Long allKey = -1L;

        // 初始化进度和取消标志
        RebuildProgress progress = new RebuildProgress(0, 0, RebuildProgress.STATUS_RUNNING, null);
        progress.setStartTime(System.currentTimeMillis());
        progressMap.put(allKey, progress);
        cancelFlags.put(allKey, new AtomicBoolean(false));

        try {
            // 1. 获取所有 Model
            List<ModelDO> models = modelMapper.selectList();
            if (models.isEmpty()) {
                log.info("没有 Model，跳过全量索引重建");
                completeProgress(progress, progressCallback);
                return;
            }

            // 2. 统计总 Entity 数量（通过 Repository）
            long totalEntities = 0;
            for (ModelDO model : models) {
                List<EntityDO> entities = entityRepository.findByModelId(model.getId(), model.getEntityTypeCode());
                totalEntities += entities.size();
            }
            progress.setTotal(totalEntities);

            if (totalEntities == 0) {
                log.info("没有 Entity，跳过全量索引重建");
                completeProgress(progress, progressCallback);
                return;
            }

            // 3. 逐个 Model 重建
            long processed = 0;
            int failCount = 0;

            for (ModelDO model : models) {
                // 检查是否取消
                if (cancelFlags.get(allKey).get()) {
                    log.info("全量索引重建被取消: processed={}", processed);
                    cancelProgress(progress, progressCallback);
                    return;
                }

                log.info("重建 Model 索引: modelId={}, modelCode={}", model.getId(), model.getCode());

                // 删除该 Model 的旧索引
                entityFieldIndexMapper.deleteByModelId(model.getId());

                // 通过 Repository 获取该 Model 的所有 Entity
                List<EntityDO> entities = entityRepository.findByModelId(model.getId(), model.getEntityTypeCode());

                for (EntityDO entity : entities) {
                    // 检查是否取消
                    if (cancelFlags.get(allKey).get()) {
                        log.info("全量索引重建被取消: processed={}", processed);
                        cancelProgress(progress, progressCallback);
                        return;
                    }

                    try {
                        entitySyncService.syncEntity(entity);
                        processed++;
                    } catch (Exception e) {
                        failCount++;
                        log.error("同步 Entity 失败: entityId={}, error={}", entity.getId(), e.getMessage());
                    }

                    // 更新进度
                    progress.setProcessed(processed);

                    // 定期报告进度
                    if (processed % PROGRESS_REPORT_INTERVAL == 0 && progressCallback != null) {
                        progressCallback.accept(progress);
                    }
                }
            }

            // 4. 完成
            log.info("全量索引重建完成: total={}, processed={}, failed={}", totalEntities, processed, failCount);
            completeProgress(progress, progressCallback);

        } catch (Exception e) {
            log.error("全量索引重建失败: error={}", e.getMessage(), e);
            failProgress(progress, e.getMessage(), progressCallback);
        } finally {
            // 清理取消标志
            cancelFlags.remove(allKey);
        }
    }

    // ==================== 进度管理 ====================

    @Override
    public RebuildProgress getCurrentProgress(Long modelId) {
        Long key = modelId == null ? -1L : modelId;
        return progressMap.get(key);
    }

    @Override
    public boolean cancelRebuild(Long modelId) {
        Long key = modelId == null ? -1L : modelId;
        AtomicBoolean cancelFlag = cancelFlags.get(key);
        if (cancelFlag != null) {
            cancelFlag.set(true);
            log.info("请求取消索引重建: modelId={}", modelId);
            return true;
        }
        return false;
    }

    @Override
    public boolean isRebuilding(Long modelId) {
        Long key = modelId == null ? -1L : modelId;
        RebuildProgress progress = progressMap.get(key);
        return progress != null && RebuildProgress.STATUS_RUNNING.equals(progress.getStatus());
    }

    // ==================== 补同步功能 ====================

    @Override
    public boolean resyncByEntityId(Long entityId, String entityTypeCode) {
        if (entityId == null) {
            log.warn("[resyncByEntityId][Entity ID 不能为空]");
            return false;
        }
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            log.warn("[resyncByEntityId][entityTypeCode 不能为空][entityId={}]", entityId);
            return false;
        }

        // 使用 Repository 获取实体 DO
        EntityDO entity = entityRepository.findById(entityId, entityTypeCode);
        if (entity == null) {
            log.warn("[resyncByEntityId][实体不存在][entityId={}, entityTypeCode={}]", 
                    entityId, entityTypeCode);
            return false;
        }

        try {
            entitySyncService.syncEntity(entity);
            // 同步成功后，清理该 Entity 的失败日志
            entitySyncFailLogMapper.deleteByEntityId(entityId);
            log.info("[resyncByEntityId][补同步成功][entityId={}, entityTypeCode={}]", 
                    entityId, entityTypeCode);
            return true;
        } catch (Exception e) {
            log.error("[resyncByEntityId][补同步失败][entityId={}, entityTypeCode={}, error={}]", 
                    entityId, entityTypeCode, e.getMessage());
            return false;
        }
    }

    @Override
    public int resyncByEntityIds(List<Long> entityIds, String entityTypeCode) {
        if (entityIds == null || entityIds.isEmpty()) {
            return 0;
        }
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            log.warn("[resyncByEntityIds][entityTypeCode 不能为空]");
            return 0;
        }

        log.info("[resyncByEntityIds][开始批量补同步][count={}, entityTypeCode={}]", 
                entityIds.size(), entityTypeCode);
        int successCount = 0;

        for (Long entityId : entityIds) {
            if (resyncByEntityId(entityId, entityTypeCode)) {
                successCount++;
            }
        }

        log.info("[resyncByEntityIds][批量补同步完成][total={}, success={}, entityTypeCode={}]", 
                entityIds.size(), successCount, entityTypeCode);
        return successCount;
    }

    @Override
    public int resyncFailedByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            log.warn("时间范围不能为空");
            return 0;
        }

        // 查询时间范围内的失败日志
        List<EntitySyncFailLogDO> failLogs = entitySyncFailLogMapper.selectByTimeRange(
                startTime, endTime, EntitySyncFailLogDO.STATUS_FAILED);

        if (failLogs.isEmpty()) {
            log.info("时间范围内没有失败的同步记录: {} - {}", startTime, endTime);
            return 0;
        }

        log.info("开始补同步失败记录: count={}, timeRange={} - {}", failLogs.size(), startTime, endTime);
        int successCount = 0;

        for (EntitySyncFailLogDO failLog : failLogs) {
            if (processFailLog(failLog)) {
                successCount++;
            }
        }

        log.info("补同步完成: total={}, success={}", failLogs.size(), successCount);
        return successCount;
    }

    @Override
    public int resyncPendingFailLogs(int limit) {
        if (limit <= 0) {
            limit = 100; // 默认处理 100 条
        }

        List<EntitySyncFailLogDO> pendingLogs = entitySyncFailLogMapper.selectPendingLogs(limit);
        if (pendingLogs.isEmpty()) {
            log.info("没有待处理的失败日志");
            return 0;
        }

        log.info("开始处理待处理的失败日志: count={}", pendingLogs.size());
        int successCount = 0;

        for (EntitySyncFailLogDO failLog : pendingLogs) {
            if (processFailLog(failLog)) {
                successCount++;
            }
        }

        log.info("处理完成: total={}, success={}", pendingLogs.size(), successCount);
        return successCount;
    }

    @Override
    public ResyncStatistics getResyncStatistics() {
        long pendingCount = entitySyncFailLogMapper.countByStatus(EntitySyncFailLogDO.STATUS_PENDING);
        long retryingCount = entitySyncFailLogMapper.countByStatus(EntitySyncFailLogDO.STATUS_RETRYING);
        long failedCount = entitySyncFailLogMapper.countByStatus(EntitySyncFailLogDO.STATUS_FAILED);
        long successCount = entitySyncFailLogMapper.countByStatus(EntitySyncFailLogDO.STATUS_SUCCESS);

        return new ResyncStatistics(pendingCount, retryingCount, failedCount, successCount);
    }

    /**
     * 处理单个失败日志
     *
     * <p>使用失败日志中记录的 entityTypeCode 路由到正确的存储策略。</p>
     */
    private boolean processFailLog(EntitySyncFailLogDO failLog) {
        if (failLog == null) {
            return false;
        }

        Long entityId = failLog.getEntityId();
        String entityTypeCode = failLog.getEntityTypeCode();

        // 检查 entityTypeCode 是否存在
        if (entityTypeCode == null || entityTypeCode.isEmpty()) {
            log.warn("[processFailLog][失败日志缺少 entityTypeCode，尝试从 Model 获取][failLogId={}, entityId={}]",
                    failLog.getId(), entityId);
            // 尝试从 Model 获取 entityTypeCode
            if (failLog.getModelId() != null) {
                ModelDO model = modelMapper.selectById(failLog.getModelId());
                if (model != null) {
                    entityTypeCode = model.getEntityTypeCode();
                }
            }
            if (entityTypeCode == null || entityTypeCode.isEmpty()) {
                log.error("[processFailLog][无法确定 entityTypeCode，跳过处理][failLogId={}, entityId={}]",
                        failLog.getId(), entityId);
                return false;
            }
        }

        // 使用 Repository 获取实体 DO
        EntityDO entity = entityRepository.findById(entityId, entityTypeCode);

        if (entity == null) {
            // Entity 已被删除，标记为成功
            log.info("[processFailLog][实体已删除，标记为成功][failLogId={}, entityId={}]",
                    failLog.getId(), entityId);
            entitySyncFailLogMapper.updateStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_SUCCESS, LocalDateTime.now());
            return true;
        }

        // 更新状态为重试中
        entitySyncFailLogMapper.updateRetryInfo(failLog.getId(), EntitySyncFailLogDO.STATUS_RETRYING,
                LocalDateTime.now(), LocalDateTime.now());

        try {
            entitySyncService.syncEntity(entity);
            // 同步成功，更新状态
            entitySyncFailLogMapper.updateStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_SUCCESS, LocalDateTime.now());
            log.info("[processFailLog][补同步成功][failLogId={}, entityId={}, entityTypeCode={}]",
                    failLog.getId(), entityId, entityTypeCode);
            return true;
        } catch (Exception e) {
            log.error("[processFailLog][补同步失败][failLogId={}, entityId={}, entityTypeCode={}, error={}]",
                    failLog.getId(), entityId, entityTypeCode, e.getMessage());
            // 同步失败，更新状态
            entitySyncFailLogMapper.updateStatus(failLog.getId(), EntitySyncFailLogDO.STATUS_FAILED, LocalDateTime.now());
            return false;
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 完成进度
     */
    private void completeProgress(RebuildProgress progress, Consumer<RebuildProgress> callback) {
        progress.setStatus(RebuildProgress.STATUS_COMPLETED);
        progress.setEndTime(System.currentTimeMillis());
        if (callback != null) {
            callback.accept(progress);
        }
        // 保留进度信息一段时间，供查询
        // 实际生产环境可以考虑定时清理
    }

    /**
     * 失败进度
     */
    private void failProgress(RebuildProgress progress, String errorMessage, Consumer<RebuildProgress> callback) {
        progress.setStatus(RebuildProgress.STATUS_FAILED);
        progress.setErrorMessage(errorMessage);
        progress.setEndTime(System.currentTimeMillis());
        if (callback != null) {
            callback.accept(progress);
        }
    }

    /**
     * 取消进度
     */
    private void cancelProgress(RebuildProgress progress, Consumer<RebuildProgress> callback) {
        progress.setStatus(RebuildProgress.STATUS_CANCELLED);
        progress.setEndTime(System.currentTimeMillis());
        if (callback != null) {
            callback.accept(progress);
        }
    }
}
