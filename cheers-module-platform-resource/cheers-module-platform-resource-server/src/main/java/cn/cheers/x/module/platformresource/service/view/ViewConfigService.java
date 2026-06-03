package cn.cheers.x.module.platformresource.service.view;

import cn.cheers.x.module.platformresource.controller.admin.view.vo.*;

import java.util.List;

public interface ViewConfigService {

    ViewConfigRespVO getViewConfig(Long viewId);

    ViewConfigRespVO getViewConfigByCode(String viewCode);

    List<ViewConfigRespVO> getViewConfigList(ViewConfigListReqVO reqVO);

    Long createTemplate(ViewConfigCreateTemplateReqVO reqVO);

    Long createInstance(ViewConfigCreateInstanceReqVO reqVO);

    void saveViewConfig(Long viewId, ViewConfigSaveReqVO reqVO);

    void deleteViewConfig(Long viewId);
}
