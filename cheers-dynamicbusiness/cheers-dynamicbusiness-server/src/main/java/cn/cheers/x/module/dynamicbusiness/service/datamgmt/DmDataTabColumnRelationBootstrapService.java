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
 * 栏间关系<strong>默认写出</strong>（仅写路径 · 只 insert）。
 * <p>
 * ## 本类负责什么
 * 在下列写时机补缺的同类型「条件筛选」边（分类→型号、型号→实体）：
 * <ul>
 *   <li>实例化该数据目录的页面布局后</li>
 *   <li>保存布局（加栏等）后，由 {@link DmDataTabLayoutServiceImpl#saveLayouts} 调用</li>
 * </ul>
 * <p>
 * ## 本类明确不负责什么（禁止再加回来）
 * <ul>
 *   <li><b>不删边</b>：删边只有两种标准触发，见 {@link DmDataTabColumnRelationService}</li>
 *   <li>不写 write（修改关联）边：一律由数据关系图手配</li>
 *   <li>不在读列表接口里猜边、补边</li>
 * </ul>
 * 连接策略（与《关联实现》一致）：仅自动写同类型 filter；异类型与全部 write 不自动写。
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
     * 实例化页面布局后调用：只 insert 缺的默认 filter 边，不覆盖、不删已有行。
     *
     * @param registryEntityTypeCode 目录注册编码（写入 relation.entity_type_code）
     * @param storageBaseTypeCode    底座类型编码（保留入参；本方法不用其自动写异类型边）
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

        // 同类型 型号→实体（台账链）
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
