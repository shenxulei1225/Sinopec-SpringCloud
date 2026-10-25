package cn.cheers.x.module.dynamicbusiness.api.directory;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.module.dynamicbusiness.service.directory.UserAccountDirectoryQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.cheers.x.framework.common.pojo.CommonResult.success;

/**
 * 用户目录 RPC 入口。
 *
 * <p>只负责参数接收和响应包装。禁止在入口解析通用实体字段袋或访问系统用户。</p>
 */
@RestController
@Validated
@RequiredArgsConstructor
public class UserAccountApiImpl implements UserAccountApi {

    private final UserAccountDirectoryQueryService queryService;

    @Override
    public CommonResult<UserAccountDTO> getUserAccount(Long id) {
        return success(queryService.getUserAccount(id));
    }

    @Override
    public CommonResult<List<UserAccountDTO>> listUserAccountsByIds(List<Long> ids) {
        return success(queryService.listUserAccountsByIds(ids));
    }

    @Override
    public CommonResult<UserAccountDTO> getUserAccountByAccount(String account) {
        return success(queryService.getUserAccountByAccount(account));
    }

    @Override
    public CommonResult<UserAccountDTO> getUserAccountByMobile(String mobile) {
        return success(queryService.getUserAccountByMobile(mobile));
    }

    @Override
    public CommonResult<List<UserAccountDTO>> listUserAccountsByNickname(String nickname) {
        return success(queryService.listUserAccountsByNickname(nickname));
    }
}
