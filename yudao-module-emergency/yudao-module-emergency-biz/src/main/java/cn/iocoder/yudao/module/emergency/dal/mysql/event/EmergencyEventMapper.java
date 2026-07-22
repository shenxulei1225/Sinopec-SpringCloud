package cn.iocoder.yudao.module.emergency.dal.mysql.event;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.emergency.controller.admin.statistics.vo.EventStatisticsReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;


@Mapper
public interface EmergencyEventMapper extends BaseMapperX<EmergencyEventDO> {

    // 统计相关方法
    Long countEvents(@Param("reqVO") EventStatisticsReqVO reqVO);

    Long countConfirmedEvents(@Param("reqVO") EventStatisticsReqVO reqVO);

    Long countHandledEvents(@Param("reqVO") EventStatisticsReqVO reqVO);

    /**
     * 按告警关联键查已转事件（幂等 / 防重）
     */
    default EmergencyEventDO selectBySourceAlert(String sourceAlertType, String sourceAlertId) {
        if (sourceAlertType == null || sourceAlertType.isBlank()
                || sourceAlertId == null || sourceAlertId.isBlank()) {
            return null;
        }
        return selectOne(new LambdaQueryWrapperX<EmergencyEventDO>()
                .eq(EmergencyEventDO::getSourceAlertType, sourceAlertType.trim())
                .eq(EmergencyEventDO::getSourceAlertId, sourceAlertId.trim())
                .last("LIMIT 1"));
    }

    /**
     * 查询事件的 attachments 字段（用于调试和修复）
     *
     * 说明：直接用 PostgreSQL 的 ::text 把 jsonb 转成字符串，避免 TypeHandler / 自动映射不生效导致取到 null
     */
    @Select("SELECT attachments::text FROM emergency_event WHERE id = #{id} AND deleted = false AND tenant_id = #{tenantId}")
    String selectAttachmentsTextById(@Param("id") Long id, @Param("tenantId") Long tenantId);
}











