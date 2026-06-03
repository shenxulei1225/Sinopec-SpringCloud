package cn.cheers.x.module.dynamicbusiness.service.capability;

import cn.cheers.x.module.dynamicbusiness.controller.admin.capability.vo.CapabilityFieldCheckRespVO;

public interface CapabilityFieldCheckService {

    CapabilityFieldCheckRespVO checkField(
            String instanceKey, String fieldKey, String value, Long excludeId);
}
