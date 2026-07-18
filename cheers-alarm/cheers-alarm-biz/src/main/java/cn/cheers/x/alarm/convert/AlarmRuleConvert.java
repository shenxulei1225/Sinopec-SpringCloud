package cn.cheers.x.alarm.convert;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.alarm.controller.admin.vo.rule.*;
import cn.cheers.x.alarm.dal.dataobject.AlarmRuleDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 告警规则 Convert
 * 
 * <p>负责告警规则相关的 DO 和 VO 之间的转换</p>
 *
 * 
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlarmRuleConvert {

    AlarmRuleConvert INSTANCE = Mappers.getMapper(AlarmRuleConvert.class);

    // ========== 创建/更新转换 ==========

    /**
     * 创建请求转换为 DO
     * 
     * @param bean 创建请求 VO
     * @return 告警规则 DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    AlarmRuleDO convert(AlarmRuleCreateReqVO bean);

    /**
     * 更新请求转换为 DO
     * 
     * @param bean 更新请求 VO
     * @return 告警规则 DO
     */
    @Mapping(target = "ruleCode", ignore = true) // 规则编码不允许更新
    @Mapping(target = "tenantId", ignore = true)
    AlarmRuleDO convert(AlarmRuleUpdateReqVO bean);

    /**
     * 更新请求更新到已有 DO
     * 
     * @param bean 更新请求 VO
     * @param target 目标 DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleCode", ignore = true) // 规则编码不允许更新
    @Mapping(target = "tenantId", ignore = true)
    void update(AlarmRuleUpdateReqVO bean, @MappingTarget AlarmRuleDO target);

    // ========== 查询转换 ==========

    /**
     * DO 转换为响应 VO
     * 
     * @param bean 告警规则 DO
     * @return 告警规则响应 VO
     */
    AlarmRuleRespVO convert(AlarmRuleDO bean);

    /**
     * DO 列表转换为响应 VO 列表
     * 
     * @param list 告警规则 DO 列表
     * @return 告警规则响应 VO 列表
     */
    List<AlarmRuleRespVO> convertList(List<AlarmRuleDO> list);

    /**
     * DO 分页结果转换为响应 VO 分页结果
     * 
     * @param page 告警规则 DO 分页结果
     * @return 告警规则响应 VO 分页结果
     */
    PageResult<AlarmRuleRespVO> convertPage(PageResult<AlarmRuleDO> page);

}
