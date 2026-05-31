package cn.iocoder.yudao.module.system.dal.mysql.view;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.view.ViewDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 视图配置 Mapper
 */
@Mapper
public interface ViewMapper extends BaseMapperX<ViewDO> {

    /**
     * 根据 key 查询
     */
    default ViewDO selectByKey(String key) {
        return selectOne(new LambdaQueryWrapperX<ViewDO>()
                .eq(ViewDO::getKey, key));
    }


    /**
     * 查询所有视图
     */
    default List<ViewDO> selectAllList() {
        return selectList(new LambdaQueryWrapperX<ViewDO>()
                .orderByAsc(ViewDO::getSort));
    }

    /**
     * 查询所有启用的视图
     */
    default List<ViewDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<ViewDO>()
                .eq(ViewDO::getStatus, 1)
                .orderByAsc(ViewDO::getSort));
    }

    /**
     * 查询所有模板视图
     */
    default List<ViewDO> selectTemplateList() {
        return selectList(new LambdaQueryWrapperX<ViewDO>()
                .eq(ViewDO::getIsTemplate, true)
                .orderByAsc(ViewDO::getSort));
    }

    /**
     * 判断 key 是否存在
     */
    default boolean existsByKey(String key) {
        return selectCount(new LambdaQueryWrapperX<ViewDO>()
                .eq(ViewDO::getKey, key)) > 0;
    }
}
