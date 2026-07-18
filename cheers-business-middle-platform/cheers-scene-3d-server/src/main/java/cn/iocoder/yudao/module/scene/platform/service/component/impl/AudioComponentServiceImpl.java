package cn.iocoder.yudao.module.scene.platform.service.component.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.component.AudioComponentDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.component.AudioComponentMapper;
import cn.iocoder.yudao.module.scene.platform.service.component.AudioComponentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class AudioComponentServiceImpl implements AudioComponentService {

    @Resource
    private AudioComponentMapper audioComponentMapper;

    @Override
    public List<AudioComponentDO> getAudioComponentList() {
        return audioComponentMapper.selectList();
    }

    @Override
    public AudioComponentDO getAudioComponent(Long id) {
        AudioComponentDO audioComponentDO = audioComponentMapper.selectById(id);
        if (audioComponentDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "音频组件不存在");
        }
        return audioComponentDO;
    }

    @Override
    public void updateAudioComponent(Long id, AudioComponentDO audioComponentDO) {
        AudioComponentDO db = getAudioComponent(id);
        BeanUtils.copyProperties(audioComponentDO, db);
        audioComponentMapper.updateById(db);
    }

    @Override
    public void updateAudioComponentDefaults(Long id, AudioComponentDO audioComponentDO) {
        AudioComponentDO db = getAudioComponent(id);
        db.setSoundCode(audioComponentDO.getSoundCode());
        db.setAutoActivate(audioComponentDO.getAutoActivate());
        db.setAllowSpatialisation(audioComponentDO.getAllowSpatialisation());
        db.setStopWhenOwnerDestroyed(audioComponentDO.getStopWhenOwnerDestroyed());
        db.setVolumeMultiplier(audioComponentDO.getVolumeMultiplier());
        db.setPitchMultiplier(audioComponentDO.getPitchMultiplier());
        db.setOverrideAttenuation(audioComponentDO.getOverrideAttenuation());
        db.setAttenuationJson(audioComponentDO.getAttenuationJson());
        db.setConcurrencyJson(audioComponentDO.getConcurrencyJson());
        db.setMetadataJson(audioComponentDO.getMetadataJson());
        audioComponentMapper.updateById(db);
    }

    @Override
    public void updateAudioComponentSchema(Long id, AudioComponentDO audioComponentDO) {
        AudioComponentDO db = getAudioComponent(id);
        db.setMetadataJson(audioComponentDO.getMetadataJson());
        audioComponentMapper.updateById(db);
    }
}
