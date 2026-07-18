package cn.cheers.x.alarm.convert;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.alarm.controller.admin.vo.linkage.*;
import cn.cheers.x.alarm.dal.dataobject.LinkageExecutionDO;
import cn.cheers.x.alarm.dal.dataobject.LinkageRuleDO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 联动规则 Convert
 * 
 * <p>负责联动规则和联动执行记录相关的 DO 和 VO 之间的转换</p>
 *
 * 
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LinkageRuleConvert {

    LinkageRuleConvert INSTANCE = Mappers.getMapper(LinkageRuleConvert.class);

    // ========== 联动规则创建/更新转换 ==========

    /**
     * 创建请求转换为 DO
     * 
     * @param bean 创建请求 VO
     * @return 联动规则 DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    LinkageRuleDO convert(LinkageRuleCreateReqVO bean);

    /**
     * 更新请求转换为 DO
     * 
     * @param bean 更新请求 VO
     * @return 联动规则 DO
     */
    @Mapping(target = "ruleCode", ignore = true) // 规则编码不允许更新
    @Mapping(target = "tenantId", ignore = true)
    LinkageRuleDO convert(LinkageRuleUpdateReqVO bean);

    /**
     * 更新请求更新到已有 DO
     * 
     * @param bean 更新请求 VO
     * @param target 目标 DO
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ruleCode", ignore = true) // 规则编码不允许更新
    @Mapping(target = "tenantId", ignore = true)
    void update(LinkageRuleUpdateReqVO bean, @MappingTarget LinkageRuleDO target);

    // ========== 联动规则查询转换 ==========

    /**
     * DO 转换为响应 VO
     * 
     * @param bean 联动规则 DO
     * @return 联动规则响应 VO
     */
    LinkageRuleRespVO convert(LinkageRuleDO bean);

    /**
     * DO 列表转换为响应 VO 列表
     * 
     * @param list 联动规则 DO 列表
     * @return 联动规则响应 VO 列表
     */
    List<LinkageRuleRespVO> convertList(List<LinkageRuleDO> list);

    /**
     * DO 分页结果转换为响应 VO 分页结果
     * 
     * @param page 联动规则 DO 分页结果
     * @return 联动规则响应 VO 分页结果
     */
    PageResult<LinkageRuleRespVO> convertPage(PageResult<LinkageRuleDO> page);

    // ========== 联动执行记录转换 ==========

    /**
     * 联动执行 DO 转换为响应 VO
     * 
     * @param bean 联动执行 DO
     * @return 联动执行响应 VO
     */
    LinkageExecutionRespVO convertExecution(LinkageExecutionDO bean);

    /**
     * 联动执行 DO 列表转换为响应 VO 列表
     * 
     * @param list 联动执行 DO 列表
     * @return 联动执行响应 VO 列表
     */
    List<LinkageExecutionRespVO> convertExecutionList(List<LinkageExecutionDO> list);

    /**
     * 联动执行 DO 分页结果转换为响应 VO 分页结果
     * 
     * @param page 联动执行 DO 分页结果
     * @return 联动执行响应 VO 分页结果
     */
    PageResult<LinkageExecutionRespVO> convertExecutionPage(PageResult<LinkageExecutionDO> page);

}
