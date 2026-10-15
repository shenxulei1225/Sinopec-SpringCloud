package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantCatalogRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantItemReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeSimpleVO;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantItem;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 平台配置「角色与权限」：列出本租户数据目录，并保存每个目录下分类、模型、实体、详情的操作。
 * 不分配旧菜单。不判断实体表。不创建数据目录。不改其它页面的权限。
 */
@Tag(name = "管理后台 - 数据目录角色授权")
@RestController
@RequestMapping("/dynamicbusiness/catalog-role-grant")
@Validated
public class CatalogRoleGrantController {

    @Resource
    private CatalogRoleGrantService catalogRoleGrantService;

    @Resource
    private EntityTypeService entityTypeService;

    /**
     * 角色页右侧列出本租户已有的数据目录。
     * 只返回注册编码和名称。不创建、不修改目录。
     * 能查看本租户角色的人可以列这些目录。管理目录本身仍走数据目录接口。
     */
    @GetMapping("/catalogs")
    @Operation(summary = "列出本租户数据目录，供角色页勾选")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<List<CatalogRoleGrantCatalogRespVO>> catalogs() {
        List<CatalogRoleGrantCatalogRespVO> rows = new ArrayList<>();
        for (EntityTypeSimpleVO item : entityTypeService.listSimple()) {
            if (item.getCode() == null || item.getCode().isBlank()) {
                continue;
            }
            CatalogRoleGrantCatalogRespVO row = new CatalogRoleGrantCatalogRespVO();
            row.setCode(item.getCode().trim());
            row.setName(item.getName() == null ? "" : item.getName().trim());
            rows.add(row);
        }
        return success(rows);
    }

    @GetMapping("/list")
    @Operation(summary = "读取角色已勾选的数据目录操作")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<List<CatalogRoleGrantItemRespVO>> list(@RequestParam("roleId") Long roleId) {
        return success(toResp(catalogRoleGrantService.listByRoleId(roleId)));
    }

    @PutMapping("/save")
    @Operation(summary = "整份保存角色的数据目录操作")
    @PreAuthorize("@ss.hasPermission('system:permission:assign-role-menu')")
    public CommonResult<Boolean> save(@Valid @RequestBody CatalogRoleGrantSaveReqVO reqVO) {
        catalogRoleGrantService.replaceRoleGrants(reqVO.getRoleId(), toItems(reqVO.getItems()));
        return success(true);
    }

    private static List<CatalogRoleGrantItem> toItems(List<CatalogRoleGrantItemReqVO> items) {
        if (items == null) {
            return List.of();
        }
        List<CatalogRoleGrantItem> result = new ArrayList<>();
        for (CatalogRoleGrantItemReqVO item : items) {
            result.add(new CatalogRoleGrantItem(item.getRegistryCode(), item.getSubject(), item.getOperations()));
        }
        return result;
    }

    private static List<CatalogRoleGrantItemRespVO> toResp(List<CatalogRoleGrantItem> items) {
        List<CatalogRoleGrantItemRespVO> result = new ArrayList<>();
        for (CatalogRoleGrantItem item : items) {
            CatalogRoleGrantItemRespVO row = new CatalogRoleGrantItemRespVO();
            row.setRegistryCode(item.registryCode());
            row.setSubject(item.subject());
            row.setOperations(item.operations());
            result.add(row);
        }
        return result;
    }
}
