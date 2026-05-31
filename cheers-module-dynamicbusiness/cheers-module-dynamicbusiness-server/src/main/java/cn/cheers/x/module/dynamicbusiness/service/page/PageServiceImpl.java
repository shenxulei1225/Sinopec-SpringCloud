package cn.cheers.x.module.dynamicbusiness.service.page;

import cn.hutool.core.util.ObjUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PagePageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.page.vo.PageSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.page.PageConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.page.PageDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.page.PageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.PAGE_CONFIG_NOT_EXISTS;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.PAGE_CONFIG_PAGE_CODE_EXISTS;

/**
 * 页面管理 Service 实现
 */
@Service
@Validated
public class PageServiceImpl implements PageService {

    @Resource
    private PageMapper pageMapper;

    @Override
    public Long create(PageSaveReqVO reqVO) {
        validatePageCodeUnique(reqVO.getPageCode(), null);
        PageDO page = PageConvert.INSTANCE.convert(reqVO);
        pageMapper.insert(page);
        return page.getId();
    }

    @Override
    public void update(PageSaveReqVO reqVO) {
        PageDO existing = validateExists(reqVO.getId());
        validatePageCodeUnique(reqVO.getPageCode(), existing.getId());
        PageDO updateObj = PageConvert.INSTANCE.convertUpdate(reqVO);
        pageMapper.updateById(updateObj);
    }

    @Override
    public void delete(Long id) {
        validateExists(id);
        pageMapper.deleteById(id);
    }

    @Override
    public PageDO get(Long id) {
        return pageMapper.selectById(id);
    }

    @Override
    public PageResult<PageDO> getPage(PagePageReqVO reqVO) {
        return pageMapper.selectPage(reqVO);
    }

    @Override
    public List<PageDO> getListByParentMenuId(Long parentMenuId, Integer status) {
        return pageMapper.selectListByParentMenuIdAndStatus(parentMenuId, status);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        PageDO page = validateExists(id);
        if (ObjUtil.equal(page.getStatus(), status)) {
            return;
        }
        PageDO updateObj = new PageDO();
        updateObj.setId(id);
        updateObj.setStatus(status);
        pageMapper.updateById(updateObj);
    }

    private PageDO validateExists(Long id) {
        PageDO page = pageMapper.selectById(id);
        if (page == null) {
            throw exception(PAGE_CONFIG_NOT_EXISTS);
        }
        return page;
    }

    private void validatePageCodeUnique(String pageCode, Long excludeId) {
        if (pageMapper.existsByPageCode(pageCode, excludeId)) {
            throw exception(PAGE_CONFIG_PAGE_CODE_EXISTS, pageCode);
        }
    }
}
