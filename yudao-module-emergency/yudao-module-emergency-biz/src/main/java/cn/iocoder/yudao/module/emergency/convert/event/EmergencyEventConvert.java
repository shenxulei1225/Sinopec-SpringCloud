package cn.iocoder.yudao.module.emergency.convert.event;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.event.vo.*;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventAssessDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportInternalDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventReportExternalDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventStatusHistoryDO;
import org.mapstruct.*;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EmergencyEventConvert {

    EmergencyEventConvert INSTANCE = Mappers.getMapper(EmergencyEventConvert.class);

    @Mappings({
            @Mapping(target = "id", ignore = true), // ID由数据库自动生成
            @Mapping(target = "status", constant = "pending"),
            @Mapping(target = "isGovernmentConfirmed", constant = "false"),
            @Mapping(target = "locationGis", expression = "java(convertLocationToWkt(req.getLocation()))"),
            @Mapping(target = "locationAddress", expression = "java(req.getLocation() != null ? req.getLocation().getAddress() : null)"),
            // 兼容 attachments 传数组或对象：数组视为 photos 列表
            @Mapping(target = "attachments", qualifiedByName = "normalizeAttachmentsForEvent"),
            // impactSummary直接映射，暂时忽略，避免MapStruct歧义
            @Mapping(target = "impactSummary", ignore = true)
    })
    EmergencyEventDO convert(EventCreateReqVO req);

    @Mappings({
            @Mapping(target = "attachments", qualifiedByName = "directMapMapping"),
            @Mapping(target = "impactSummary", qualifiedByName = "directMapMapping")
    })
    EventRespVO convert(EmergencyEventDO event);

    /**
     * 转换内部上报（使用新的DO和表结构）
     */
    @Mappings({
            @Mapping(target = "id", ignore = true), // ID由数据库自动生成
            @Mapping(target = "emergencyEventId", source = "eventId"),
            @Mapping(target = "reporterId", ignore = true), // 需要从当前用户上下文获取
            @Mapping(target = "reporterName", ignore = true), // 需要从UserId翻译
            @Mapping(target = "reportTime", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "content", source = "req.content"),
            @Mapping(target = "attachments", expression = "java(convertAttachmentsToMapForReport(req.getAttachments()))"),
            @Mapping(target = "customFields", ignore = true),
            @Mapping(target = "locationAddress", ignore = true),
            @Mapping(target = "locationGis", ignore = true),
            @Mapping(target = "locationBim", ignore = true),
            @Mapping(target = "formTemplateId", ignore = true)
    })
    EmergencyEventReportInternalDO convertInternalReport(Long eventId, EventReportReqVO req);

    /**
     * 转换外部上报（使用新的DO和表结构）
     */
    @Mappings({
            @Mapping(target = "id", ignore = true), // ID由数据库自动生成
            @Mapping(target = "emergencyEventId", source = "eventId"),
            @Mapping(target = "reporterId", ignore = true), // 需要从当前用户上下文获取
            @Mapping(target = "reporterName", source = "req.reporterName"),
            @Mapping(target = "reportOrgName", source = "req.reportOrgName"),
            @Mapping(target = "reporterPhone", source = "req.reporterContact"),
            @Mapping(target = "reportTime", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "descriptionPart1", source = "req.content"), // 将content映射到descriptionPart1
            @Mapping(target = "attachments", expression = "java(convertAttachmentsToMapForReport(req.getAttachments()))"),
            @Mapping(target = "auditStatus", constant = "draft"), // 默认草稿状态
            @Mapping(target = "formTemplateId", ignore = true),
            @Mapping(target = "customFields", ignore = true)
    })
    EmergencyEventReportExternalDO convertExternalReport(Long eventId, EventReportReqVO req);

    default String convertLocationToWkt(LocationVO location) {
        if (location == null || location.getLongitude() == null || location.getLatitude() == null) {
            return null;
        }
        return String.format("POINT(%s %s)", location.getLongitude(), location.getLatitude());
    }

    default Map<String, Object> convertAttachments(List<String> photos) {
        if (photos == null || photos.isEmpty()) {
            return null;
        }
        Map<String, Object> attachments = new HashMap<>();
        attachments.put("photos", photos);
        return attachments;
    }

    /**
     * 将 attachments（可能是字符串列表、对象列表或其他）转换为 Map（用于上报）
     * - 若为 List：封装为 {"photos": list}
     * - 若为 Map：直接返回
     * - 其他：返回 null
     */
    @Named("convertAttachmentsToMapForReport")
    @SuppressWarnings("unchecked")
    default Map<String, Object> convertAttachmentsToMapForReport(Object attachments) {
        if (attachments == null) {
            return null;
        }
        if (attachments instanceof Map) {
            return (Map<String, Object>) attachments;
        }
        if (attachments instanceof List) {
            Map<String, Object> result = new HashMap<>();
            result.put("photos", attachments);
            return result;
        }
        // 如果是字符串，也封装为列表
        if (attachments instanceof String) {
            Map<String, Object> result = new HashMap<>();
            result.put("photos", List.of((String) attachments));
            return result;
        }
        return null;
    }
    
    /**
     * 直接映射Map类型，用于impactSummary等字段
     * 避免MapStruct自动选择转换方法导致的歧义
     */
    @Named("directMapMapping")
    default Map<String, Object> directMapMapping(Map<String, Object> map) {
        return map;
    }

    /**
     * 兼容前端传 attachments 为数组或对象（用于事件创建）
     * - 若为 Map：直接返回
     * - 若为 List：视为 photos 列表（支持字符串或对象），封装成 {"photos": list}
     * - 其他：返回 null
     */
    @Named("normalizeAttachmentsForEvent")
    @SuppressWarnings("unchecked")
    default Map<String, Object> normalizeAttachmentsForEvent(Object attachments) {
        if (attachments == null) {
            return null;
        }
        if (attachments instanceof Map) {
            return (Map<String, Object>) attachments;
        }
        if (attachments instanceof List) {
            // 支持字符串数组或对象数组，直接封装为 photos 列表
            Map<String, Object> result = new HashMap<>();
            result.put("photos", attachments);
            return result;
        }
        return null;
    }

    @Mappings({
            @Mapping(target = "id", ignore = true), // ID由数据库自动生成
            @Mapping(target = "emergencyEventId", source = "eventId"),
            @Mapping(target = "assessTime", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "originalLevel", ignore = true), // 需要从事件当前级别获取
            @Mapping(target = "newLevel", source = "req.responseLevel"), // 假设responseLevel对应newLevel
            @Mapping(target = "reason", source = "req.comment"), // 将comment映射到reason
            @Mapping(target = "meetingRecord", ignore = true) // 暂时忽略，后续可以扩展
    })
    EmergencyEventAssessDO convertAssess(Long eventId, EventAssessReqVO req);

    /**
     * 转换外部上报（使用新的DO和表结构）
     */
    @Mappings({
            @Mapping(target = "id", ignore = true), // ID由数据库自动生成
            @Mapping(target = "emergencyEventId", source = "eventId"),
            @Mapping(target = "reportTime", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "descriptionPart1", source = "req.content"), // 使用descriptionPart1存储上报内容
            @Mapping(target = "reportOrgName", source = "req.reportOrgName"),
            @Mapping(target = "reporterName", source = "req.reporterName"),
            @Mapping(target = "reporterPhone", source = "req.reporterContact"),
            @Mapping(target = "attachments", expression = "java(convertAttachmentsToMapForReport(req.getAttachments()))"),
            @Mapping(target = "reporterId", ignore = true), // 外部上报不需要内部用户ID
            @Mapping(target = "formTemplateId", ignore = true),
            @Mapping(target = "customFields", ignore = true),
            @Mapping(target = "auditStatus", constant = "draft"), // 默认草稿状态
            @Mapping(target = "receiveOrgNames", ignore = true),
            @Mapping(target = "occurredAt", ignore = true),
            @Mapping(target = "province", ignore = true),
            @Mapping(target = "cityOrCounty", ignore = true),
            @Mapping(target = "town", ignore = true),
            @Mapping(target = "locationAddress", ignore = true),
            @Mapping(target = "locationGis", ignore = true),
            @Mapping(target = "locationBim", ignore = true),
            @Mapping(target = "pipelineName", ignore = true),
            @Mapping(target = "sectionName", ignore = true),
            @Mapping(target = "unitName", ignore = true),
            @Mapping(target = "siteName", ignore = true),
            @Mapping(target = "eventOrgType", ignore = true),
            @Mapping(target = "eventOrgName", ignore = true),
            @Mapping(target = "descriptionPart2", ignore = true),
            @Mapping(target = "descriptionPart3", ignore = true),
            @Mapping(target = "descriptionOther", ignore = true),
            @Mapping(target = "measurePipelineAndOther", ignore = true),
            @Mapping(target = "measureReportToUpperLower", ignore = true),
            @Mapping(target = "measureResponseActivation", ignore = true),
            @Mapping(target = "measureReportToGovernment", ignore = true),
            @Mapping(target = "measureOther", ignore = true),
            @Mapping(target = "auditTime", ignore = true),
            @Mapping(target = "auditorId", ignore = true),
            @Mapping(target = "auditorName", ignore = true),
            @Mapping(target = "auditComment", ignore = true),
            @Mapping(target = "receiveTime", ignore = true),
            @Mapping(target = "receiveDeptName", ignore = true),
            @Mapping(target = "receiverName", ignore = true),
            @Mapping(target = "receiverPhone", ignore = true),
            @Mapping(target = "leaderInstruction", ignore = true),
            @Mapping(target = "signerId", ignore = true),
            @Mapping(target = "signerName", ignore = true)
    })
    EmergencyEventReportExternalDO convertExternalReport(Long eventId, EventExternalReportReqVO req);

    /**
     * 转换外部上报DO到响应VO
     */
    @Mappings({
            @Mapping(target = "id", source = "report.id"),
            @Mapping(target = "eventId", source = "report.emergencyEventId"),
            @Mapping(target = "content", source = "report.descriptionPart1"), // 从descriptionPart1获取上报内容
            @Mapping(target = "reportOrgName", source = "report.reportOrgName"),
            @Mapping(target = "reporterName", source = "report.reporterName"),
            @Mapping(target = "reporterContact", source = "report.reporterPhone"),
            @Mapping(target = "reportTime", source = "report.reportTime"),
            @Mapping(target = "attachments", source = "report.attachments")
    })
    EventExternalReportRespVO convertExternalReport(EmergencyEventReportExternalDO report);

    PageResult<EventRespVO> convertPage(PageResult<EmergencyEventDO> page);

    /**
     * 转换状态变更历史
     */
    EventStatusHistoryRespVO convertStatusHistory(EmergencyEventStatusHistoryDO history);

    /**
     * 转换状态变更历史分页结果
     */
    PageResult<EventStatusHistoryRespVO> convertStatusHistoryPage(PageResult<EmergencyEventStatusHistoryDO> page);

    /**
     * 转换研判记录列表
     */
    @Mappings({
            @Mapping(target = "id", source = "assess.id"),
            @Mapping(target = "responseLevel", source = "assess.newLevel"),
            @Mapping(target = "recommendedPlanLevel", ignore = true), // 数据库中暂无此字段，暂时忽略
            @Mapping(target = "comment", source = "assess.reason"),
            @Mapping(target = "creator", source = "assess.creator"),
            @Mapping(target = "creatorName", ignore = true), // 需要从API获取
            @Mapping(target = "createTime", source = "assess.createTime"),
            @Mapping(target = "assessTime", source = "assess.assessTime")
    })
    EventAssessListRespVO convertAssess(EmergencyEventAssessDO assess);

    List<EventAssessListRespVO> convertAssessList(List<EmergencyEventAssessDO> assessList);
}
