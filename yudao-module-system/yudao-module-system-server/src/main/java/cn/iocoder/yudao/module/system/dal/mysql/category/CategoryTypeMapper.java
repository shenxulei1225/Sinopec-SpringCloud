package cn.iocoder.yudao.module.system.dal.mysql.category;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.system.dal.dataobject.category.CategoryTypeDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryTypeMapper extends BaseMapperX<CategoryTypeDO> {

    default CategoryTypeDO selectByCategoryTypeCode(String categoryTypeCode) {
        return selectOne(new LambdaQueryWrapperX<CategoryTypeDO>()
                .eq(CategoryTypeDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryTypeDO::getDeleted, false));
    }

    default Long selectCountByCategoryTypeCode(String categoryTypeCode) {
        return selectCount(new LambdaQueryWrapperX<CategoryTypeDO>()
                .eq(CategoryTypeDO::getCategoryTypeCode, categoryTypeCode)
                .eq(CategoryTypeDO::getDeleted, false));
    }

    default List<CategoryTypeDO> selectEnabledList() {
        return selectList(new LambdaQueryWrapperX<CategoryTypeDO>()
                .eq(CategoryTypeDO::getStatus, 1)
                .eq(CategoryTypeDO::getDeleted, false)
                .orderByAsc(CategoryTypeDO::getId));
    }

    default List<CategoryTypeDO> selectByCreator(String creator) {
        return selectList(new LambdaQueryWrapperX<CategoryTypeDO>()
                .eq(CategoryTypeDO::getCreator, creator)
                .eq(CategoryTypeDO::getDeleted, false)
                .orderByDesc(CategoryTypeDO::getCreateTime));
    }

    default List<CategoryTypeDO> searchLike(String keyword) {
        return selectList(new LambdaQueryWrapperX<CategoryTypeDO>()
                .and(StringUtils.isNotBlank(keyword), q -> q.like(CategoryTypeDO::getName, keyword)
                        .or().like(CategoryTypeDO::getCategoryTypeCode, keyword)
                        .or().like(CategoryTypeDO::getDescription, keyword))
                .eq(CategoryTypeDO::getDeleted, false)
                .orderByDesc(CategoryTypeDO::getCreateTime));
    }
}
