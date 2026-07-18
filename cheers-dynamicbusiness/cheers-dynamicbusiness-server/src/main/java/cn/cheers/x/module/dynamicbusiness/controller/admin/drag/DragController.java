package cn.cheers.x.module.dynamicbusiness.controller.admin.drag;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo.DragExecuteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.drag.vo.DragExecuteRespVO;
import cn.cheers.x.module.dynamicbusiness.service.drag.DragService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 通用拖拽执行")
@RestController
@RequestMapping("/dynamicbusiness/drag")
@Validated
public class DragController {

    @Resource
    private DragService dragService;

    @PostMapping("/execute")
    @Operation(summary = "执行一次拖拽动作")
    @PreAuthorize("@ss.hasPermission('system:model:update')")
    public CommonResult<DragExecuteRespVO> execute(@Valid @RequestBody DragExecuteReqVO reqVO) {
        return success(dragService.execute(reqVO));
    }
}
