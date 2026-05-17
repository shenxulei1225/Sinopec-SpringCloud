package cn.iocoder.yudao.framework.common.biz.system.category;

import cn.iocoder.yudao.framework.common.biz.system.category.dto.CategoryCreateReqDTO;
import cn.iocoder.yudao.framework.common.biz.system.category.dto.CategoryUpdateReqDTO;
import cn.iocoder.yudao.framework.common.enums.RpcConstants;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 分类服务通用 RPC 接口
 *
 * 用于所有模块通过 RPC 调用 System 模块的分类功能
 *
 * @author 系统生成
 */
@FeignClient(name = RpcConstants.SYSTEM_NAME, primary = false)
@Tag(name = "RPC 服务 - 分类管理")
public interface CategoryCommonApi {

    String PREFIX = RpcConstants.SYSTEM_PREFIX + "/category";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "通过分类 ID 查询分类信息")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Map<String, Object>> getCategory(@RequestParam("id") Long id,
                                                  @RequestParam("categoryTypeCode") String categoryTypeCode);

    @GetMapping(PREFIX + "/exists")
    @Operation(summary = "验证分类 ID 是否存在")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Boolean> existsCategory(@RequestParam("id") Long id,
                                         @RequestParam("categoryTypeCode") String categoryTypeCode);

    @GetMapping(PREFIX + "/tree")
    @Operation(summary = "查询分类树")
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    @Parameter(name = "status", description = "状态（1启用，0禁用）", example = "1", required = false)
    CommonResult<List<Map<String, Object>>> getCategoryTree(@RequestParam("categoryTypeCode") String categoryTypeCode,
                                                            @RequestParam(value = "status", required = false) Integer status);

    @PostMapping(PREFIX + "/create")
    @Operation(summary = "创建分类")
    CommonResult<Long> createCategory(@Valid @RequestBody CategoryCreateReqDTO reqDTO);

    @PutMapping(PREFIX + "/update")
    @Operation(summary = "更新分类")
    CommonResult<Boolean> updateCategory(@Valid @RequestBody CategoryUpdateReqDTO reqDTO);

    @DeleteMapping(PREFIX + "/delete")
    @Operation(summary = "删除分类")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "cascade", description = "是否级联删除子分类", example = "false", required = false)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Boolean> deleteCategory(@RequestParam("id") Long id,
                                         @RequestParam(value = "cascade", defaultValue = "false") boolean cascade,
                                         @RequestParam("categoryTypeCode") String categoryTypeCode);

    @PutMapping(PREFIX + "/move")
    @Operation(summary = "移动分类")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "targetParentId", description = "目标父分类ID，可为空表示移到根", example = "0", required = false)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Boolean> moveCategory(@RequestParam("id") Long id,
                                       @RequestParam(value = "targetParentId", required = false) Long targetParentId,
                                       @RequestParam("categoryTypeCode") String categoryTypeCode);

    @PutMapping(PREFIX + "/sort")
    @Operation(summary = "排序分类")
    @Parameter(name = "categoryIds", description = "分类ID列表（按排序顺序）", example = "1,2,3", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Boolean> sortCategories(@RequestParam("categoryIds") List<Long> categoryIds,
                                         @RequestParam("categoryTypeCode") String categoryTypeCode);

    @PutMapping(PREFIX + "/enable")
    @Operation(summary = "启用分类")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Boolean> enableCategory(@RequestParam("id") Long id,
                                         @RequestParam("categoryTypeCode") String categoryTypeCode);

    @PutMapping(PREFIX + "/disable")
    @Operation(summary = "禁用分类")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<Boolean> disableCategory(@RequestParam("id") Long id,
                                          @RequestParam("categoryTypeCode") String categoryTypeCode);

    @GetMapping(PREFIX + "/search")
    @Operation(summary = "搜索分类")
    @Parameter(name = "keyword", description = "关键词", example = "设备", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<List<Map<String, Object>>> searchCategories(@RequestParam("keyword") String keyword,
                                                             @RequestParam("categoryTypeCode") String categoryTypeCode);

    @GetMapping(PREFIX + "/get-path")
    @Operation(summary = "获取分类路径")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    CommonResult<List<Map<String, Object>>> getPath(@RequestParam("id") Long id,
                                                    @RequestParam("categoryTypeCode") String categoryTypeCode);

    @GetMapping(PREFIX + "/get-children")
    @Operation(summary = "获取子分类列表")
    @Parameter(name = "id", description = "分类编号", example = "1", required = true)
    @Parameter(name = "categoryTypeCode", description = "分类类型编码", example = "emergency_event", required = true)
    @Parameter(name = "status", description = "状态（1启用，0禁用）", example = "1", required = false)
    CommonResult<List<Map<String, Object>>> getChildren(@RequestParam("id") Long id,
                                                        @RequestParam("categoryTypeCode") String categoryTypeCode,
                                                        @RequestParam(value = "status", required = false) Integer status);
}
