package cn.cheers.x.module.dynamicbusiness.api.persongroup;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 人员编组的在册成员。
 * 工单认领、指定执行人时用来核对是不是这个班组里的人。
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 人员编组成员")
public interface PersonGroupMemberApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/person-group-member";

    @GetMapping(PREFIX + "/contains")
    @Operation(summary = "用户是否在这个编组里")
    CommonResult<Boolean> contains(@RequestParam("categoryId") Long categoryId,
                                   @RequestParam("userId") Long userId);
}
