package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmLayoutSectionVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmWorkbenchLayoutSettingsRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.datamgmt.vo.DmWorkbenchLayoutSettingsUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmPageLayoutRefDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmWorkbenchLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmPageLayoutRefMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmWorkbenchLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmWorkbenchLayoutNames;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工作台布局：模版列表、从模版生成实例、解析页面/目录上的 layoutId。
 * <p>
 * 权威：创建目录时从此处用<strong>同一份</strong>「通用台账」模版实例化布局行——
 * 一次写好同类分类/型号/实体栏、类型码，以及栏所在<strong>区域编号</strong>（本份布局头上的区域清单），禁止半截空壳。
 * <ul>
 *   <li>基础数据（NATIVE）与 REUSE/DOMAIN/SCOPE：四栏全开（列表台账）。</li>
 *   <li>分类绑定实体（CATEGORY）：仍复制四栏，再关掉型号/实体栏（树即对象）。</li>
 * </ul>
 * 显示名用底座类型 / 分类种类 / 本目录中文名，不用类型编码冒充 label。
 * 创建时同步写好分类/型号/实体栏默认展示配置 propsId（经资源服务）；不负责打开页现造 props。
 * 不负责：按固定区段名字猜区域；读路径把旧区段语义映射成新区；另起第二套模版。
 */
@Service
public class DmWorkbenchLayoutService {

    @Resource
    private DmWorkbenchLayoutMapper dmWorkbenchLayoutMapper;

    @Resource
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;

    @Resource
    private DmPageLayoutRefMapper dmPageLayoutRefMapper;

    @Resource
    private DmCatalogLayoutDisplayPropsBootstrapService dmCatalogLayoutDisplayPropsBootstrapService;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    @Resource
    private CategoryTypeMapper categoryTypeMapper;

    @Lazy
    @Resource
    private DmDataTabColumnRelationBootstrapService dmDataTabColumnRelationBootstrapService;

    public List<DmWorkbenchLayoutDO> listTemplates() {
        return dmWorkbenchLayoutMapper.selectTemplates();
    }

    public DmWorkbenchLayoutDO requireLayout(Long layoutId) {
        if (layoutId == null) {
            throw new ServiceException(400, "layoutId 不能为空");
        }
        DmWorkbenchLayoutDO row = dmWorkbenchLayoutMapper.selectById(layoutId);
        if (row == null || Boolean.TRUE.equals(row.getDeleted())) {
            throw new ServiceException(404, "布局不存在：" + layoutId);
        }
        return row;
    }

    /** 读取布局头：区域清单 + 按区域编号的隐藏。缺清单不补默认三块。 */
    public DmWorkbenchLayoutSettingsRespVO getSettings(Long layoutId) {
        return toSettingsVo(requireLayout(layoutId).getSettingsJson());
    }

    /**
     * 只替换布局头的 sectionHidden，保留 settingsJson 中其它设置（含区域清单）。
     * 隐藏键必须是本布局已有区域编号；栏行 enabled 与浏览器折叠状态不得补写该字段。
     */
    @Transactional(rollbackFor = Exception.class)
    public DmWorkbenchLayoutSettingsRespVO updateSettings(
            Long layoutId,
            DmWorkbenchLayoutSettingsUpdateReqVO reqVO) {
        DmWorkbenchLayoutDO layout = requireLayout(layoutId);
        Map<String, Object> settings = layout.getSettingsJson() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(layout.getSettingsJson());
        Set<String> allowed = sectionIds(readSections(settings));
        Map<String, Boolean> sectionHidden = normalizeSectionHidden(reqVO.getSectionHidden(), allowed);
        settings.put("sectionHidden", sectionHidden);
        DmWorkbenchLayoutDO update = new DmWorkbenchLayoutDO();
        update.setId(layoutId);
        update.setSettingsJson(settings);
        dmWorkbenchLayoutMapper.updateById(update);
        return toSettingsVo(settings);
    }

    /** 本份页面布局上已写入的区域编号；缺清单返回空，不发明默认三块。 */
    public Set<String> sectionIdsOf(Long layoutId) {
        return sectionIds(readSections(requireLayout(layoutId).getSettingsJson()));
    }

    /**
     * 目录「数据」页签：若尚无 dataLayoutId，从通用台账模版生成实例并写回类型。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long ensureCatalogDataLayout(EntityTypeDO entityType,
                                        String categoryTypeCode,
                                        boolean categoryAsEntityLayout) {
        if (entityType.getDataLayoutId() != null) {
            List<DmDataTabLayoutDO> rows =
                    dmDataTabLayoutMapper.selectListByLayoutId(entityType.getDataLayoutId());
            if (!rows.isEmpty()) {
                return entityType.getDataLayoutId();
            }
        }
        Long templateId = requireDefaultTemplateId();
        String name = "数据页签·" + entityType.getCode();
        Long layoutId = instantiateFromTemplate(
                templateId,
                name,
                entityType.getCode(),
                categoryTypeCode,
                categoryAsEntityLayout);
        entityType.setDataLayoutId(layoutId);
        entityTypeMapper.updateById(entityType);
        return layoutId;
    }

    public Long resolveLayoutIdForEntityType(String entityTypeCode) {
        EntityTypeDO entityType = entityTypeMapper.selectByCode(entityTypeCode.trim());
        if (entityType == null) {
            throw new ServiceException(404, "数据类型不存在：" + entityTypeCode);
        }
        if (entityType.getDataLayoutId() == null) {
            throw new ServiceException(400, "数据类型尚未挂载 dataLayoutId：" + entityTypeCode);
        }
        return entityType.getDataLayoutId();
    }

    public Long resolveDataLayoutIdForPageKey(String pageKey) {
        DmPageLayoutRefDO ref = dmPageLayoutRefMapper.selectByPageKey(pageKey.trim());
        if (ref == null || ref.getDataLayoutId() == null) {
            return null;
        }
        return ref.getDataLayoutId();
    }

    public DmPageLayoutRefDO requirePageRef(String pageKey) {
        DmPageLayoutRefDO ref = dmPageLayoutRefMapper.selectByPageKey(pageKey.trim());
        if (ref == null) {
            throw new ServiceException(404, "页面布局引用不存在：" + pageKey);
        }
        return ref;
    }

    /**
     * 从模版复制栏行并一次写全本页栏身份（禁止留下缺类型码的空壳）。
     * <p>
     * NATIVE：分类/型号/实体类型码 = 本目录编码。<br>
     * SCOPE / DOMAIN / REUSE：分类/型号/实体类型码 = 底座（同类台账），由调用方传入
     * {@code categoryTypeCode}（底座编码）。实体栏显示名可用本目录中文名，类型码仍是底座。
     * <p>
     * 栏所在区域：分类、型号 → 本模板第一块；实体 → 第二块。按清单位置盖章，不按 5W 名称猜。
     * 分类 Tab 编号必须非空。启用中的分类/型号/实体一次写好 propsId（禁止半截交给前端补）。
     */
    @Transactional(rollbackFor = Exception.class)
    public Long instantiateFromTemplate(Long templateId,
                                        String instanceName,
                                        String entityTypeCode,
                                        String categoryTypeCode,
                                        boolean categoryAsEntityLayout) {
        DmWorkbenchLayoutDO template = requireLayout(templateId);
        if (!Boolean.TRUE.equals(template.getIsTemplate())) {
            throw new ServiceException(400, "只能从模版生成实例：" + templateId);
        }
        List<DmDataTabLayoutDO> templateRows = dmDataTabLayoutMapper.selectListByLayoutId(templateId);
        if (templateRows.isEmpty()) {
            throw new ServiceException(500, "模版无栏行：" + templateId);
        }
        assertLedgerTemplateCoreKinds(templateId, templateRows);
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "实例化布局必须提供目录注册编码");
        }

        DmWorkbenchLayoutDO instance = new DmWorkbenchLayoutDO();
        instance.setName(instanceName);
        instance.setIsTemplate(false);
        instance.setSourceTemplateId(templateId);
        Map<String, Object> instanceSettings = template.getSettingsJson() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(template.getSettingsJson());
        if (readSections(instanceSettings).isEmpty()) {
            throw new ServiceException(500, "布局模版缺少区域清单，无法实例化目录数据页");
        }
        instance.setSettingsJson(instanceSettings);
        dmWorkbenchLayoutMapper.insert(instance);

        List<Map<String, Object>> sections = readSections(instance.getSettingsJson());
        Set<String> allowedSectionIds = sectionIds(sections);
        if (allowedSectionIds.isEmpty()) {
            throw new ServiceException(500, "布局模版未配置可用区域，无法实例化栏布局");
        }

        String code = entityTypeCode.trim();
        // 同类台账类型码：调用方已解析底座；缺省与注册码相同（NATIVE）
        String storageCode = StringUtils.hasText(categoryTypeCode) ? categoryTypeCode.trim() : code;
        if (!StringUtils.hasText(storageCode)) {
            throw new ServiceException(400, "实例化布局缺少同类类型编码（底座/本类型）");
        }
        String storageDisplayName = resolveEntityTypeDisplayName(storageCode);
        String registryDisplayName = resolveEntityTypeDisplayName(code);
        String categoryDisplayName = resolveCategoryTypeDisplayName(storageCode);
        if (!StringUtils.hasText(categoryDisplayName)) {
            categoryDisplayName = storageDisplayName;
        }
        // 分类 Tab 编号：按同类种类生成稳定非空编号；栏分组键（columnKey）与编号分开
        String categoryTabId = storageCode + "-1";
        Set<String> usedDetailTabIds = new HashSet<>();
        for (DmDataTabLayoutDO existing : templateRows) {
            if (!DmDataTabLayoutKindEnum.DETAIL.getCode().equals(existing.getColumnKind())) {
                continue;
            }
            String tab = existing.getTabId() == null ? "" : existing.getTabId().trim();
            if (!StringUtils.hasText(tab) || "default".equalsIgnoreCase(tab)) {
                continue;
            }
            usedDetailTabIds.add(tab);
        }

        for (DmDataTabLayoutDO src : templateRows) {
            DmDataTabLayoutDO row = new DmDataTabLayoutDO();
            row.setLayoutId(instance.getId());
            row.setEntityTypeCode(code);
            row.setColumnKind(src.getColumnKind());
            row.setTabId(src.getTabId());
            row.setPropsId(null);
            row.setEnabled(src.getEnabled());
            row.setColumnMeta(copyMeta(src.getColumnMeta()));
            String sourceSectionId = requireSectionIdInTemplateRow(
                    row.getColumnMeta(), row.getColumnKind(), allowedSectionIds);

            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(src.getColumnKind())) {
                // 模版若仍是旧字面 default / 空，换成真实编号；已有合法编号则保留
                String srcTab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(srcTab) || "default".equalsIgnoreCase(srcTab)) {
                    row.setTabId(categoryTabId);
                }
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                meta.put("categoryTypeCode", storageCode);
                meta.put("label", categoryDisplayName);
                meta.put("columnSection", sourceSectionId);
                Object existingKey = meta.get("columnKey");
                String keyStr = existingKey == null ? "" : String.valueOf(existingKey).trim();
                // 分组键用种类码，禁止 default；看身份能知道是哪类分类
                if (!StringUtils.hasText(keyStr) || "default".equalsIgnoreCase(keyStr)) {
                    meta.put("columnKey", storageCode);
                }
                if (!(meta.get("widthPx") instanceof Number)) {
                    meta.put("widthPx", DmDataTabLayoutBootstrapMeta.CATEGORY_COLUMN_WIDTH_PX);
                }
                if (!(meta.get("sectionWidthPx") instanceof Number)) {
                    meta.put("sectionWidthPx", DmDataTabLayoutBootstrapMeta.FILTER_SECTION_WIDTH_PX);
                }
                row.setColumnMeta(meta);
            } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(src.getColumnKind())) {
                // 型号栏 tabId = 底座类型编码；身份 MODEL:{tabId}；禁止空与 default
                String modelTab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(modelTab) || "default".equalsIgnoreCase(modelTab)) {
                    row.setTabId(storageCode);
                }
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                meta.put("modelEntityTypeCode", storageCode);
                meta.put("label", storageDisplayName);
                meta.put("columnSection", sourceSectionId);
                if (!(meta.get("widthPx") instanceof Number)) {
                    meta.put("widthPx", DmDataTabLayoutBootstrapMeta.MODEL_COLUMN_WIDTH_PX);
                }
                // 筛选区宽已由分类领头栏写出；型号不抢 sectionWidthPx
                meta.remove("sectionWidthPx");
                row.setColumnMeta(meta);
            } else if (DmDataTabLayoutKindEnum.ENTITY.getCode().equals(src.getColumnKind())) {
                // 实体栏 tabId = 底座类型编码；身份 ENTITY:{tabId}；禁止空与 default
                String entityTab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(entityTab) || "default".equalsIgnoreCase(entityTab)) {
                    row.setTabId(storageCode);
                }
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                // 类型码 = 同类底座；显示名优先本目录名（划分页仍认设备账）
                meta.put("entityEntityTypeCode", storageCode);
                meta.put("label", StringUtils.hasText(registryDisplayName)
                        ? registryDisplayName : storageDisplayName);
                meta.put("columnSection", sourceSectionId);
                if (!(meta.get("widthPx") instanceof Number)) {
                    meta.put("widthPx", DmDataTabLayoutBootstrapMeta.ENTITY_COLUMN_WIDTH_PX);
                }
                if (!(meta.get("sectionWidthPx") instanceof Number)) {
                    meta.put("sectionWidthPx", DmDataTabLayoutBootstrapMeta.OBJECT_SECTION_WIDTH_PX);
                }
                row.setColumnMeta(meta);
            } else if (DmDataTabLayoutKindEnum.DETAIL.getCode().equals(src.getColumnKind())) {
                String detailTab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(detailTab) || "default".equalsIgnoreCase(detailTab)) {
                    row.setTabId(allocateTabId("detail", usedDetailTabIds));
                }
                row.setEnabled(true);
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                meta.put("columnSection", sourceSectionId);
                if (!metaHasText(meta, "label")) {
                    // 仅在模板未配置展示名时给中性默认文案；禁止这里写死业务语义名。
                    meta.put("label", "区段栏");
                }
                meta.put("entityEntityTypeCode", storageCode);
                if (!(meta.get("sectionWidthPx") instanceof Number)) {
                    meta.put("sectionWidthPx", DmDataTabLayoutBootstrapMeta.DETAIL_SECTION_WIDTH_PX);
                }
                row.setColumnMeta(meta);
            }

            if (categoryAsEntityLayout) {
                applyCategoryAsEntityEnables(row);
            }
            dmDataTabLayoutMapper.insert(row);
        }
        assertInstantiatedColumnIdentities(instance.getId());
        dmCatalogLayoutDisplayPropsBootstrapService.bindDefaultDisplayProps(
                instance.getId(), code, storageCode);
        dmDataTabColumnRelationBootstrapService.applyInitialDefaultRelations(
                instance.getId(), code, storageCode);
        return instance.getId();
    }

    /**
     * 实例化后兜底核对：启用中的分类/型号/实体必须已有类型码，否则回滚创建。
     * 禁止「栏有了、类型码没有」半截布局交给用户补。
     */
    private void assertInstantiatedColumnIdentities(Long layoutId) {
        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        for (DmDataTabLayoutDO row : rows) {
            if (Boolean.FALSE.equals(row.getEnabled())) {
                continue;
            }
            String kind = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
            Map<String, Object> meta = row.getColumnMeta();
            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
                if (!metaHasText(meta, "categoryTypeCode")) {
                    throw new ServiceException(500, "自动创建布局失败：分类栏缺少 categoryTypeCode");
                }
                String tab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(tab) || "default".equalsIgnoreCase(tab)
                        || tab.toLowerCase().endsWith("-default")) {
                    throw new ServiceException(500, "自动创建布局失败：分类栏 tabId 须为种类码编号，禁止 default/-default");
                }
                Object keyObj = meta == null ? null : meta.get("columnKey");
                String key = keyObj == null ? "" : String.valueOf(keyObj).trim();
                if (!StringUtils.hasText(key) || "default".equalsIgnoreCase(key)
                        || key.toLowerCase().endsWith("-default")) {
                    throw new ServiceException(500, "自动创建布局失败：分类栏 columnKey 须为种类码，禁止 default/-default");
                }
            } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)) {
                if (!metaHasText(meta, "modelEntityTypeCode")) {
                    throw new ServiceException(500, "自动创建布局失败：型号栏缺少 modelEntityTypeCode");
                }
                String tab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(tab) || "default".equalsIgnoreCase(tab)) {
                    throw new ServiceException(500, "自动创建布局失败：型号栏 tabId 须为底座类型编码");
                }
            } else if (DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
                if (!metaHasText(meta, "entityEntityTypeCode")) {
                    throw new ServiceException(500, "自动创建布局失败：实体栏缺少 entityEntityTypeCode");
                }
                String tab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(tab) || "default".equalsIgnoreCase(tab)) {
                    throw new ServiceException(500, "自动创建布局失败：实体栏 tabId 须为底座类型编码");
                }
            } else if (DmDataTabLayoutKindEnum.DETAIL.getCode().equals(kind)) {
                String tab = row.getTabId() == null ? "" : row.getTabId().trim();
                if (!StringUtils.hasText(tab) || "default".equalsIgnoreCase(tab)) {
                    throw new ServiceException(500, "自动创建布局失败：详情栏 tabId 不能为空或 default");
                }
            }
        }
    }

    private static boolean metaHasText(Map<String, Object> meta, String key) {
        if (meta == null || !StringUtils.hasText(key)) {
            return false;
        }
        Object value = meta.get(key);
        return value != null && StringUtils.hasText(String.valueOf(value).trim());
    }

    /** 数据类型中文名；缺则退回编码（仅无名称数据时） */
    private String resolveEntityTypeDisplayName(String typeCode) {
        if (!StringUtils.hasText(typeCode)) {
            return "";
        }
        String code = typeCode.trim();
        EntityTypeDO type = entityTypeMapper.selectByCode(code);
        if (type != null && StringUtils.hasText(type.getName())) {
            return type.getName().trim();
        }
        return code;
    }

    /** 分类种类中文名；没有种类行则返回空，由调用方退回底座名 */
    private String resolveCategoryTypeDisplayName(String categoryTypeCode) {
        if (!StringUtils.hasText(categoryTypeCode)) {
            return "";
        }
        CategoryTypeDO type = categoryTypeMapper.selectByCategoryTypeCode(categoryTypeCode.trim());
        if (type != null && StringUtils.hasText(type.getName())) {
            return type.getName().trim();
        }
        return "";
    }

    private Long requireDefaultTemplateId() {
        DmWorkbenchLayoutDO tpl = dmWorkbenchLayoutMapper.selectDefaultTemplate();
        if (tpl == null) {
            List<DmWorkbenchLayoutDO> all = dmWorkbenchLayoutMapper.selectTemplates();
            if (all.isEmpty()) {
                return createDefaultLedgerTemplate();
            }
            throw new ServiceException(500, "缺少默认布局模版：请先设置默认模版后再创建目录");
        }
        Long templateId = tpl.getId();
        // 共用一份通用台账：模版不完整时直接报错，禁止按固定区段猜测补行。
        ensureLedgerTemplateCoreColumns(templateId);
        return templateId;
    }

    /**
     * 通用台账模版必须具备分类/型号/实体/详情四栏。
     * 模版行故意不写底座类型码（实例化时再盖章）；缺口要暴露，禁止在此处补丁式猜测补行。
     */
    private void ensureLedgerTemplateCoreColumns(Long templateId) {
        DmWorkbenchLayoutDO header = requireLayout(templateId);
        Map<String, Object> settings = header.getSettingsJson() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(header.getSettingsJson());
        List<Map<String, Object>> sections = readSections(settings);
        Set<String> existingSections = sectionIds(sections);
        if (existingSections.isEmpty()) {
            throw new ServiceException(500, "通用台账模版缺少区域清单（layoutId=" + templateId + "）");
        }
        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByLayoutId(templateId);
        Set<String> kinds = new LinkedHashSet<>();
        for (DmDataTabLayoutDO row : rows) {
            if (row.getColumnKind() != null) {
                kinds.add(row.getColumnKind().trim().toUpperCase());
            }
        }
        for (DmDataTabLayoutKindEnum required : List.of(
                DmDataTabLayoutKindEnum.CATEGORY,
                DmDataTabLayoutKindEnum.MODEL,
                DmDataTabLayoutKindEnum.ENTITY,
                DmDataTabLayoutKindEnum.DETAIL)) {
            if (!kinds.contains(required.getCode())) {
                throw new ServiceException(500,
                        "通用台账模版缺少「" + required.getCode() + "」栏（layoutId=" + templateId + "）");
            }
        }
        for (DmDataTabLayoutDO row : rows) {
            if (row.getColumnKind() == null) {
                continue;
            }
            String kind = row.getColumnKind().trim().toUpperCase();
            if (!DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    && !DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                    && !DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)
                    && !DmDataTabLayoutKindEnum.DETAIL.getCode().equals(kind)) {
                continue;
            }
            requireSectionIdInTemplateRow(row.getColumnMeta(), kind, existingSections);
        }
        assertLedgerTemplateCoreKinds(templateId, dmDataTabLayoutMapper.selectListByLayoutId(templateId));
    }

    /** 模版缺任一核心栏则拒绝实例化，避免再写出半截目录布局。 */
    private static void assertLedgerTemplateCoreKinds(Long templateId, List<DmDataTabLayoutDO> rows) {
        Set<String> kinds = new LinkedHashSet<>();
        for (DmDataTabLayoutDO row : rows) {
            if (row.getColumnKind() != null) {
                kinds.add(row.getColumnKind().trim().toUpperCase());
            }
        }
        for (DmDataTabLayoutKindEnum required : List.of(
                DmDataTabLayoutKindEnum.CATEGORY,
                DmDataTabLayoutKindEnum.MODEL,
                DmDataTabLayoutKindEnum.ENTITY,
                DmDataTabLayoutKindEnum.DETAIL)) {
            if (!kinds.contains(required.getCode())) {
                throw new ServiceException(500,
                        "通用台账模版缺少「" + required.getCode() + "」栏（layoutId=" + templateId
                                + "），无法生成目录数据页布局");
            }
        }
    }

    /** 当前租户尚无模版时写入「通用台账」模版：区域清单 + 栏行（与常用台账模板一致） */
    private Long createDefaultLedgerTemplate() {
        DmWorkbenchLayoutDO header = new DmWorkbenchLayoutDO();
        header.setName(DmWorkbenchLayoutNames.DEFAULT_LEDGER_TEMPLATE);
        header.setIsTemplate(true);
        header.setSourceTemplateId(null);
        header.setSettingsJson(DmDataTabLayoutBootstrapMeta.settingsWithLedgerSections());
        dmWorkbenchLayoutMapper.insert(header);
        Long layoutId = header.getId();

        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.CATEGORY.getCode(), "category-1", true,
                DmDataTabLayoutBootstrapMeta.categoryMeta(
                        "分类", null, "category", DmDataTabLayoutBootstrapMeta.SECTION_ID_A));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.MODEL.getCode(), null, true,
                DmDataTabLayoutBootstrapMeta.modelMeta(DmDataTabLayoutBootstrapMeta.SECTION_ID_A, false));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.ENTITY.getCode(), null, true,
                DmDataTabLayoutBootstrapMeta.entityMeta(DmDataTabLayoutBootstrapMeta.SECTION_ID_B, true));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.DETAIL.getCode(), "tab-detail-1", true,
                DmDataTabLayoutBootstrapMeta.detailMeta(DmDataTabLayoutBootstrapMeta.SECTION_ID_C, "区段C栏"));
        return layoutId;
    }

    private void insertTemplateRow(Long layoutId, String kind, String tabId, boolean enabled,
                                   Map<String, Object> meta) {
        DmDataTabLayoutDO row = new DmDataTabLayoutDO();
        row.setLayoutId(layoutId);
        row.setEntityTypeCode(null);
        row.setColumnKind(kind);
        row.setTabId(tabId);
        row.setEnabled(enabled);
        row.setColumnMeta(meta == null ? null : new LinkedHashMap<>(meta));
        dmDataTabLayoutMapper.insert(row);
    }

    private void applyCategoryAsEntityEnables(DmDataTabLayoutDO row) {
        String kind = row.getColumnKind();
        if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
            row.setEnabled(true);
        } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                || DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
            row.setEnabled(false);
        } else if (DmDataTabLayoutKindEnum.DETAIL.getCode().equals(kind)) {
            row.setEnabled(true);
        }
    }

    private Map<String, Object> copyMeta(Map<String, Object> src) {
        if (src == null) {
            return null;
        }
        return new LinkedHashMap<>(src);
    }

    private DmWorkbenchLayoutSettingsRespVO toSettingsVo(Map<String, Object> settings) {
        List<Map<String, Object>> sections = readSections(settings);
        DmWorkbenchLayoutSettingsRespVO vo = new DmWorkbenchLayoutSettingsRespVO();
        vo.setSections(toSectionVos(sections));
        vo.setSectionHidden(readSectionHidden(settings, sectionIds(sections)));
        return vo;
    }

    private static List<DmLayoutSectionVO> toSectionVos(List<Map<String, Object>> sections) {
        List<DmLayoutSectionVO> vos = new ArrayList<>();
        for (Map<String, Object> row : sections) {
            DmLayoutSectionVO vo = new DmLayoutSectionVO();
            vo.setId(String.valueOf(row.get("id")));
            vo.setName(String.valueOf(row.get("name")));
            Object arrange = row.get("arrange");
            if (arrange != null) {
                vo.setArrange(String.valueOf(arrange));
            }
            vos.add(vo);
        }
        return vos;
    }

    static List<Map<String, Object>> readSections(Map<String, Object> settings) {
        if (settings == null || !(settings.get("sections") instanceof List<?> rawList)) {
            return List.of();
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (Object item : rawList) {
            if (!(item instanceof Map<?, ?> map)) {
                continue;
            }
            Object idObj = map.get("id");
            String id = idObj == null ? "" : String.valueOf(idObj).trim();
            if (!StringUtils.hasText(id)) {
                continue;
            }
            Object nameObj = map.get("name");
            String name = nameObj == null ? "" : String.valueOf(nameObj).trim();
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", id);
            row.put("name", StringUtils.hasText(name) ? name : id);
            String arrange = readArrange(map.get("arrange"));
            if (arrange != null) {
                row.put("arrange", arrange);
            }
            out.add(row);
        }
        return out;
    }

    static Set<String> sectionIds(List<Map<String, Object>> sections) {
        Set<String> ids = new LinkedHashSet<>();
        for (Map<String, Object> row : sections) {
            ids.add(String.valueOf(row.get("id")));
        }
        return ids;
    }

    /**
     * 只认创建/迁移已写入的摆法。缺字段或非法值原样丢掉，读路径不猜水平并排或自由摆放。
     */
    static String readArrange(Object raw) {
        if (raw == null) {
            return null;
        }
        String arrange = String.valueOf(raw).trim().toLowerCase();
        if (DmDataTabLayoutBootstrapMeta.ARRANGE_HORIZONTAL.equals(arrange)
                || DmDataTabLayoutBootstrapMeta.ARRANGE_FREE.equals(arrange)) {
            return arrange;
        }
        return null;
    }

    private static String requireSectionIdInTemplateRow(
            Map<String, Object> meta, String kind, Set<String> allowedSectionIds) {
        if (meta == null || !metaHasText(meta, "columnSection")) {
            throw new ServiceException(500,
                    "布局模版栏缺少 columnSection（kind=" + kind + "），无法实例化");
        }
        String sectionId = String.valueOf(meta.get("columnSection")).trim();
        if (!allowedSectionIds.contains(sectionId)) {
            throw new ServiceException(500,
                    "布局模版栏的 columnSection 不在区域清单中（kind=" + kind + ", section=" + sectionId + "）");
        }
        return sectionId;
    }

    private static String allocateTabId(String kindSlug, Set<String> usedTabIds) {
        String slug = StringUtils.hasText(kindSlug) ? kindSlug.trim().toLowerCase() : "column";
        int seq = 1;
        while (true) {
            String candidate = "tab-" + slug + "-" + seq;
            if (!usedTabIds.contains(candidate)) {
                usedTabIds.add(candidate);
                return candidate;
            }
            seq++;
        }
    }

    private Map<String, Boolean> normalizeSectionHidden(
            Map<String, Boolean> input,
            Set<String> allowedIds) {
        if (input == null || input.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Boolean> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : input.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey().trim();
            if (!StringUtils.hasText(key) || !allowedIds.contains(key)) {
                throw new ServiceException(400, "无效的区域隐藏键：" + entry.getKey());
            }
            if (Boolean.TRUE.equals(entry.getValue())) {
                normalized.put(key, true);
            }
        }
        return normalized;
    }

    private Map<String, Boolean> readSectionHidden(Map<String, Object> settings, Set<String> allowedIds) {
        if (settings == null || !(settings.get("sectionHidden") instanceof Map<?, ?> raw)) {
            return new LinkedHashMap<>();
        }
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (String key : allowedIds) {
            if (Boolean.TRUE.equals(raw.get(key))) {
                result.put(key, true);
            }
        }
        return result;
    }
}
