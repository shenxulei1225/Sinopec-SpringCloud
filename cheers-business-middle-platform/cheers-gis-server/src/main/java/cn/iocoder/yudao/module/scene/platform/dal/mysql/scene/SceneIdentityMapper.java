package cn.iocoder.yudao.module.scene.platform.dal.mysql.scene;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneIdentityDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SceneIdentityMapper extends BaseMapperX<SceneIdentityDO> {

    default SceneIdentityDO selectBySceneCode(String sceneCode) {
        return selectOne(SceneIdentityDO::getSceneCode, sceneCode);
    }
}
