package cn.cheers.x.alarm.dal.mysql;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.alarm.controller.admin.vo.linkage.LinkageRulePageReqVO;
import cn.cheers.x.alarm.dal.dataobject.LinkageRuleDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 联动规则 Mapper
 *
 * @author 告警管理模块
 */
@Mapper
public interface LinkageRuleMapper extends BaseMapperX<LinkageRuleDO> {

    /**
     * 分页查询联动规则列表
     *
     * @param reqVO 查询条件
     * @return 联动规则分页结果
     */
    default PageResult<LinkageRuleDO> selectPage(LinkageRulePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LinkageRuleDO>()
                .likeIfPresent(LinkageRuleDO::getRuleName, reqVO.getRuleName())
                .likeIfPresent(LinkageRuleDO::getRuleCode, reqVO.getRuleCode())
                .eqIfPresent(LinkageRuleDO::getAlarmRuleId, reqVO.getAlarmRuleId())
                .eqIfPresent(LinkageRuleDO::getAlarmTypeId, reqVO.getAlarmTypeId())
                .eqIfPresent(LinkageRuleDO::getAlarmLevel, reqVO.getAlarmLevel())
                .eqIfPresent(LinkageRuleDO::getEnabled, reqVO.getEnabled())
                .orderByDesc(LinkageRuleDO::getPriority)
                .orderByDesc(LinkageRuleDO::getId));
    }

    /**
     * 根据规则编码查询联动规则
     *
     * @param ruleCode 规则编码
     * @return 联动规则
     */
    default LinkageRuleDO selectByRuleCode(String ruleCode) {
        return selectOne(LinkageRuleDO::getRuleCode, ruleCode);
    }

    /**
     * 查询所有启用的联动规则
     *
     * @return 启用的联动规则列表
     */
    default List<LinkageRuleDO> selectEnabledRules() {
        return selectList(new LambdaQueryWrapperX<LinkageRuleDO>()
                .eq(LinkageRuleDO::getEnabled, true)
                .orderByDesc(LinkageRuleDO::getPriority));
    }

    /**
     * 根据告警规则ID查询启用的联动规则
     *
     * @param alarmRuleId 告警规则ID
     * @return 联动规则列表
     */
    default List<LinkageRuleDO> selectEnabledRulesByAlarmRuleId(Long alarmRuleId) {
        return selectList(new LambdaQueryWrapperX<LinkageRuleDO>()
                .eq(LinkageRuleDO::getEnabled, true)
                .and(wrapper -> wrapper
                        .isNull(LinkageRuleDO::getAlarmRuleId)
                        .or()
                        .eq(LinkageRuleDO::getAlarmRuleId, alarmRuleId))
                .orderByDesc(LinkageRuleDO::getPriority));
    }

    /**
     * 根据告警类型ID查询启用的联动规则
     *
     * @param alarmTypeId 告警类型ID
     * @return 联动规则列表
     */
    default List<LinkageRuleDO> selectEnabledRulesByAlarmTypeId(Long alarmTypeId) {
        return selectList(new LambdaQueryWrapperX<LinkageRuleDO>()
                .eq(LinkageRuleDO::getEnabled, true)
                .and(wrapper -> wrapper
                        .isNull(LinkageRuleDO::getAlarmTypeId)
                        .or()
                        .eq(LinkageRuleDO::getAlarmTypeId, alarmTypeId))
                .orderByDesc(LinkageRuleDO::getPriority));
    }

    /**
     * 根据告警级别查询启用的联动规则
     *
     * @param alarmLevel 告警级别
     * @return 联动规则列表
     */
    default List<LinkageRuleDO> selectEnabledRulesByAlarmLevel(String alarmLevel) {
        return selectList(new LambdaQueryWrapperX<LinkageRuleDO>()
                .eq(LinkageRuleDO::getEnabled, true)
                .and(wrapper -> wrapper
                        .isNull(LinkageRuleDO::getAlarmLevel)
                        .or()
                        .eq(LinkageRuleDO::getAlarmLevel, alarmLevel))
                .orderByDesc(LinkageRuleDO::getPriority));
    }

    /**
     * 查询匹配告警的联动规则
     * 
     * @param alarmRuleId 告警规则ID（可为空）
     * @param alarmTypeId 告警类型ID
     * @param alarmCategoryId 告警分类ID
     * @param alarmLevel 告警级别
     * @return 匹配的联动规则列表
     */
    default List<LinkageRuleDO> selectMatchingRules(Long alarmRuleId, Long alarmTypeId, 
                                                     Long alarmCategoryId, String alarmLevel) {
        LambdaQueryWrapperX<LinkageRuleDO> wrapper = new LambdaQueryWrapperX<LinkageRuleDO>()
                .eq(LinkageRuleDO::getEnabled, true);
        
        // 匹配告警规则ID（为空表示适用所有，或者匹配指定的告警规则ID）
        if (alarmRuleId != null) {
            wrapper.and(w -> w
                    .isNull(LinkageRuleDO::getAlarmRuleId)
                    .or()
                    .eq(LinkageRuleDO::getAlarmRuleId, alarmRuleId));
        } else {
            wrapper.isNull(LinkageRuleDO::getAlarmRuleId);
        }
        
        // 匹配告警类型ID（为空表示适用所有）
        wrapper.and(w -> w
                .isNull(LinkageRuleDO::getAlarmTypeId)
                .or()
                .eq(LinkageRuleDO::getAlarmTypeId, alarmTypeId));
        
        // 匹配告警分类ID（为空表示适用所有）
        wrapper.and(w -> w
                .isNull(LinkageRuleDO::getAlarmCategoryId)
                .or()
                .eq(LinkageRuleDO::getAlarmCategoryId, alarmCategoryId));
        
        // 匹配告警级别（为空表示适用所有）
        wrapper.and(w -> w
                .isNull(LinkageRuleDO::getAlarmLevel)
                .or()
                .eq(LinkageRuleDO::getAlarmLevel, alarmLevel));
        
        wrapper.orderByDesc(LinkageRuleDO::getPriority);
        
        return selectList(wrapper);
    }

    /**
     * 检查规则编码是否存在
     *
     * @param ruleCode 规则编码
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    default boolean existsByRuleCode(String ruleCode, Long excludeId) {
        return selectCount(new LambdaQueryWrapperX<LinkageRuleDO>()
                .eq(LinkageRuleDO::getRuleCode, ruleCode)
                .neIfPresent(LinkageRuleDO::getId, excludeId)) > 0;
    }

}
