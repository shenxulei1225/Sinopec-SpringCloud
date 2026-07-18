package cn.cheers.x.system.api.permission;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.cheers.x.system.api.permission.dto.MenuRespDTO;
import cn.cheers.x.system.api.permission.dto.MenuSaveReqDTO;
import cn.cheers.x.system.dal.dataobject.permission.MenuDO;
import cn.cheers.x.system.service.permission.MenuService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@RestController
@Validated
@Slf4j
public class MenuApiImpl implements MenuApi {

    @Resource
    private MenuService menuService;

    @Override
    public CommonResult<Long> createMenu(MenuSaveReqDTO createReqDTO) {
        return success(menuService.createMenu(BeanUtils.toBean(createReqDTO, cn.cheers.x.system.controller.admin.permission.vo.menu.MenuSaveVO.class)));
    }

    @Override
    public CommonResult<Boolean> updateMenu(MenuSaveReqDTO updateReqDTO) {
        menuService.updateMenu(BeanUtils.toBean(updateReqDTO, cn.cheers.x.system.controller.admin.permission.vo.menu.MenuSaveVO.class));
        return success(true);
    }

    @Override
    public CommonResult<Boolean> deleteMenu(Long id) {
        menuService.deleteMenu(id);
        return success(true);
    }

    @Override
    public CommonResult<MenuRespDTO> getMenu(Long id) {
        MenuDO menu = menuService.getMenu(id);
        return success(BeanUtils.toBean(menu, MenuRespDTO.class));
    }

    @Override
    public CommonResult<List<MenuRespDTO>> getMenuList(java.util.Collection<Long> ids) {
        return success(BeanUtils.toBean(menuService.getMenuList(ids), MenuRespDTO.class));
    }

    @Override
    public CommonResult<MenuRespDTO> getMenuByParentIdAndName(Long parentId, String name) {
        // 复用服务层的校验方法逻辑，直接通过列表过滤较重，这里暂时只提供基础实现
        List<MenuDO> menuList = menuService.getMenuList();
        return success(menuList.stream()
                .filter(menu -> java.util.Objects.equals(menu.getParentId(), parentId)
                        && java.util.Objects.equals(menu.getName(), name))
                .findFirst()
                .map(menu -> BeanUtils.toBean(menu, MenuRespDTO.class))
                .orElse(null));
    }

    @Override
    public CommonResult<MenuRespDTO> getMenuByComponentName(String componentName) {
        List<MenuDO> menuList = menuService.getMenuList();
        return success(menuList.stream()
                .filter(menu -> java.util.Objects.equals(menu.getComponentName(), componentName))
                .findFirst()
                .map(menu -> BeanUtils.toBean(menu, MenuRespDTO.class))
                .orElse(null));
    }

    @Override
    public CommonResult<Boolean> existsByPath(String path, Long excludeId) {
        List<MenuDO> menuList = menuService.getMenuList();
        boolean exists = menuList.stream().anyMatch(menu -> java.util.Objects.equals(menu.getPath(), path)
                && (excludeId == null || !java.util.Objects.equals(menu.getId(), excludeId)));
        return success(exists);
    }

}
