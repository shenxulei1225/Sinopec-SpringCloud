package cn.cheers.x.module.dynamicbusiness.controller.admin.entity;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.IncomingRecordRespVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.IncomingRecordQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 详情页签读取指向当前记录的对方名单。
 *
 * 管什么：按打开的目录核查询权限，再列出关联记录。
 * 不管什么：页签上勾了哪些字段；把名单写回当前记录。
 */
@Tag(name = "管理后台 - 详情页签关联记录")
@RestController
@RequestMapping("/dynamicbusiness/business/entities")
@Validated
public class IncomingRecordController {

    @Resource
    private IncomingRecordQueryService incomingRecordQueryService;

    @GetMapping("/incoming-records")
    @Operation(summary = "列出指向当前记录的对方")
    @PreAuthorize("@catalogAccess.allow(#registryCode, 'query')")
    public CommonResult<List<IncomingRecordRespVO>> list(
            @RequestParam("registryCode") String registryCode,
            @RequestParam("hostEntityId") Long hostEntityId,
            @RequestParam("hostEntityTypeCode") String hostEntityTypeCode,
            @RequestParam(value = "modelName", required = false) String modelName,
            @RequestParam(value = "facilityId", required = false) Long facilityId) {
        return success(incomingRecordQueryService.list(
                registryCode, hostEntityId, hostEntityTypeCode, modelName, facilityId));
    }
}
