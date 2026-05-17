package cn.iocoder.yudao.module.scene.platform.controller.admin.component.vo;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.AudioComponentDO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 音频组件保存请求 VO")
@Data
public class AudioComponentSaveReqVO {

    private String componentCode;

    private String displayName;

    private String soundCode;

    private Boolean autoActivate;

    private Boolean allowSpatialisation;

    private Boolean stopWhenOwnerDestroyed;

    private Float volumeMultiplier;

    private Float pitchMultiplier;

    private Boolean overrideAttenuation;

    private String attenuationJson;

    private String concurrencyJson;

    private String metadataJson;

    public AudioComponentDO toDO() {
        AudioComponentDO item = new AudioComponentDO();
        item.setComponentCode(componentCode);
        item.setDisplayName(displayName);
        item.setSoundCode(soundCode);
        item.setAutoActivate(autoActivate);
        item.setAllowSpatialisation(allowSpatialisation);
        item.setStopWhenOwnerDestroyed(stopWhenOwnerDestroyed);
        item.setVolumeMultiplier(volumeMultiplier);
        item.setPitchMultiplier(pitchMultiplier);
        item.setOverrideAttenuation(overrideAttenuation);
        item.setAttenuationJson(attenuationJson);
        item.setConcurrencyJson(concurrencyJson);
        item.setMetadataJson(metadataJson);
        return item;
    }
}
