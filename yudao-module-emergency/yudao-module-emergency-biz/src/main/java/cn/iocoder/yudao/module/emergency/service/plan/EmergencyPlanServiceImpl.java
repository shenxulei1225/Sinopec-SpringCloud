package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.biz.system.category.CategoryCommonApi;
import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.*;
import cn.iocoder.yudao.module.emergency.convert.plan.EmergencyPlanConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanAttachmentDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanAttachmentMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;

import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants.*;

/**
 * 应急预案 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class EmergencyPlanServiceImpl implements EmergencyPlanService {

    @Resource
    private EmergencyPlanMapper emergencyPlanMapper;
    @Resource
    private EmergencyPlanStepMapper emergencyPlanStepMapper;
    @Resource
    private EmergencyPlanAttachmentMapper emergencyPlanAttachmentMapper;

    @Resource
    private CategoryCommonApi categoryApi;

    @Resource
    private EmergencyPlanConvert emergencyPlanConvert;

    /**
     * 预案分组的业务类型编码
     */
    private static final String PLAN_GROUP_BUSINESS_TYPE = "emergency_plan_group";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmergencyPlan(EmergencyPlanCreateReqVO createReqVO) {
        // 1. 检查预案编号是否已存在（排除软删除的数据）
        LambdaQueryWrapper<EmergencyPlanDO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(EmergencyPlanDO::getPlanNo, createReqVO.getPlanNo());

        if (emergencyPlanMapper.selectCount(queryWrapper) > 0) {
            throw exception(PLAN_EXISTS);
        }

        // 2. 验证分组存在（如果指定了分组）
        if (createReqVO.getPlanGroupId() != null && createReqVO.getPlanGroupId() != 0L) {
            // 验证分组是否存在（可选，如果需要严格校验可以添加）
            // 使用 CategoryCommonApi 验证分组是否存在
            CommonResult<Boolean> existsResult = categoryApi.existsCategory(
                createReqVO.getPlanGroupId(), 
                PLAN_GROUP_BUSINESS_TYPE
            );
            if (!existsResult.isSuccess() || !Boolean.TRUE.equals(existsResult.getData())) {
                log.warn("预案分组不存在：planGroupId={}", createReqVO.getPlanGroupId());
            }
        }

        // 3. 插入主表
        EmergencyPlanDO plan = emergencyPlanConvert.convert(createReqVO);
        // planLevels、planGroupId 和 steps 都是可选字段，允许为空
        // planLevels 为空或空数组时，表示该预案不限制级别（手动选择场景）
        emergencyPlanMapper.insert(plan);

        // 3. 插入步骤（可选字段，如果为空则跳过）
        // 注意：步骤的创建应该通过 PlanStepService 的 createStep 方法，这里仅处理批量创建场景
        // 如果前端传入步骤列表，按顺序插入（假设已按层级排序）
        if (createReqVO.getSteps() != null && !createReqVO.getSteps().isEmpty()) {
            createReqVO.getSteps().forEach(stepVO -> {
                EmergencyPlanStepDO step = emergencyPlanConvert.convert(stepVO);
                step.setPlanId(plan.getId());
                // 如果步骤没有指定 planLevel，使用预案的第一个 planLevel（如果有）
                if (step.getPlanLevel() == null && plan.getPlanLevels() != null && !plan.getPlanLevels().isEmpty()) {
                    step.setPlanLevel(plan.getPlanLevels().get(0));
                }
                emergencyPlanStepMapper.insert(step);
            });
        }

        // 4. 插入附件（可选字段）
        if (createReqVO.getAttachments() != null && !createReqVO.getAttachments().isEmpty()) {
            createReqVO.getAttachments().forEach(attachmentVO -> {
                EmergencyPlanAttachmentDO attachment = emergencyPlanConvert.convert(attachmentVO);
                attachment.setPlanId(plan.getId());
                emergencyPlanAttachmentMapper.insert(attachment);
            });
        }

        return plan.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmergencyPlan(EmergencyPlanUpdateReqVO updateReqVO) {
        // 1. 校验存在
        validateEmergencyPlanExists(updateReqVO.getId());

        // 2. 验证分组存在（如果指定了分组）
        if (updateReqVO.getPlanGroupId() != null && updateReqVO.getPlanGroupId() != 0L) {
            // 验证分组是否存在（可选，如果需要严格校验可以添加）
            // 使用 CategoryCommonApi 验证分组是否存在
            CommonResult<Boolean> existsResult = categoryApi.existsCategory(
                updateReqVO.getPlanGroupId(), 
                PLAN_GROUP_BUSINESS_TYPE
            );
            if (!existsResult.isSuccess() || !Boolean.TRUE.equals(existsResult.getData())) {
                log.warn("预案分组不存在：planGroupId={}", updateReqVO.getPlanGroupId());
            }
        }

        // 3. 更新主表
        EmergencyPlanDO updateObj = emergencyPlanConvert.convert(updateReqVO);
        // planLevels、planGroupId 都是可选字段，允许为空或更新
        emergencyPlanMapper.updateById(updateObj);

        // 4. 更新步骤 (全量替换策略：简单粗暴，先删后增)
        // steps 是可选字段，如果为 null 则不更新步骤，如果为空数组则清空所有步骤
        if (updateReqVO.getSteps() != null) {
            emergencyPlanStepMapper.deleteByPlanId(updateReqVO.getId()); // 需要在 Mapper 增加此方法 或用 delete(new LambdaQueryWrapper...
            // 递归插入步骤及其子步骤
            // 类型安全检查，避免未检查的类型转换警告
            List<?> steps = updateReqVO.getSteps();
            if (steps instanceof List) {
                @SuppressWarnings("unchecked")
                List<EmergencyPlanStepBaseVO> typedSteps = (List<EmergencyPlanStepBaseVO>) steps;
                insertStepsRecursively(typedSteps, updateReqVO.getId(), null);
            } else {
                throw new IllegalArgumentException("步骤列表类型不匹配");
            }
        }

        // 5. 更新附件 (全量替换)
        // attachments 是可选字段，如果为 null 则不更新附件，如果为空数组则清空所有附件
        if (updateReqVO.getAttachments() != null) {
            emergencyPlanAttachmentMapper.deleteByPlanId(updateReqVO.getId());
            updateReqVO.getAttachments().forEach(attachmentVO -> {
                EmergencyPlanAttachmentDO attachment = emergencyPlanConvert.convert(attachmentVO);
                attachment.setPlanId(updateReqVO.getId());
                attachment.setId(null);
                emergencyPlanAttachmentMapper.insert(attachment);
            });
        }
    }

    @Override
    public void deleteEmergencyPlan(Long id) {
        // 1. 校验存在
        validateEmergencyPlanExists(id);
        // 2. 删除
        emergencyPlanMapper.deleteById(id);
        // 逻辑删除会自动处理，且级联删除（如果数据库配置了 FK CASCADE，否则需要手动删子表）
        // 这里手动删子表以防万一
        emergencyPlanStepMapper.deleteByPlanId(id);
        emergencyPlanAttachmentMapper.deleteByPlanId(id);
    }

    /**
     * 递归插入步骤及其子步骤
     */
    private void insertStepsRecursively(List<EmergencyPlanStepBaseVO> stepVOs, Long planId, Long parentId) {
        if (stepVOs == null || stepVOs.isEmpty()) {
            return;
        }

        for (EmergencyPlanStepBaseVO stepVO : stepVOs) {
            EmergencyPlanStepDO step = emergencyPlanConvert.convert(stepVO);
            step.setPlanId(planId);
            step.setParentId(parentId);
            step.setId(null); // 重新生成 ID
            emergencyPlanStepMapper.insert(step);

            // 递归处理子步骤 - 使用反射或instanceof来处理不同类型的VO
            List<EmergencyPlanStepBaseVO> children = getChildrenFromVO(stepVO);
            if (children != null && !children.isEmpty()) {
                insertStepsRecursively(children, planId, step.getId());
            }
        }
    }

    /**
     * 从VO对象中获取子步骤列表 - 处理不同类型的VO
     */
    @SuppressWarnings("unchecked")
    private List<EmergencyPlanStepBaseVO> getChildrenFromVO(EmergencyPlanStepBaseVO stepVO) {
        try {
            // 使用反射获取children字段
            java.lang.reflect.Field field = stepVO.getClass().getDeclaredField("children");
            field.setAccessible(true);
            return (List<EmergencyPlanStepBaseVO>) field.get(stepVO);
        } catch (Exception e) {
            // 如果没有children字段或获取失败，返回null
            return null;
        }
    }

    private void validateEmergencyPlanExists(Long id) {
        if (emergencyPlanMapper.selectById(id) == null) {
            throw exception(PLAN_NOT_EXISTS);
        }
    }

    @Override
    public EmergencyPlanRespVO getEmergencyPlan(Long id) {
        // 1. 查主表
        EmergencyPlanDO plan = emergencyPlanMapper.selectById(id);
        if (plan == null) {
            return null;
        }
        // 2. 查步骤
        List<EmergencyPlanStepDO> steps = emergencyPlanStepMapper.selectListByPlanId(id);
        // 3. 查附件
        List<EmergencyPlanAttachmentDO> attachments = emergencyPlanAttachmentMapper.selectListByPlanId(id);

        // 4. 组装
        return emergencyPlanConvert.convertFull(plan, steps, attachments);
    }

    @Override
    public PageResult<EmergencyPlanRespVO> getEmergencyPlanPage(cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.EmergencyPlanPageReqVO pageReqVO) {
        try {
            log.debug("[getEmergencyPlanPage][开始查询] pageNo={}, pageSize={}, planGroupId={}", 
                    pageReqVO.getPageNo(), pageReqVO.getPageSize(), pageReqVO.getPlanGroupId());
            
            PageResult<EmergencyPlanDO> pageResult = emergencyPlanMapper.selectPage(pageReqVO);
            
            log.debug("[getEmergencyPlanPage][查询完成] 总数={}, 当前页数据量={}",
                    pageResult.getTotal(), pageResult.getList() != null ? pageResult.getList().size() : 0);

            PageResult<EmergencyPlanRespVO> respPageResult = emergencyPlanConvert.convertPage(pageResult);

            // 为每个预案设置预案类型名称（从预案分组表查询）
            if (respPageResult.getList() != null && !respPageResult.getList().isEmpty()) {
                try {
                    log.info("[getEmergencyPlanPage][开始分组查询] 共有 {} 条记录需要查询分组", respPageResult.getList().size());

                    // 创建 planType -> planTypeName 的映射
                    Map<Integer, String> planTypeMap = new java.util.HashMap<>();

                    int mappedCount = 0;
                    for (EmergencyPlanRespVO plan : respPageResult.getList()) {
                        log.debug("[getEmergencyPlanPage][处理预案] id={}, planType={}", plan.getId(), plan.getPlanType());

                        if (plan.getPlanType() != null) {
                            // 如果还没有查询过这个分组，查询并缓存
                            if (!planTypeMap.containsKey(plan.getPlanType())) {
                                try {
                                    log.debug("[getEmergencyPlanPage][查询分组] planType={}", plan.getPlanType());
                                    // 使用 CategoryCommonApi 查询分组信息
                                    CommonResult<Map<String, Object>> categoryResult = categoryApi.getCategory(
                                        plan.getPlanType().longValue(), 
                                        PLAN_GROUP_BUSINESS_TYPE
                                    );
                                    String planTypeName = null;
                                    if (categoryResult.isSuccess() && categoryResult.getData() != null) {
                                        planTypeName = (String) categoryResult.getData().get("name");
                                    }
                                    planTypeMap.put(plan.getPlanType(), planTypeName);

                                    log.debug("[getEmergencyPlanPage][分组查询结果] planType={} -> planTypeName={}",
                                            plan.getPlanType(), planTypeName);
                                } catch (Exception e) {
                                    log.error("[getEmergencyPlanPage][分组查询异常] planType={} 错误: {}", plan.getPlanType(), e.getMessage(), e);
                                    planTypeMap.put(plan.getPlanType(), null);
                                }
                            }

                            // 设置预案类型名称
                            String planTypeName = planTypeMap.get(plan.getPlanType());
                            plan.setPlanTypeName(planTypeName);

                            log.debug("[getEmergencyPlanPage][设置分组名称] plan.id={}, planType={}, planTypeName={}",
                                    plan.getId(), plan.getPlanType(), planTypeName);

                            if (planTypeName != null) {
                                mappedCount++;
                            } else {
                                log.warn("[getEmergencyPlanPage][映射失败] planType={} 没有找到对应的分组", plan.getPlanType());
                            }
                        } else {
                            log.warn("[getEmergencyPlanPage][预案类型为空] plan.id={}", plan.getId());
                        }
                    }

                    log.info("[getEmergencyPlanPage][映射结果] 成功映射 {}/{} 条记录，查询了 {} 个唯一分组",
                            mappedCount, respPageResult.getList().size(), planTypeMap.size());
                } catch (Exception e) {
                    log.error("[getEmergencyPlanPage][分组查询失败] 错误信息: {}", e.getMessage(), e);
                    // 不抛出异常，继续返回结果，只是没有分组名称
                }
            } else {
                log.info("[getEmergencyPlanPage][无需分组查询] 查询结果为空");
            }

            return respPageResult;
        } catch (Exception e) {
            log.error("[getEmergencyPlanPage][查询异常] 请求参数: pageNo={}, pageSize={}, planGroupId={}, 异常类型: {}, 异常信息: {}", 
                    pageReqVO != null ? pageReqVO.getPageNo() : "null",
                    pageReqVO != null ? pageReqVO.getPageSize() : "null",
                    pageReqVO != null ? pageReqVO.getPlanGroupId() : "null",
                    e.getClass().getName(),
                    e.getMessage(), 
                    e);
            throw e; // 重新抛出异常
        }
    }

    @Override
    public List<EmergencyPlanRespVO> recommendPlans(String responseLevel, Integer planType) {
        // 参数验证
        if (responseLevel == null || responseLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("响应级别不能为空");
        }
        // 验证响应级别格式（I/II/III/IV/V）
        if (!responseLevel.matches("^[IVX]+$")) {
            throw new IllegalArgumentException("响应级别格式不正确，应为 I/II/III/IV/V");
        }
        
        // 使用PostgreSQL的JSONB查询功能：plan_levels @> '["I"]'::jsonb
        // 排除plan_levels为NULL或空数组的预案（自动推荐场景）
        // 允许手动选择没有plan_levels的预案（通过其他查询方法）
        // 构建JSON数组字符串：["I"] -> '["I"]'
        // 使用参数化查询防止SQL注入
        String responseLevelJson = "[\"" + responseLevel.trim() + "\"]";
        
        List<EmergencyPlanDO> plans = emergencyPlanMapper.selectRecommendedPlans(responseLevelJson, planType);
        
        return emergencyPlanConvert.convertList(plans);
    }
}
