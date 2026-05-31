package cn.iocoder.yudao.module.inspection.task.service.schedule.impl;

import cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementCreateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementRespVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.InspectionTaskScheduleRequirementUpdateReqVO;
import cn.iocoder.yudao.module.inspection.task.controller.admin.vo.schedulepolicy.ScheduleTemplateConfigVO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskSchedulePolicyDO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO;
import cn.iocoder.yudao.module.inspection.task.dal.dataobject.schedule.InspectionTaskScheduleRequirementDO.ScheduleTemplateConfig;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule.InspectionTaskSchedulePolicyMapper;
import cn.iocoder.yudao.module.inspection.task.dal.mysql.schedule.InspectionTaskScheduleRequirementMapper;
import cn.iocoder.yudao.module.inspection.task.service.schedule.InspectionTaskScheduleRequirementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.NOT_FOUND;

/**
 * 巡检任务排期需求 Service 实现类
 *
 * <p>排期需求是一个配置容器，包含多个模板组合。</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InspectionTaskScheduleRequirementServiceImpl implements InspectionTaskScheduleRequirementService {

    private final InspectionTaskScheduleRequirementMapper scheduleRequirementMapper;
    private final InspectionTaskSchedulePolicyMapper schedulePolicyMapper;

    // ==================== 基础操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createScheduleRequirement(InspectionTaskScheduleRequirementCreateReqVO createReqVO) {
        // 生成需求编码
        String requirementCode = generateRequirementCode();

        InspectionTaskScheduleRequirementDO requirementDO = new InspectionTaskScheduleRequirementDO();
        requirementDO.setRequirementCode(requirementCode);
        requirementDO.setRequirementName(createReqVO.getRequirementName());
        requirementDO.setTaskId(createReqVO.getTaskId());
        requirementDO.setSchedulePolicyId(createReqVO.getSchedulePolicyId());
        requirementDO.setDescription(createReqVO.getDescription());
        
        // 转换模板组合配置
        if (createReqVO.getScheduleTemplates() != null && !createReqVO.getScheduleTemplates().isEmpty()) {
            requirementDO.setScheduleTemplates(convertToDOConfigs(createReqVO.getScheduleTemplates()));
        } else {
            requirementDO.setScheduleTemplates(new ArrayList<>());
        }
        
        scheduleRequirementMapper.insert(requirementDO);
        log.info("创建排期需求成功, id={}, code={}", requirementDO.getId(), requirementCode);
        return requirementDO.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScheduleRequirement(InspectionTaskScheduleRequirementUpdateReqVO updateReqVO) {
        // 校验排期需求存在
        InspectionTaskScheduleRequirementDO existingDO = validateRequirementExists(updateReqVO.getId());
        
        // 更新字段
        existingDO.setRequirementName(updateReqVO.getRequirementName());
        existingDO.setSchedulePolicyId(updateReqVO.getSchedulePolicyId());
        existingDO.setDescription(updateReqVO.getDescription());
        
        // 更新模板组合配置
        if (updateReqVO.getScheduleTemplates() != null) {
            existingDO.setScheduleTemplates(convertToDOConfigs(updateReqVO.getScheduleTemplates()));
        }
        
        scheduleRequirementMapper.updateById(existingDO);
        log.info("更新排期需求成功, id={}", updateReqVO.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteScheduleRequirement(Long id) {
        // 校验排期需求存在
        validateRequirementExists(id);
        
        scheduleRequirementMapper.deleteById(id);
        log.info("删除排期需求成功, id={}", id);
    }

    @Override
    public InspectionTaskScheduleRequirementRespVO getScheduleRequirement(Long id) {
        InspectionTaskScheduleRequirementDO requirementDO = validateRequirementExists(id);
        return convertToRespVO(requirementDO);
    }

    @Override
    public InspectionTaskScheduleRequirementRespVO getScheduleRequirementByTaskId(Long taskId) {
        InspectionTaskScheduleRequirementDO requirementDO = scheduleRequirementMapper.selectByTaskId(taskId);
        if (requirementDO == null) {
            return null;
        }
        return convertToRespVO(requirementDO);
    }

    // ==================== 模板组合操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addTemplateConfig(Long requirementId, ScheduleTemplateConfig templateConfig) {
        InspectionTaskScheduleRequirementDO requirementDO = validateRequirementExists(requirementId);
        
        // 添加模板组合
        if (requirementDO.getScheduleTemplates() == null) {
            requirementDO.setScheduleTemplates(new ArrayList<>());
        }
        
        // 检查是否已存在相同模板ID
        boolean exists = requirementDO.getScheduleTemplates().stream()
                .anyMatch(c -> c.getTemplateId().equals(templateConfig.getTemplateId()));
        if (exists) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST, "模板ID {} 已存在", templateConfig.getTemplateId());
        }
        
        requirementDO.getScheduleTemplates().add(templateConfig);
        scheduleRequirementMapper.updateById(requirementDO);
        log.info("添加模板组合成功, requirementId={}, templateId={}", requirementId, templateConfig.getTemplateId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplateConfig(Long requirementId, Long templateId, ScheduleTemplateConfig templateConfig) {
        InspectionTaskScheduleRequirementDO requirementDO = validateRequirementExists(requirementId);
        
        // 查找并更新模板组合
        Optional<ScheduleTemplateConfig> existingConfig = requirementDO.getScheduleTemplates().stream()
                .filter(c -> c.getTemplateId().equals(templateId))
                .findFirst();
        
        if (existingConfig.isEmpty()) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "模板组合不存在, templateId={}", templateId);
        }
        
        // 更新模板组合（保留 templateId）
        ScheduleTemplateConfig existing = existingConfig.get();
        existing.setTemplateName(templateConfig.getTemplateName());
        existing.setOriginalConfig(templateConfig.getOriginalConfig());
        existing.setChangedFields(templateConfig.getChangedFields());
        existing.setEnabled(templateConfig.getEnabled());
        
        scheduleRequirementMapper.updateById(requirementDO);
        log.info("更新模板组合成功, requirementId={}, templateId={}", requirementId, templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeTemplateConfig(Long requirementId, Long templateId) {
        InspectionTaskScheduleRequirementDO requirementDO = validateRequirementExists(requirementId);
        
        boolean removed = requirementDO.getScheduleTemplates().removeIf(
                c -> c.getTemplateId().equals(templateId));
        
        if (!removed) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "模板组合不存在, templateId={}", templateId);
        }
        
        scheduleRequirementMapper.updateById(requirementDO);
        log.info("删除模板组合成功, requirementId={}, templateId={}", requirementId, templateId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreTemplateConfig(Long requirementId, Long templateId) {
        InspectionTaskScheduleRequirementDO requirementDO = validateRequirementExists(requirementId);
        
        // 查找模板组合
        Optional<ScheduleTemplateConfig> existingConfig = requirementDO.getScheduleTemplates().stream()
                .filter(c -> c.getTemplateId().equals(templateId))
                .findFirst();
        
        if (existingConfig.isEmpty()) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "模板组合不存在, templateId={}", templateId);
        }
        
        // 清空 changedFields，恢复到原始模板配置
        ScheduleTemplateConfig config = existingConfig.get();
        config.setChangedFields(null);
        
        scheduleRequirementMapper.updateById(requirementDO);
        log.info("恢复模板配置成功, requirementId={}, templateId={}", requirementId, templateId);
    }

    // ==================== 查询操作 ====================

    @Override
    public List<InspectionTaskScheduleRequirementRespVO> getScheduleRequirementsByTaskId(Long taskId) {
        List<InspectionTaskScheduleRequirementDO> requirements = scheduleRequirementMapper.selectByTaskIdList(
                List.of(taskId));
        return requirements.stream()
                .map(this::convertToRespVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleTemplateConfig> getEnabledTemplateConfigs() {
        // 获取所有排期需求，收集启用的模板组合
        List<InspectionTaskScheduleRequirementDO> allRequirements = scheduleRequirementMapper.selectList(null);
        
        return allRequirements.stream()
                .filter(r -> r.getScheduleTemplates() != null)
                .flatMap(r -> r.getScheduleTemplates().stream())
                .filter(c -> Boolean.TRUE.equals(c.getEnabled()))
                .collect(Collectors.toList());
    }

    // ==================== 私有方法 ====================

    /**
     * 生成需求编码
     * 格式：REQ-yyyyMMdd-xxxx
     */
    private String generateRequirementCode() {
        String datePart = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uniquePart = String.format("%04d", Math.abs((int) (System.currentTimeMillis() % 10000)));
        return "REQ-" + datePart + "-" + uniquePart;
    }

    /**
     * 校验排期需求是否存在
     */
    private InspectionTaskScheduleRequirementDO validateRequirementExists(Long id) {
        InspectionTaskScheduleRequirementDO requirementDO = scheduleRequirementMapper.selectById(id);
        if (requirementDO == null) {
            throw ServiceExceptionUtil.exception(NOT_FOUND, "排期需求不存在");
        }
        return requirementDO;
    }

    /**
     * 转换为响应 VO
     */
    private InspectionTaskScheduleRequirementRespVO convertToRespVO(InspectionTaskScheduleRequirementDO requirementDO) {
        InspectionTaskScheduleRequirementRespVO respVO = BeanUtils.toBean(requirementDO, InspectionTaskScheduleRequirementRespVO.class);
        respVO.setScheduleTemplates(convertToVOConfigs(requirementDO.getScheduleTemplates()));
        enrichSchedulePolicyName(respVO);
        return respVO;
    }

    private void enrichSchedulePolicyName(InspectionTaskScheduleRequirementRespVO respVO) {
        Long schedulePolicyId = respVO.getSchedulePolicyId();
        if (schedulePolicyId == null) {
            return;
        }
        InspectionTaskSchedulePolicyDO policy = schedulePolicyMapper.selectById(schedulePolicyId);
        if (policy != null) {
            respVO.setSchedulePolicyName(policy.getPolicyName());
        }
    }

    /**
     * 将 DO 的模板组合配置转换为 VO
     */
    private List<ScheduleTemplateConfigVO> convertToVOConfigs(List<ScheduleTemplateConfig> configList) {
        if (configList == null) {
            return new ArrayList<>();
        }
        return configList.stream()
                .map(config -> {
                    ScheduleTemplateConfigVO vo = new ScheduleTemplateConfigVO();
                    vo.setTemplateId(config.getTemplateId());
                    vo.setTemplateName(config.getTemplateName());
                    vo.setOriginalConfig(config.getOriginalConfig());
                    vo.setChangedFields(config.getChangedFields());
                    vo.setEnabled(config.getEnabled());
                    return vo;
                })
                .collect(Collectors.toList());
    }

    /**
     * 将 VO 的模板组合配置转换为 DO 的内部类
     */
    private List<ScheduleTemplateConfig> convertToDOConfigs(List<ScheduleTemplateConfigVO> voList) {
        if (voList == null) {
            return new ArrayList<>();
        }
        return voList.stream()
                .map(vo -> {
                    ScheduleTemplateConfig config = new ScheduleTemplateConfig();
                    config.setTemplateId(vo.getTemplateId());
                    config.setTemplateName(vo.getTemplateName());
                    config.setOriginalConfig(vo.getOriginalConfig());
                    config.setChangedFields(vo.getChangedFields());
                    config.setEnabled(vo.getEnabled());
                    return config;
                })
                .collect(Collectors.toList());
    }
}
