package cn.cheers.x.facility.management.api;

import cn.cheers.x.facility.management.api.dto.FacilityStationRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 站场 API
 */
@FeignClient(name = "facility-management")
public interface FacilityStationApi {

    String PREFIX = "facility/station";

    /**
     * 获取站场树
     */
    @GetMapping(PREFIX + "/tree")
    CommonResult<List<FacilityStationRespDTO>> getFacilityTree();

    /**
     * 获取站场详情
     */
    @GetMapping(PREFIX + "/get")
    CommonResult<FacilityStationRespDTO> getFacility(@RequestParam("facilityId") Long facilityId);

    /**
     * 获取站场下的所有子节点ID
     */
    @GetMapping(PREFIX + "/list-child-ids")
    CommonResult<List<Long>> listChildIds(@RequestParam("parentId") Long parentId);

}
