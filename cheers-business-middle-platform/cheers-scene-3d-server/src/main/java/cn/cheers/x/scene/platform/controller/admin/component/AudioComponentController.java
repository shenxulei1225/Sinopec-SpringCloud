package cn.cheers.x.scene.platform.controller.admin.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.scene.platform.controller.admin.component.vo.AudioComponentRespVO;
import cn.cheers.x.scene.platform.controller.admin.component.vo.AudioComponentSaveReqVO;
import cn.cheers.x.scene.platform.dal.dataobject.component.AudioComponentDO;
import cn.cheers.x.scene.platform.service.component.AudioComponentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 音频组件")
@RestController
@RequestMapping("/scene-platform/audio-components")
@Validated
public class AudioComponentController {

    @Resource
    private AudioComponentService audioComponentService;

    @GetMapping
    @Operation(summary = "获得音频组件列表")
    public CommonResult<List<AudioComponentRespVO>> getAudioComponentList() {
        return success(audioComponentService.getAudioComponentList().stream().map(this::convert).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得音频组件详情")
    public CommonResult<AudioComponentRespVO> getAudioComponent(@PathVariable Long id) {
        return success(convert(audioComponentService.getAudioComponent(id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新音频组件")
    public CommonResult<Boolean> updateAudioComponent(@PathVariable Long id,
                                                      @Valid @RequestBody AudioComponentSaveReqVO reqVO) {
        audioComponentService.updateAudioComponent(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/defaults")
    @Operation(summary = "更新音频组件默认值")
    public CommonResult<Boolean> updateAudioComponentDefaults(@PathVariable Long id,
                                                              @Valid @RequestBody AudioComponentSaveReqVO reqVO) {
        audioComponentService.updateAudioComponentDefaults(id, reqVO.toDO());
        return success(true);
    }

    @PutMapping("/{id}/schema")
    @Operation(summary = "更新音频组件属性定义")
    public CommonResult<Boolean> updateAudioComponentSchema(@PathVariable Long id,
                                                            @Valid @RequestBody AudioComponentSaveReqVO reqVO) {
        audioComponentService.updateAudioComponentSchema(id, reqVO.toDO());
        return success(true);
    }

    private AudioComponentRespVO convert(AudioComponentDO item) {
        AudioComponentRespVO vo = new AudioComponentRespVO();
        vo.setId(item.getId());
        vo.setComponentCode(item.getComponentCode());
        vo.setDisplayName(item.getDisplayName());
        vo.setSoundCode(item.getSoundCode());
        vo.setAutoActivate(item.getAutoActivate());
        vo.setAllowSpatialisation(item.getAllowSpatialisation());
        vo.setStopWhenOwnerDestroyed(item.getStopWhenOwnerDestroyed());
        vo.setVolumeMultiplier(item.getVolumeMultiplier());
        vo.setPitchMultiplier(item.getPitchMultiplier());
        vo.setOverrideAttenuation(item.getOverrideAttenuation());
        vo.setAttenuationJson(item.getAttenuationJson());
        vo.setConcurrencyJson(item.getConcurrencyJson());
        vo.setMetadataJson(item.getMetadataJson());
        return vo;
    }
}
