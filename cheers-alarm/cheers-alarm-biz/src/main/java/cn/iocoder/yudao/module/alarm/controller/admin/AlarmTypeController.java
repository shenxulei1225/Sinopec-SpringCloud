package cn.iocoder.yudao.module.alarm.controller.admin;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeCategoryVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeEntityVO;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.type.AlarmTypeModelVO;
import cn.iocoder.yudao.module.alarm.service.type.AlarmTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 管理后台 - 告警类型管理 Controller
 * 
 * <p>提供告警类型的查询功能，告警类型使用三层模型（Category/Model/Entity）进行分类管理。</p>
 * 
 * <p>三层模型说明：
 * <ul>
 *   <li>Category（分类）：告警大类，如环境告警、设备告警、火灾告警、安防告警</li>
 *   <li>Model（模型）：告警子类，如水位告警、气体告警、温度告警</li>
 *   <li>Entity（实体）：具体告警项，如水位超标、水位过低、水位急升</li>
 * </ul>
 * </p>
 * 
 * <p>核心功能：
 * <ul>
 *   <li>告警类型树查询（FR-006）</li>
 *   <li>告警类型路径查询（FR-006）</li>
 * </ul>
 * </p>
 *
 * @author 告警管理模块
 */
@Tag(name = "管理后台 - 告警类型管理")
@RestController
@RequestMapping("/alarm/types")
@Validated
public class AlarmTypeController {

    @Resource
    private AlarmTypeService alarmTypeService;

    // ========== 告警类型树查询 ==========

    @GetMapping("/tree")
    @Operation(summary = "获取告警类型树", description = "返回完整的告警类型树，包含 Category -> Model -> Entity 三层结构")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<List<AlarmTypeCategoryVO>> getAlarmTypeTree() {
        List<AlarmTypeCategoryVO> tree = alarmTypeService.getAlarmTypeTree();
        return success(tree);
    }

    @GetMapping("/tree-enabled")
    @Operation(summary = "获取启用的告警类型树", description = "返回启用状态的告警类型树，用于前端下拉选择")
    // 无需权限认证，因为前端全局都需要
    public CommonResult<List<AlarmTypeCategoryVO>> getEnabledAlarmTypeTree() {
        // status = 0 表示启用
        List<AlarmTypeCategoryVO> tree = alarmTypeService.getAlarmTypeTree(0);
        return success(tree);
    }

    // ========== 告警分类查询 ==========

    @GetMapping("/categories")
    @Operation(summary = "获取所有告警分类", description = "获取所有告警分类（Category层），不包含子节点")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<List<AlarmTypeCategoryVO>> getAllCategories() {
        List<AlarmTypeCategoryVO> categories = alarmTypeService.getAllCategories();
        return success(categories);
    }

    @GetMapping("/categories-enabled")
    @Operation(summary = "获取启用的告警分类", description = "获取启用状态的告警分类，用于前端下拉选择")
    // 无需权限认证，因为前端全局都需要
    public CommonResult<List<AlarmTypeCategoryVO>> getEnabledCategories() {
        // status = 0 表示启用
        List<AlarmTypeCategoryVO> categories = alarmTypeService.getAllCategories(0);
        return success(categories);
    }

    @GetMapping("/category/get")
    @Operation(summary = "获取告警分类详情")
    @Parameter(name = "id", description = "分类ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<AlarmTypeCategoryVO> getCategory(@RequestParam("id") Long id) {
        AlarmTypeCategoryVO category = alarmTypeService.getCategory(id);
        return success(category);
    }

    // ========== 告警模型查询 ==========

    @GetMapping("/models")
    @Operation(summary = "根据分类获取告警模型列表")
    @Parameter(name = "categoryId", description = "分类ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<List<AlarmTypeModelVO>> getModelsByCategory(@RequestParam("categoryId") Long categoryId) {
        List<AlarmTypeModelVO> models = alarmTypeService.getModelsByCategory(categoryId);
        return success(models);
    }

    @GetMapping("/model/get")
    @Operation(summary = "获取告警模型详情")
    @Parameter(name = "id", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<AlarmTypeModelVO> getModel(@RequestParam("id") Long id) {
        AlarmTypeModelVO model = alarmTypeService.getModel(id);
        return success(model);
    }

    // ========== 告警实体查询 ==========

    @GetMapping("/entities")
    @Operation(summary = "根据模型获取告警实体列表")
    @Parameter(name = "modelId", description = "模型ID", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<List<AlarmTypeEntityVO>> getEntitiesByModel(@RequestParam("modelId") Long modelId) {
        List<AlarmTypeEntityVO> entities = alarmTypeService.getEntitiesByModel(modelId);
        return success(entities);
    }

    @GetMapping("/entity/get")
    @Operation(summary = "获取告警类型实体详情")
    @Parameter(name = "id", description = "实体ID（告警类型ID）", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<AlarmTypeEntityVO> getAlarmTypeEntity(@RequestParam("id") Long id) {
        AlarmTypeEntityVO entity = alarmTypeService.getAlarmTypeEntity(id);
        return success(entity);
    }

    @GetMapping("/entity/get-by-code")
    @Operation(summary = "根据编码获取告警类型实体")
    @Parameter(name = "code", description = "实体编码", required = true, example = "WATER_LEVEL_HIGH")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<AlarmTypeEntityVO> getAlarmTypeEntityByCode(@RequestParam("code") String code) {
        AlarmTypeEntityVO entity = alarmTypeService.getAlarmTypeEntityByCode(code);
        return success(entity);
    }

    // ========== 告警类型路径查询 ==========

    @GetMapping("/path")
    @Operation(summary = "获取告警类型完整路径", description = "根据实体ID获取完整路径，格式如：环境告警 > 水位告警 > 水位超标")
    @Parameter(name = "entityId", description = "实体ID（告警类型ID）", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<String> getAlarmTypePath(@RequestParam("entityId") Long entityId) {
        String path = alarmTypeService.getAlarmTypePath(entityId);
        return success(path);
    }

    // ========== 告警类型验证 ==========

    @GetMapping("/exists")
    @Operation(summary = "验证告警类型是否存在")
    @Parameter(name = "entityId", description = "实体ID（告警类型ID）", required = true, example = "1")
    @PreAuthorize("@ss.hasPermission('alarm:type:query')")
    public CommonResult<Boolean> existsAlarmType(@RequestParam("entityId") Long entityId) {
        boolean exists = alarmTypeService.existsAlarmType(entityId);
        return success(exists);
    }

}
