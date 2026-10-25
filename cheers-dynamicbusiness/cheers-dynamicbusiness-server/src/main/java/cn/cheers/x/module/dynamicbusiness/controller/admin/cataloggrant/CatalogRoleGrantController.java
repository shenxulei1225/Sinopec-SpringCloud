package cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogMyStationScopeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantCatalogRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantItemReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantItemRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleGrantSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleStationScopeReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.cataloggrant.vo.CatalogRoleStationScopeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeSimpleVO;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantItem;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogRoleGrantService;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogStationScope;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogStationScopeDecision;
import cn.cheers.x.module.dynamicbusiness.service.cataloggrant.CatalogStationScopeService;
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
    private CatalogStationScopeService catalogStationScopeService;

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
        catalogRoleGrantService.saveRoleCatalogPermission(
                reqVO.getRoleId(), toItems(reqVO.getItems()), toScopes(reqVO.getStationScopes()));
        return success(true);
    }

    @GetMapping("/station-scopes")
    @Operation(summary = "读取角色在各数据目录上的场站范围")
    @PreAuthorize("@ss.hasPermission('system:role:query')")
    public CommonResult<List<CatalogRoleStationScopeRespVO>> stationScopes(@RequestParam("roleId") Long roleId) {
        return success(toScopeResp(catalogStationScopeService.listByRoleId(roleId)));
    }

    /**
     * 数据管理打开某个目录时，用这份结果决定列表、表单和新建、修改、删除按钮。
     * 不在这里改角色。不从人员记录的所属场站反推。
     */
    @GetMapping("/my-station-scope")
    @Operation(summary = "当前登录人在一个数据目录上的场站范围")
    public CommonResult<CatalogMyStationScopeRespVO> myStationScope(@RequestParam("registryCode") String registryCode) {
        CatalogStationScopeDecision decision = catalogStationScopeService.resolveForLoginUser(registryCode);
        CatalogMyStationScopeRespVO row = new CatalogMyStationScopeRespVO();
        row.setViewScope(decision.viewScope());
        row.setWriteScope(decision.writeScope());
        row.setCanQuery(decision.canQuery());
        row.setCanCreate(decision.canCreate());
        row.setCanUpdate(decision.canUpdate());
        row.setCanDelete(decision.canDelete());
        row.setStationFieldCodes(catalogStationScopeService.facilityFieldCodes(registryCode));
        return success(row);
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

    /** null 表示这次保存不改场站范围。空列表表示全部改回全部场站。 */
    private static List<CatalogStationScope> toScopes(List<CatalogRoleStationScopeReqVO> scopes) {
        if (scopes == null) {
            return null;
        }
        List<CatalogStationScope> result = new ArrayList<>();
        for (CatalogRoleStationScopeReqVO scope : scopes) {
            result.add(new CatalogStationScope(scope.getRegistryCode(), scope.getViewScope(), scope.getWriteScope()));
        }
        return result;
    }

    private static List<CatalogRoleStationScopeRespVO> toScopeResp(List<CatalogStationScope> scopes) {
        List<CatalogRoleStationScopeRespVO> result = new ArrayList<>();
        for (CatalogStationScope scope : scopes) {
            CatalogRoleStationScopeRespVO row = new CatalogRoleStationScopeRespVO();
            row.setRegistryCode(scope.registryCode());
            row.setViewScope(scope.viewScope());
            row.setWriteScope(scope.writeScope());
            result.add(row);
        }
        return result;
    }
}
