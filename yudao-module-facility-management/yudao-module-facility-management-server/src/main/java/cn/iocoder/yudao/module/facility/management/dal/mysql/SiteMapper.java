package cn.iocoder.yudao.module.facility.management.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.facility.management.dal.dataobject.SiteDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 站场 Mapper
 */
@Mapper
public interface SiteMapper extends BaseMapperX<SiteDO> {

    /**
     * 根据父ID查询子节点
     */
    default List<SiteDO> selectByParentId(Long parentId) {
        return selectList(new LambdaQueryWrapperX<SiteDO>()
                .eq(SiteDO::getParentId, parentId)
                .eq(SiteDO::getDeleted, false)
                .orderByAsc(SiteDO::getLevel)
                .orderByAsc(SiteDO::getSortNo)
                .orderByAsc(SiteDO::getSiteId));
    }

    /**
     * 根据站点编码查询
     */
    default SiteDO selectByCode(String siteCode) {
        return selectOne(new LambdaQueryWrapperX<SiteDO>()
                .eq(SiteDO::getSiteCode, siteCode)
                .eq(SiteDO::getDeleted, false));
    }

    /**
     * 获取所有顶级节点
     */
    default List<SiteDO> selectTopLevelSites() {
        return selectList(new LambdaQueryWrapperX<SiteDO>()
                .eq(SiteDO::getParentId, 0)
                .eq(SiteDO::getDeleted, false)
                .orderByAsc(SiteDO::getLevel)
                .orderByAsc(SiteDO::getSortNo)
                .orderByAsc(SiteDO::getSiteId));
    }

    /**
     * 根据站点名称查询
     */
    default SiteDO selectByName(String siteName) {
        return selectOne(new LambdaQueryWrapperX<SiteDO>()
                .eq(SiteDO::getSiteName, siteName)
                .eq(SiteDO::getDeleted, false));
    }

    /**
     * 根据名称和父ID查询
     */
    default SiteDO selectByNameAndParentId(String siteName, Long parentId) {
        return selectOne(new LambdaQueryWrapperX<SiteDO>()
                .eq(SiteDO::getSiteName, siteName)
                .eqIfPresent(SiteDO::getParentId, parentId)
                .eq(SiteDO::getDeleted, false));
    }

}
