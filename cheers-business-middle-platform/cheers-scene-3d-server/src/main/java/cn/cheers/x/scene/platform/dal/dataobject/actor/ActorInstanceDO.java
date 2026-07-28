package cn.cheers.x.scene.platform.dal.dataobject.actor;

import cn.cheers.x.framework.tenant.core.db.TenantBaseDO;
import cn.cheers.x.scene.platform.dal.dataobject.JsonStringTypeHandler;
import cn.cheers.x.scene.platform.dal.dataobject.TransformTypeHandler;
import cn.cheers.x.scene.platform.model.Transform;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "actor_instance", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class ActorInstanceDO extends TenantBaseDO {

    /**
     * scene_platform 表主键为 BIGINT 且无 DB sequence；PG 全局 id-type=INPUT 时须应用侧生成。
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    private Long sceneId;

    private String actorCode;

    private String instanceCode;

    private String instanceName;

    private String parentInstanceCode;

    private String instanceStatus;

    private Boolean visibleFlag;

    private Integer versionNo;

    @TableField(typeHandler = TransformTypeHandler.class)
    private Transform transform;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;

    private String path;

    /** 所属图层编码列表（JSON 数组），如 ["main", "sub"] */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String layerKeys;

    /** 上次状态变更时间戳 (毫秒)，用于 WebSocket 推送和前端插值动画 */
    private Long positionTimestamp;

    /** 是否启用平滑动画过渡 */
    private Boolean animationEnabled;

    /** 动画持续时间 (ms)，默认 500 */
    private Integer animationDuration;

    /** 动画类型 (linear / easeInOutCubic) */
    private String animationType;

    /** 实例 GPS 经度（WGS84） */
    private java.math.BigDecimal gpsLng;

    /** 实例 GPS 纬度（WGS84） */
    private java.math.BigDecimal gpsLat;

    /** 实例 GPS 椭球高（米） */
    private java.math.BigDecimal gpsHeight;

    /** GPS 高程来源：TERRAIN_SAMPLE / MANUAL 等 */
    private String gpsHeightSource;
}
