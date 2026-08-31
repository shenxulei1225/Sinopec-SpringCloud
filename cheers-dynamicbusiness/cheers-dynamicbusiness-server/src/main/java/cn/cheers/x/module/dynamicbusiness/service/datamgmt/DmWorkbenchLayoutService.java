package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
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

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 工作台布局：模版列表、从模版生成实例、解析页面/目录上的 layoutId。
 * <p>
 * 权威：创建 NATIVE / SCOPE / DOMAIN / REUSE 目录时从此处实例化布局行——
 * 一次写好同类分类/型号/实体栏及类型码与区段，禁止半截空壳。
 * 显示名用底座类型 / 分类种类 / 本目录中文名，不用类型编码冒充 label。
 * 创建时同步写好分类/型号/实体栏默认展示配置 propsId（经资源服务）；不负责打开页现造 props。
 */
@Service
public class DmWorkbenchLayoutService {

    private static final Set<String> SECTION_KEYS = Set.of("FILTER", "OBJECT", "WHAT");

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

    /** 读取布局头设置；缺省 JSON 或缺键均返回未隐藏语义。 */
    public DmWorkbenchLayoutSettingsRespVO getSettings(Long layoutId) {
        DmWorkbenchLayoutSettingsRespVO vo = new DmWorkbenchLayoutSettingsRespVO();
        vo.setSectionHidden(readSectionHidden(requireLayout(layoutId).getSettingsJson()));
        return vo;
    }

    /**
     * 只替换布局头的 sectionHidden，保留 settingsJson 中其它设置。
     * 区段隐藏权威只在此写入；栏行 enabled 与浏览器折叠状态不得补写该字段。
     */
    @Transactional(rollbackFor = Exception.class)
    public DmWorkbenchLayoutSettingsRespVO updateSettings(
            Long layoutId,
            DmWorkbenchLayoutSettingsUpdateReqVO reqVO) {
        DmWorkbenchLayoutDO layout = requireLayout(layoutId);
        Map<String, Object> settings = layout.getSettingsJson() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(layout.getSettingsJson());
        Map<String, Boolean> sectionHidden = normalizeSectionHidden(reqVO.getSectionHidden());
        settings.put("sectionHidden", sectionHidden);
        DmWorkbenchLayoutDO update = new DmWorkbenchLayoutDO();
        update.setId(layoutId);
        update.setSettingsJson(settings);
        dmWorkbenchLayoutMapper.updateById(update);

        DmWorkbenchLayoutSettingsRespVO vo = new DmWorkbenchLayoutSettingsRespVO();
        vo.setSectionHidden(sectionHidden);
        return vo;
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
     * 区段：分类、型号 → 筛选；实体 → Who/对象。即使模版旧数据区段不对，实例化时也会盖章。
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
        if (!StringUtils.hasText(entityTypeCode)) {
            throw new ServiceException(400, "实例化布局必须提供目录注册编码");
        }

        DmWorkbenchLayoutDO instance = new DmWorkbenchLayoutDO();
        instance.setName(instanceName);
        instance.setIsTemplate(false);
        instance.setSourceTemplateId(templateId);
        instance.setSettingsJson(template.getSettingsJson() == null
                ? new LinkedHashMap<>()
                : new LinkedHashMap<>(template.getSettingsJson()));
        dmWorkbenchLayoutMapper.insert(instance);

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

        for (DmDataTabLayoutDO src : templateRows) {
            DmDataTabLayoutDO row = new DmDataTabLayoutDO();
            row.setLayoutId(instance.getId());
            row.setEntityTypeCode(code);
            row.setColumnKind(src.getColumnKind());
            row.setTabId(src.getTabId());
            row.setPropsId(null);
            row.setEnabled(src.getEnabled());
            row.setColumnMeta(copyMeta(src.getColumnMeta()));

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
                meta.put("columnSection", DmDataTabLayoutBootstrapMeta.SECTION_FILTER);
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
                meta.put("columnSection", DmDataTabLayoutBootstrapMeta.SECTION_FILTER);
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
                meta.put("columnSection", DmDataTabLayoutBootstrapMeta.SECTION_OBJECT);
                if (!(meta.get("widthPx") instanceof Number)) {
                    meta.put("widthPx", DmDataTabLayoutBootstrapMeta.ENTITY_COLUMN_WIDTH_PX);
                }
                if (!(meta.get("sectionWidthPx") instanceof Number)) {
                    meta.put("sectionWidthPx", DmDataTabLayoutBootstrapMeta.OBJECT_SECTION_WIDTH_PX);
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
            if (!all.isEmpty()) {
                return all.get(0).getId();
            }
            return createDefaultLedgerTemplate();
        }
        return tpl.getId();
    }

    /** 当前租户尚无模版时写入「通用台账」模版栏行（与 Flyway V66 种子一致） */
    private Long createDefaultLedgerTemplate() {
        DmWorkbenchLayoutDO header = new DmWorkbenchLayoutDO();
        header.setName(DmWorkbenchLayoutNames.DEFAULT_LEDGER_TEMPLATE);
        header.setIsTemplate(true);
        header.setSourceTemplateId(null);
        header.setSettingsJson(new LinkedHashMap<>());
        dmWorkbenchLayoutMapper.insert(header);
        Long layoutId = header.getId();

        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.CATEGORY.getCode(), "category-1", true,
                Map.of(
                        "label", "分类",
                        "columnKey", "category",
                        "columnSection", DmDataTabLayoutBootstrapMeta.SECTION_FILTER,
                        "widthPx", DmDataTabLayoutBootstrapMeta.CATEGORY_COLUMN_WIDTH_PX,
                        "sectionWidthPx", DmDataTabLayoutBootstrapMeta.FILTER_SECTION_WIDTH_PX
                ));
        // 型号进筛选（不领头）；实体进 Who 并领头写区段宽
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.MODEL.getCode(), null, true,
                DmDataTabLayoutBootstrapMeta.modelMeta(false));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.ENTITY.getCode(), null, true,
                DmDataTabLayoutBootstrapMeta.entityMeta(true));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.DETAIL.getCode(), null, false, null);
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

    private Map<String, Boolean> normalizeSectionHidden(Map<String, Boolean> input) {
        if (input == null || input.isEmpty()) {
            return new LinkedHashMap<>();
        }
        Map<String, Boolean> normalized = new LinkedHashMap<>();
        for (Map.Entry<String, Boolean> entry : input.entrySet()) {
            String key = entry.getKey() == null ? "" : entry.getKey().trim().toUpperCase();
            if (!SECTION_KEYS.contains(key)) {
                throw new ServiceException(400, "无效的区段隐藏键：" + entry.getKey());
            }
            normalized.put(key, Boolean.TRUE.equals(entry.getValue()));
        }
        return normalized;
    }

    private Map<String, Boolean> readSectionHidden(Map<String, Object> settings) {
        if (settings == null || !(settings.get("sectionHidden") instanceof Map<?, ?> raw)) {
            return new LinkedHashMap<>();
        }
        Map<String, Boolean> result = new LinkedHashMap<>();
        for (String key : SECTION_KEYS) {
            if (Boolean.TRUE.equals(raw.get(key))) {
                result.put(key, true);
            }
        }
        return result;
    }
}
