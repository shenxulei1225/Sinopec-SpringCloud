package cn.iocoder.yudao.module.system.api.permission;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.system.api.permission.dto.MenuRespDTO;
import cn.iocoder.yudao.module.system.api.permission.dto.MenuSaveReqDTO;
import cn.iocoder.yudao.module.system.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 菜单")
public interface MenuApi {

    String PREFIX = ApiConstants.PREFIX + "/menu";

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建菜单")
    @Parameter(name = "createReqDTO", description = "创建菜单请求")
    CommonResult<Long> createMenu(@RequestBody MenuSaveReqDTO createReqDTO);

    @PostMapping(PREFIX + "/update")
    @Operation(summary = "更新菜单")
    @Parameter(name = "updateReqDTO", description = "更新菜单请求")
    CommonResult<Boolean> updateMenu(@RequestBody MenuSaveReqDTO updateReqDTO);

    @PostMapping(PREFIX + "/delete")
    @Operation(summary = "删除菜单")
    @Parameter(name = "id", description = "菜单编号", required = true)
    CommonResult<Boolean> deleteMenu(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获取菜单")
    @Parameter(name = "id", description = "菜单编号", required = true)
    CommonResult<MenuRespDTO> getMenu(@RequestParam("id") Long id);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "获取菜单列表")
    @Parameter(name = "ids", description = "菜单编号数组", required = true)
    CommonResult<List<MenuRespDTO>> getMenuList(@RequestParam("ids") Collection<Long> ids);

    @GetMapping(PREFIX + "/get-by-parent-id-and-name")
    @Operation(summary = "根据父菜单和名称获取菜单")
    @Parameter(name = "parentId", description = "父菜单编号", required = true)
    @Parameter(name = "name", description = "菜单名称", required = true)
    CommonResult<MenuRespDTO> getMenuByParentIdAndName(@RequestParam("parentId") Long parentId,
                                                       @RequestParam("name") String name);

    @GetMapping(PREFIX + "/get-by-component-name")
    @Operation(summary = "根据组件名获取菜单")
    @Parameter(name = "componentName", description = "组件名", required = true)
    CommonResult<MenuRespDTO> getMenuByComponentName(@RequestParam("componentName") String componentName);

    @GetMapping(PREFIX + "/exists-by-path")
    @Operation(summary = "判断菜单路径是否存在")
    @Parameter(name = "path", description = "菜单路径", required = true)
    @Parameter(name = "excludeId", description = "排除的菜单编号")
    CommonResult<Boolean> existsByPath(@RequestParam("path") String path,
                                       @RequestParam(value = "excludeId", required = false) Long excludeId);

}
