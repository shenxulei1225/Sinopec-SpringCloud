package cn.cheers.x.module.dynamicbusiness.controller.admin.relation;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.relation.vo.RefConstraintLibraryUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.relation.RefConstraintLibraryService;
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

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - Ref 约束器库")
@RestController
@RequestMapping("/dynamicbusiness/ref-constraint-library")
@Validated
public class RefConstraintLibraryController {

    @Resource
    private RefConstraintLibraryService refConstraintLibraryService;

    @PostMapping("/create")
    @Operation(summary = "创建约束器库")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:create')")
    public CommonResult<Long> create(@Valid @RequestBody RefConstraintLibraryCreateReqVO reqVO) {
        return success(refConstraintLibraryService.create(reqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新约束器库")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:update')")
    public CommonResult<Boolean> update(@Valid @RequestBody RefConstraintLibraryUpdateReqVO reqVO) {
        refConstraintLibraryService.update(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除约束器库")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:delete')")
    public CommonResult<Boolean> delete(@RequestParam("id") Long id) {
        refConstraintLibraryService.delete(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取约束器库详情")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:query')")
    public CommonResult<RefConstraintLibraryRespVO> get(@RequestParam("id") Long id) {
        return success(refConstraintLibraryService.get(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询约束器库")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:query')")
    public CommonResult<PageResult<RefConstraintLibraryRespVO>> page(@Valid RefConstraintLibraryPageReqVO reqVO) {
        return success(refConstraintLibraryService.getPage(reqVO));
    }

    @GetMapping("/list-by-business-type")
    @Operation(summary = "按业务类型查询约束器库")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:query')")
    public CommonResult<List<RefConstraintLibraryRespVO>> listByBusinessType(
            @RequestParam("businessTypeCode") String businessTypeCode,
            @RequestParam(value = "refTargetType", required = false) String refTargetType) {
        return success(refConstraintLibraryService.listByBusinessType(businessTypeCode, refTargetType));
    }

    @GetMapping("/list-all")
    @Operation(summary = "获取全部约束器库")
    @PreAuthorize("@ss.hasPermission('system:ref-constraint-library:query')")
    public CommonResult<List<RefConstraintLibraryRespVO>> listAll() {
        return success(refConstraintLibraryService.listAll());
    }
}
