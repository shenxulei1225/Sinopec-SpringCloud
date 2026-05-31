package cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 同步失败日志 DO
 * 
 * 业务含义：记录 Entity 数据同步到查询索引失败的日志，支持重试机制。
 * 
 * 同步失败处理流程：
 * 1. Entity 保存时异步同步到查询索引
 * 2. 同步失败时自动重试（1秒、5秒、30秒）
 * 3. 超过重试次数后记录到此表
 * 4. 连续失败超过 10 次触发告警
 * 5. 支持手动补同步
 * 
 * 继承 TenantBaseDO 以支持多租户，定时任务可直接获取 tenant_id。
 * 
 * @author yudao
 */
@TableName("dynamic_entity_sync_fail_log")
@KeySequence("dynamic_entity_sync_fail_log_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntitySyncFailLogDO extends TenantBaseDO {

    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 实体ID
     */
    private Long entityId;

    /**
     * 业务类型编码
     * 
     * 用于路由到正确的存储策略（通用表或动态表）
     */
    private String businessTypeCode;

    /**
     * 模型ID
     */
    private Long modelId;

    /**
     * 引擎类型
     * 
     * 可选值：postgresql、mysql、es
     */
    private String engineType;

    /**
     * 失败原因
     */
    private String failReason;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最后重试时间
     */
    private LocalDateTime lastRetryAt;

    /**
     * 状态
     * 
     * 可选值：
     * - PENDING: 待处理
     * - RETRYING: 重试中
     * - FAILED: 失败
     * - SUCCESS: 成功
     */
    private String status;

    // ========== 状态常量 ==========

    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_RETRYING = "RETRYING";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_SUCCESS = "SUCCESS";

    // ========== 引擎类型常量 ==========

    public static final String ENGINE_POSTGRESQL = "postgresql";
    public static final String ENGINE_MYSQL = "mysql";
    public static final String ENGINE_ES = "es";
}
