package cn.cheers.x.module.dynamicbusiness.service.template;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateFieldAssignmentRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplatePageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.template.vo.TemplateUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.template.TemplateDO;

import java.util.List;

/**
 * 字段模板 Service 接口
 * 
 * Template（模板）是字段组合的预设模板，作为创建 Model 的起点。
 * Template 定义一组常用字段组合，用户创建 Model 时可选择一个 Template，
 * 系统将 Template 的字段**复制**到新 Model 中。
 * 
 * Template 与 Model 是"复制"关系而非"继承"关系：
 * - 创建 Model 时，Template 的字段被复制到 Model
 * - 之后 Model 与 Template 完全独立
 * - 修改 Template 不影响已创建的 Model
 * - 修改 Model 也不影响 Template
 * 
 * @author yudao
 */
public interface TemplateService {

    /**
     * 创建字段模板
     *
     * @param reqVO 创建信息
     * @return 模板ID
     */
    Long createTemplate(TemplateCreateReqVO reqVO);

    /**
     * 更新字段模板
     *
     * @param reqVO 更新信息
     */
    void updateTemplate(TemplateUpdateReqVO reqVO);

    /**
     * 删除字段模板
     * 
     * 系统预设模板不允许删除
     *
     * @param id 模板ID
     */
    void deleteTemplate(Long id);

    /**
     * 获取字段模板详情
     *
     * @param id 模板ID
     * @return 模板详情
     */
    TemplateRespVO getTemplate(Long id);

    /**
     * 获取字段模板 DO（内部使用）
     *
     * @param id 模板ID
     * @return 模板 DO
     */
    TemplateDO getTemplateDO(Long id);

    /**
     * 查询字段模板列表
     *
     * @param entityTypeCode 业务类型编码（可选）
     * @return 模板列表
     */
    List<TemplateRespVO> listTemplates(String entityTypeCode);

    /**
     * 分页查询字段模板
     *
     * @param reqVO 分页查询条件
     * @return 分页结果
     */
    PageResult<TemplateRespVO> pageTemplate(TemplatePageReqVO reqVO);

    /**
     * 搜索字段模板
     *
     * @param keyword 关键词
     * @param entityTypeCode 业务类型编码（可选）
     * @return 模板列表
     */
    List<TemplateRespVO> searchTemplates(String keyword, String entityTypeCode);

    /**
     * 复制字段模板
     * 
     * 基于现有模板创建新模板，复制所有字段分配
     *
     * @param sourceTemplateId 源模板ID
     * @param newName 新模板名称
     * @return 新模板ID
     */
    Long copyTemplate(Long sourceTemplateId, String newName);

    // ========== 字段分配相关 ==========

    /**
     * 为模板分配字段
     *
     * @param templateId 模板ID
     * @param reqVO 字段分配信息
     * @return 分配ID
     */
    Long assignFieldToTemplate(Long templateId, TemplateFieldAssignmentReqVO reqVO);

    /**
     * 批量为模板分配字段
     *
     * @param templateId 模板ID
     * @param reqVOList 字段分配信息列表
     */
    void batchAssignFieldsToTemplate(Long templateId, List<TemplateFieldAssignmentReqVO> reqVOList);

    /**
     * 取消模板的字段分配
     *
     * @param templateId 模板ID
     * @param fieldId 字段ID
     */
    void unassignFieldFromTemplate(Long templateId, Long fieldId);

    /**
     * 更新模板字段分配的配置（排序、必填、默认值）
     *
     * @param templateId 模板ID
     * @param fieldId 字段ID
     * @param reqVO 更新信息
     */
    void updateFieldAssignment(Long templateId, Long fieldId, TemplateFieldAssignmentReqVO reqVO);

    /**
     * 获取模板已分配的字段列表
     *
     * @param templateId 模板ID
     * @return 字段分配列表
     */
    List<TemplateFieldAssignmentRespVO> getTemplateFields(Long templateId);
}
