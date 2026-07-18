package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 模型字段分组关联 Service 实现类
 * 
 * 职责：管理字段与分组的关联关系
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ModelFieldGroupAssignmentServiceImpl implements ModelFieldGroupAssignmentService {

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    private ModelFieldGroupService modelFieldGroupService;

    @Resource
    private ModelFieldAssignmentService modelFieldAssignmentService;

    @Resource
    @Lazy
    private BusinessCapabilityService businessCapabilityService;

    private void notifyModelFieldDefinitionChanged(Long modelId) {
        if (modelId == null) {
            return;
        }
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignFieldToGroup(Long modelId, Long fieldId, Long groupId) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 校验分组存在（如果不存在会抛异常）
        try {
            modelFieldGroupService.getModelFieldGroup(modelId, groupId);
        } catch (ServiceException e) {
            throw new ServiceException(404, "字段分组不存在");
        }

        // 1) 更新 JSON 配置中的分组字段列表（基础字段也允许分组，即使没有 assignment 记录）
        modelFieldGroupService.assignFieldToGroup(modelId, fieldId, groupId);
        notifyModelFieldDefinitionChanged(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unassignFieldFromGroup(Long modelId, Long fieldId) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 1) 从 JSON 配置中移除分组关联（基础字段也允许从分组移除，即使没有 assignment 记录）
        modelFieldGroupService.unassignFieldFromGroup(modelId, fieldId);
        notifyModelFieldDefinitionChanged(modelId);
    }

    @Override
    public List<ModelFieldAssignmentRespVO> getFieldsByGroup(Long modelId, Long groupId) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 校验分组存在
        try {
            modelFieldGroupService.getModelFieldGroup(modelId, groupId);
        } catch (ServiceException e) {
            throw new ServiceException(404, "字段分组不存在");
        }

        // 1) 从配置中获取该分组下的字段ID（已按分组内排序）
        List<Long> fieldIdsInGroup = modelFieldGroupService.getFieldIdsByGroup(modelId, groupId);
        if (fieldIdsInGroup.isEmpty()) {
            return new ArrayList<>();
        }

        // 2) 获取模型的所有字段
        List<ModelFieldAssignmentRespVO> allFields = modelFieldAssignmentService.getModelFields(modelId);
        if (allFields.isEmpty()) {
            return new ArrayList<>();
        }

        // 3) 按配置中的顺序组装结果列表
        Map<Long, ModelFieldAssignmentRespVO> fieldMap = allFields.stream()
                .filter(field -> field.getField() != null && field.getField().getId() != null)
                .collect(java.util.stream.Collectors.toMap(
                        field -> field.getField().getId(),
                        field -> field,
                        (a, b) -> a));

        List<ModelFieldAssignmentRespVO> result = new ArrayList<>();
        for (Long fieldId : fieldIdsInGroup) {
            ModelFieldAssignmentRespVO vo = fieldMap.get(fieldId);
            if (vo != null) {
                result.add(vo);
            }
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reorderGroupFields(Long modelId, Long groupId, List<Long> orderedFieldIds) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 校验分组存在（如果不存在会抛异常）
        try {
            modelFieldGroupService.getModelFieldGroup(modelId, groupId);
        } catch (ServiceException e) {
            throw new ServiceException(404, "字段分组不存在");
        }

        // 仅通过 JSON 配置层重排分组内字段顺序
        // 说明：
        // - 排序信息完全由 Model.fieldGroupsConfig 中的字段引用列表维护
        // - assignment 表中暂不维护 sort
        modelFieldGroupService.reorderFieldsInGroup(modelId, groupId, orderedFieldIds);
        notifyModelFieldDefinitionChanged(modelId);
    }
}
