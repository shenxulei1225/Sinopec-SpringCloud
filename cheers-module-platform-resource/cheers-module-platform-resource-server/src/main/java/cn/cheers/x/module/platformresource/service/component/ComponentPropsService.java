package cn.cheers.x.module.platformresource.service.component;

import cn.cheers.x.module.platformresource.controller.admin.component.vo.*;

import java.util.List;

public interface ComponentPropsService {

    ComponentPropsRespVO getProps(Long propsId);

    List<ComponentPropsRespVO> getPropsList(ComponentPropsListReqVO reqVO);

    Long createTemplate(ComponentPropsCreateTemplateReqVO reqVO);

    Long createInstance(ComponentPropsCreateInstanceReqVO reqVO);

    void saveProps(Long propsId, ComponentPropsSaveReqVO reqVO);

    void deleteProps(Long propsId);
}
