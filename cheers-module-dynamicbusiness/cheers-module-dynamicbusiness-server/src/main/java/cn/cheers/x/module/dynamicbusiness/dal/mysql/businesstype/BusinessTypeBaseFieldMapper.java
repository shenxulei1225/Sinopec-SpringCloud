package cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeBaseFieldDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessTypeBaseFieldMapper extends BaseMapperX<BusinessTypeBaseFieldDO> {

    default List<BusinessTypeBaseFieldDO> selectByBusinessTypeCode(String businessTypeCode) {
        return selectList(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .eq(BusinessTypeBaseFieldDO::getStatus, 1)
                .orderByAsc(BusinessTypeBaseFieldDO::getSortOrder));
    }

    default List<BusinessTypeBaseFieldDO> selectAllByBusinessTypeCode(String businessTypeCode) {
        return selectList(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .orderByAsc(BusinessTypeBaseFieldDO::getSortOrder));
    }

    default BusinessTypeBaseFieldDO selectByBusinessTypeCodeAndFieldCode(String businessTypeCode, String fieldCode) {
        return selectOne(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .eq(BusinessTypeBaseFieldDO::getFieldCode, fieldCode));
    }

    default boolean existsByFieldCode(String businessTypeCode, String fieldCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .eq(BusinessTypeBaseFieldDO::getFieldCode, fieldCode)
                .neIfPresent(BusinessTypeBaseFieldDO::getId, excludeId)) > 0;
    }

    default Integer selectMaxSortOrder(String businessTypeCode) {
        BusinessTypeBaseFieldDO field = selectOne(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .orderByDesc(BusinessTypeBaseFieldDO::getSortOrder)
                .last("LIMIT 1"));
        return field == null ? 0 : field.getSortOrder();
    }

    default Long countByBusinessTypeCode(String businessTypeCode) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeBaseFieldDO>()
                .eq(BusinessTypeBaseFieldDO::getBusinessTypeCode, businessTypeCode)
                .eq(BusinessTypeBaseFieldDO::getStatus, 1));
    }
}
