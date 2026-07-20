package cn.iocoder.yudao.module.emergency.service.response;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.emergency.controller.admin.response.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.response.EmergencyResponseDO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseExpandRespDTO;
import cn.iocoder.yudao.module.emergency.api.orchestration.dto.EmergencyStartResponseReqDTO;

public interface EmergencyResponseService {

    ResponseRespVO start(ResponseStartReqVO reqVO);

    void validateStartForOrchestration(EmergencyStartResponseReqDTO req);

    EmergencyStartResponseExpandRespDTO expandStartForOrchestration(EmergencyStartResponseReqDTO req);

    void persistStartForOrchestration(EmergencyStartResponseExpandRespDTO expandResult);

    void upgrade(String responseNo, ResponseUpgradeReqVO reqVO);

    void cancel(String responseNo, ResponseCancelReqVO reqVO);

    PageResult<TimelineItemRespVO> getTimeline(String responseNo, TimelinePageReqVO reqVO);

    ResponseRespVO getResponse(Long id);

    EmergencyResponseDO getResponseByNo(String responseNo);

    ResponseRespVO getResponseByEventId(Long eventId);

    PageResult<ResponseRespVO> getResponsePage(PageParam pageReqVO);
}


