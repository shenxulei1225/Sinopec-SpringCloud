package cn.cheers.x.scene.platform.service.component;

import cn.cheers.x.scene.platform.dal.dataobject.component.AudioComponentDO;

import java.util.List;

public interface AudioComponentService {

    List<AudioComponentDO> getAudioComponentList();

    AudioComponentDO getAudioComponent(Long id);

    void updateAudioComponent(Long id, AudioComponentDO audioComponentDO);

    void updateAudioComponentDefaults(Long id, AudioComponentDO audioComponentDO);

    void updateAudioComponentSchema(Long id, AudioComponentDO audioComponentDO);
}
