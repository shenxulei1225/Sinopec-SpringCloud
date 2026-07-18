package cn.cheers.x.module.dynamicbusiness.service.page;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PagePageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.page.PageDO;

import java.util.List;

/**
 * 页面管理 Service 接口
 */
public interface PageService {

    Long create(PageSaveReqVO reqVO);

    void update(PageSaveReqVO reqVO);

    void delete(Long id);

    PageDO get(Long id);

    PageResult<PageDO> getPage(PagePageReqVO reqVO);

    List<PageDO> getListByParentMenuId(Long parentMenuId, Integer status);

    void updateStatus(Long id, Integer status);
}

