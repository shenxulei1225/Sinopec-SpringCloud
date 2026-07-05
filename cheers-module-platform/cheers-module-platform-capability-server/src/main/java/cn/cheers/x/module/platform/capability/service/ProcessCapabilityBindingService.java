package cn.cheers.x.module.platform.capability.service;

import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingRespDTO;
import cn.cheers.x.module.platform.capability.api.dto.ProcessCapabilityBindingSaveReqDTO;

public interface ProcessCapabilityBindingService {

    ProcessCapabilityBindingRespDTO saveBinding(String businessTypeCode, ProcessCapabilityBindingSaveReqDTO request);

    ProcessCapabilityBindingRespDTO getBinding(String businessTypeCode);

    ProcessCapabilityBindingRespDTO publishBinding(String businessTypeCode);

    ProcessCapabilityBindingRespDTO getPublishedBinding(String businessTypeCode);
}
