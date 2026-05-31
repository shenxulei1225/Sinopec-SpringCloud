package cn.cheers.x.module.dynamicbusiness.service.entity.sync;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntitySyncFailLogDO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity 数据同步服务接口
 * 
 * <p>负责将 Entity 数据同步到查询索引（entity_field_index 表），
 * 支持异步同步、重试机制、失败处理等功能。</p>
 * 
 * <h3>同步流程</h3>
 * <ol>
 *   <li>Entity 保存时触发异步同步</li>
 *   <li>获取 Model 的可查询字段列表</li>
 *   <li>将可查询字段值同步到 entity_field_index 表</li>
 *   <li>同步失败时自动重试（1秒、5秒、30秒）</li>
 *   <li>超过重试次数后记录到失败日志表</li>
 * </ol>
 * 
 * <h3>业务规则</h3>
 * <ul>
 *   <li>FR-034: Entity 保存时自动同步到查询索引</li>
 *   <li>FR-035: 异步同步，不阻塞主业务流程</li>
 *   <li>FR-036: 同步失败时自动重试（1秒、5秒、30秒）</li>
 *   <li>FR-037: 连续失败超过 10 次触发告警</li>
 *   <li>FR-042: Entity 保存时自动同步可查询字段到索引表</li>
 * </ul>
 * 
 * @author 扩展字段查询服务
 */
public interface EntitySyncService {

    /**
     * 异步同步 Entity 到索引表
     * 
     * <p>此方法为异步执行，不阻塞主业务流程。
     * 同步失败时会自动重试，超过重试次数后记录到失败日志。</p>
     * 
     * @param entity 要同步的 Entity
     */
    void syncEntityAsync(EntityDO entity);

    /**
     * 同步 Entity 到索引表（同步执行）
     * 
     * <p>此方法为同步执行，会阻塞当前线程直到同步完成。
     * 主要用于测试和手动补同步场景。</p>
     * 
     * @param entity 要同步的 Entity
     * @throws EntitySyncException 同步失败时抛出异常
     */
    void syncEntity(EntityDO entity);

    /**
     * 同步 Entity 到索引表（带重试）
     * 
     * <p>此方法会在同步失败时自动重试，重试间隔为 1秒、5秒、30秒。
     * 超过重试次数后会记录到失败日志表。</p>
     * 
     * @param entity 要同步的 Entity
     * @return 是否同步成功
     */
    boolean syncEntityWithRetry(EntityDO entity);

    /**
     * 从索引表删除 Entity
     * 
     * @param entityId Entity ID
     */
    void deleteFromIndex(Long entityId);

    /**
     * 批量同步 Entity 到索引表
     * 
     * @param entities Entity 列表
     */
    void batchSyncEntities(List<EntityDO> entities);

    /**
     * 按 Entity ID 补同步（需要提供 businessTypeCode）
     * 
     * <p>通过 businessTypeCode 路由到正确的存储策略，支持通用表和动态表。</p>
     * 
     * @param entityId Entity ID
     * @param businessTypeCode 业务类型编码（必填，用于路由到正确的存储策略）
     * @return 是否同步成功
     */
    boolean resyncByEntityId(Long entityId, String businessTypeCode);

    /**
     * 按时间范围补同步失败的记录
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 成功同步的数量
     */
    int resyncFailedByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取待处理的失败日志列表
     * 
     * @param limit 最大数量
     * @return 失败日志列表
     */
    List<EntitySyncFailLogDO> getPendingFailLogs(int limit);

    /**
     * 处理失败日志（重试同步）
     * 
     * @param failLog 失败日志
     * @return 是否处理成功
     */
    boolean processFailLog(EntitySyncFailLogDO failLog);

    /**
     * 统计待处理的失败日志数量
     * 
     * @return 待处理数量
     */
    long countPendingFailLogs();

    /**
     * 获取待处理的失败日志列表（按业务类型过滤）
     * 
     * @param businessTypeCode 业务类型编码
     * @param limit 最大数量
     * @return 失败日志列表
     */
    List<EntitySyncFailLogDO> getPendingFailLogsByBusinessTypeCode(String businessTypeCode, int limit);

    /**
     * 统计待处理的失败日志数量（按业务类型过滤）
     * 
     * @param businessTypeCode 业务类型编码
     * @return 待处理数量
     */
    long countPendingFailLogsByBusinessTypeCode(String businessTypeCode);

    /**
     * 统计指定 Entity 的连续失败次数
     * 
     * @param entityId Entity ID
     * @return 连续失败次数
     */
    long countConsecutiveFailures(Long entityId);
}
