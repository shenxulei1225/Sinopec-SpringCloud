package cn.cheers.x.module.dynamicbusiness.framework.category.mapper;

import cn.cheers.x.module.dynamicbusiness.framework.category.core.CategoryContract;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface FrameworkCategoryMapper<T extends CategoryContract<Long>> extends BaseMapperX<T> {

    List<T> selectByParentIdAndCategoryTypeCode(@Param("parentId") Long parentId,
                                                @Param("categoryTypeCode") String categoryTypeCode);

    List<T> selectByCategoryTypeCode(@Param("categoryTypeCode") String categoryTypeCode);

    T selectByIdAndCategoryTypeCode(@Param("id") Long id, @Param("categoryTypeCode") String categoryTypeCode);

    T selectByNameAndParent(@Param("name") String name,
                            @Param("parentId") Long parentId,
                            @Param("categoryTypeCode") String categoryTypeCode);

    T selectBySortAndParent(@Param("sort") Integer sort,
                            @Param("parentId") Long parentId,
                            @Param("categoryTypeCode") String categoryTypeCode);
}
