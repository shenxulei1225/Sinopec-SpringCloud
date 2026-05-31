package cn.cheers.x.module.platformresource.service.component;

import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentCreateReqVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentRespVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentUpdateReqVO;

import java.util.List;
import java.util.Map;

public interface ComponentService {

    Map<String, ComponentRespVO> getEnabledComponents();

    ComponentRespVO getComponent(String key);

    List<ComponentRespVO> getComponentList();

    Long createComponent(ComponentCreateReqVO reqVO);

    void updateComponent(String key, ComponentUpdateReqVO reqVO);

    void deleteComponent(String key);
}