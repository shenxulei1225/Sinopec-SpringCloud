package cn.cheers.x.module.platform.capability.api;

import cn.cheers.x.module.platform.capability.api.dto.ResolveWorkItemsReqDTO;
import cn.cheers.x.module.platform.capability.service.MappingProfileService;
import cn.cheers.x.module.platform.contract.dto.work.WorkItemDTO;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

@RestController
@Validated
public class MappingProfileApiImpl implements MappingProfileApi {

    @Resource
    private MappingProfileService mappingProfileService;

    @Override
    public CommonResult<List<WorkItemDTO>> resolveWorkItems(String profileId, ResolveWorkItemsReqDTO request) {
        return success(mappingProfileService.resolveWorkItems(profileId, request));
    }
}
