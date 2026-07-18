package cn.cheers.x.module.platformresource.service.component;

import cn.cheers.x.module.platformresource.controller.admin.component.vo.*;

import java.util.List;

public interface ComponentPropsService {

    ComponentPropsRespVO getProps(Long propsId);

    /** 按 propsId 批量查询，跳过不存在的 id（不抛错） */
    List<ComponentPropsRespVO> getPropsBatch(List<Long> propsIds);

    List<ComponentPropsRespVO> getPropsList(ComponentPropsListReqVO reqVO);

    Long createTemplate(ComponentPropsCreateTemplateReqVO reqVO);

    Long createInstance(ComponentPropsCreateInstanceReqVO reqVO);

    void saveProps(Long propsId, ComponentPropsSaveReqVO reqVO);

    void deleteProps(Long propsId);
}
