package cn.iocoder.yudao.module.facility.management.api;

import cn.iocoder.yudao.module.facility.management.api.dto.FacilityPageReqDTO;
import cn.iocoder.yudao.module.facility.management.api.dto.FacilityRespDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.facility.management.enums.ApiConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设施读 API（Facade）。
 *
 * @deprecated 已废弃。跨模块读请改用 {@link cn.cheers.x.module.dynamicbusiness.api.entity.EntityRpcApi}，
 *             并传入 {@code entityTypeCode=facility}。
 */
@Deprecated
@FeignClient(name = ApiConstants.FEIGN_NAME)
public interface FacilityApi {

    String PREFIX = ApiConstants.PREFIX;

    /**
     * 获取简单设施列表（用于巡检对象选择）
     */
    @GetMapping(PREFIX + "/list-simple")
    CommonResult<List<FacilityRespDTO>> getSimpleFacilities(
            @RequestParam(value = "facilityName", required = false) String facilityName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "facilityModel", required = false) String facilityModel
    );

    /**
     * 根据编码列表批量获取设施
     */
    @GetMapping(PREFIX + "/list-by-codes")
    CommonResult<List<FacilityRespDTO>> getFacilitiesByCodes(@RequestParam("codes") List<String> codes);

    /**
     * 根据 ID 列表批量获取设施
     */
    @GetMapping(PREFIX + "/list-by-ids")
    CommonResult<List<FacilityRespDTO>> getFacilitiesByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 获取设施详情
     */
    @GetMapping(PREFIX + "/get")
    CommonResult<FacilityRespDTO> getFacility(@RequestParam("id") Long id);

    /**
     * 获取设施详情（根据编码）
     */
    @GetMapping(PREFIX + "/get-by-code")
    CommonResult<FacilityRespDTO> getFacilityByCode(@RequestParam("code") String code);

    /**
     * 分页查询设施
     */
    @GetMapping(PREFIX + "/page")
    CommonResult<PageResult<FacilityRespDTO>> getFacilityPage(@RequestBody FacilityPageReqDTO reqDTO);

}
