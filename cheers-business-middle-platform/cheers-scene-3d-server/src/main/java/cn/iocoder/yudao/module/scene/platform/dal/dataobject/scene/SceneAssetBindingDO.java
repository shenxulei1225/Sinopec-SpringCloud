package cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene;

import cn.iocoder.yudao.framework.tenant.core.db.TenantBaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 场景资源绑定 DO
 *
 * 说明：当前仅保留工作区查询转换所需的最小字段，待后续场景资源绑定子域恢复时再补齐完整模型。
 */
@TableName(value = "scene_asset_binding", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class SceneAssetBindingDO extends TenantBaseDO {

    @TableId
    private Long id;

    private Long sceneId;

    private Long assetId;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson;
}
