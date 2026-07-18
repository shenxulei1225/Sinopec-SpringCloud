// cspell:ignore Spatialisation
package cn.iocoder.yudao.module.scene.platform.dal.dataobject.component;

import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.JsonStringTypeHandler;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@TableName(value = "audio_component", autoResultMap = true)
@Data
@EqualsAndHashCode(callSuper = true)
public class AudioComponentDO extends BaseDO {

    @TableId
    private Long id;

    private String componentCode;

    private String displayName = "Audio";

    private String soundCode;

    private Boolean autoActivate = Boolean.TRUE;

    private Boolean allowSpatialisation = Boolean.TRUE;

    private Boolean stopWhenOwnerDestroyed = Boolean.TRUE;

    private Float volumeMultiplier = 1.0f;

    private Float pitchMultiplier = 1.0f;

    private Boolean overrideAttenuation = Boolean.FALSE;

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String attenuationJson = "{}";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String concurrencyJson = "{}";

    @TableField(typeHandler = JsonStringTypeHandler.class)
    private String metadataJson = "{}";
}
