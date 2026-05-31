package cn.cheers.x.module.dynamicbusiness.dal.mysql.page;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PagePageReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.page.PageDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PageMapper extends BaseMapperX<PageDO> {

    default PageResult<PageDO> selectPage(PagePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<PageDO>()
                .likeIfPresent(PageDO::getPageName, reqVO.getPageName())
                .eqIfPresent(PageDO::getPageCode, reqVO.getPageCode())
                .eqIfPresent(PageDO::getPageType, reqVO.getPageType())
                .eqIfPresent(PageDO::getStatus, reqVO.getStatus())
                .eqIfPresent(PageDO::getParentMenuId, reqVO.getParentMenuId())
                .orderByDesc(PageDO::getId));
    }

    default boolean existsByPageCode(String pageCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getPageCode, pageCode)
                .neIfPresent(PageDO::getId, excludeId)) > 0;
    }

    default List<PageDO> selectListByParentMenuIdAndStatus(Long parentMenuId, Integer status) {
        return selectList(new LambdaQueryWrapperX<PageDO>()
                .eq(PageDO::getParentMenuId, parentMenuId)
                .eqIfPresent(PageDO::getStatus, status)
                .orderByAsc(PageDO::getId));
    }
}
