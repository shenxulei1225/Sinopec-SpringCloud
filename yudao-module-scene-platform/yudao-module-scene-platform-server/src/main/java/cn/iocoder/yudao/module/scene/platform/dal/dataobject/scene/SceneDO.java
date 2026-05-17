package cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 场景 DO（合并 SceneDO + SceneInstanceDO）
 * 对应 UE 场景编辑器中的 Scene
 */
@TableName(value = "scene", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneDO extends TenantBaseDO {

    @TableId
    private Long id;

    // === 场景基本信息 ===
    /** 场景编码 */
    private String sceneCode;

    /** 场景名称 */
    private String sceneName;

    /** 场景类型: INDOOR / OUTDOOR / HYBRID */
    private String sceneType;

    /** 引擎配置: threejs / cesium / hybrid */
    private String engineProfile;

    /** 场景能力配置（JSON 数组） */
    private String capabilitiesJson;

    /** 图层配置（JSON） */
    private String layerConfigJson;

    /** 默认视角配置（JSON） */
    private String defaultViewpointJson;

    // === 运行时/实例信息 ===
    /** 所属项目编码 */
    private String projectCode;

    /** 业务键（关联外部系统） */
    private String businessKey;

    /** 状态: 1-启用 0-停用 */
    private Integer status;

    /** 发布状态: DRAFT / PUBLISHED */
    private String publishStatus;

    /** 发布时间 */
    private LocalDateTime publishedAt;

    /** 发布人 */
    private String publishedBy;

    /** 关联的 Actor 实例编码列表（JSON 数组） */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String actorInstanceCodesJson;

    // === 运行时快照 ===
    /** 运行时状态快照（JSON）：所有 Actor 的当前位置/状态，定时落盘 */
    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String actorStatesJson;
}
