package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static cn.cheers.x.module.dynamicbusiness.service.datamgmt.ColumnRelationLayoutEndpoints.ColumnEndpoint;
import static cn.cheers.x.module.dynamicbusiness.service.datamgmt.ColumnRelationLayoutEndpoints.LayoutEndpoints;
import static cn.cheers.x.module.dynamicbusiness.service.datamgmt.ColumnRelationLayoutEndpoints.fromLayoutRows;
import static cn.cheers.x.module.dynamicbusiness.service.datamgmt.ColumnRelationLayoutEndpoints.pairKey;
import static cn.cheers.x.module.dynamicbusiness.service.datamgmt.ColumnRelationLayoutEndpoints.sameTypeCode;

/**
 * 栏间关系写出（仅写路径）：实例化页面布局时补默认「条件筛选」边；布局保存后清理孤儿边。
 * <p>
 * ## 职责边界
 * <ul>
 *   <li>本类只写 <b>filter（条件筛选）</b>：点上游收窄下游，供查数消费。</li>
 *   <li><b>write（修改关联）</b>（勾选/拖入）一律不自动写，由数据关系图「修改关联」按钮手配。</li>
 * </ul>
 * 连接策略（与《关联实现》一致）：
 * <ul>
 *   <li>仅自动写同类型 filter：分类→型号、型号→实体</li>
 *   <li>不自动写：任何 write；任何异类型边；同类型分类→实体；分类→分类</li>
 * </ul>
 * 禁止在读列表接口调用；不删用户已在数据关系图保存的边。
 * 关系图加栏后若需补齐同类型 filter，应再次调用 {@link #applyInitialDefaultRelations}（或等价写路径），勿在读路径猜边。
 */
@Service
public class DmDataTabColumnRelationBootstrapService {

    @Resource
    private DmDataTabColumnRelationMapper dmDataTabColumnRelationMapper;

    @Resource
    private DmDataTabLayoutMapper dmDataTabLayoutMapper;

    @Resource
    private DmWorkbenchLayoutService dmWorkbenchLayoutService;

    /**
     * 实例化该数据目录的页面布局后调用：只 insert 缺的默认 filter 边，不覆盖、不删已有行。
     *
     * @param registryEntityTypeCode 目录注册编码（写入 relation.entity_type_code）
     * @param storageBaseTypeCode    底座类型编码（保留入参供调用方；本方法不再用其自动写异类型边）
     */
    @Transactional(rollbackFor = Exception.class)
    @SuppressWarnings("unused")
    public void applyInitialDefaultRelations(
            Long layoutId, String registryEntityTypeCode, String storageBaseTypeCode) {
        if (layoutId == null) {
            return;
        }
        dmWorkbenchLayoutService.requireLayout(layoutId);
        List<DmDataTabLayoutDO> layouts = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        if (layouts.isEmpty()) {
            return;
        }

        String registryCode =
                StringUtils.hasText(registryEntityTypeCode) ? registryEntityTypeCode.trim() : null;

        LayoutEndpoints parsed = fromLayoutRows(layouts);
        Set<String> existingPairs = loadExistingFilterPairKeys(layoutId);

        List<DmDataTabColumnRelationDO> toInsert = new ArrayList<>();

        // 同类型 分类→型号
        for (ColumnEndpoint category : parsed.categories()) {
            for (ColumnEndpoint model : parsed.models()) {
                if (!sameTypeCode(category.typeCode(), model.typeCode())) {
                    continue;
                }
                if (existingPairs.contains(pairKey(category.identity(), model.identity(), "filter"))) {
                    continue;
                }
                toInsert.add(newFilterRelation(
                        layoutId, registryCode, category, model, "CATEGORY_MODEL"));
                existingPairs.add(pairKey(category.identity(), model.identity(), "filter"));
            }
        }

        // 同类型 型号→实体（台账链）；异类型适用只走用户配置的 write
        for (ColumnEndpoint model : parsed.models()) {
            for (ColumnEndpoint entity : parsed.entities()) {
                if (!sameTypeCode(model.typeCode(), entity.typeCode())) {
                    continue;
                }
                if (existingPairs.contains(pairKey(model.identity(), entity.identity(), "filter"))) {
                    continue;
                }
                toInsert.add(newFilterRelation(
                        layoutId, registryCode, model, entity, "MODEL_ENTITY"));
                existingPairs.add(pairKey(model.identity(), entity.identity(), "filter"));
            }
        }

        for (DmDataTabColumnRelationDO row : toInsert) {
            dmDataTabColumnRelationMapper.insert(row);
        }
    }

    /**
     * 布局行删除后：去掉两端列身份已不存在的栏间关系。仅在保存布局时调用。
     */
    @Transactional(rollbackFor = Exception.class)
    public void pruneOrphanColumnRelations(Long layoutId) {
        if (layoutId == null) {
            return;
        }
        List<DmDataTabLayoutDO> layouts = dmDataTabLayoutMapper.selectListByLayoutId(layoutId);
        LayoutEndpoints parsed = fromLayoutRows(layouts);
        for (DmDataTabColumnRelationDO row :
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            String from = row.getFromColumnIdentity() == null ? "" : row.getFromColumnIdentity().trim();
            String to = row.getToColumnIdentity() == null ? "" : row.getToColumnIdentity().trim();
            if (!parsed.validIdentities().contains(from) || !parsed.validIdentities().contains(to)) {
                dmDataTabColumnRelationMapper.deleteById(row.getId());
            }
        }
    }

    private Set<String> loadExistingFilterPairKeys(Long layoutId) {
        Set<String> existingPairs = new HashSet<>();
        for (DmDataTabColumnRelationDO row :
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            String from = row.getFromColumnIdentity() == null ? "" : row.getFromColumnIdentity().trim();
            String to = row.getToColumnIdentity() == null ? "" : row.getToColumnIdentity().trim();
            existingPairs.add(pairKey(from, to, edgeRoleFromMeta(row.getRelationMeta())));
        }
        return existingPairs;
    }

    private static String edgeRoleFromMeta(Map<String, Object> meta) {
        if (meta == null) {
            return "filter";
        }
        Object role = meta.get("edgeRole");
        if (role != null) {
            String text = String.valueOf(role).trim();
            if ("write".equals(text) || "filter".equals(text)) {
                return text;
            }
        }
        return "filter";
    }

    private static DmDataTabColumnRelationDO newFilterRelation(
            Long layoutId,
            String registryEntityTypeCode,
            ColumnEndpoint from,
            ColumnEndpoint to,
            String relationKind) {
        DmDataTabColumnRelationDO row = new DmDataTabColumnRelationDO();
        row.setLayoutId(layoutId);
        row.setEntityTypeCode(registryEntityTypeCode);
        row.setEdgeId("edge-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12));
        row.setFromColumnIdentity(from.identity());
        row.setToColumnIdentity(to.identity());
        row.setRelationKind(relationKind);
        row.setFromTypeCode(from.typeCode());
        row.setToTypeCode(to.typeCode());
        Map<String, Object> meta = new LinkedHashMap<>();
        meta.put("edgeRole", "filter");
        meta.put("enabledInteractions", List.of());
        row.setRelationMeta(meta);
        return row;
    }
}
