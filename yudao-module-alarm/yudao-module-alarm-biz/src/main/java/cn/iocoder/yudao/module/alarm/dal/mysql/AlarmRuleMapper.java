package cn.iocoder.yudao.module.alarm.dal.mysql;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.alarm.controller.admin.vo.rule.AlarmRulePageReqVO;
import cn.iocoder.yudao.module.alarm.dal.dataobject.AlarmRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 告警规则 Mapper
 *
 * @author 告警管理模块
 */
@Mapper
public interface AlarmRuleMapper extends BaseMapperX<AlarmRuleDO> {

    /**
     * 分页查询告警规则列表
     *
     * @param reqVO 查询条件
     * @return 告警规则分页结果
     */
    default PageResult<AlarmRuleDO> selectPage(AlarmRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AlarmRuleDO>()
                .likeIfPresent(AlarmRuleDO::getRuleName, reqVO.getRuleName())
                .likeIfPresent(AlarmRuleDO::getRuleCode, reqVO.getRuleCode())
                .eqIfPresent(AlarmRuleDO::getRuleType, reqVO.getRuleType())
                .eqIfPresent(AlarmRuleDO::getAlarmTypeId, reqVO.getAlarmTypeId())
                .eqIfPresent(AlarmRuleDO::getAlarmCategoryId, reqVO.getAlarmCategoryId())
                .eqIfPresent(AlarmRuleDO::getAlarmLevel, reqVO.getAlarmLevel())
                .eqIfPresent(AlarmRuleDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(AlarmRuleDO::getPriority)
                .orderByDesc(AlarmRuleDO::getId));
    }

    /**
     * 根据规则编码查询规则
     *
     * @param ruleCode 规则编码
     * @return 告警规则
     */
    default AlarmRuleDO selectByRuleCode(String ruleCode) {
        return selectOne(AlarmRuleDO::getRuleCode, ruleCode);
    }

    /**
     * 查询所有启用的规则
     *
     * @return 启用的规则列表
     */
    default List<AlarmRuleDO> selectEnabledRules() {
        return selectList(new LambdaQueryWrapperX<AlarmRuleDO>()
                .eq(AlarmRuleDO::getEnabled, true)
                .orderByDesc(AlarmRuleDO::getPriority));
    }

    /**
     * 根据设备类型查询启用的规则
     *
     * @param deviceType 设备类型
     * @return 规则列表
     */
    default List<AlarmRuleDO> selectEnabledRulesByDeviceType(String deviceType) {
        return selectList(new LambdaQueryWrapperX<AlarmRuleDO>()
                .eq(AlarmRuleDO::getEnabled, true)
                .and(wrapper -> wrapper
                        .isNull(AlarmRuleDO::getDeviceType)
                        .or()
                        .eq(AlarmRuleDO::getDeviceType, deviceType))
                .orderByDesc(AlarmRuleDO::getPriority));
    }

    /**
     * 根据告警类型ID查询启用的规则
     *
     * @param alarmTypeId 告警类型ID
     * @return 规则列表
     */
    default List<AlarmRuleDO> selectEnabledRulesByAlarmTypeId(Long alarmTypeId) {
        return selectList(new LambdaQueryWrapperX<AlarmRuleDO>()
                .eq(AlarmRuleDO::getEnabled, true)
                .eq(AlarmRuleDO::getAlarmTypeId, alarmTypeId)
                .orderByDesc(AlarmRuleDO::getPriority));
    }

    /**
     * 根据告警分类ID查询启用的规则
     *
     * @param alarmCategoryId 告警分类ID
     * @return 规则列表
     */
    default List<AlarmRuleDO> selectEnabledRulesByAlarmCategoryId(Long alarmCategoryId) {
        return selectList(new LambdaQueryWrapperX<AlarmRuleDO>()
                .eq(AlarmRuleDO::getEnabled, true)
                .eq(AlarmRuleDO::getAlarmCategoryId, alarmCategoryId)
                .orderByDesc(AlarmRuleDO::getPriority));
    }

    /**
     * 根据规则类型查询规则列表
     *
     * @param ruleType 规则类型
     * @return 规则列表
     */
    default List<AlarmRuleDO> selectListByRuleType(String ruleType) {
        return selectList(AlarmRuleDO::getRuleType, ruleType);
    }

    /**
     * 检查规则编码是否存在
     *
     * @param ruleCode 规则编码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    default boolean existsByRuleCode(String ruleCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<AlarmRuleDO>()
                .eq(AlarmRuleDO::getRuleCode, ruleCode)
                .neIfPresent(AlarmRuleDO::getId, excludeId)) > 0;
    }

}
