package cn.cheers.x.module.dynamicbusiness.service.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplatePageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.convert.template.TemplateConvert;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateFieldAssignmentDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.template.TemplateFieldAssignmentMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.template.TemplateMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.cheers.x.module.dynamicbusiness.enums.ErrorCodeConstants.*;

/**
 * 字段模板 Service 实现类
 * 
 * @author yudao
 */
@Service
@Validated
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    @Resource
    private TemplateMapper templateMapper;

    @Resource
    private TemplateFieldAssignmentMapper templateFieldAssignmentMapper;

    @Resource
    private FieldMapper fieldMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createTemplate(TemplateCreateReqVO reqVO) {
        // 1. 校验模板名称唯一性
        validateTemplateNameUnique(null, reqVO.getName());

        // 2. 生成模板编码
        String code = generateTemplateCode();

        // 3. 创建模板
        TemplateDO template = TemplateConvert.INSTANCE.convert(reqVO);
        template.setCode(code);
        if (template.getIsSystem() == null) {
            template.setIsSystem(false);
        }
        templateMapper.insert(template);

        log.info("[createTemplate][创建字段模板成功，id={}, code={}, name={}]", 
                template.getId(), template.getCode(), template.getName());
        return template.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateTemplate(TemplateUpdateReqVO reqVO) {
        // 1. 校验模板存在
        TemplateDO existTemplate = validateTemplateExists(reqVO.getId());

        // 2. 校验模板名称唯一性
        validateTemplateNameUnique(reqVO.getId(), reqVO.getName());

        // 3. 更新模板
        TemplateDO updateObj = TemplateConvert.INSTANCE.convert(reqVO);
        templateMapper.updateById(updateObj);

        log.info("[updateTemplate][更新字段模板成功，id={}, name={}]", reqVO.getId(), reqVO.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTemplate(Long id) {
        // 1. 校验模板存在
        TemplateDO template = validateTemplateExists(id);

        // 2. 校验是否为系统预设模板
        if (template.isSystemTemplate()) {
            throw exception(TEMPLATE_SYSTEM_CANNOT_DELETE);
        }

        // 3. 删除模板的字段分配
        templateFieldAssignmentMapper.deleteByTemplateId(id);

        // 4. 删除模板
        templateMapper.deleteById(id);

        log.info("[deleteTemplate][删除字段模板成功，id={}, name={}]", id, template.getName());
    }

    @Override
    public TemplateRespVO getTemplate(Long id) {
        TemplateDO template = templateMapper.selectById(id);
        if (template == null) {
            return null;
        }
        TemplateRespVO respVO = TemplateConvert.INSTANCE.convert(template);
        // 设置字段数量
        List<TemplateFieldAssignmentDO> assignments = templateFieldAssignmentMapper.selectByTemplateId(id);
        respVO.setFieldCount(assignments.size());
        return respVO;
    }

    @Override
    public TemplateDO getTemplateDO(Long id) {
        return templateMapper.selectById(id);
    }

    @Override
    public List<TemplateRespVO> listTemplates(String entityTypeCode) {
        List<TemplateDO> templates;
        if (StrUtil.isNotBlank(entityTypeCode)) {
            templates = templateMapper.selectByEntityTypeCode(entityTypeCode);
        } else {
            templates = templateMapper.selectList();
        }
        return convertWithFieldCount(templates);
    }

    @Override
    public PageResult<TemplateRespVO> pageTemplate(TemplatePageReqVO reqVO) {
        PageResult<TemplateDO> pageResult = templateMapper.selectPage(reqVO);
        List<TemplateRespVO> respVOList = convertWithFieldCount(pageResult.getList());
        return new PageResult<>(respVOList, pageResult.getTotal());
    }

    @Override
    public List<TemplateRespVO> searchTemplates(String keyword, String entityTypeCode) {
        if (StrUtil.isBlank(keyword)) {
            return listTemplates(entityTypeCode);
        }
        List<TemplateDO> templates = templateMapper.search(keyword, entityTypeCode);
        return convertWithFieldCount(templates);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long copyTemplate(Long sourceTemplateId, String newName) {
        // 1. 校验源模板存在
        TemplateDO sourceTemplate = validateTemplateExists(sourceTemplateId);

        // 2. 校验新名称唯一性
        validateTemplateNameUnique(null, newName);

        // 3. 创建新模板
        TemplateDO newTemplate = new TemplateDO();
        newTemplate.setCode(generateTemplateCode());
        newTemplate.setName(newName);
        newTemplate.setEntityTypeCode(sourceTemplate.getEntityTypeCode());
        newTemplate.setDescription(sourceTemplate.getDescription());
        newTemplate.setStatus(sourceTemplate.getStatus());
        newTemplate.setIsSystem(false);  // 复制的模板不是系统模板
        templateMapper.insert(newTemplate);

        // 4. 复制字段分配
        List<TemplateFieldAssignmentDO> sourceAssignments = 
                templateFieldAssignmentMapper.selectByTemplateId(sourceTemplateId);
        for (TemplateFieldAssignmentDO sourceAssignment : sourceAssignments) {
            TemplateFieldAssignmentDO newAssignment = new TemplateFieldAssignmentDO();
            newAssignment.setTemplateId(newTemplate.getId());
            newAssignment.setFieldId(sourceAssignment.getFieldId());
            newAssignment.setSortOrder(sourceAssignment.getSortOrder());
            newAssignment.setRequired(sourceAssignment.getRequired());
            newAssignment.setDefaultValue(sourceAssignment.getDefaultValue());
            syncTemplateAssignmentIdentity(newAssignment, newTemplate.getId(), sourceAssignment.getFieldId());
            templateFieldAssignmentMapper.insert(newAssignment);
        }

        log.info("[copyTemplate][复制字段模板成功，sourceId={}, newId={}, newName={}]", 
                sourceTemplateId, newTemplate.getId(), newName);
        return newTemplate.getId();
    }

    // ========== 字段分配相关 ==========

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long assignFieldToTemplate(Long templateId, TemplateFieldAssignmentReqVO reqVO) {
        // 1. 校验模板存在
        validateTemplateExists(templateId);

        // 2. 校验字段存在
        FieldDO field = fieldMapper.selectById(reqVO.getFieldId());
        if (field == null) {
            throw exception(DICT_DATA_NOT_EXISTS);  // 复用字段不存在的错误码
        }

        // 3. 校验字段是否已分配
        TemplateFieldAssignmentDO existAssignment = 
                templateFieldAssignmentMapper.selectByTemplateIdAndFieldId(templateId, reqVO.getFieldId());
        if (existAssignment != null) {
            throw exception(TEMPLATE_FIELD_ALREADY_ASSIGNED);
        }

        // 4. 创建字段分配
        TemplateFieldAssignmentDO assignment = TemplateConvert.INSTANCE.convert(reqVO);
        assignment.setTemplateId(templateId);
        if (assignment.getSortOrder() == null) {
            assignment.setSortOrder(0);
        }
        if (assignment.getRequired() == null) {
            assignment.setRequired(false);
        }
        syncTemplateAssignmentIdentity(assignment, templateId, reqVO.getFieldId());
        templateFieldAssignmentMapper.insert(assignment);

        log.info("[assignFieldToTemplate][为模板分配字段成功，templateId={}, fieldId={}]", 
                templateId, reqVO.getFieldId());
        return assignment.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAssignFieldsToTemplate(Long templateId, List<TemplateFieldAssignmentReqVO> reqVOList) {
        if (CollUtil.isEmpty(reqVOList)) {
            return;
        }
        // 1. 校验模板存在
        validateTemplateExists(templateId);

        // 2. 逐个分配字段
        for (TemplateFieldAssignmentReqVO reqVO : reqVOList) {
            // 校验字段存在
            FieldDO field = fieldMapper.selectById(reqVO.getFieldId());
            if (field == null) {
                log.warn("[batchAssignFieldsToTemplate][字段不存在，跳过，fieldId={}]", reqVO.getFieldId());
                continue;
            }

            // 校验字段是否已分配
            TemplateFieldAssignmentDO existAssignment = 
                    templateFieldAssignmentMapper.selectByTemplateIdAndFieldId(templateId, reqVO.getFieldId());
            if (existAssignment != null) {
                log.warn("[batchAssignFieldsToTemplate][字段已分配，跳过，templateId={}, fieldId={}]", 
                        templateId, reqVO.getFieldId());
                continue;
            }

            // 创建字段分配
            TemplateFieldAssignmentDO assignment = TemplateConvert.INSTANCE.convert(reqVO);
            assignment.setTemplateId(templateId);
            if (assignment.getSortOrder() == null) {
                assignment.setSortOrder(0);
            }
            if (assignment.getRequired() == null) {
                assignment.setRequired(false);
            }
            syncTemplateAssignmentIdentity(assignment, templateId, reqVO.getFieldId());
            templateFieldAssignmentMapper.insert(assignment);
        }

        log.info("[batchAssignFieldsToTemplate][批量为模板分配字段成功，templateId={}, count={}]", 
                templateId, reqVOList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unassignFieldFromTemplate(Long templateId, Long fieldId) {
        // 1. 校验模板存在
        validateTemplateExists(templateId);

        // 2. 校验字段分配存在
        TemplateFieldAssignmentDO assignment = 
                templateFieldAssignmentMapper.selectByTemplateIdAndFieldId(templateId, fieldId);
        if (assignment == null) {
            throw exception(TEMPLATE_FIELD_ASSIGNMENT_NOT_EXISTS);
        }

        // 3. 删除字段分配
        templateFieldAssignmentMapper.deleteById(assignment.getId());

        log.info("[unassignFieldFromTemplate][取消模板字段分配成功，templateId={}, fieldId={}]", 
                templateId, fieldId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateFieldAssignment(Long templateId, Long fieldId, TemplateFieldAssignmentReqVO reqVO) {
        // 1. 校验模板存在
        validateTemplateExists(templateId);

        // 2. 校验字段分配存在
        TemplateFieldAssignmentDO assignment = 
                templateFieldAssignmentMapper.selectByTemplateIdAndFieldId(templateId, fieldId);
        if (assignment == null) {
            throw exception(TEMPLATE_FIELD_ASSIGNMENT_NOT_EXISTS);
        }

        // 3. 更新字段分配
        assignment.setSortOrder(reqVO.getSortOrder());
        assignment.setRequired(reqVO.getRequired());
        assignment.setDefaultValue(reqVO.getDefaultValue());
        templateFieldAssignmentMapper.updateById(assignment);

        log.info("[updateFieldAssignment][更新模板字段分配成功，templateId={}, fieldId={}]", 
                templateId, fieldId);
    }

    @Override
    public List<TemplateFieldAssignmentRespVO> getTemplateFields(Long templateId) {
        // 1. 获取字段分配列表
        List<TemplateFieldAssignmentDO> assignments = templateFieldAssignmentMapper.selectByTemplateId(templateId);
        if (CollUtil.isEmpty(assignments)) {
            return new ArrayList<>();
        }

        // 2. 批量获取字段信息
        List<Long> fieldIds = assignments.stream()
                .map(TemplateFieldAssignmentDO::getFieldId)
                .collect(Collectors.toList());
        List<FieldDO> fields = fieldMapper.selectByIds(fieldIds);
        Map<Long, FieldDO> fieldMap = fields.stream()
                .collect(Collectors.toMap(FieldDO::getId, f -> f));

        // 3. 转换为响应 VO
        return assignments.stream()
                .map(assignment -> {
                    FieldDO field = fieldMap.get(assignment.getFieldId());
                    return TemplateConvert.INSTANCE.convert(assignment, field);
                })
                .collect(Collectors.toList());
    }

    // ========== 私有方法 ==========

    /**
     * 校验模板存在
     */
    private TemplateDO validateTemplateExists(Long id) {
        TemplateDO template = templateMapper.selectById(id);
        if (template == null) {
            throw exception(TEMPLATE_NOT_EXISTS);
        }
        return template;
    }

    /**
     * 校验模板名称唯一性
     */
    private void validateTemplateNameUnique(Long id, String name) {
        TemplateDO template = templateMapper.selectByName(name);
        if (template == null) {
            return;
        }
        // 如果是更新操作，且名称没有变化，则不报错
        if (id != null && id.equals(template.getId())) {
            return;
        }
        throw exception(TEMPLATE_NAME_DUPLICATE, name);
    }

    /**
     * 生成模板编码
     */
    private String generateTemplateCode() {
        return "TPL-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

    /**
     * 转换模板列表并设置字段数量
     */
    private List<TemplateRespVO> convertWithFieldCount(List<TemplateDO> templates) {
        if (CollUtil.isEmpty(templates)) {
            return new ArrayList<>();
        }
        return templates.stream()
                .map(template -> {
                    TemplateRespVO respVO = TemplateConvert.INSTANCE.convert(template);
                    List<TemplateFieldAssignmentDO> assignments = 
                            templateFieldAssignmentMapper.selectByTemplateId(template.getId());
                    respVO.setFieldCount(assignments.size());
                    return respVO;
                })
                .collect(Collectors.toList());
    }

    /** 写入模板字段分配时同步 template_code + field_code（迁移以 code 为幂等键）。 */
    private void syncTemplateAssignmentIdentity(TemplateFieldAssignmentDO assignment, Long templateId, Long fieldId) {
        if (assignment == null) {
            return;
        }
        TemplateDO template = templateMapper.selectById(templateId);
        FieldDO field = fieldMapper.selectById(fieldId);
        if (template != null) {
            assignment.setTemplateId(template.getId());
            assignment.setTemplateCode(template.getCode());
        }
        if (field != null) {
            assignment.setFieldId(field.getId());
            assignment.setFieldCode(field.getCode());
        }
    }
}
