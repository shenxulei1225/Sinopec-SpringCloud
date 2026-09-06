package cn.cheers.x.module.dynamicbusiness.service.datamgmt;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabColumnRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datamgmt.DmDataTabLayoutDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabColumnRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datamgmt.DmDataTabLayoutMapper;
import cn.cheers.x.framework.common.exception.ServiceException;
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
 * 在下列写时机补缺的同类型台账链边：
 * <ul>
 *   <li>条件筛选 filter：分类→型号、型号→实体（查数上游→下游）</li>
 *   <li>修改关联 write：型号→分类、实体→分类（列表挂到分类树；默认仅拖入）</li>
 * </ul>
 * filter 与 write <b>方向不同</b>：筛选跟台账链；挂接从型号/实例指回分类。
 * 触发：实例化该数据目录的页面布局后；保存布局（加栏等）后由
 * {@link DmDataTabLayoutServiceImpl#saveLayouts} 调用。
 * <p>
 * ## 本类明确不负责什么
 * <ul>
 *   <li><b>不删边</b>：删边只有两种标准触发，见 {@link DmDataTabColumnRelationService}</li>
 *   <li>不写异类型边：外类型 / 跨类型一律关系图手配</li>
 *   <li>不自动写型号↔实体的 write（挂接分类靠「型号/实体→分类」）</li>
 *   <li>不在读列表接口里猜边、补边</li>
 * </ul>
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
     * 实例化 / 保存布局后：只 insert 缺的默认 filter + write 边，不覆盖、不删已有行。
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
        Set<String> existingPairs = loadExistingPairKeys(layoutId);

        List<DmDataTabColumnRelationDO> toInsert = new ArrayList<>();

        // filter：分类→型号；write：型号→分类
        for (ColumnEndpoint category : parsed.categories()) {
            for (ColumnEndpoint model : parsed.models()) {
                if (!sameTypeCode(category.typeCode(), model.typeCode())) {
                    continue;
                }
                appendIfAbsent(
                        toInsert,
                        existingPairs,
                        layoutId,
                        registryCode,
                        category,
                        model,
                        "CATEGORY_MODEL",
                        "filter",
                        List.of());
                appendIfAbsent(
                        toInsert,
                        existingPairs,
                        layoutId,
                        registryCode,
                        model,
                        category,
                        "CATEGORY_MODEL",
                        "write",
                        List.of("dragAssociate"));
            }
        }

        // filter：型号→实体（无对应默认 write）
        for (ColumnEndpoint model : parsed.models()) {
            for (ColumnEndpoint entity : parsed.entities()) {
                if (!sameTypeCode(model.typeCode(), entity.typeCode())) {
                    continue;
                }
                appendIfAbsent(
                        toInsert,
                        existingPairs,
                        layoutId,
                        registryCode,
                        model,
                        entity,
                        "MODEL_ENTITY",
                        "filter",
                        List.of());
            }
        }

        // write：实体→分类
        for (ColumnEndpoint category : parsed.categories()) {
            for (ColumnEndpoint entity : parsed.entities()) {
                if (!sameTypeCode(category.typeCode(), entity.typeCode())) {
                    continue;
                }
                appendIfAbsent(
                        toInsert,
                        existingPairs,
                        layoutId,
                        registryCode,
                        entity,
                        category,
                        "CATEGORY_ENTITY",
                        "write",
                        List.of("dragAssociate"));
            }
        }

        // 详情跟随关系由关系图显式配置；这里不做默认猜测连线。

        for (DmDataTabColumnRelationDO row : toInsert) {
            dmDataTabColumnRelationMapper.insert(row);
        }
    }

    /**
     * 缺则 insert 一条边（已存在同 from/to/用途则跳过）。
     * write 默认仅拖入，与关系图新建修改关联边一致。
     */
    private static void appendIfAbsent(
            List<DmDataTabColumnRelationDO> toInsert,
            Set<String> existingPairs,
            Long layoutId,
            String registryCode,
            ColumnEndpoint from,
            ColumnEndpoint to,
            String relationKind,
            String edgeAction,
            List<String> enabledInteractions) {
        String key = pairKey(from.identity(), to.identity(), edgeAction);
        if (existingPairs.contains(key)) {
            return;
        }
        toInsert.add(newRelation(
                layoutId, registryCode, from, to, relationKind, edgeAction, enabledInteractions));
        existingPairs.add(key);
    }

    private Set<String> loadExistingPairKeys(Long layoutId) {
        Set<String> existingPairs = new HashSet<>();
        for (DmDataTabColumnRelationDO row :
                dmDataTabColumnRelationMapper.selectListByLayoutId(layoutId)) {
            String from = row.getFromColumnIdentity() == null ? "" : row.getFromColumnIdentity().trim();
            String to = row.getToColumnIdentity() == null ? "" : row.getToColumnIdentity().trim();
            existingPairs.add(pairKey(from, to, edgeActionFromMeta(row.getRelationMeta())));
        }
        return existingPairs;
    }

    private static String edgeActionFromMeta(Map<String, Object> meta) {
        if (meta == null) {
            throw new ServiceException(500, "栏间关系缺少 relationMeta，无法识别 edgeAction");
        }
        Object role = meta.get("edgeAction");
        if (role == null) {
            throw new ServiceException(500, "栏间关系缺少 edgeAction，无法识别用途");
        }
        String text = String.valueOf(role).trim();
        if ("write".equals(text) || "filter".equals(text)) {
            return text;
        }
        throw new ServiceException(500, "栏间关系 edgeAction 非法：" + text);
    }

    private static DmDataTabColumnRelationDO newRelation(
            Long layoutId,
            String registryEntityTypeCode,
            ColumnEndpoint from,
            ColumnEndpoint to,
            String relationKind,
            String edgeAction,
            List<String> enabledInteractions) {
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
        meta.put("edgeAction", edgeAction);
        meta.put("enabledInteractions", enabledInteractions);
        row.setRelationMeta(meta);
        return row;
    }
}
