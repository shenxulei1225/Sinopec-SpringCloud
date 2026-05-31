package cn.cheers.x.module.dynamicbusiness.dal.mysql.businesstype;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.businesstype.BusinessTypeRelationDO;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BusinessTypeRelationMapper extends BaseMapperX<BusinessTypeRelationDO> {

    default List<BusinessTypeRelationDO> selectBySourceBusinessTypeCode(String sourceBusinessTypeCode) {
        return selectList(new LambdaQueryWrapperX<BusinessTypeRelationDO>()
                .eq(BusinessTypeRelationDO::getSourceBusinessTypeCode, sourceBusinessTypeCode));
    }

    default List<BusinessTypeRelationDO> selectByTargetBusinessTypeCode(String targetBusinessTypeCode) {
        return selectList(new LambdaQueryWrapperX<BusinessTypeRelationDO>()
                .eq(BusinessTypeRelationDO::getTargetBusinessTypeCode, targetBusinessTypeCode));
    }

    default boolean existsBySourceAndTarget(String sourceBusinessTypeCode, String targetBusinessTypeCode) {
        return selectCount(new LambdaQueryWrapperX<BusinessTypeRelationDO>()
                .eq(BusinessTypeRelationDO::getSourceBusinessTypeCode, sourceBusinessTypeCode)
                .eq(BusinessTypeRelationDO::getTargetBusinessTypeCode, targetBusinessTypeCode)) > 0;
    }
}
