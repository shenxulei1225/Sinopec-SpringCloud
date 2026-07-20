package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;

public interface ProcessTimelineService {

    Long append(ProcessTimelineActionAppendReqDTO request);

    PageResult<ProcessTimelineActionRespDTO> pageByTarget(
            String targetType, String targetId, Integer pageNo, Integer pageSize);
}
