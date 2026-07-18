package cn.cheers.x.module.dynamicbusiness.dal.mysql.business;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.business.BusinessEntryDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessEntryMapper extends BaseMapperX<BusinessEntryDO> {

    default List<BusinessEntryDO> selectByBusinessId(Long businessId) {
        return selectList(new LambdaQueryWrapperX<BusinessEntryDO>()
                .eq(BusinessEntryDO::getBusinessId, businessId)
                .eq(BusinessEntryDO::getDeleted, false)
                .orderByAsc(BusinessEntryDO::getSort)
                .orderByAsc(BusinessEntryDO::getId));
    }

    default BusinessEntryDO selectByBusinessIdAndCode(Long businessId, String code) {
        return selectOne(new LambdaQueryWrapperX<BusinessEntryDO>()
                .eq(BusinessEntryDO::getBusinessId, businessId)
                .eq(BusinessEntryDO::getCode, code)
                .eq(BusinessEntryDO::getDeleted, false));
    }

    default boolean existsByBusinessIdAndCodeExcludeId(Long businessId, String code, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<BusinessEntryDO>()
                .eq(BusinessEntryDO::getBusinessId, businessId)
                .eq(BusinessEntryDO::getCode, code)
                .ne(BusinessEntryDO::getId, excludeId)
                .eq(BusinessEntryDO::getDeleted, false)) > 0;
    }
}
