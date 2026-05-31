package cn.cheers.x.module.dynamicbusiness.framework.tree.mapper;

import cn.cheers.x.module.dynamicbusiness.framework.tree.core.TreeContract;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FrameworkTreeMapper<T extends TreeContract<Long>> extends BaseMapperX<T> {

    List<T> selectByParentId(@Param("parentId") Long parentId);

    T selectByNameAndParent(@Param("name") String name, @Param("parentId") Long parentId);

    T selectBySortAndParent(@Param("sort") Integer sort, @Param("parentId") Long parentId);
}
