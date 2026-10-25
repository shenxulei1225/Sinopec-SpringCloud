package cn.cheers.x.module.dynamicbusiness.api.directory;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.module.dynamicbusiness.enums.ApiConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 用户目录跨模块只读接口。
 *
 * <p>权威来源是动态业务 {@code user_account} 实体。接口不修改用户，不访问系统用户表补值。</p>
 */
@FeignClient(name = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 用户目录")
public interface UserAccountApi {

    String PREFIX = ApiConstants.DYNAMICBUSINESS_PREFIX + "/user-account-directory";

    /** 按用户目录 ID 读取用户。不存在时显式报错。 */
    @GetMapping(PREFIX + "/get")
    @Operation(summary = "按 ID 读取用户目录")
    CommonResult<UserAccountDTO> getUserAccount(@RequestParam("id") Long id);

    /** 一次读取全部指定用户。任一 ID 不存在时显式报错。 */
    @GetMapping(PREFIX + "/list-by-ids")
    @Operation(summary = "按 ID 批量读取用户目录")
    CommonResult<List<UserAccountDTO>> listUserAccountsByIds(@RequestParam("ids") List<Long> ids);

    /** 按租户内唯一账号读取用户。不存在时显式报错。 */
    @GetMapping(PREFIX + "/get-by-account")
    @Operation(summary = "按账号读取用户目录")
    CommonResult<UserAccountDTO> getUserAccountByAccount(@RequestParam("account") String account);

    /** 按手机读取用户。不存在或不唯一时显式报错。 */
    @GetMapping(PREFIX + "/get-by-mobile")
    @Operation(summary = "按手机读取用户目录")
    CommonResult<UserAccountDTO> getUserAccountByMobile(@RequestParam("mobile") String mobile);

    /**
     * 按昵称筛选用户目录。昵称为空时返回当前租户全部用户。
     * 只匹配用户目录名称，不查询系统用户。
     */
    @GetMapping(PREFIX + "/list-by-nickname")
    @Operation(summary = "按昵称读取用户目录")
    CommonResult<List<UserAccountDTO>> listUserAccountsByNickname(
            @RequestParam(value = "nickname", required = false) String nickname);
}
