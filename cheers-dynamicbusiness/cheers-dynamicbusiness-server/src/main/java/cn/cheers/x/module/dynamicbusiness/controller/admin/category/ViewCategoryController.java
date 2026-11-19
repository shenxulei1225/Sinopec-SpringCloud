package cn.cheers.x.module.dynamicbusiness.controller.admin.category;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryDragReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.CREATE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.DELETE;
import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 视图库左侧的「视图分类」树：平台配置人员给视图分组用。
 * <p>
 * 管什么：只读写分类体系 view 下的节点。权限只看视图库的菜单权限（system:view:*），
 * 因为视图分类不是任何一本数据目录的业务数据。
 * 不管什么：业务数据目录用到的分类树（走 CategoryController，按目录授权核对）。
 * 禁止：借这里读写其它分类体系的节点。按编号改、删、拖的节点不在 view 下一律拒绝。
 */
@Tag(name = "管理后台 - 视图分类", description = "视图库自建的分类树，只认视图库菜单权限")
@RestController
@RequestMapping("/dynamicbusiness/view-category")
@Validated
public class ViewCategoryController {

    static final String VIEW_CATEGORY_TYPE = "view";

    @Resource
    private CategoryService categoryService;

    @GetMapping("/tree")
    @Operation(summary = "视图分类树")
    @PreAuthorize("@ss.hasPermission('system:view:query')")
    public CommonResult<List<CategoryTreeRespVO>> tree(
            @RequestParam(value = "status", required = false) Integer status) {
        return success(categoryService.getCategoryTreeByType(VIEW_CATEGORY_TYPE, status));
    }

    @PostMapping("/create")
    @Operation(summary = "新建视图分类节点")
    @ApiAccessLog(operateType = CREATE)
    @PreAuthorize("@ss.hasPermission('system:view:create')")
    public CommonResult<Long> create(@Valid @RequestBody CategoryCreateReqVO reqVO) {
        if (!VIEW_CATEGORY_TYPE.equals(reqVO.getCategoryTypeCode())) {
            throw new ServiceException(403, "这里只能新建视图分类");
        }
        assertViewParent(reqVO.getParentId());
        return success(categoryService.createCategory(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "修改视图分类节点")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:view:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody CategoryUpdateReqVO reqVO) {
        assertViewCategory(reqVO.getId());
        if (reqVO.getCategoryTypeCode() != null && !VIEW_CATEGORY_TYPE.equals(reqVO.getCategoryTypeCode())) {
            throw new ServiceException(403, "视图分类不能改成别的分类体系");
        }
        categoryService.updateCategory(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除视图分类节点")
    @ApiAccessLog(operateType = DELETE)
    @PreAuthorize("@ss.hasPermission('system:view:delete')")
    public CommonResult<Boolean> delete(@Valid @RequestBody CategoryDeleteReqVO reqVO) {
        assertViewCategory(reqVO.getId());
        categoryService.deleteCategory(reqVO);
        return success(true);
    }

    @PostMapping("/drag")
    @Operation(summary = "拖动视图分类节点")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:view:update')")
    public CommonResult<Boolean> drag(@Valid @RequestBody CategoryDragReqVO reqVO) {
        assertViewCategory(reqVO.getDragId());
        assertViewParent(reqVO.getTargetId());
        assertViewParent(reqVO.getTargetParentId());
        if (!VIEW_CATEGORY_TYPE.equals(reqVO.getCategoryTypeCode())) {
            throw new ServiceException(403, "这里只能拖动视图分类");
        }
        categoryService.dragCategory(reqVO);
        return success(true);
    }

    /** 上级或目标节点：空或 0 表示根，不用核对；其余必须是视图分类的节点。 */
    private void assertViewParent(Long id) {
        if (id != null && id > 0) {
            assertViewCategory(id);
        }
    }

    private void assertViewCategory(Long id) {
        CategoryRespVO category = id == null ? null : categoryService.getCategoryVO(id);
        if (category == null || !Objects.equals(VIEW_CATEGORY_TYPE, category.getCategoryTypeCode())) {
            throw new ServiceException(403, "这里只能改视图分类的节点");
        }
    }
}
