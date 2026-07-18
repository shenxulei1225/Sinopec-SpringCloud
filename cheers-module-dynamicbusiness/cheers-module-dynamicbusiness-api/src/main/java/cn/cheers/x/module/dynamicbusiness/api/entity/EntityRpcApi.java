package cn.cheers.x.module.dynamicbusiness.api.entity;

import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import cn.cheers.x.framework.common.pojo.CommonResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 动态业务通用实体读 RPC 接口。
 *
 * <p>跨模块只存实体 id；读时须带 {@code entityTypeCode} 路由到对应存储策略。</p>
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 动态业务实体读")
public interface EntityRpcApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/entity";

    @GetMapping(PREFIX + "/get")
    @Operation(summary = "获取单个实体详情")
    CommonResult<EntityRespDTO> getEntity(
            @RequestParam("id") Long id,
            @Parameter(description = "实体类型编码", required = true, example = "facility")
            @RequestParam("entityTypeCode") String entityTypeCode);

    @GetMapping(PREFIX + "/list-by-ids")
    @Operation(summary = "按 ID 批量获取实体")
    CommonResult<List<EntityRespDTO>> listEntitiesByIds(
            @RequestParam("ids") List<Long> ids,
            @Parameter(description = "实体类型编码", required = true, example = "facility")
            @RequestParam("entityTypeCode") String entityTypeCode);

    @GetMapping(PREFIX + "/exists")
    @Operation(summary = "判断实体是否存在")
    CommonResult<Boolean> existsEntity(
            @RequestParam("id") Long id,
            @Parameter(description = "实体类型编码", required = true, example = "facility")
            @RequestParam("entityTypeCode") String entityTypeCode);

    @GetMapping(PREFIX + "/list")
    @Operation(summary = "按实体类型列出实体（可选按 modelId 过滤）")
    CommonResult<List<EntityRespDTO>> listEntities(
            @Parameter(description = "实体类型编码", required = true, example = "facility")
            @RequestParam("entityTypeCode") String entityTypeCode,
            @RequestParam(value = "modelId", required = false) Long modelId);

    @GetMapping(PREFIX + "/get-by-code")
    @Operation(summary = "按业务编码获取实体")
    CommonResult<EntityRespDTO> getEntityByCode(
            @RequestParam("code") String code,
            @Parameter(description = "实体类型编码", required = true, example = "facility")
            @RequestParam("entityTypeCode") String entityTypeCode);

    @GetMapping(PREFIX + "/list-by-codes")
    @Operation(summary = "按业务编码批量获取实体")
    CommonResult<List<EntityRespDTO>> listEntitiesByCodes(
            @RequestParam("codes") List<String> codes,
            @Parameter(description = "实体类型编码", required = true, example = "facility")
            @RequestParam("entityTypeCode") String entityTypeCode);

}
