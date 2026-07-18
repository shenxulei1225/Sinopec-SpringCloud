package cn.cheers.x.facility.management.dal.mysql;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.facility.management.dal.dataobject.SiteTypeDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 站场类型 Mapper
 */
@Mapper
public interface SiteTypeMapper extends BaseMapperX<SiteTypeDO> {

    /**
     * 根据类型编码查询
     */
    default SiteTypeDO selectByTypeCode(String typeCode) {
        return selectOne(new LambdaQueryWrapperX<SiteTypeDO>()
                .eq(SiteTypeDO::getTypeCode, typeCode));
    }

    /**
     * 获取所有正常状态的类型列表
     */
    default List<SiteTypeDO> selectNormalList() {
        return selectList(new LambdaQueryWrapperX<SiteTypeDO>()
                .eq(SiteTypeDO::getStatus, 0)
                .orderByAsc(SiteTypeDO::getSortNo)
                .orderByAsc(SiteTypeDO::getId));
    }

    /**
     * 获取所有类型列表
     */
    default List<SiteTypeDO> selectAllList() {
        return selectList(new LambdaQueryWrapperX<SiteTypeDO>()
                .eq(SiteTypeDO::getDeleted, false)
                .orderByAsc(SiteTypeDO::getSortNo)
                .orderByAsc(SiteTypeDO::getId));
    }

}
