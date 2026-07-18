package cn.cheers.x.scene.platform.service.scene.impl;

import cn.cheers.x.scene.platform.controller.admin.scene.vo.ActorRuntimeRespVO;
import cn.cheers.x.scene.platform.model.Transform;
import cn.cheers.x.scene.platform.service.scene.SceneRuntimeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class SceneRuntimeServiceImpl implements SceneRuntimeService {

    private static final String RUNTIME_PREFIX = "scene:runtime:";
    private static final String ONLINE_COUNT_KEY = "scene:onlineCount:";
    private static final Duration DEFAULT_EXPIRE = Duration.ofHours(24);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ObjectMapper objectMapper;

    @Override
    public void updateActorRuntime(String sceneCode, Long instanceId, String instanceCode, Transform transform, String status, String actorCategory) {
        String redisKey = getActorRuntimeRedisKey(sceneCode, instanceCode);
        try {
            Map<String, Object> data = Map.of(
                    "sceneCode", sceneCode,
                    "instanceId", instanceId,
                    "instanceCode", instanceCode,
                    "transform", transform,
                    "status", status,
                    "actorCategory", actorCategory,
                    "timestamp", System.currentTimeMillis()
            );
            String json = objectMapper.writeValueAsString(data);
            stringRedisTemplate.opsForValue().set(redisKey, json, DEFAULT_EXPIRE);
            stringRedisTemplate.opsForSet().add(getSceneActorSetKey(sceneCode), instanceCode);
            stringRedisTemplate.expire(getSceneActorSetKey(sceneCode), DEFAULT_EXPIRE);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize actor runtime data, sceneCode={}, instanceCode={}", sceneCode, instanceCode, e);
        }
    }

    @Override
    public ActorRuntimeRespVO getActorRuntime(String sceneCode, String instanceCode) {
        String redisKey = getActorRuntimeRedisKey(sceneCode, instanceCode);
        String json = stringRedisTemplate.opsForValue().get(redisKey);
        if (json == null) {
            return null;
        }
        try {
            Map<String, Object> data = objectMapper.readValue(json, new TypeReference<>() {});
            return toActorRuntimeRespVO(data);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize actor runtime data, sceneCode={}, instanceCode={}", sceneCode, instanceCode, e);
            return null;
        }
    }

    @Override
    public List<ActorRuntimeRespVO> getAllActorsRuntime(String sceneCode) {
        String setKey = getSceneActorSetKey(sceneCode);
        var members = stringRedisTemplate.opsForSet().members(setKey);
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        List<ActorRuntimeRespVO> result = new ArrayList<>();
        for (String instanceCode : members) {
            ActorRuntimeRespVO vo = getActorRuntime(sceneCode, instanceCode);
            if (vo != null) {
                result.add(vo);
            }
        }
        return result;
    }

    @Override
    public void removeActorRuntime(String sceneCode, String instanceCode) {
        stringRedisTemplate.delete(getActorRuntimeRedisKey(sceneCode, instanceCode));
        stringRedisTemplate.opsForSet().remove(getSceneActorSetKey(sceneCode), instanceCode);
    }

    @Override
    public void clearSceneRuntime(String sceneCode) {
        String setKey = getSceneActorSetKey(sceneCode);
        var members = stringRedisTemplate.opsForSet().members(setKey);
        if (members != null) {
            for (String instanceCode : members) {
                stringRedisTemplate.delete(getActorRuntimeRedisKey(sceneCode, instanceCode));
            }
        }
        stringRedisTemplate.delete(setKey);
        stringRedisTemplate.delete(getOnlineCountRedisKey(sceneCode));
    }

    @Override
    public void updateSceneOnlineCount(String sceneCode, int delta) {
        String redisKey = getOnlineCountRedisKey(sceneCode);
        Long count = stringRedisTemplate.opsForValue().increment(redisKey, delta);
        if (count != null && count < 0) {
            stringRedisTemplate.opsForValue().set(redisKey, "0", DEFAULT_EXPIRE);
        } else {
            stringRedisTemplate.expire(redisKey, DEFAULT_EXPIRE);
        }
    }

    @Override
    public int getSceneOnlineCount(String sceneCode) {
        String redisKey = getOnlineCountRedisKey(sceneCode);
        Object value = stringRedisTemplate.opsForValue().get(redisKey);
        if (value == null) {
            return 0;
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Failed to parse online count for scene: {}, value: {}", sceneCode, value);
            return 0;
        }
    }

    private String getActorRuntimeRedisKey(String sceneCode, String instanceCode) {
        return RUNTIME_PREFIX + sceneCode + ":actor:" + instanceCode;
    }

    private String getSceneActorSetKey(String sceneCode) {
        return RUNTIME_PREFIX + sceneCode + ":actors";
    }

    private String getOnlineCountRedisKey(String sceneCode) {
        return ONLINE_COUNT_KEY + sceneCode;
    }

    private ActorRuntimeRespVO toActorRuntimeRespVO(Map<String, Object> data) {
        Long instanceId = data.get("instanceId") instanceof Number number ? number.longValue() : null;
        String instanceCode = (String) data.get("instanceCode");
        String actorCategory = (String) data.get("actorCategory");
        String status = (String) data.get("status");
        Transform transform = objectMapper.convertValue(data.get("transform"), Transform.class);
        ActorRuntimeRespVO vo = ActorRuntimeRespVO.fromTransform(instanceId, instanceCode, transform, status);
        vo.setActorCategory(actorCategory);
        return vo;
    }
}
