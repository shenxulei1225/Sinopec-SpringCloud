package cn.iocoder.yudao.module.scene.platform.controller.admin.scene;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneLoadRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.ScenePageReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneRespVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.scene.vo.SceneSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.service.scene.SceneService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

/**
 * 场景管理 Controller
 * 核心接口：
 * 1. 场景 CRUD（管理后台）
 * 2. 场景加载（合并 DB 定义 + Redis 运行时数据）
 * 3. 场景发布/取消发布
 *
 * @author Sinopec
 */
@Tag(name = "管理后台 - 场景管理")
@RestController
@RequestMapping("/scene-platform/scene")
@Validated
public class SceneController {

    @Resource
    private SceneService sceneService;

    // ==================== 场景 CRUD（管理后台） ====================

    @PostMapping("/create")
    @Operation(summary = "创建场景")
    public CommonResult<Long> createScene(@Valid @RequestBody SceneSaveReqVO reqVO) {
        Long id = sceneService.createScene(reqVO);
        return success(id);
    }

    @PutMapping("/update")
    @Operation(summary = "更新场景")
    public CommonResult<Boolean> updateScene(@Valid @RequestBody SceneSaveReqVO reqVO) {
        sceneService.updateScene(reqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除场景")
    @Parameter(name = "id", description = "场景 ID", required = true)
    public CommonResult<Boolean> deleteScene(@RequestParam("id") Long id) {
        sceneService.deleteScene(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取场景详情")
    @Parameter(name = "id", description = "场景 ID", required = true)
    public CommonResult<SceneRespVO> getScene(@RequestParam("id") Long id) {
        SceneRespVO scene = sceneService.getScene(id);
        return success(scene);
    }

    @GetMapping("/list-all")
    @Operation(summary = "获取场景列表")
    public CommonResult<List<SceneRespVO>> listScene(@Valid ScenePageReqVO reqVO) {
        List<SceneRespVO> list = sceneService.listScene(reqVO);
        return success(list);
    }

    // ==================== 场景加载（合并 DB + Redis） ====================

    @GetMapping("/load")
    @Operation(summary = "加载场景（合并定义 + 运行时数据）")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<SceneLoadRespVO> loadScene(@RequestParam("sceneCode") String sceneCode) {
        SceneLoadRespVO respVO = sceneService.loadScene(sceneCode);
        return success(respVO);
    }

    // ==================== 场景发布 ====================

    @PostMapping("/publish")
    @Operation(summary = "发布场景")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<Boolean> publishScene(@RequestParam("sceneCode") String sceneCode) {
        sceneService.publishScene(sceneCode);
        return success(true);
    }

    @PostMapping("/unpublish")
    @Operation(summary = "取消发布场景")
    @Parameter(name = "sceneCode", description = "场景编码", required = true)
    public CommonResult<Boolean> unpublishScene(@RequestParam("sceneCode") String sceneCode) {
        sceneService.unpublishScene(sceneCode);
        return success(true);
    }
}