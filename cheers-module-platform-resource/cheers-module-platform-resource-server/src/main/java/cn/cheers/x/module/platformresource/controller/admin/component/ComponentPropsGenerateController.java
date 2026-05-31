package cn.cheers.x.module.platformresource.controller.admin.component;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.cheers.x.module.platformresource.controller.admin.component.vo.ComponentPropsGenerateFromContractReqVO;
import cn.cheers.x.module.platformresource.service.component.ComponentPropsGenerateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 组件 Props 契约生成")
@RestController
@RequestMapping("/platformresource/component-props")
public class ComponentPropsGenerateController {

    @Resource
    private ComponentPropsGenerateService componentPropsGenerateService;

    @PostMapping("/generate-from-contract/preview")
    @Operation(summary = "根据数据能力契约预览 Props JSON（不落库）")
    public CommonResult<Map<String, Object>> previewFromContract(
            @Valid @RequestBody ComponentPropsGenerateFromContractReqVO reqVO) {
        reqVO.setPreviewOnly(true);
        return success(componentPropsGenerateService.previewPropsFromContract(reqVO));
    }

    @PostMapping("/generate-from-contract")
    @Operation(summary = "根据数据能力契约生成并保存 Props 模板")
    public CommonResult<Long> generateFromContract(
            @Valid @RequestBody ComponentPropsGenerateFromContractReqVO reqVO) {
        reqVO.setPreviewOnly(false);
        return success(componentPropsGenerateService.generateTemplateFromContract(reqVO));
    }

    @PostMapping("/seed/system-templates")
    @Operation(summary = "先重建 system 能力契约，再为全部 system:* 生成/更新 Props（含 CRUD requestFields，幂等）")
    public CommonResult<Integer> seedSystemTemplates() {
        return success(componentPropsGenerateService.seedAllSystemPropsTemplates());
    }

    @PostMapping("/seed/dynamic-templates")
    @Operation(summary = "先重建 dynamic 能力契约（按模型字段分配），再生成 Props 模板（幂等）")
    public CommonResult<Integer> seedDynamicTemplates() {
        return success(componentPropsGenerateService.seedAllDynamicPropsTemplates());
    }

    @PostMapping("/seed/all-templates")
    @Operation(summary = "system + dynamic 全部能力生成 Props 模板（幂等）")
    public CommonResult<Integer> seedAllTemplates() {
        return success(componentPropsGenerateService.seedAllCapabilityPropsTemplates());
    }
}
