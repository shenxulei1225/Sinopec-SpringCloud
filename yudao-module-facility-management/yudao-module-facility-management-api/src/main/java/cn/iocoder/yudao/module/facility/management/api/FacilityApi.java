package cn.iocoder.yudao.module.facility.management.api;

import cn.iocoder.yudao.module.facility.management.api.dto.FacilityPageReqDTO;
import cn.iocoder.yudao.module.facility.management.api.dto.FacilityRespDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 设施 API
 */
@FeignClient(name = "facility-management")
public interface FacilityApi {

    String PREFIX = "facility/facility";

    /**
     * 获取简单设施列表（用于巡检对象选择）
     *
     * @param facilityName 设施名称（模糊查询）
     * @param categoryId   分类ID
     * @param facilityModel 设施型号
     * @return 设施列表
     */
    @GetMapping(PREFIX + "/list-simple")
    CommonResult<List<FacilityRespDTO>> getSimpleFacilities(
            @RequestParam(value = "facilityName", required = false) String facilityName,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "facilityModel", required = false) String facilityModel
    );

    /**
     * 根据编码列表批量获取设施
     *
     * @param codes 设施编码列表
     * @return 设施列表
     */
    @GetMapping(PREFIX + "/list-by-codes")
    CommonResult<List<FacilityRespDTO>> getFacilitiesByCodes(@RequestParam("codes") List<String> codes);

    /**
     * 获取设施详情
     *
     * @param id 设施ID
     * @return 设施详情
     */
    @GetMapping(PREFIX + "/get")
    CommonResult<FacilityRespDTO> getFacility(@RequestParam("id") Long id);

    /**
     * 获取设施详情（根据编码）
     *
     * @param code 设施编码
     * @return 设施详情
     */
    @GetMapping(PREFIX + "/get-by-code")
    CommonResult<FacilityRespDTO> getFacilityByCode(@RequestParam("code") String code);

    /**
     * 分页查询设施
     *
     * @param reqDTO 分页请求参数
     * @return 分页结果
     */
    @GetMapping(PREFIX + "/page")
    CommonResult<PageResult<FacilityRespDTO>> getFacilityPage(@RequestBody FacilityPageReqDTO reqDTO);

}
