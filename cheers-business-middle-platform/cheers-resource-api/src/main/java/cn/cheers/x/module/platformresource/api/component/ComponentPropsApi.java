package cn.cheers.x.module.platformresource.api.component;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentPropsCreateTemplateReqDTO;
import cn.cheers.x.module.platformresource.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 跨模块：创建组件展示配置模板。
 * <p>
 * 数据管理创建目录实例化布局时调用，一次写好 propsId，禁止前端再补权威展示配置。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 组件 Props")
public interface ComponentPropsApi {

    String PREFIX = ApiConstants.PREFIX + "/component-props";

    @PostMapping(PREFIX + "/create-template")
    @Operation(summary = "创建组件 Props 模板")
    CommonResult<Long> createTemplate(@Valid @RequestBody ComponentPropsCreateTemplateReqDTO reqDTO);
}
