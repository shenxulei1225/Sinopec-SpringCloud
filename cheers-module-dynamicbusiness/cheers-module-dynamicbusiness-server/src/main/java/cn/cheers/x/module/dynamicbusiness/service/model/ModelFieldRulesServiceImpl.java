package cn.cheers.x.module.dynamicbusiness.service.model;

import cn.iocoder.yudao.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.FieldRulesUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.capability.BusinessCapabilityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 模型字段业务规则 Service 实现类
 * 
 * 职责：管理字段在模型中的业务规则（必填、默认值、验证规则等）
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class ModelFieldRulesServiceImpl implements ModelFieldRulesService {

    @Resource
    private ModelFieldAssignmentMapper modelFieldAssignmentMapper;

    @Resource
    private ModelMapper modelMapper;

    @Resource
    @Lazy
    private BusinessCapabilityService businessCapabilityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFieldRules(Long modelId, Long fieldId, FieldRulesUpdateReqVO reqVO) {
        // 校验模型存在
        ModelDO model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new ServiceException(404, "模型不存在");
        }

        // 查询字段分配
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, fieldId);
        if (assignment == null) {
            throw new ServiceException(404, "字段分配不存在");
        }

        // 更新业务规则（只更新业务规则相关字段，不涉及分组关联）
        if (reqVO.getRequired() != null) {
            assignment.setRequired(reqVO.getRequired());
        }
        if (reqVO.getIsSearchable() != null) {
            assignment.setIsSearchable(reqVO.getIsSearchable());
        }
        if (reqVO.getIsSortable() != null) {
            assignment.setIsSortable(reqVO.getIsSortable());
        }
        if (reqVO.getIsFilterable() != null) {
            assignment.setIsFilterable(reqVO.getIsFilterable());
        }
        if (reqVO.getDefaultValue() != null) {
            assignment.setDefaultValue(reqVO.getDefaultValue());
        }
        if (reqVO.getValidationRules() != null) {
            assignment.setValidationRules(reqVO.getValidationRules());
        }

        modelFieldAssignmentMapper.updateById(assignment);
        businessCapabilityService.refreshModelCrudFormDefinition(modelId);
    }

    @Override
    public FieldRulesUpdateReqVO getFieldRules(Long modelId, Long fieldId) {
        // 查询字段分配
        ModelFieldAssignmentDO assignment = modelFieldAssignmentMapper.selectByModelIdAndFieldId(modelId, fieldId);
        if (assignment == null) {
            throw new ServiceException(404, "字段分配不存在");
        }

        // 转换为 VO
        FieldRulesUpdateReqVO reqVO = new FieldRulesUpdateReqVO();
        reqVO.setRequired(assignment.getRequired());
        reqVO.setIsSearchable(assignment.getIsSearchable());
        reqVO.setIsSortable(assignment.getIsSortable());
        reqVO.setIsFilterable(assignment.getIsFilterable());
        reqVO.setDefaultValue(assignment.getDefaultValue());
        reqVO.setValidationRules(assignment.getValidationRules());

        return reqVO;
    }
}
