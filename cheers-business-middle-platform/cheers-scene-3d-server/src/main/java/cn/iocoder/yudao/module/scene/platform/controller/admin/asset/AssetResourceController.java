package cn.iocoder.yudao.module.scene.platform.controller.admin.asset;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.AssetResourceSaveReqVO;
import cn.iocoder.yudao.module.scene.platform.controller.admin.asset.vo.SceneAssetRespVO;
import cn.iocoder.yudao.module.scene.platform.service.asset.AssetConvertService;
import cn.iocoder.yudao.module.scene.platform.service.asset.AssetResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 场景资源")
@RestController
@RequestMapping("/scene-platform/assets")
@Validated
public class AssetResourceController {

    @Resource
    private AssetResourceService assetResourceService;

    @Resource
    private AssetConvertService assetConvertService;

    @GetMapping
    @Operation(summary = "获得资源列表")
    public CommonResult<List<SceneAssetRespVO>> getAssetList() {
        return success(assetResourceService.getAssetList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "获得资源详情")
    public CommonResult<SceneAssetRespVO> getAsset(@PathVariable Long id) {
        return success(assetResourceService.getAsset(id));
    }

    @PostMapping("/upload")
    @Operation(summary = "上传资产文件")
    public CommonResult<SceneAssetRespVO> uploadAsset(
            @RequestParam("file") MultipartFile file,
            @RequestParam String assetCode,
            @RequestParam String assetName,
            @RequestParam String assetType) {
        return success(assetResourceService.uploadAsset(file, assetCode, assetName, assetType));
    }

    @PostMapping("/{id}/convert")
    @Operation(summary = "重新转换资产为 GLB")
    public CommonResult<SceneAssetRespVO> convertAsset(@PathVariable Long id) {
        assetConvertService.convertToGlb(id);
        return success(assetResourceService.getAsset(id));
    }

    @PostMapping
    @Operation(summary = "创建资源")
    public CommonResult<Long> createAsset(@Valid @RequestBody AssetResourceSaveReqVO reqVO) {
        return success(assetResourceService.createAsset(reqVO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新资源")
    public CommonResult<Boolean> updateAsset(@PathVariable Long id,
                                             @Valid @RequestBody AssetResourceSaveReqVO reqVO) {
        assetResourceService.updateAsset(id, reqVO);
        return success(true);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除资源")
    public CommonResult<Boolean> deleteAsset(@PathVariable Long id) {
        assetResourceService.deleteAsset(id);
        return success(true);
    }
}
