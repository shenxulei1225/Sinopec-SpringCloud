package cn.cheers.x.module.dynamicbusiness.service.pageconfig;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.ApplyTemplateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.ApplyTemplateRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigDraftCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPageReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigPublishReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigSummaryRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.pageconfig.vo.PageConfigUpdateConfigByIdReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.pageconfig.PageConfigDO;

import java.util.List;
import java.util.Map;

/**
 * 页面配置 Service 接口
 *
 * @author yudao
 */
public interface PageConfigService {

    /**
     * 创建页面配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createPageConfig(PageConfigSaveReqVO createReqVO);

    /**
     * 更新页面配置
     *
     * @param updateReqVO 更新信息
     */
    void updatePageConfig(PageConfigSaveReqVO updateReqVO);

    /**
     * 更新页面配置内容（仅更新 config 字段）
     *
     * @param menuId 菜单ID
     * @param config 配置内容
     */
    void updatePageConfigByMenuId(Long menuId, Map<String, Object> config);

    /**
     * 删除页面配置
     *
     * @param id 编号
     */
    void deletePageConfig(Long id);

    /**
     * 删除页面（级联删除菜单与配置）
     *
+     * 场景：动态业务页面需要同时删除页面配置记录以及所生成的菜单
     *
     * @param pageConfigId 页面配置ID
     */
    void deletePageWithMenu(Long pageConfigId);

    /**
     * 根据菜单ID删除页面配置
     *
     * @param menuId 菜单ID
     */
    void deletePageConfigByMenuId(Long menuId);

    /**
     * 获得页面配置
     *
     * @param id 编号
     * @return 页面配置
     */
    PageConfigDO getPageConfig(Long id);

    /**
     * 根据菜单ID获得页面配置
     *
     * @param menuId 菜单ID
     * @return 页面配置
     */
    PageConfigDO getPageConfigByMenuId(Long menuId);

    /**
     * 根据页面代码获得页面配置
     *
     * @param pageCode 页面代码
     * @return 页面配置
     */
    PageConfigDO getPageConfigByPageCode(String pageCode);

    /**
     * 根据页面类型获得页面配置列表
     *
     * @param pageType 页面类型
     * @return 页面配置列表
     */
    List<PageConfigDO> getPageConfigListByPageType(String pageType);

    /**
     * 根据业务类型获得页面配置列表
     * 
     * 用于动态业务管理，获取某个业务类型的所有页面配置
     * 通过 config_code 字段匹配（格式：{businessType}-{pageName}）
     *
     * @param businessType 业务类型代码
     * @return 页面配置列表
     */
    List<PageConfigDO> getPageConfigListByEntityType(String entityTypeCode);

    /**
     * 按门户业务 id 获取功能页面摘要列表
     */
    List<PageConfigSummaryRespVO> getPageConfigSummaryListByBusinessId(Long businessId);

    /**
     * 获得页面配置分页
     *
     * @param pageReqVO 分页查询
     * @return 页面配置分页
     */
    PageResult<PageConfigDO> getPageConfigPage(PageConfigPageReqVO pageReqVO);

    /**
     * 创建或更新页面配置（根据 menuId 判断）
     * 
     * 如果 menuId 对应的配置已存在，则更新；否则创建新配置
     *
     * @param saveReqVO 保存信息
     * @return 编号
     */
    Long savePageConfig(PageConfigSaveReqVO saveReqVO);

    /**
     * 创建页面配置草稿（不绑定菜单）
     *
     * @param createReqVO 草稿创建信息
     * @return 页面配置ID
     */
    Long createDraft(PageConfigDraftCreateReqVO createReqVO);

    /**
     * 按页面配置ID更新配置内容（仅更新 config 字段）
     *
     * @param updateReqVO 更新信息
     */
    void updatePageConfigById(PageConfigUpdateConfigByIdReqVO updateReqVO);

    /**
     * 发布页面配置：创建菜单并绑定 dynamic_menu.page_config_id
     *
     * @param reqVO 发布请求
     * @return 菜单ID
     */
    Long publish(PageConfigPublishReqVO reqVO);

    /**
     * 应用模板创建页面配置
     * 
     * 根据模板配置和用户参数生成完整的PageConfig，包括：
     * - 自动生成视图ID（UUID）
     * - 第一个Tab设为默认视图
     * - 所有Tab默认启用
     * - 根据业务类型推断pattern（如果未显式配置）
     * 
     * 支持两种模式：
     * 1. 如果 menuId 存在且有效，使用现有菜单
     * 2. 如果 menuId 为 0 或 null，自动创建新菜单
     * 
     * @param reqVO 应用模板请求
     * @return 应用结果（包含菜单ID和页面配置ID）
     */
    ApplyTemplateRespVO applyTemplate(ApplyTemplateReqVO reqVO);
}
