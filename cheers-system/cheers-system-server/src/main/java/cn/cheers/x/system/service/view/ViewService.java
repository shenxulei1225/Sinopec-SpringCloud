package cn.cheers.x.system.service.view;

import cn.cheers.x.system.controller.admin.view.vo.ViewCreateReqVO;
import cn.cheers.x.system.controller.admin.view.vo.ViewRespVO;
import cn.cheers.x.system.controller.admin.view.vo.ViewUpdateReqVO;

import java.util.List;
import java.util.Map;

/**
 * 视图配置 Service 接口
 */
public interface ViewService {

    /**
     * 获取所有启用的视图配置
     */
    Map<String, ViewRespVO> getEnabledViews();

    /**
     * 获取视图详情
     */
    ViewRespVO getView(String key);

    /**
     * 创建视图
     */
    Long createView(ViewCreateReqVO reqVO);

    /**
     * 更新视图配置
     */
    void updateView(String key, ViewUpdateReqVO reqVO);

    /**
     * 删除视图配置
     */
    void deleteView(String key);

    /**
     * 获取视图配置列表（管理后台）
     */
    List<ViewRespVO> getViewList();


}
