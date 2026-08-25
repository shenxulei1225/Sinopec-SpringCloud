package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
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

/**
 * 工作台布局：模版列表、从模版生成实例、解析页面/目录上的 layoutId。
 * <p>
 * 权威：创建目录时从此处实例化布局行（含 columnMeta 栏身份与显示名）。
 * 显示名用底座类型 / 分类种类的中文名，不用类型编码冒充 label。
 * 不负责：组件配置 props（前端按栏身份同步）。
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
     * 从模版复制栏行并写入本页栏身份。
     * 模型/实体类型码与 label：底座（categoryTypeCode / 存储类型）的编码与中文名。
     * 分类栏：种类码 + 分类种类中文名（无则退回底座中文名）。
     * 单栏允许空 tabId。不写 propsId。
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

        DmWorkbenchLayoutDO instance = new DmWorkbenchLayoutDO();
        instance.setName(instanceName);
        instance.setIsTemplate(false);
        instance.setSourceTemplateId(templateId);
        dmWorkbenchLayoutMapper.insert(instance);

        String code = StringUtils.hasText(entityTypeCode) ? entityTypeCode.trim() : null;
        String tabId = code != null ? code + "-default" : "default";
        String storageCode = StringUtils.hasText(categoryTypeCode) ? categoryTypeCode.trim() : code;
        String storageDisplayName = resolveEntityTypeDisplayName(storageCode);
        String categoryDisplayName = resolveCategoryTypeDisplayName(storageCode);
        if (!StringUtils.hasText(categoryDisplayName)) {
            categoryDisplayName = storageDisplayName;
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

            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(src.getColumnKind())) {
                if (!StringUtils.hasText(row.getTabId())) {
                    row.setTabId(tabId);
                }
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                if (code != null) {
                    if (storageCode != null) {
                        meta.put("categoryTypeCode", storageCode);
                    }
                    // 显示名用分类种类中文名；模版里的「分类」等占位一律覆盖
                    meta.put("label", categoryDisplayName);
                    meta.put("columnKey", row.getTabId());
                }
                row.setColumnMeta(meta);
            } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(src.getColumnKind())
                    && code != null) {
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                String typeCode = storageCode != null ? storageCode : code;
                meta.put("modelEntityTypeCode", typeCode);
                meta.put("label", storageDisplayName);
                row.setColumnMeta(meta);
            } else if (DmDataTabLayoutKindEnum.ENTITY.getCode().equals(src.getColumnKind())
                    && code != null) {
                Map<String, Object> meta = row.getColumnMeta() != null
                        ? new LinkedHashMap<>(row.getColumnMeta())
                        : new LinkedHashMap<>();
                String typeCode = storageCode != null ? storageCode : code;
                meta.put("entityEntityTypeCode", typeCode);
                meta.put("label", storageDisplayName);
                row.setColumnMeta(meta);
            }

            if (categoryAsEntityLayout) {
                applyCategoryAsEntityEnables(row);
            }
            dmDataTabLayoutMapper.insert(row);
        }
        dmDataTabColumnRelationBootstrapService.applyInitialDefaultRelations(
                instance.getId(), code, storageCode);
        return instance.getId();
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
        dmWorkbenchLayoutMapper.insert(header);
        Long layoutId = header.getId();

        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.CATEGORY.getCode(), "default", true,
                Map.of(
                        "label", "分类",
                        "columnKey", "default",
                        "columnSection", DmDataTabLayoutBootstrapMeta.SECTION_FILTER,
                        "widthPx", DmDataTabLayoutBootstrapMeta.CATEGORY_COLUMN_WIDTH_PX,
                        "sectionWidthPx", DmDataTabLayoutBootstrapMeta.FILTER_SECTION_WIDTH_PX
                ));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.MODEL.getCode(), null, true,
                DmDataTabLayoutBootstrapMeta.modelMeta(true));
        insertTemplateRow(layoutId, DmDataTabLayoutKindEnum.ENTITY.getCode(), null, true,
                DmDataTabLayoutBootstrapMeta.entityMeta(false));
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
}
