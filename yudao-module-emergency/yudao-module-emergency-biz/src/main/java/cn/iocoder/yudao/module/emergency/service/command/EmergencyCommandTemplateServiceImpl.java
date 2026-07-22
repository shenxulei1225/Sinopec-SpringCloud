package cn.iocoder.yudao.module.emergency.service.command;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.EmergencyCommandTemplateCreateReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.EmergencyCommandTemplatePageReqVO;
import cn.iocoder.yudao.module.emergency.controller.admin.command.vo.EmergencyCommandTemplateUpdateReqVO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.command.EmergencyCommandTemplateDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.command.EmergencyCommandTemplateMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.ArrayList;
import java.util.List;


/**
 * 应急指令模板 Service 实现类
 */
@Service
@Validated
@Slf4j
public class EmergencyCommandTemplateServiceImpl implements EmergencyCommandTemplateService {

    @Autowired
    private EmergencyCommandTemplateMapper commandTemplateMapper;

    @Override
    public Long createCommandTemplate(EmergencyCommandTemplateCreateReqVO createReqVO) {
        log.info("创建指令模板: name={}, category={}, applicableScenarios={}",
                createReqVO.getName(), createReqVO.getCategory(), createReqVO.getApplicableScenarios());

        // 插入
        EmergencyCommandTemplateDO commandTemplate = new EmergencyCommandTemplateDO();
        commandTemplate.setName(createReqVO.getName());
        commandTemplate.setCategory(createReqVO.getCategory());
        commandTemplate.setTitleTemplate(createReqVO.getTitleTemplate());
        commandTemplate.setContentTemplate(createReqVO.getContentTemplate());

        // 确保applicableScenarios不为null，并记录日志
        List<Integer> scenarios = createReqVO.getApplicableScenarios();
        if (scenarios != null && !scenarios.isEmpty()) {
            log.info("设置适用场景: {}", scenarios);
            commandTemplate.setApplicableScenarios(scenarios);
        } else {
            log.warn("适用场景为空，将设置为null");
            commandTemplate.setApplicableScenarios(null);
        }

        commandTemplate.setStage(createReqVO.getStage());
        commandTemplate.setPriority(createReqVO.getPriority() != null ? createReqVO.getPriority() : "MEDIUM");
        commandTemplate.setFormId(createReqVO.getFormId());
        commandTemplate.setIsSystem(false);
        commandTemplate.setIsEnabled(true);
        commandTemplate.setUsageCount(0);

        commandTemplateMapper.insert(commandTemplate);

        // 验证插入后的数据
        EmergencyCommandTemplateDO inserted = commandTemplateMapper.selectById(commandTemplate.getId());
        log.info("插入后验证: id={}, applicableScenarios={}", inserted.getId(), inserted.getApplicableScenarios());

        // 返回
        return commandTemplate.getId();
    }

    @Override
    public void updateCommandTemplate(EmergencyCommandTemplateUpdateReqVO updateReqVO) {
        log.info("更新指令模板: id={}, name={}, applicableScenarios={}",
                updateReqVO.getId(), updateReqVO.getName(), updateReqVO.getApplicableScenarios());

        // 校验存在
        validateCommandTemplateExists(updateReqVO.getId());

        // 获取原数据用于对比
        EmergencyCommandTemplateDO original = commandTemplateMapper.selectById(updateReqVO.getId());
        log.info("原数据适用场景: {}", original.getApplicableScenarios());

        // 更新
        EmergencyCommandTemplateDO updateObj = new EmergencyCommandTemplateDO();
        updateObj.setId(updateReqVO.getId());
        updateObj.setName(updateReqVO.getName());
        updateObj.setCategory(updateReqVO.getCategory());
        updateObj.setTitleTemplate(updateReqVO.getTitleTemplate());
        updateObj.setContentTemplate(updateReqVO.getContentTemplate());

        // 确保applicableScenarios正确设置
        List<Integer> scenarios = updateReqVO.getApplicableScenarios();
        if (scenarios != null) {
            log.info("更新适用场景: {}", scenarios);
            updateObj.setApplicableScenarios(scenarios);
        } else {
            log.warn("更新时适用场景为null，保持原有值或设为null");
            // 如果前端传null，保持原有值（这里设为null表示清空）
            updateObj.setApplicableScenarios(null);
        }

        updateObj.setStage(updateReqVO.getStage());
        updateObj.setPriority(updateReqVO.getPriority());
        updateObj.setFormId(updateReqVO.getFormId());

        commandTemplateMapper.updateById(updateObj);

        // 验证更新后的数据
        EmergencyCommandTemplateDO updated = commandTemplateMapper.selectById(updateReqVO.getId());
        log.info("更新后验证: id={}, applicableScenarios={}", updated.getId(), updated.getApplicableScenarios());
    }

    @Override
    public void deleteCommandTemplate(Long id) {
        // 校验存在
        validateCommandTemplateExists(id);
        // 删除
        commandTemplateMapper.deleteById(id);
    }

    private void validateCommandTemplateExists(Long id) {
        if (commandTemplateMapper.selectById(id) == null) {
            throw new IllegalArgumentException("指令模板不存在: " + id);
        }
    }

    @Override
    public EmergencyCommandTemplateDO getCommandTemplate(Long id) {
        return commandTemplateMapper.selectById(id);
    }

    @Override
    public List<EmergencyCommandTemplateDO> getCommandTemplateList(List<Long> ids) {
        return commandTemplateMapper.selectList(new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<EmergencyCommandTemplateDO>()
                .in(EmergencyCommandTemplateDO::getId, ids));
    }

    @Override
    public PageResult<EmergencyCommandTemplateDO> getCommandTemplatePage(EmergencyCommandTemplatePageReqVO pageReqVO) {
        // 简化实现，实际项目中应该使用正确的分页查询
        List<EmergencyCommandTemplateDO> list = commandTemplateMapper.selectList(
                new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<EmergencyCommandTemplateDO>()
                .likeIfPresent(EmergencyCommandTemplateDO::getName, pageReqVO.getName())
                .eqIfPresent(EmergencyCommandTemplateDO::getCategory, pageReqVO.getCategory())
                .eqIfPresent(EmergencyCommandTemplateDO::getStage, pageReqVO.getStage())
                .eqIfPresent(EmergencyCommandTemplateDO::getIsEnabled, pageReqVO.getIsEnabled())
                .eqIfPresent(EmergencyCommandTemplateDO::getIsSystem, pageReqVO.getIsSystem())
                .orderByDesc(EmergencyCommandTemplateDO::getUsageCount)
                .orderByDesc(EmergencyCommandTemplateDO::getCreateTime)
        );

        // 简化分页逻辑，实际项目中应该使用框架的分页功能
        int total = list.size();
        int start = (pageReqVO.getPageNo() - 1) * pageReqVO.getPageSize();
        int end = Math.min(start + pageReqVO.getPageSize(), list.size());
        List<EmergencyCommandTemplateDO> pageList = start < end ? list.subList(start, end) : new ArrayList<>();

        return new PageResult<>(pageList, (long) total);
    }

    @Override
    public List<EmergencyCommandTemplateDO> getCommandTemplateListByCategory(String category, String stage) {
        return commandTemplateMapper.selectList(
            new cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX<EmergencyCommandTemplateDO>()
                .eqIfPresent(EmergencyCommandTemplateDO::getCategory, category)
                .eqIfPresent(EmergencyCommandTemplateDO::getStage, stage)
                .eq(EmergencyCommandTemplateDO::getIsEnabled, true)
                .orderByDesc(EmergencyCommandTemplateDO::getUsageCount)
        );
    }

    @Override
    public void enableCommandTemplate(Long id, Boolean enabled) {
        // 校验存在
        validateCommandTemplateExists(id);
        // 更新
        EmergencyCommandTemplateDO updateObj = new EmergencyCommandTemplateDO();
        updateObj.setId(id);
        updateObj.setIsEnabled(enabled);

        commandTemplateMapper.updateById(updateObj);
    }

    @Override
    public void increaseUsageCount(Long id) {
        // 简化实现，实际项目中应该使用数据库的原子操作
        EmergencyCommandTemplateDO template = commandTemplateMapper.selectById(id);
        if (template != null) {
            template.setUsageCount(template.getUsageCount() + 1);
            commandTemplateMapper.updateById(template);
        }
    }
}
