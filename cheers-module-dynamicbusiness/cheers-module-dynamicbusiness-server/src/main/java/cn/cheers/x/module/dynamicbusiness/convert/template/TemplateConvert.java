package cn.cheers.x.module.dynamicbusiness.convert.template;

import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateFieldAssignmentDO;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 字段模板 Convert
 * 
 * @author yudao
 */
@Mapper(
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface TemplateConvert {

    TemplateConvert INSTANCE = Mappers.getMapper(TemplateConvert.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    TemplateDO convert(TemplateCreateReqVO bean);

    @Mapping(target = "code", ignore = true)
    @Mapping(target = "entityTypeCode", ignore = true)  // 业务类型不可修改
    @Mapping(target = "isSystem", ignore = true)          // 系统标记不可修改
    TemplateDO convert(TemplateUpdateReqVO bean);

    /**
     * DO 转响应 VO
     * 
     * 注意：fieldCount 字段需要在 Service 层设置
     */
    default TemplateRespVO convert(TemplateDO bean) {
        if (bean == null) {
            return null;
        }
        TemplateRespVO vo = new TemplateRespVO();
        vo.setId(bean.getId());
        vo.setCode(bean.getCode());
        vo.setName(bean.getName());
        vo.setEntityTypeCode(bean.getEntityTypeCode());
        vo.setDescription(bean.getDescription());
        vo.setStatus(bean.getStatus());
        vo.setIsSystem(bean.getIsSystem());
        vo.setCreateTime(bean.getCreateTime());
        vo.setUpdateTime(bean.getUpdateTime());
        // fieldCount 需要在 Service 层设置
        return vo;
    }

    default List<TemplateRespVO> convertList(List<TemplateDO> list) {
        if (list == null) {
            return null;
        }
        return list.stream().map(this::convert).toList();
    }

    // ========== 字段分配相关 ==========

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "templateId", ignore = true)
    TemplateFieldAssignmentDO convert(TemplateFieldAssignmentReqVO bean);

    /**
     * 字段分配 DO 转响应 VO
     * 
     * 注意：fieldCode、fieldName、dataType 字段需要在 Service 层设置
     */
    default TemplateFieldAssignmentRespVO convert(TemplateFieldAssignmentDO bean) {
        if (bean == null) {
            return null;
        }
        TemplateFieldAssignmentRespVO vo = new TemplateFieldAssignmentRespVO();
        vo.setId(bean.getId());
        vo.setTemplateId(bean.getTemplateId());
        vo.setFieldId(bean.getFieldId());
        vo.setSortOrder(bean.getSortOrder());
        vo.setRequired(bean.getRequired());
        vo.setDefaultValue(bean.getDefaultValue());
        vo.setCreateTime(bean.getCreateTime());
        // fieldCode、fieldName、dataType 需要在 Service 层设置
        return vo;
    }

    /**
     * 字段分配 DO 转响应 VO（带字段信息）
     */
    default TemplateFieldAssignmentRespVO convert(TemplateFieldAssignmentDO assignment, FieldDO field) {
        TemplateFieldAssignmentRespVO vo = convert(assignment);
        if (vo != null && field != null) {
            vo.setFieldCode(field.getCode());
            vo.setFieldName(field.getName());
            vo.setDataType(field.getType());  // FieldDO 中字段类型是 type
        }
        return vo;
    }
}
