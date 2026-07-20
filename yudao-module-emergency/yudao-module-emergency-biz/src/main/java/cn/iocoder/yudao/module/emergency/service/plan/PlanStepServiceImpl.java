package cn.iocoder.yudao.module.emergency.service.plan;

import cn.cheers.x.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCopyReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepRespVO;
import cn.iocoder.yudao.module.emergency.controller.admin.plan.vo.PlanStepUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.plan.EmergencyPlanStepDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.command.EmergencyCommandTemplateMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.plan.EmergencyPlanStepMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 预案步骤 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class PlanStepServiceImpl implements PlanStepService {

    @Resource
    private EmergencyPlanStepMapper planStepMapper;

    @Resource
    private EmergencyPlanMapper planMapper;

    @Resource
    private EmergencyCommandTemplateMapper commandTemplateMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createStep(PlanStepCreateReqVO createReqVO) {
        // 验证预案存在
        EmergencyPlanDO plan = planMapper.selectById(createReqVO.getPlanId());
        if (plan == null) {
            throw new RuntimeException("预案不存在");
        }

        // 调试：记录请求参数
        log.info("[createStep][请求参数] planId={}, parentId={}, stepTitle={}",
            createReqVO.getPlanId(), createReqVO.getParentId(), createReqVO.getStepTitle());

        // 验证父步骤存在（如果指定了父步骤）
        if (createReqVO.getParentId() != null && createReqVO.getParentId() != 0L) {
            EmergencyPlanStepDO parentStep = planStepMapper.selectById(createReqVO.getParentId());
            if (parentStep == null) {
                throw new RuntimeException("父步骤不存在，步骤ID: " + createReqVO.getParentId());
            }
            if (!Objects.equals(parentStep.getPlanId(), createReqVO.getPlanId())) {
                throw new RuntimeException("父步骤不属于该预案，父步骤预案ID: " + parentStep.getPlanId() +
                    ", 当前预案ID: " + createReqVO.getPlanId());
            }
            if (parentStep.getDeleted()) {
                throw new RuntimeException("父步骤已被删除，步骤ID: " + createReqVO.getParentId());
            }
        }

        // 创建新的DO对象用于调试
        EmergencyPlanStepDO newStep = new EmergencyPlanStepDO();
        log.info("[createStep][新建对象] deleted={}", newStep.getDeleted());

        // 构建步骤对象
        EmergencyPlanStepDO step = BeanUtils.toBean(createReqVO, EmergencyPlanStepDO.class);
        
        // 调试：记录转换后的deleted字段状态
        log.info("[createStep][转换后] stepId={}, deleted={}, planId={}, parentId={}",
            step.getId(), step.getDeleted(), step.getPlanId(), step.getParentId());

        // 处理 parentId：如果为 null 或 0，设为 null
        if (step.getParentId() != null && step.getParentId() == 0L) {
            step.setParentId(null);
        }
        
        // 设置默认值
        if (step.getStepOrder() == null) {
            // 计算下一个序号
            Integer maxOrder = getMaxStepOrder(createReqVO.getPlanId(), 
                createReqVO.getPlanLevel(), 
                createReqVO.getParentId());
            step.setStepOrder(maxOrder + 1);
        }
        
        // 如果使用兼容字段，同步到标准字段
        if (step.getName() != null && step.getStepTitle() == null) {
            step.setStepTitle(step.getName());
        }
        if (step.getPlannedStartTime() != null && step.getScheduledStartTime() == null) {
            // 这里需要根据实际情况计算分钟数，暂时设为0
            step.setScheduledStartTime(0);
        }

        // 验证commandIdList（如果设置）- 验证的是指令模板ID
        if (step.getCommandIdList() != null && !step.getCommandIdList().isEmpty()) {
            validateCommandTemplateIdList(step.getCommandIdList());
        }

        // 验证timeLimit（如果设置）
        if (step.getTimeLimit() != null) {
            validateTimeLimit(step.getTimeLimit());
        }

        // 调试：记录插入前的状态
        log.info("[createStep][插入前] stepId={}, deleted={}, planId={}, parentId={}",
            step.getId(), step.getDeleted(), step.getPlanId(), step.getParentId());

        // 插入步骤
        planStepMapper.insert(step);

        // 调试：记录插入后的状态
        log.info("[createStep][插入后] stepId={}, deleted={}, planId={}, parentId={}",
            step.getId(), step.getDeleted(), step.getPlanId(), step.getParentId());

        log.info("[createStep][创建步骤成功] stepId={}, planId={}, planLevel={}", 
            step.getId(), step.getPlanId(), step.getPlanLevel());
        
        return step.getId();
    }

    @Override
    public List<PlanStepRespVO> getStepTree(Long planId, String planLevel) {
        // 查询所有步骤
        List<EmergencyPlanStepDO> allSteps = planStepMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getPlanId, planId)
                .eq(planLevel != null, EmergencyPlanStepDO::getPlanLevel, planLevel)
                .orderByAsc(EmergencyPlanStepDO::getStepOrder)
        );

        // 转换为VO
        List<PlanStepRespVO> stepVOs = BeanUtils.toBean(allSteps, PlanStepRespVO.class);

        // 构建树形结构
        return buildTree(stepVOs, null);
    }

    @Override
    public EmergencyPlanStepDO getStep(Long id) {
        EmergencyPlanStepDO step = planStepMapper.selectById(id);
        if (step == null) {
            throw new RuntimeException("步骤不存在");
        }
        return step;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStep(PlanStepUpdateReqVO updateReqVO) {
        // 查询当前步骤（包含版本号）
        EmergencyPlanStepDO existingStep = validateStepExists(updateReqVO.getId());
        
        // 保存原始版本号（用于日志记录）
        Integer originalVersion = existingStep.getVersion();

        // 如果更新了父节点，需要验证
        if (!Objects.equals(updateReqVO.getParentId(), existingStep.getParentId())) {
            if (updateReqVO.getParentId() != null && updateReqVO.getParentId() != 0L) {
                EmergencyPlanStepDO parentStep = planStepMapper.selectById(updateReqVO.getParentId());
                if (parentStep == null) {
                    throw new RuntimeException("父步骤不存在，步骤ID: " + updateReqVO.getParentId());
                }
                if (!Objects.equals(parentStep.getPlanId(), existingStep.getPlanId())) {
                    throw new RuntimeException("父步骤不属于该预案，父步骤预案ID: " + parentStep.getPlanId() +
                        ", 当前预案ID: " + existingStep.getPlanId());
                }
                if (parentStep.getDeleted()) {
                    throw new RuntimeException("父步骤已被删除，步骤ID: " + updateReqVO.getParentId());
                }
                // 防止循环引用
                if (isAncestor(updateReqVO.getId(), updateReqVO.getParentId())) {
                    throw new RuntimeException("不能将步骤移动到其子节点下");
                }
            }
        }

        // 更新步骤（不更新version字段，由MyBatis-Plus乐观锁插件自动处理）
        EmergencyPlanStepDO step = BeanUtils.toBean(updateReqVO, EmergencyPlanStepDO.class);
        // 保留原始版本号，确保乐观锁正常工作
        step.setVersion(existingStep.getVersion());
        
        // 验证commandIdList（如果设置）- 验证的是指令模板ID
        if (step.getCommandIdList() != null && !step.getCommandIdList().isEmpty()) {
            validateCommandTemplateIdList(step.getCommandIdList());
        }

        // 验证timeLimit（如果设置）
        if (step.getTimeLimit() != null) {
            validateTimeLimit(step.getTimeLimit());
        }
        
        // 如果父节点变化，需要更新所有子节点的路径
        if (!Objects.equals(updateReqVO.getParentId(), existingStep.getParentId())) {
            // 这里可以扩展实现树路径更新逻辑（如果需要）
        }

        // 使用MyBatis-Plus的updateById方法，乐观锁插件会自动检查version字段
        // 如果version不匹配，updateCount会为0，表示记录已被其他用户修改
        int updateCount = planStepMapper.updateById(step);
        
        if (updateCount == 0) {
            // 更新失败，说明版本号不匹配（记录已被其他用户修改）
            log.warn("[updateStep][乐观锁冲突] stepId={}, originalVersion={}", 
                updateReqVO.getId(), originalVersion);
            throw new RuntimeException("数据已被其他用户修改，请刷新后重试");
        }
        
        log.info("[updateStep][更新步骤成功] stepId={}, version={}", step.getId(), step.getVersion());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void moveStep(Long id, Long targetParentId) {
        EmergencyPlanStepDO step = validateStepExists(id);

        // 验证目标父节点
        if (targetParentId != null && targetParentId != 0L) {
            EmergencyPlanStepDO targetParent = planStepMapper.selectById(targetParentId);
            if (targetParent == null) {
                throw new RuntimeException("目标父步骤不存在，步骤ID: " + targetParentId);
            }
            if (!Objects.equals(targetParent.getPlanId(), step.getPlanId())) {
                throw new RuntimeException("目标父步骤不属于该预案，目标父步骤预案ID: " + targetParent.getPlanId() +
                    ", 当前预案ID: " + step.getPlanId());
            }
            if (targetParent.getDeleted()) {
                throw new RuntimeException("目标父步骤已被删除，步骤ID: " + targetParentId);
            }
            // 防止循环引用
            if (isAncestor(id, targetParentId)) {
                throw new RuntimeException("不能将步骤移动到其子节点下");
            }
        }

        // 更新父节点
        step.setParentId(targetParentId != null && targetParentId != 0L ? targetParentId : null);
        planStepMapper.updateById(step);
        log.info("[moveStep][移动步骤成功] stepId={}, targetParentId={}", id, targetParentId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStep(Long id, boolean cascade) {
        EmergencyPlanStepDO step = validateStepExists(id);

        // 获取所有子步骤
        List<EmergencyPlanStepDO> children = getChildrenByParentId(id);

        if (cascade) {
            // 级联删除：递归删除所有子步骤
            deleteStepRecursive(id);
        } else {
            // 提升子步骤：将子步骤的parent_id设为原父节点的parent_id
            Long newParentId = step.getParentId();
            // 处理 parentId：如果为 null、0 或等于当前步骤ID（异常情况），都设为 null
            if (newParentId == null || newParentId == 0L || newParentId.equals(id)) {
                newParentId = null;
            }
            // 使用批量更新提高效率
            if (!children.isEmpty()) {
                planStepMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<EmergencyPlanStepDO>()
                        .in(EmergencyPlanStepDO::getId, children.stream().map(EmergencyPlanStepDO::getId).collect(java.util.stream.Collectors.toList()))
                        .set(EmergencyPlanStepDO::getParentId, newParentId)
                );
            }
            // 删除当前步骤
            planStepMapper.deleteById(id);
        }

        log.info("[deleteStep][删除步骤成功] stepId={}, cascade={}", id, cascade);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyStep(PlanStepCopyReqVO copyReqVO) {
        EmergencyPlanStepDO sourceStep = validateStepExists(copyReqVO.getSourceStepId());

        // 验证目标预案
        EmergencyPlanDO targetPlan = planMapper.selectById(copyReqVO.getTargetPlanId());
        if (targetPlan == null) {
            throw new RuntimeException("目标预案不存在");
        }

        // 验证目标父步骤（如果指定）
        Long targetParentId = copyReqVO.getTargetParentId();
        if (targetParentId != null && targetParentId != 0L) {
            EmergencyPlanStepDO targetParent = planStepMapper.selectById(targetParentId);
            if (targetParent == null) {
                throw new RuntimeException("目标父步骤不存在，步骤ID: " + targetParentId);
            }
            if (!Objects.equals(targetParent.getPlanId(), copyReqVO.getTargetPlanId())) {
                throw new RuntimeException("目标父步骤不属于目标预案，目标父步骤预案ID: " + targetParent.getPlanId() +
                    ", 目标预案ID: " + copyReqVO.getTargetPlanId());
            }
            if (targetParent.getDeleted()) {
                throw new RuntimeException("目标父步骤已被删除，步骤ID: " + targetParentId);
            }
        }

        // 创建ID映射（旧ID -> 新ID）
        Map<Long, Long> idMapping = new HashMap<>();

        // 计算时间偏移（如果需要调整）
        Integer timeOffset = copyReqVO.getTimeOffsetMinutes() != null ? copyReqVO.getTimeOffsetMinutes() : 0;

        // 递归复制步骤及其所有子步骤
        Long rootNewId = copyStepRecursive(sourceStep, targetParentId, 
            copyReqVO.getTargetPlanId(), copyReqVO.getTargetPlanLevel(), 
            idMapping, timeOffset);

        log.info("[copyStep][复制步骤成功] sourceStepId={}, targetPlanId={}, newRootId={}", 
            copyReqVO.getSourceStepId(), copyReqVO.getTargetPlanId(), rootNewId);

        return rootNewId;
    }

    // ==================== 私有方法 ====================

    /**
     * 验证步骤存在
     */
    private EmergencyPlanStepDO validateStepExists(Long id) {
        EmergencyPlanStepDO step = planStepMapper.selectById(id);
        if (step == null) {
            throw new RuntimeException("步骤不存在");
        }
        return step;
    }

    /**
     * 获取最大序号
     */
    private Integer getMaxStepOrder(Long planId, String planLevel, Long parentId) {
        List<EmergencyPlanStepDO> steps = planStepMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getPlanId, planId)
                .eq(planLevel != null, EmergencyPlanStepDO::getPlanLevel, planLevel)
                .eq(parentId != null && parentId != 0L, EmergencyPlanStepDO::getParentId, parentId)
                .orderByDesc(EmergencyPlanStepDO::getStepOrder)
                .last("LIMIT 1")
        );
        return steps.isEmpty() ? 0 : (steps.get(0).getStepOrder() != null ? steps.get(0).getStepOrder() : 0);
    }

    /**
     * 根据父节点ID查询子步骤
     */
    private List<EmergencyPlanStepDO> getChildrenByParentId(Long parentId) {
        return planStepMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getParentId, parentId)
                .orderByAsc(EmergencyPlanStepDO::getStepOrder)
        );
    }

    /**
     * 获取所有后代步骤（包括子步骤、孙步骤等）
     */
    private List<EmergencyPlanStepDO> getAllDescendants(Long stepId) {
        List<EmergencyPlanStepDO> allDescendants = new ArrayList<>();
        List<EmergencyPlanStepDO> children = getChildrenByParentId(stepId);
        for (EmergencyPlanStepDO child : children) {
            allDescendants.add(child);
            allDescendants.addAll(getAllDescendants(child.getId()));
        }
        return allDescendants;
    }

    /**
     * 递归删除步骤及其所有子步骤
     */
    private void deleteStepRecursive(Long stepId) {
        List<EmergencyPlanStepDO> children = getChildrenByParentId(stepId);
        for (EmergencyPlanStepDO child : children) {
            deleteStepRecursive(child.getId());
        }
        planStepMapper.deleteById(stepId);
    }

    /**
     * 递归复制步骤及其所有子步骤
     */
    private Long copyStepRecursive(EmergencyPlanStepDO sourceStep, Long targetParentId,
                                   Long targetPlanId, String targetPlanLevel,
                                   Map<Long, Long> idMapping, Integer timeOffset) {
        // 创建新步骤（使用BeanUtils复制属性）
        EmergencyPlanStepDO newStep = BeanUtils.toBean(sourceStep, EmergencyPlanStepDO.class);
        newStep.setId(null); // 清除ID，让数据库自动生成
        newStep.setPlanId(targetPlanId);
        newStep.setPlanLevel(targetPlanLevel != null ? targetPlanLevel : sourceStep.getPlanLevel());
        newStep.setParentId(targetParentId != null && targetParentId != 0L ? targetParentId : null);
        
        // 调整计划启动时间
        if (newStep.getScheduledStartTime() != null) {
            newStep.setScheduledStartTime(newStep.getScheduledStartTime() + timeOffset);
        }

        // 插入新步骤
        planStepMapper.insert(newStep);
        Long newStepId = newStep.getId();

        // 记录ID映射
        idMapping.put(sourceStep.getId(), newStepId);

        // 获取并复制子步骤
        List<EmergencyPlanStepDO> children = getChildrenByParentId(sourceStep.getId());
        for (EmergencyPlanStepDO child : children) {
            copyStepRecursive(child, newStepId, targetPlanId, targetPlanLevel, 
                idMapping, timeOffset);
        }

        return newStepId;
    }

    /**
     * 构建树形结构
     */
    private List<PlanStepRespVO> buildTree(List<PlanStepRespVO> allSteps, Long parentId) {
        return allSteps.stream()
            .filter(step -> {
                Long stepParentId = step.getParentId();
                return (parentId == null || parentId == 0L) 
                    ? (stepParentId == null || stepParentId == 0L)
                    : parentId.equals(stepParentId);
            })
            .map(step -> {
                List<PlanStepRespVO> children = buildTree(allSteps, step.getId());
                step.setChildren(children);
                step.setLeaf(children.isEmpty());
                return step;
            })
            .collect(Collectors.toList());
    }

    /**
     * 检查targetId是否是sourceId的祖先节点
     */
    private boolean isAncestor(Long sourceId, Long targetId) {
        if (sourceId.equals(targetId)) {
            return false; // 自己不是自己的祖先
        }
        EmergencyPlanStepDO step = planStepMapper.selectById(sourceId);
        if (step == null || step.getParentId() == null || step.getParentId() == 0L) {
            return false;
        }
        if (Objects.equals(step.getParentId(), targetId)) {
            return true;
        }
        return isAncestor(step.getParentId(), targetId);
    }

    /**
     * 检测并修复数据完整性问题
     * 查找所有父步骤不存在的步骤，并将其parent_id设为null
     *
     * @return 修复的步骤数量
     */
    public int fixOrphanedSteps() {
        List<EmergencyPlanStepDO> allSteps = planStepMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .isNotNull(EmergencyPlanStepDO::getParentId)
                .ne(EmergencyPlanStepDO::getParentId, 0L)
                .eq(EmergencyPlanStepDO::getDeleted, false)
        );

        int fixedCount = 0;
        for (EmergencyPlanStepDO step : allSteps) {
            EmergencyPlanStepDO parentStep = planStepMapper.selectById(step.getParentId());
            if (parentStep == null || parentStep.getDeleted()) {
                // 父步骤不存在或已被删除，将parent_id设为null
                step.setParentId(null);
                planStepMapper.updateById(step);
                fixedCount++;
                log.warn("[fixOrphanedSteps] 修复孤立步骤: stepId={}, 原parentId={}",
                    step.getId(), step.getParentId());
            }
        }

        if (fixedCount > 0) {
            log.info("[fixOrphanedSteps] 共修复{}个孤立步骤", fixedCount);
        }
        return fixedCount;
    }

    /**
     * 清理测试数据 - 删除所有已删除的步骤记录
     * 注意：此方法仅用于清理测试过程中的垃圾数据，生产环境请谨慎使用
     *
     * @return 删除的步骤数量
     */
    public int cleanupDeletedSteps() {
        List<EmergencyPlanStepDO> deletedSteps = planStepMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getDeleted, true)
        );

        int deletedCount = 0;
        for (EmergencyPlanStepDO step : deletedSteps) {
            // 物理删除已软删除的记录
            planStepMapper.deleteById(step.getId());
            deletedCount++;
        }

        if (deletedCount > 0) {
            log.info("[cleanupDeletedSteps] 共清理{}个已删除的步骤记录", deletedCount);
        }
        return deletedCount;
    }

    /**
     * 获取指定预案的活跃步骤ID列表
     * 用于前端刷新步骤选择器，避免引用已删除的步骤
     *
     * @param planId 预案ID
     * @return 活跃步骤ID列表
     */
    public List<Long> getActiveStepIds(Long planId) {
        List<EmergencyPlanStepDO> steps = planStepMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyPlanStepDO>()
                .eq(EmergencyPlanStepDO::getPlanId, planId)
                .eq(EmergencyPlanStepDO::getDeleted, false)
                .select(EmergencyPlanStepDO::getId)
        );
        return steps.stream().map(EmergencyPlanStepDO::getId).collect(java.util.stream.Collectors.toList());
    }

    /**
     * 验证指令模板ID列表的有效性
     * 预案步骤关联的是指令模板，而不是实际指令
     * 
     * @param commandTemplateIdList 指令模板ID列表
     */
    private void validateCommandTemplateIdList(List<Long> commandTemplateIdList) {
        if (commandTemplateIdList == null || commandTemplateIdList.isEmpty()) {
            return;
        }
        // 去重并过滤空值
        List<Long> uniqueIds = commandTemplateIdList.stream()
            .filter(Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
        
        if (uniqueIds.isEmpty()) {
            throw new RuntimeException("指令模板ID列表为空或全部为空值");
        }
        
        // 批量查询所有模板，提高性能
        // 注意：MyBatis-Plus 会自动添加租户过滤（TenantBaseDO）和逻辑删除过滤（@TableLogic）
        List<EmergencyCommandTemplateDO> templates = commandTemplateMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<EmergencyCommandTemplateDO>()
                .in(EmergencyCommandTemplateDO::getId, uniqueIds)
        );
        
        // 记录查询结果，帮助排查问题（使用info级别，便于排查）
        log.info("[validateCommandTemplateIdList] 查询模板ID列表: {}, 查询到模板数量: {}", 
            uniqueIds, templates.size());
        if (!templates.isEmpty()) {
            log.info("[validateCommandTemplateIdList] 查询到的模板ID: {}", 
                templates.stream().map(EmergencyCommandTemplateDO::getId).collect(Collectors.toList()));
        }
        
        // 构建已查询到的模板ID映射（ID -> 模板对象）
        Map<Long, EmergencyCommandTemplateDO> templateMap = templates.stream()
            .collect(java.util.stream.Collectors.toMap(
                EmergencyCommandTemplateDO::getId,
                template -> template,
                (existing, replacement) -> existing
            ));
        
        // 验证每个指令模板ID
        List<String> errors = new ArrayList<>();
        List<Long> notFoundIds = new ArrayList<>();
        for (Long templateId : uniqueIds) {
            EmergencyCommandTemplateDO template = templateMap.get(templateId);
            if (template == null) {
                notFoundIds.add(templateId);
                // 模板不存在的原因可能是：1) 模板不存在；2) 模板属于其他租户；3) 模板已被删除
                // 4) JavaScript精度丢失导致ID不匹配（大整数在JS中可能丢失精度）
                errors.add(String.format("模板ID: %d（不存在或属于其他租户）", templateId));
                continue;
            }
            // 检查模板是否启用
            if (template.getIsEnabled() != null && !template.getIsEnabled()) {
                errors.add(String.format("模板ID: %d（未启用）", templateId));
            }
        }
        
        // 如果有未找到的ID，记录警告日志（帮助发现可能是精度丢失问题）
        if (!notFoundIds.isEmpty() && !templates.isEmpty()) {
            log.warn("[validateCommandTemplateIdList] 部分模板ID未找到: {}, 但查询到了其他模板: {}", 
                notFoundIds, 
                templates.stream().map(EmergencyCommandTemplateDO::getId).collect(Collectors.toList()));
        }
        
        // 如果有错误，抛出异常并列出所有问题
        if (!errors.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("指令模板验证失败：\n");
            errorMessage.append(String.join("\n", errors));
            errorMessage.append("\n\n可能原因：");
            errorMessage.append("\n1) 模板不存在");
            errorMessage.append("\n2) 模板属于其他租户");
            errorMessage.append("\n3) 模板已被删除");
            errorMessage.append("\n4) JavaScript精度丢失：如果模板ID是很大的整数（超过2^53），");
            errorMessage.append("前端JavaScript可能丢失精度，导致ID不匹配");
            errorMessage.append("\n   建议：前端传递大整数ID时，应使用字符串类型，后端接收后再转换为Long");
            
            throw new RuntimeException(errorMessage.toString());
        }
    }

    /**
     * 验证执行时限的有效性
     * @param timeLimit 执行时限（分钟）
     * @throws RuntimeException 如果时限不在有效范围内
     */
    private void validateTimeLimit(Integer timeLimit) {
        if (timeLimit == null) {
            return;
        }
        if (timeLimit < 1 || timeLimit > 1440) {
            throw new RuntimeException("执行时限必须在1-1440分钟（24小时）范围内，当前值: " + timeLimit);
        }
    }
}
