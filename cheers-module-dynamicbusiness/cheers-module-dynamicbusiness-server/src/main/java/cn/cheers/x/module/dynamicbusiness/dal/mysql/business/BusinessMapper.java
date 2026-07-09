package cn.cheers.x.module.dynamicbusiness.dal.mysql.business;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessMapper extends BaseMapperX<BusinessDO> {

    default BusinessDO selectByCode(String code) {
        return selectOne(new LambdaQueryWrapperX<BusinessDO>()
                .eq(BusinessDO::getCode, code)
                .eq(BusinessDO::getDeleted, false));
    }

    default boolean existsByCode(String code) {
        return selectCount(new LambdaQueryWrapperX<BusinessDO>()
                .eq(BusinessDO::getCode, code)
                .eq(BusinessDO::getDeleted, false)) > 0;
    }

    default boolean existsByCodeExcludeId(String code, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<BusinessDO>()
                .eq(BusinessDO::getCode, code)
                .ne(BusinessDO::getId, excludeId)
                .eq(BusinessDO::getDeleted, false)) > 0;
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(new LambdaQueryWrapperX<BusinessDO>()
                .eq(BusinessDO::getParentId, parentId)
                .eq(BusinessDO::getDeleted, false));
    }

    default List<BusinessDO> selectAllList() {
        return selectList(new LambdaQueryWrapperX<BusinessDO>()
                .eq(BusinessDO::getDeleted, false)
                .orderByAsc(BusinessDO::getSort)
                .orderByDesc(BusinessDO::getCreateTime));
    }
}
