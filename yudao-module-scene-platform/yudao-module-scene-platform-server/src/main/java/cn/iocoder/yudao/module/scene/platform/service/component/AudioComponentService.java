package cn.iocoder.yudao.module.scene.platform.service.component;

import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.AudioComponentDO;

import java.util.List;

public interface AudioComponentService {

    List<AudioComponentDO> getAudioComponentList();

    AudioComponentDO getAudioComponent(Long id);

    void updateAudioComponent(Long id, AudioComponentDO audioComponentDO);

    void updateAudioComponentDefaults(Long id, AudioComponentDO audioComponentDO);

    void updateAudioComponentSchema(Long id, AudioComponentDO audioComponentDO);
}
