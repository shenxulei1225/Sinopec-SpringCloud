package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingSaveReqDTO;

public interface ProcessCapabilityBindingService {

    ProcessCapabilityBindingRespDTO saveBinding(String entityTypeCode, ProcessCapabilityBindingSaveReqDTO request);

    ProcessCapabilityBindingRespDTO getBinding(String entityTypeCode);

    ProcessCapabilityBindingRespDTO publishBinding(String entityTypeCode);

    ProcessCapabilityBindingRespDTO getPublishedBinding(String entityTypeCode);
}
