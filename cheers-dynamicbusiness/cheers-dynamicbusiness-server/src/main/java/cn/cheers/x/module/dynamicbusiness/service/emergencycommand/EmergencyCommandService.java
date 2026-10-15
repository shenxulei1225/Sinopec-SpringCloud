package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandAssessReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCompleteStepReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandDetailRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandEventRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandFromAlertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartReqVO;

/**
 * 应急指挥写入。
 *
 * 管什么：告警转入应急事件、研判、按类型和级别启动响应、完成响应上的步骤。
 * 不管什么：预案正文怎么编排。预案在标准规范里用步骤管理写好，这里只读取。
 * 禁止：写入旧应急服务的事件表，或在研判时绑定预案、复制步骤。
 */
public interface EmergencyCommandService {

    PageResult<EmergencyCommandEventRespVO> pageEvents(String eventStatus, String eventType, Integer pageNo, Integer pageSize);

    EmergencyCommandEventRespVO getEvent(String eventId);

    /** 同一告警已转入时拒绝，并说明已经转入。 */
    String openFromAlert(EmergencyCommandFromAlertReqVO reqVO);

    void assess(EmergencyCommandAssessReqVO reqVO);

    String start(EmergencyCommandStartReqVO reqVO);

    EmergencyCommandDetailRespVO detail(String eventId, String facilityId);

    void completeStep(EmergencyCommandCompleteStepReqVO reqVO);

    void startStep(EmergencyCommandCompleteStepReqVO reqVO);
}
