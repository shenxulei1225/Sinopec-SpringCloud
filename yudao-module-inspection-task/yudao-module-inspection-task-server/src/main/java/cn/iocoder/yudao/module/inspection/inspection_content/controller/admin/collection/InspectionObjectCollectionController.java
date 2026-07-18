package cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.collection;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionCreateReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionPageReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionRespVO;
import cn.iocoder.yudao.module.inspection.inspection_content.controller.admin.vo.collection.InspectionObjectCollectionUpdateReqVO;
import cn.iocoder.yudao.module.inspection.inspection_content.service.collection.InspectionObjectCollectionQueryService;
import cn.iocoder.yudao.module.inspection.inspection_content.service.collection.InspectionObjectCollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 巡检对象集合 Controller。
 *
 * <p>管理巡检配置模板，方便快速添加到巡检任务中。</p>
 */
@Tag(name = "管理后台 - 巡检对象集合")
@RestController
@RequestMapping("/inspection-content/object-collection")
@Validated
public class InspectionObjectCollectionController {

    @Resource
    private InspectionObjectCollectionService objectCollectionService;

    @Resource
    private InspectionObjectCollectionQueryService objectCollectionQueryService;

    // ========== 基础 CRUD ==========

    @PostMapping("/create")
    @Operation(summary = "创建巡检对象集合（模板）")
    public CommonResult<Long> createCollection(@Valid @RequestBody InspectionObjectCollectionCreateReqVO createReqVO) {
        return success(objectCollectionService.createCollection(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新巡检对象集合（模板）")
    public CommonResult<Boolean> updateCollection(@Valid @RequestBody InspectionObjectCollectionUpdateReqVO updateReqVO) {
        objectCollectionService.updateCollection(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除巡检对象集合（模板）")
    @Parameter(name = "id", description = "集合ID", required = true)
    public CommonResult<Boolean> deleteCollection(@RequestParam("id") Long id) {
        objectCollectionService.deleteCollection(id);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获取巡检对象集合详情")
    @Parameter(name = "id", description = "集合ID", required = true)
    public CommonResult<InspectionObjectCollectionRespVO> getCollection(@RequestParam("id") Long id) {
        return success(objectCollectionQueryService.getCollection(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询巡检对象集合")
    public CommonResult<List<InspectionObjectCollectionRespVO>> getCollectionPage(InspectionObjectCollectionPageReqVO reqVO) {
        return success(objectCollectionQueryService.getCollectionPage(reqVO));
    }

    @GetMapping("/list")
    @Operation(summary = "获取巡检对象集合列表（简单信息）")
    public CommonResult<List<InspectionObjectCollectionRespVO>> getSimpleCollectionList() {
        return success(objectCollectionQueryService.getSimpleCollectionList());
    }
}
