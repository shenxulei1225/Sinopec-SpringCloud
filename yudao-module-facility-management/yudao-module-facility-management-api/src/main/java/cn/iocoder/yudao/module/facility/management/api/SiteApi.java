package cn.iocoder.yudao.module.facility.management.api;

import cn.iocoder.yudao.module.facility.management.api.dto.SiteRespDTO;
import cn.cheers.x.framework.common.pojo.CommonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 站场 API
 */
@FeignClient(name = "facility-management")
public interface SiteApi {

    String PREFIX = "facility/site";

    /**
     * 获取站场树
     */
    @GetMapping(PREFIX + "/tree")
    CommonResult<List<SiteRespDTO>> getSiteTree();

    /**
     * 获取站场详情
     */
    @GetMapping(PREFIX + "/get")
    CommonResult<SiteRespDTO> getSite(@RequestParam("siteId") Long siteId);

    /**
     * 获取站场下的所有子节点ID
     */
    @GetMapping(PREFIX + "/list-child-ids")
    CommonResult<List<Long>> listChildIds(@RequestParam("parentId") Long parentId);

}
