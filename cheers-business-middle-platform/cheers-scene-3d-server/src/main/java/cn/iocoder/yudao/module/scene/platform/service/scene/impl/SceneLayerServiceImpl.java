package cn.iocoder.yudao.module.scene.platform.service.scene.impl;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.util.json.JsonUtils;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLayerFilterRuleVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLayerRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLayerSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.actor.ActorInstanceDO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneLayerDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.actor.ActorInstanceMapper;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.scene.SceneLayerMapper;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneLayerService;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

@Service
public class SceneLayerServiceImpl implements SceneLayerService {

    @Resource
    private SceneLayerMapper sceneLayerMapper;
    @Resource
    private ActorInstanceMapper actorInstanceMapper;
    @Resource
    private SceneService sceneService;

    @Override
    public List<SceneLayerRespVO> getSceneLayers(String sceneCode) {
        Long sceneId = validateAndGetSceneId(sceneCode);
        return sceneLayerMapper.selectListBySceneId(sceneId).stream()
                .map(this::convertToRespVO)
                .toList();
    }

    @Override
    public SceneLayerRespVO getSceneLayer(String sceneCode, String layerKey) {
        Long sceneId = validateAndGetSceneId(sceneCode);
        SceneLayerDO layer = sceneLayerMapper.selectBySceneIdAndLayerKey(sceneId, layerKey);
        if (layer == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景图层不存在");
        }
        return convertToRespVO(layer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSceneLayer(String sceneCode, SceneLayerSaveReqVO reqVO) {
        Long sceneId = validateAndGetSceneId(sceneCode);
        SceneLayerDO layer = convertToDO(reqVO);
        layer.setSceneId(sceneId);
        sceneLayerMapper.insert(layer);
        return layer.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSceneLayer(String sceneCode, String layerKey, SceneLayerSaveReqVO reqVO) {
        Long sceneId = validateAndGetSceneId(sceneCode);
        SceneLayerDO existed = sceneLayerMapper.selectBySceneIdAndLayerKey(sceneId, layerKey);
        if (existed == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景图层不存在");
        }
        SceneLayerDO updateObj = convertToDO(reqVO);
        updateObj.setId(existed.getId());
        updateObj.setSceneId(sceneId);
        sceneLayerMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSceneLayer(String sceneCode, String layerKey) {
        Long sceneId = validateAndGetSceneId(sceneCode);
        SceneLayerDO existed = sceneLayerMapper.selectBySceneIdAndLayerKey(sceneId, layerKey);
        if (existed == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景图层不存在");
        }
        sceneLayerMapper.deleteById(existed.getId());
    }

    @Override
    public List<ActorInstanceDO> getLayerInstances(String sceneCode, String layerKey) {
        Long sceneId = validateAndGetSceneId(sceneCode);
        return actorInstanceMapper.selectListByLayer(sceneId, layerKey);
    }

    @Override
    public PageResult<ActorInstanceDO> getLayerInstancesPaginated(String sceneCode, String layerKey, int pageNo, int pageSize) {
        List<ActorInstanceDO> instances = getLayerInstances(sceneCode, layerKey);
        if (instances.isEmpty()) {
            return PageResult.empty();
        }
        int fromIndex = Math.max((pageNo - 1) * pageSize, 0);
        if (fromIndex >= instances.size()) {
            return new PageResult<>(List.of(), (long) instances.size());
        }
        int toIndex = Math.min(fromIndex + pageSize, instances.size());
        return new PageResult<>(instances.subList(fromIndex, toIndex), (long) instances.size());
    }

    private Long validateAndGetSceneId(String sceneCode) {
        SceneRespVO scene = sceneService.getSceneByCode(sceneCode);
        if (scene == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "场景不存在");
        }
        return scene.getId();
    }

    private SceneLayerRespVO convertToRespVO(SceneLayerDO layer) {
        SceneLayerRespVO respVO = new SceneLayerRespVO();
        respVO.setId(layer.getId());
        respVO.setLayerCode(layer.getLayerCode());
        respVO.setLayerName(layer.getLayerName());
        respVO.setLayerDisplayName(layer.getLayerDisplayName());
        respVO.setLayerType(layer.getLayerType());
        respVO.setLayerConfigJson(layer.getLayerConfigJson());
        respVO.setVisibleFlag(layer.getVisibleFlag());
        respVO.setInteractiveFlag(layer.getInteractiveFlag());
        respVO.setSortNo(layer.getSortNo());
        respVO.setInstanceIds(JsonUtils.parseArray(layer.getInstanceIdsJson(), Long.class));
        respVO.setFilterRules(JsonUtils.parseObject(layer.getFilterRulesJson(), new TypeReference<List<SceneLayerFilterRuleVO>>() {}));
        return respVO;
    }

    private SceneLayerDO convertToDO(SceneLayerSaveReqVO reqVO) {
        SceneLayerDO layer = new SceneLayerDO();
        layer.setLayerCode(reqVO.getLayerCode());
        layer.setLayerName(reqVO.getLayerName());
        layer.setLayerDisplayName(reqVO.getLayerDisplayName());
        layer.setLayerType(reqVO.getLayerType());
        layer.setLayerConfigJson(reqVO.getLayerConfigJson());
        layer.setVisibleFlag(reqVO.getVisibleFlag());
        layer.setInteractiveFlag(reqVO.getInteractiveFlag());
        layer.setSortNo(reqVO.getSortNo());
        layer.setInstanceIdsJson(JsonUtils.toJsonString(reqVO.getInstanceIds()));
        layer.setFilterRulesJson(JsonUtils.toJsonString(reqVO.getFilterRules()));
        return layer;
    }
}
