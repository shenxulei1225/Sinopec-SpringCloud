package cn.cheers.x.module.dynamicbusiness.dal.mysql.pagegrant;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pagegrant.PageRoleGrantDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 页面角色授权。
 * 只读写本表。不查菜单，不查数据目录授权。
 */
@Mapper
public interface PageRoleGrantMapper extends BaseMapperX<PageRoleGrantDO> {

    default List<PageRoleGrantDO> selectListByRoleId(Long roleId) {
        return selectList(new LambdaQueryWrapperX<PageRoleGrantDO>()
                .eq(PageRoleGrantDO::getRoleId, roleId));
    }

    default void deleteByRoleId(Long roleId) {
        delete(new LambdaQueryWrapperX<PageRoleGrantDO>()
                .eq(PageRoleGrantDO::getRoleId, roleId));
    }
}
