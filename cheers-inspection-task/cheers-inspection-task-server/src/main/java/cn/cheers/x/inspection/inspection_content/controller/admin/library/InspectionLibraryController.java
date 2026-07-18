package cn.cheers.x.inspection.inspection_content.controller.admin.library;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.inspection.inspection_content.controller.admin.library.vo.*;
import cn.cheers.x.inspection.inspection_content.service.collection.InspectionObjectCollectionQueryService;
import cn.cheers.x.inspection.inspection_content.service.library.InspectionLibraryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 巡检对象库 Controller。
 *
 * <p>统一入口，前端只调用此接口获取巡检对象和检查项，</p>
 * <p>不感知底层对接了哪些业务系统（facility、device 等）。</p>
 *
 * <p>提供三种数据获取方式：</p>
 * <ul>
 *     <li>方式一：分步获取（推荐，设备多时性能好）</li>
 *     <li>方式二：一次性获取（设备少时使用）</li>
 *     <li>方式三：直接查询检查项</li>
 * </ul>
 */
@Tag(name = "管理后台 - 巡检对象库")
@RestController
@RequestMapping("/inspection-model/library")
@Validated
public class InspectionLibraryController {

    @Resource
    private InspectionLibraryService inspectionLibraryService;

    @Resource
    private InspectionObjectCollectionQueryService collectionQueryService;

    // ==================== 来源管理 ====================

    @GetMapping("/sources")
    @Operation(summary = "获取可用的巡检对象来源列表")
    public CommonResult<List<LibrarySourceVO>> getSources() {
        return success(inspectionLibraryService.getSources());
    }

    // ==================== 方式一：分步获取（推荐） ====================

    @GetMapping("/models")
    @Operation(summary = "【方式一】获取分类下的型号列表（含检查项）")
    public CommonResult<List<ModelWithItemsVO>> getModelsWithItems(
            @RequestParam("sourceCode") String sourceCode,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return success(inspectionLibraryService.getModelsWithItems(sourceCode, categoryId));
    }

    @GetMapping("/objects")
    @Operation(summary = "【方式一】查询巡检对象列表（可按型号筛选）")
    public CommonResult<List<LibraryObjectVO>> getObjects(
            @RequestParam("sourceCode") String sourceCode,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "objectModel", required = false) String objectModel) {
        return success(inspectionLibraryService.getObjects(sourceCode, categoryId, objectModel));
    }

    // ==================== 方式二：一次性获取 ====================

    @GetMapping("/full-tree")
    @Operation(summary = "【方式二】一次性获取分类下的型号-对象-检查项完整树")
    public CommonResult<FullTreeVO> getFullTree(
            @RequestParam("sourceCode") String sourceCode,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return success(inspectionLibraryService.getFullTree(sourceCode, categoryId));
    }

    // ==================== 方式三：直接查询检查项 ====================

    @GetMapping("/items")
    @Operation(summary = "【方式三】直接查询指定型号的检查项")
    public CommonResult<List<LibraryItemVO>> getItems(
            @RequestParam("sourceCode") String sourceCode,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam("objectModel") String objectModel) {
        return success(inspectionLibraryService.getItems(sourceCode, categoryId, objectModel));
    }

    // ==================== 模板管理 ====================

    @GetMapping("/templates")
    @Operation(summary = "获取巡检配置模板列表")
    public CommonResult<List<LibraryTemplateVO>> getTemplates() {
        return success(collectionQueryService.getSimpleCollectionList().stream()
                .map(t -> {
                    LibraryTemplateVO vo = new LibraryTemplateVO();
                    vo.setId(t.getId());
                    vo.setCollectionCode(t.getCollectionCode());
                    vo.setCollectionName(t.getCollectionName());
                    vo.setDescription(t.getDescription());
                    vo.setObjectCount(t.getObjectCount());
                    vo.setItemCount(t.getItemCount());
                    return vo;
                })
                .toList());
    }

    @GetMapping("/templates/{id}")
    @Operation(summary = "获取巡检配置模板详情")
    @Parameter(name = "id", description = "模板ID", required = true)
    public CommonResult<LibraryTemplateVO> getTemplate(@PathVariable("id") Long id) {
        var t = collectionQueryService.getCollection(id);
        LibraryTemplateVO vo = new LibraryTemplateVO();
        vo.setId(t.getId());
        vo.setCollectionCode(t.getCollectionCode());
        vo.setCollectionName(t.getCollectionName());
        vo.setDescription(t.getDescription());
        vo.setObjectCount(t.getObjectCount());
        vo.setItemCount(t.getItemCount());
        vo.setContent(t.getContent());
        return success(vo);
    }
}
