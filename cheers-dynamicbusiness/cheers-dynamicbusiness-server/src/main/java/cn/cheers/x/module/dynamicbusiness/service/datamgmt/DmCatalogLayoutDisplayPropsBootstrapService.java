package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.module.dynamicbusiness.enums.datamgmt.DmDataTabLayoutKindEnum;
import cn.cheers.x.module.platformresource.api.component.ComponentPropsApi;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentDataSourceDTO;
import cn.cheers.x.module.platformresource.api.component.dto.ComponentPropsCreateTemplateReqDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据页布局实例化后：为启用中的分类/型号/实体栏创建默认展示配置并回填 propsId。
 * <p>
 * <b>管什么</b>：创建目录时一次写好各栏 propsId（树/列表组件模板 + dataSource）。<br>
 * <b>不管什么</b>：栏身份类型码、筛选连线、打开页现造配置、模型管理中间表列表。<br>
 * <b>禁止</b>：缺栏身份仍建 props；前端创建后再补权威 propsId。
 * <p>
 * 默认 props 内容与前端 create 时一致：分类 tree、型号/实体 list 嵌入列壳层 + displayContent=[name]。
 */
@Service
public class DmCatalogLayoutDisplayPropsBootstrapService {

    @Resource
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;

    @Resource
    private ComponentPropsApi componentPropsApi;

    /**
     * 对布局实例中启用且尚无 propsId 的分类/型号/实体栏创建展示配置并回写。
     *
     * @param layoutId     工作台布局实例 id
     * @param registryCode 目录注册编码（模板命名隔离）
     * @param storageCode  底座/同类类型编码（dataSource 取数）
     * @throws ServiceException 缺栏身份或远程创建失败时回滚创建
     */
    public void bindDefaultDisplayProps(Long layoutId, String registryCode, String storageCode) {
        if (layoutId == null || layoutId <= 0) {
            throw new ServiceException(500, "自动创建布局失败：缺少 layoutId，无法绑定展示配置");
        }
        String registry = StringUtils.hasText(registryCode) ? registryCode.trim() : "";
        String storage = StringUtils.hasText(storageCode) ? storageCode.trim() : "";
        if (!StringUtils.hasText(registry) || !StringUtils.hasText(storage)) {
            throw new ServiceException(500, "自动创建布局失败：缺少目录/底座编码，无法绑定展示配置");
        }

        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        for (DmDataTabLayoutDO row : rows) {
            if (Boolean.FALSE.equals(row.getEnabled())) {
                continue;
            }
            if (row.getPropsId() != null && row.getPropsId() > 0) {
                continue;
            }
            String kind = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
            Map<String, Object> meta = row.getColumnMeta();
            Long propsId;
            if (DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)) {
                String categoryTypeCode = metaText(meta, "categoryTypeCode");
                if (!StringUtils.hasText(categoryTypeCode)) {
                    throw new ServiceException(500, "自动创建布局失败：分类栏缺少 categoryTypeCode，无法创建展示配置");
                }
                String label = metaText(meta, "label");
                propsId = createCategoryTreeProps(registry, categoryTypeCode, label);
            } else if (DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)) {
                String modelCode = metaText(meta, "modelEntityTypeCode");
                if (!StringUtils.hasText(modelCode)) {
                    throw new ServiceException(500, "自动创建布局失败：型号栏缺少 modelEntityTypeCode，无法创建展示配置");
                }
                String label = metaText(meta, "label");
                propsId = createListProps(registry, modelCode, label, true);
            } else if (DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
                String entityCode = metaText(meta, "entityEntityTypeCode");
                if (!StringUtils.hasText(entityCode)) {
                    throw new ServiceException(500, "自动创建布局失败：实体栏缺少 entityEntityTypeCode，无法创建展示配置");
                }
                String label = metaText(meta, "label");
                propsId = createListProps(registry, entityCode, label, false);
            } else {
                continue;
            }
            DmDataTabLayoutDO update = new DmDataTabLayoutDO();
            update.setId(row.getId());
            update.setPropsId(propsId);
            dmDataTabLayoutMapper.updateById(update);
        }
        assertEnabledIdentityColumnsHaveProps(layoutId);
    }

    private void assertEnabledIdentityColumnsHaveProps(Long layoutId) {
        List<DmDataTabLayoutDO> rows = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        for (DmDataTabLayoutDO row : rows) {
            if (Boolean.FALSE.equals(row.getEnabled())) {
                continue;
            }
            String kind = row.getColumnKind() == null ? "" : row.getColumnKind().trim().toUpperCase();
            if (!DmDataTabLayoutKindEnum.CATEGORY.getCode().equals(kind)
                    && !DmDataTabLayoutKindEnum.MODEL.getCode().equals(kind)
                    && !DmDataTabLayoutKindEnum.ENTITY.getCode().equals(kind)) {
                continue;
            }
            if (row.getPropsId() == null || row.getPropsId() <= 0) {
                throw new ServiceException(500,
                        "自动创建布局失败：" + kind + " 栏未绑定展示配置 propsId");
            }
        }
    }

    private Long createCategoryTreeProps(String registry, String categoryTypeCode, String label) {
        String name = StringUtils.hasText(label)
                ? label.trim() + "-" + categoryTypeCode
                : "分类树-" + registry + "-" + categoryTypeCode;
        ComponentPropsCreateTemplateReqDTO req = new ComponentPropsCreateTemplateReqDTO();
        req.setComponentCode("tree");
        req.setSchemaVersion("tree@1");
        req.setName(name);
        req.setProps(defaultCategoryTreeProps());
        req.setDataSource(dataSource("category", categoryTypeCode, "entity"));
        return createTemplateOrThrow(req, "分类树");
    }

    private Long createListProps(String registry, String listTypeCode, String label, boolean model) {
        String name;
        if (StringUtils.hasText(label) && StringUtils.hasText(listTypeCode)) {
            name = label.trim() + "-" + listTypeCode.trim();
        } else if (StringUtils.hasText(label)) {
            name = label.trim();
        } else {
            name = (model ? "模型列表-" : "实体列表-") + registry + "-" + listTypeCode;
        }
        ComponentPropsCreateTemplateReqDTO req = new ComponentPropsCreateTemplateReqDTO();
        req.setComponentCode("list");
        req.setSchemaVersion("list@1");
        req.setName(name);
        req.setProps(defaultColumnListProps(model ? "暂无模型" : "暂无实体"));
        req.setDataSource(dataSource("dynamic", listTypeCode, model ? "model" : "entity"));
        return createTemplateOrThrow(req, model ? "型号列表" : "实体列表");
    }

    private Long createTemplateOrThrow(ComponentPropsCreateTemplateReqDTO req, String what) {
        try {
            Long id = componentPropsApi.createTemplate(req).getCheckedData();
            if (id == null || id <= 0) {
                throw new ServiceException(500, "自动创建布局失败：创建" + what + "展示配置未返回 propsId");
            }
            return id;
        } catch (ServiceException ex) {
            throw ex;
        } catch (Exception ex) {
            String detail = ex.getMessage() == null ? "未知错误" : ex.getMessage();
            throw new ServiceException(500, "自动创建布局失败：创建" + what + "展示配置失败（" + detail + "）");
        }
    }

    /**
     * 与前端 buildCategoryTreeUiDefaults 对齐的可落库默认值。
     */
    private static Map<String, Object> defaultCategoryTreeProps() {
        Map<String, Object> search = new LinkedHashMap<>();
        search.put("showSearch", true);
        search.put("searchPlaceholder", "搜索分类");
        search.put("searchScopeSelectedOptions", List.of());
        search.put("searchScopeLabelByKey", Map.of("name", "名称"));

        Map<String, Object> expand = new LinkedHashMap<>();
        expand.put("expandAll", false);
        expand.put("expandLevel", 1);

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("selectMode", "single");
        props.put("categoryAssociation", "MULTI");
        props.put("search", search);
        props.put("autoCrud", true);
        props.put("draggable", true);
        props.put("defaultExpandAll", expand);
        props.put("autoLoad", true);
        props.put("emptyText", "暂无分类");
        props.put("displayContent", List.of("name"));
        return props;
    }

    /**
     * 与前端 buildColumnListProps（嵌入列）+ displayContent=[name] 对齐。
     */
    private static Map<String, Object> defaultColumnListProps(String emptyText) {
        Map<String, Object> search = new LinkedHashMap<>();
        search.put("showSearch", true);
        search.put("searchPlaceholder", "搜索…");

        Map<String, Object> title = new LinkedHashMap<>();
        title.put("showTitle", false);

        Map<String, Object> props = new LinkedHashMap<>();
        props.put("emptyText", emptyText);
        props.put("search", search);
        props.put("className", "h-full min-h-0 flex-1 border-0 bg-transparent shadow-none");
        props.put("toolbarClassName", "gap-1 border-b border-white/8 bg-transparent px-1.5 py-1");
        props.put("tableClassName", "min-h-0 flex-1");
        props.put("showDataSourceEndpoint", false);
        props.put("selectedPanel", false);
        props.put("selectedPanelDetail", false);
        props.put("toolbarDensity", "compact");
        props.put("title", title);
        props.put("showSelectModeBadge", false);
        props.put("displayContent", List.of("name"));
        return props;
    }

    private static ComponentDataSourceDTO dataSource(String businessCategory, String entityTypeCode, String dataKind) {
        ComponentDataSourceDTO dto = new ComponentDataSourceDTO();
        dto.setBusinessCategory(businessCategory);
        dto.setEntityTypeCode(entityTypeCode);
        dto.setDataKind(dataKind);
        return dto;
    }

    private static String metaText(Map<String, Object> meta, String key) {
        if (meta == null || !StringUtils.hasText(key)) {
            return "";
        }
        Object value = meta.get(key);
        return value == null ? "" : String.valueOf(value).trim();
    }
}
