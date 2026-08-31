package cn.cheers.x.module.platformresource.api.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentDataSourceDTO;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentPropsCreateTemplateReqDTO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentDataSourceVO;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentPropsCreateTemplateReqVO;
import cn.cheers.x.module.platformresource.service.component.ComponentPropsService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 组件 Props RPC：委托后台 createTemplate，供 dynamicbusiness 创建布局时回填 propsId。
 * <p>
 * 不负责：布局行写入、栏身份。
 */
@RestController
@Validated
public class ComponentPropsApiImpl implements ComponentPropsApi {

    @Resource
    private ComponentPropsService componentPropsService;

    @Override
    public CommonResult<Long> createTemplate(ComponentPropsCreateTemplateReqDTO reqDTO) {
        ComponentPropsCreateTemplateReqVO vo = new ComponentPropsCreateTemplateReqVO();
        vo.setComponentCode(reqDTO.getComponentCode());
        vo.setSchemaVersion(reqDTO.getSchemaVersion());
        vo.setProps(reqDTO.getProps());
        vo.setName(reqDTO.getName());
        vo.setStatus(reqDTO.getStatus());
        vo.setSort(reqDTO.getSort());
        vo.setDescription(reqDTO.getDescription());
        vo.setDataSource(toVo(reqDTO.getDataSource()));
        return success(componentPropsService.createTemplate(vo));
    }

    private static ComponentDataSourceVO toVo(ComponentDataSourceDTO dto) {
        if (dto == null) {
            return null;
        }
        ComponentDataSourceVO vo = new ComponentDataSourceVO();
        vo.setBusinessCategory(dto.getBusinessCategory());
        vo.setEntityTypeCode(dto.getEntityTypeCode());
        vo.setDataKind(dto.getDataKind());
        return vo;
    }
}
