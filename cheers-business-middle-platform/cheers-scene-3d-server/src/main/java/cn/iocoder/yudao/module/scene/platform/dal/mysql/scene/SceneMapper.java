package cn.iocoder.yudao.module.scene.platform.dal.mysql.scene;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ScenePageReqVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.scene.SceneDO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SceneMapper extends BaseMapperX<SceneDO> {

    default SceneDO selectByCode(String sceneCode) {
        return selectOne(SceneDO::getSceneCode, sceneCode);
    }

    default SceneDO selectBySceneCode(String sceneCode) {
        return selectOne(SceneDO::getSceneCode, sceneCode);
    }

    default IPage<SceneDO> selectPage(Page<SceneDO> page, ScenePageReqVO reqVO) {
        LambdaQueryWrapperX<SceneDO> query = new LambdaQueryWrapperX<SceneDO>();
        query.likeIfPresent(SceneDO::getSceneName, reqVO.getSceneName())
                .eqIfPresent(SceneDO::getSceneCode, reqVO.getSceneCode())
                .eqIfPresent(SceneDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(SceneDO::getStatus, reqVO.getStatus())
                .eqIfPresent(SceneDO::getPublishStatus, reqVO.getPublishStatus())
                .eqIfPresent(SceneDO::getProjectCode, reqVO.getProjectCode())
                .orderByDesc(SceneDO::getId);
        return selectPage(page, query);
    }

    default List<SceneDO> selectList(ScenePageReqVO reqVO) {
        LambdaQueryWrapperX<SceneDO> query = new LambdaQueryWrapperX<SceneDO>();
        query.likeIfPresent(SceneDO::getSceneName, reqVO.getSceneName())
                .eqIfPresent(SceneDO::getSceneCode, reqVO.getSceneCode())
                .eqIfPresent(SceneDO::getSceneType, reqVO.getSceneType())
                .eqIfPresent(SceneDO::getStatus, reqVO.getStatus())
                .eqIfPresent(SceneDO::getPublishStatus, reqVO.getPublishStatus())
                .eqIfPresent(SceneDO::getProjectCode, reqVO.getProjectCode())
                .orderByDesc(SceneDO::getId);
        return selectList(query);
    }

    default List<SceneDO> selectByStatus(Integer status) {
        return selectList(SceneDO::getStatus, status);
    }

    default List<SceneDO> selectListByStatus(Integer status) {
        return selectList(SceneDO::getStatus, status);
    }

    default List<SceneDO> selectByProjectCode(String projectCode) {
        return selectList(SceneDO::getProjectCode, projectCode);
    }

    default List<SceneDO> selectListByProjectCode(String projectCode) {
        return selectList(SceneDO::getProjectCode, projectCode);
    }

    default List<SceneDO> selectByPublishStatus(String publishStatus) {
        return selectList(SceneDO::getPublishStatus, publishStatus);
    }

    default List<SceneDO> selectListByPublishStatus(String publishStatus) {
        return selectList(SceneDO::getPublishStatus, publishStatus);
    }
}
