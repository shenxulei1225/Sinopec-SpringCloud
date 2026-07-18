package cn.cheers.x.twin.dal.mysql;

import cn.cheers.x.twin.api.dto.TwinMappingDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

@Mapper
public interface TwinMappingMapper extends BaseMapper<TwinMappingDO> {

    default TwinMappingDO selectActiveByFacilityId(Long facilityId) {
        return selectOne(new LambdaQueryWrapper<TwinMappingDO>()
                .eq(TwinMappingDO::getFacilityId, facilityId)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false)
                .last("LIMIT 1"));
    }

    default TwinMappingDO selectActiveByActorInstanceId(Long actorInstanceId) {
        return selectOne(new LambdaQueryWrapper<TwinMappingDO>()
                .eq(TwinMappingDO::getActorInstanceId, actorInstanceId)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false)
                .last("LIMIT 1"));
    }

    default TwinMappingDO selectActiveById(Long id) {
        return selectOne(new LambdaQueryWrapper<TwinMappingDO>()
                .eq(TwinMappingDO::getId, id)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false)
                .last("LIMIT 1"));
    }

    default List<TwinMappingDO> selectActiveBySceneId(Long sceneId) {
        return selectList(new LambdaQueryWrapper<TwinMappingDO>()
                .eq(TwinMappingDO::getSceneId, sceneId)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false)
                .orderByAsc(TwinMappingDO::getId));
    }

    default List<TwinMappingDO> selectActiveBySceneCode(String sceneCode) {
        return selectList(new LambdaQueryWrapper<TwinMappingDO>()
                .eq(TwinMappingDO::getSceneCode, sceneCode)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false)
                .orderByAsc(TwinMappingDO::getId));
    }

    default List<TwinMappingDO> selectActiveByFacilityIds(Collection<Long> facilityIds) {
        if (facilityIds == null || facilityIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapper<TwinMappingDO>()
                .in(TwinMappingDO::getFacilityId, facilityIds)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false));
    }

    default List<TwinMappingDO> selectActiveByActorInstanceIds(Collection<Long> actorInstanceIds) {
        if (actorInstanceIds == null || actorInstanceIds.isEmpty()) {
            return List.of();
        }
        return selectList(new LambdaQueryWrapper<TwinMappingDO>()
                .in(TwinMappingDO::getActorInstanceId, actorInstanceIds)
                .eq(TwinMappingDO::getStatus, 1)
                .eq(TwinMappingDO::getDeleted, false));
    }
}
