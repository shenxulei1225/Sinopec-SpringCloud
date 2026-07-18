package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.inspection.inspection_content.service.object.ObjectSourceQueryService;
import cn.iocoder.yudao.module.inspection.inspection_content.service.source.ObjectSourceAdapter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 巡检对象来源 Controller。
 *
 * <p>提供独立的对象详情查询能力，用于：</p>
 * <ul>
 *     <li>选择巡检对象前预览详情</li>
 *     <li>单独查看某个对象的扩展信息</li>
 *     <li>校验对象是否存在</li>
 * </ul>
 */
@Tag(name = "管理后台 - 巡检对象来源")
@RestController
@RequestMapping("/inspection-task/object-source")
@Validated
public class InspectionObjectSourceController {

    @Resource
    private ObjectSourceQueryService objectSourceQueryService;

    // ==================== 来源类型 ====================

    @GetMapping("/list-source-types")
    @Operation(summary = "查询来源类型列表", description = "返回所有支持的巡检对象来源类型，如 facility、device 等")
    public CommonResult<List<String>> listSourceTypes() {
        return success(objectSourceQueryService.listSourceTypes());
    }

    // ==================== 对象详情 ====================

    @GetMapping("/get-detail")
    @Operation(summary = "查询对象详情", description = "根据来源类型和编码查询对象详情（位置、负责人、状态等）")
    @Parameter(name = "sourceType", required = true, description = "来源类型")
    @Parameter(name = "objectCode", required = true, description = "对象编码")
    public CommonResult<ObjectSourceAdapter.ObjectDetail> getObjectDetail(
            @RequestParam("sourceType") String sourceType,
            @RequestParam("objectCode") String objectCode) {
        return success(objectSourceQueryService.getObjectDetail(sourceType, objectCode));
    }

    @PostMapping("/list-details")
    @Operation(summary = "批量查询对象详情", description = "根据来源类型和编码列表批量查询对象详情")
    @Parameter(name = "sourceType", required = true, description = "来源类型")
    public CommonResult<Map<String, ObjectSourceAdapter.ObjectDetail>> listObjectDetails(
            @RequestParam("sourceType") String sourceType,
            @RequestBody List<String> objectCodes) {
        return success(objectSourceQueryService.listObjectDetails(sourceType, objectCodes));
    }

    @GetMapping("/check-exists")
    @Operation(summary = "校验对象是否存在")
    @Parameter(name = "sourceType", required = true, description = "来源类型")
    @Parameter(name = "objectCode", required = true, description = "对象编码")
    public CommonResult<Boolean> checkExists(
            @RequestParam("sourceType") String sourceType,
            @RequestParam("objectCode") String objectCode) {
        return success(objectSourceQueryService.exists(sourceType, objectCode));
    }

    // ==================== 对象列表 ====================

    @GetMapping("/list-objects")
    @Operation(summary = "查询对象列表", description = "根据来源类型查询可选择的巡检对象列表")
    @Parameter(name = "sourceType", required = true, description = "来源类型")
    @Parameter(name = "categoryId", description = "分类ID（可选）")
    public CommonResult<List<ObjectSourceAdapter.InspectionObject>> listObjects(
            @RequestParam("sourceType") String sourceType,
            @RequestParam(value = "categoryId", required = false) Long categoryId) {
        return success(objectSourceQueryService.listObjects(sourceType, categoryId, null));
    }

    @GetMapping("/search-objects")
    @Operation(summary = "搜索对象", description = "根据来源类型和关键字搜索巡检对象")
    @Parameter(name = "sourceType", required = true, description = "来源类型")
    @Parameter(name = "keyword", description = "关键字搜索")
    public CommonResult<List<ObjectSourceAdapter.InspectionObject>> searchObjects(
            @RequestParam("sourceType") String sourceType,
            @RequestParam(value = "keyword", required = false) String keyword) {
        return success(objectSourceQueryService.listObjects(sourceType, null, keyword));
    }
}
