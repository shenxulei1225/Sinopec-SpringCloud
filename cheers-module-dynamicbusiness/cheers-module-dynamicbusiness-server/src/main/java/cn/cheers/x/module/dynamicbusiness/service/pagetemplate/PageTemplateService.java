package cn.cheers.x.module.dynamicbusiness.service.pagetemplate;

import cn.cheers.x.module.dynamicbusiness.controller.admin.pagetemplate.vo.PageTemplateVO;

import java.util.List;

/**
 * 页面模板 Service 接口
 * 
 * 负责从 JSON 文件加载页面模板配置，为可视化配置界面提供模板数据。
 * 
 * 页面模板与字段模板的区别：
 * - 页面模板：定义完整的页面结构（布局、Tab、字段列表等），存储在 JSON 文件中
 * - 字段模板：定义字段组合，存储在数据库中
 * 
 * @author yudao
 */
public interface PageTemplateService {

    /**
     * 加载所有页面模板
     * 
     * 从指定目录读取所有 JSON 文件，解析为 PageTemplateVO 对象。
     * 加载失败的模板会被跳过，并记录错误日志。
     * 
     * @return 页面模板列表，按模板ID排序
     */
    List<PageTemplateVO> loadPageTemplates();

    /**
     * 根据ID获取页面模板详情
     * 
     * @param templateId 模板ID
     * @return 页面模板详情，如果不存在则返回 null
     */
    PageTemplateVO getTemplateById(String templateId);
}
