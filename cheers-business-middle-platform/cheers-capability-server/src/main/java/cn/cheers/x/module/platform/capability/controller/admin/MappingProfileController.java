package cn.cheers.x.module.platform.capability.controller.admin;

import cn.cheers.x.module.platform.capability.api.dto.MappingProfileRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.MappingProfileSaveReqDTO;
import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.capability.service.MappingProfileService;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 映射配置")
@RestController
@RequestMapping("/platform/registry")
public class MappingProfileController {

    @Resource
    private MappingProfileService mappingProfileService;

    @PostMapping("/mapping-profiles")
    @Operation(summary = "保存映射配置")
    public CommonResult<MappingProfileRespDTO> save(@Valid @RequestBody MappingProfileSaveReqDTO request) {
        return success(mappingProfileService.save(request));
    }

    @GetMapping("/mapping-profiles/{id}")
    @Operation(summary = "读取映射配置")
    public CommonResult<MappingProfileRespDTO> get(@PathVariable("id") String id) {
        return success(mappingProfileService.get(id));
    }

    @PostMapping("/mapping-profiles/{id}/resolve-work-items")
    @Operation(summary = "按映射配置解析作业项")
    public CommonResult<List<WorkItemDTO>> resolveWorkItems(
            @PathVariable("id") String id,
            @Valid @RequestBody ResolveWorkItemsReqDTO request) {
        return success(mappingProfileService.resolveWorkItems(id, request));
    }
}
