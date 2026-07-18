package cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule;

import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleResourceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 巡检任务计划点资源分配 Mapper。
 *
 * 
 */
@Mapper
public interface InspectionTaskScheduleResourceMapper extends BaseMapperX<InspectionTaskScheduleResourceDO> {

    /**
     * 根据计划点 ID 查询资源列表。
     */
    @Select("SELECT * FROM inspection_task_schedule_resource WHERE schedule_id = #{scheduleId} AND deleted = 0")
    default List<InspectionTaskScheduleResourceDO> selectListByScheduleId(@Param("scheduleId") Long scheduleId) {
        return selectList(InspectionTaskScheduleResourceDO::getScheduleId, scheduleId);
    }

    /**
     * 根据计划点 ID 列表批量查询资源。
     */
    default List<InspectionTaskScheduleResourceDO> selectListByScheduleIds(@Param("scheduleIds") List<Long> scheduleIds) {
        return selectList(InspectionTaskScheduleResourceDO::getScheduleId, scheduleIds);
    }
}
