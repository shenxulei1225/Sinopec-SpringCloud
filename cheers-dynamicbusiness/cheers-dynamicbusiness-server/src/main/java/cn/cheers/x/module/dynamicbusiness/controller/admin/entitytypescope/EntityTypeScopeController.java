package cn.cheers.x.module.dynamicbusiness.controller.admin.entitytypescope;

import cn.cheers.x.framework.apilog.core.annotation.ApiAccessLog;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytypescope.vo.EntityTypeScopeBatchReqVO;
import cn.cheers.x.module.dynamicbusiness.service.entitytypescope.EntityTypeScopeQueryService;
import cn.cheers.x.module.dynamicbusiness.service.entitytypescope.EntityTypeScopeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.apilog.core.enums.OperateTypeEnum.UPDATE;
import static cn.cheers.x.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 划分数据")
@RestController
@RequestMapping("/dynamicbusiness/entity-type-scope")
@Validated
public class EntityTypeScopeController {

    @Resource
    private EntityTypeScopeService entityTypeScopeService;
    @Resource
    private EntityTypeScopeQueryService entityTypeScopeQueryService;

    @PostMapping("/join")
    @Operation(summary = "批量加入划分数据")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> join(@Valid @RequestBody EntityTypeScopeBatchReqVO reqVO) {
        entityTypeScopeService.join(reqVO.getEntityTypeCode(), reqVO.getEntityIds());
        return success(true);
    }

    @PostMapping("/leave")
    @Operation(summary = "批量移出划分数据")
    @ApiAccessLog(operateType = UPDATE)
    @PreAuthorize("@ss.hasPermission('system:entity-type:update')")
    public CommonResult<Boolean> leave(@Valid @RequestBody EntityTypeScopeBatchReqVO reqVO) {
        entityTypeScopeService.leave(reqVO.getEntityTypeCode(), reqVO.getEntityIds());
        return success(true);
    }

    @GetMapping("/list-ids")
    @Operation(summary = "查询划分数据实体 id 列表")
    @Parameter(name = "entityTypeCode", description = "划分数据入口编码", required = true)
    @PreAuthorize("@ss.hasPermission('system:entity-type:query')")
    public CommonResult<List<Long>> listIds(
            @RequestParam("entityTypeCode") String entityTypeCode) {
        return success(entityTypeScopeQueryService.listEntityIds(entityTypeCode));
    }
}
