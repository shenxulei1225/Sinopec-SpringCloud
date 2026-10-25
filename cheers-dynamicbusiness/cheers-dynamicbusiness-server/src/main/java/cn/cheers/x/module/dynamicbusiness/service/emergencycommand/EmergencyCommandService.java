package cn.cheers.x.module.dynamicbusiness.service.emergencycommand;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandAssessReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandChangeLevelReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCloseReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandCompleteStepReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandDetailRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandEventRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandFromAlertReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandIntakeReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandJudgementReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandPlanRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandStartReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.emergencycommand.vo.EmergencyCommandSubmitReportReqVO;

import java.util.List;

/**
 * 应急指挥写入。
 *
 * 管什么：研判时把选定预案写到事件上，启动后按这份预案读步骤，完成、上报、变级和结案都追加到事件脉络。
 * 不管什么：预案正文怎么编排。预案在应急预案目录里写好，这里只读取。
 * 禁止：按事件分类另找一份预案。把步骤复制到响应上。变级时清掉原级别的完成和上报。
 */
public interface EmergencyCommandService {

    PageResult<EmergencyCommandEventRespVO> pageEvents(String eventStatus, String eventType, Integer pageNo, Integer pageSize);

    EmergencyCommandEventRespVO getEvent(String eventId);

    /** 人工接报。不写告警编号。事件进入待研判。 */
    String intake(EmergencyCommandIntakeReqVO reqVO);

    /** 同一告警已转入时拒绝，并说明已经转入。 */
    String openFromAlert(EmergencyCommandFromAlertReqVO reqVO);

    void assess(EmergencyCommandAssessReqVO reqVO);

    /** 图层上确认研判。下拉本身不写。响应中的级别变化才进入脉络。 */
    void confirmJudgement(EmergencyCommandJudgementReqVO reqVO);

    String start(EmergencyCommandStartReqVO reqVO);

    List<EmergencyCommandPlanRespVO> listPlans();

    EmergencyCommandDetailRespVO detail(String eventId, String facilityId);

    void changeLevel(EmergencyCommandChangeLevelReqVO reqVO);

    void completeStep(EmergencyCommandCompleteStepReqVO reqVO);

    void startStep(EmergencyCommandCompleteStepReqVO reqVO);

    void submitReport(EmergencyCommandSubmitReportReqVO reqVO);

    /** 后期处置之后结案。结案状态改为已结案，脉络追加一条结案。已结案不再改。 */
    void closeCase(EmergencyCommandCloseReqVO reqVO);
}
