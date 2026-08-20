package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmDataTabLayoutSaveReqVO;

import java.util.List;

public interface DmDataTabLayoutService {

    /** 按布局实例/模版 id 读栏行 */
    List<DmDataTabLayoutRespVO> listByLayoutId(Long layoutId);

    /**
     * 兼容：按目录编码读「数据」页签布局（解析 dataLayoutId；必要时 bootstrap）。
     */
    List<DmDataTabLayoutRespVO> listByEntityTypeCode(String entityTypeCode);

    void saveLayouts(DmDataTabLayoutSaveReqVO reqVO);
}
