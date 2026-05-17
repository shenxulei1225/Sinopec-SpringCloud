package cn.iocoder.yudao.module.alarm.dal.mysql;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmAttachmentDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 告警附件 Mapper
 *
 * @author 告警管理模块
 */
@Mapper
public interface AlarmAttachmentMapper extends BaseMapperX<AlarmAttachmentDO> {

    /**
     * 根据告警ID查询附件列表
     *
     * @param alarmId 告警ID
     * @return 附件列表
     */
    default List<AlarmAttachmentDO> selectListByAlarmId(Long alarmId) {
        return selectList(AlarmAttachmentDO::getAlarmId, alarmId);
    }

    /**
     * 根据告警ID删除附件
     *
     * @param alarmId 告警ID
     * @return 删除数量
     */
    default int deleteByAlarmId(Long alarmId) {
        return delete(AlarmAttachmentDO::getAlarmId, alarmId);
    }

    /**
     * 根据告警ID查询附件数量
     *
     * @param alarmId 告警ID
     * @return 附件数量
     */
    default Long selectCountByAlarmId(Long alarmId) {
        return selectCount(AlarmAttachmentDO::getAlarmId, alarmId);
    }

    /**
     * 根据文件类型查询附件列表
     *
     * @param alarmId  告警ID
     * @param fileType 文件类型
     * @return 附件列表
     */
    default List<AlarmAttachmentDO> selectListByAlarmIdAndFileType(Long alarmId, String fileType) {
        return selectList(new LambdaQueryWrapperX<AlarmAttachmentDO>()
                .eq(AlarmAttachmentDO::getAlarmId, alarmId)
                .eq(AlarmAttachmentDO::getFileType, fileType));
    }

}
