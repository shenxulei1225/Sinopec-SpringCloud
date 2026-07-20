package cn.iocoder.yudao.module.emergency.dal.mysql.event;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
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
     * 查询事件的 attachments 字段（用于调试和修复）
     *
     * 说明：直接用 PostgreSQL 的 ::text 把 jsonb 转成字符串，避免 TypeHandler / 自动映射不生效导致取到 null
     */
    @Select("SELECT attachments::text FROM emergency_event WHERE id = #{id} AND deleted = false AND tenant_id = #{tenantId}")
    String selectAttachmentsTextById(@Param("id") Long id, @Param("tenantId") Long tenantId);
}











