package cn.cheers.x.alarm.convert;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.alarm.controller.admin.vo.alarm.*;
import cn.cheers.x.alarm.dal.dataobject.AlarmAttachmentDO;
import cn.cheers.x.alarm.dal.dataobject.AlarmDO;
import cn.cheers.x.alarm.controller.admin.vo.linkage.LinkageExecutionRespVO;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 告警 Convert
 * 
 * <p>负责告警相关的 DO 和 VO 之间的转换</p>
 *
 * 
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlarmConvert {

    AlarmConvert INSTANCE = Mappers.getMapper(AlarmConvert.class);

    // ========== 告警相关转换 ==========

    /**
     * 人工上报请求转换为 DO
     * 
     * @param bean 人工上报请求 VO
     * @return 告警 DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alarmCode", ignore = true)
    @Mapping(target = "alarmCategoryId", ignore = true)
    @Mapping(target = "alarmModelId", ignore = true)
    @Mapping(target = "alarmTypePath", ignore = true)
    @Mapping(target = "alarmStatus", ignore = true)
    @Mapping(target = "alarmSource", ignore = true)
    @Mapping(target = "deviceName", ignore = true)
    @Mapping(target = "locationName", ignore = true)
    @Mapping(target = "triggerValue", ignore = true)
    @Mapping(target = "thresholdValue", ignore = true)
    @Mapping(target = "triggerCount", ignore = true)
    @Mapping(target = "ruleId", ignore = true)
    @Mapping(target = "escalationLevel", ignore = true)
    @Mapping(target = "escalationTime", ignore = true)
    @Mapping(target = "acknowledgeTime", ignore = true)
    @Mapping(target = "acknowledgeUserId", ignore = true)
    @Mapping(target = "acknowledgeUserName", ignore = true)
    @Mapping(target = "acknowledgeRemark", ignore = true)
    @Mapping(target = "handleTime", ignore = true)
    @Mapping(target = "handleUserId", ignore = true)
    @Mapping(target = "handleUserName", ignore = true)
    @Mapping(target = "handleMeasure", ignore = true)
    @Mapping(target = "handleResult", ignore = true)
    @Mapping(target = "closeTime", ignore = true)
    @Mapping(target = "closeUserId", ignore = true)
    @Mapping(target = "closeUserName", ignore = true)
    @Mapping(target = "closeReason", ignore = true)
    @Mapping(target = "closeRemark", ignore = true)
    @Mapping(target = "durationSeconds", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AlarmDO convert(AlarmReportReqVO bean);

    /**
     * DO 转换为响应 VO
     * 
     * @param bean 告警 DO
     * @return 告警响应 VO
     */
    AlarmRespVO convert(AlarmDO bean);

    /**
     * DO 列表转换为响应 VO 列表
     * 
     * @param list 告警 DO 列表
     * @return 告警响应 VO 列表
     */
    List<AlarmRespVO> convertList(List<AlarmDO> list);

    /**
     * DO 分页结果转换为响应 VO 分页结果
     * 
     * @param page 告警 DO 分页结果
     * @return 告警响应 VO 分页结果
     */
    PageResult<AlarmRespVO> convertPage(PageResult<AlarmDO> page);

    /**
     * DO 转换为详情响应 VO
     * 
     * @param bean 告警 DO
     * @return 告警详情响应 VO
     */
    AlarmDetailRespVO convertDetail(AlarmDO bean);

    // ========== 附件相关转换 ==========

    /**
     * 附件 DO 转换为响应 VO
     * 
     * @param bean 附件 DO
     * @return 附件响应 VO
     */
    AlarmDetailRespVO.AlarmAttachmentVO convertAttachment(AlarmAttachmentDO bean);

    /**
     * 附件 DO 列表转换为响应 VO 列表
     * 
     * @param list 附件 DO 列表
     * @return 附件响应 VO 列表
     */
    List<AlarmDetailRespVO.AlarmAttachmentVO> convertAttachmentList(List<AlarmAttachmentDO> list);

    /**
     * 附件请求 VO 转换为 DO
     * 
     * @param bean 附件请求 VO
     * @return 附件 DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "alarmId", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AlarmAttachmentDO convertAttachment(AlarmReportReqVO.AttachmentVO bean);

    /**
     * 附件请求 VO 列表转换为 DO 列表
     * 
     * @param list 附件请求 VO 列表
     * @return 附件 DO 列表
     */
    List<AlarmAttachmentDO> convertAttachmentReqList(List<AlarmReportReqVO.AttachmentVO> list);

    // ========== 联动执行相关转换 ==========

    /**
     * 联动执行 DO 转换为响应 VO
     * 
     * @param bean 联动执行 DO
     * @return 联动执行响应 VO
     */
    LinkageExecutionRespVO convertLinkageExecution(LinkageExecutionDO bean);

    /**
     * 联动执行 DO 列表转换为响应 VO 列表
     * 
     * @param list 联动执行 DO 列表
     * @return 联动执行响应 VO 列表
     */
    List<LinkageExecutionRespVO> convertLinkageExecutionList(List<LinkageExecutionDO> list);

    /**
     * 联动执行 DO 转换为简要信息 VO（用于告警详情）
     * 
     * @param bean 联动执行 DO
     * @return 联动执行简要信息 VO
     */
    AlarmDetailRespVO.LinkageExecutionSimpleVO convertLinkageExecutionSimple(LinkageExecutionDO bean);

    /**
     * 联动执行 DO 列表转换为简要信息 VO 列表（用于告警详情）
     * 
     * @param list 联动执行 DO 列表
     * @return 联动执行简要信息 VO 列表
     */
    List<AlarmDetailRespVO.LinkageExecutionSimpleVO> convertLinkageExecutionSimpleList(List<LinkageExecutionDO> list);

}
