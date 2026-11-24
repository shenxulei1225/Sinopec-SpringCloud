package cn.cheers.x.module.dynamicbusiness.service.alarm;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.CategoryIdGroupReqVO;
import cn.cheers.x.module.dynamicbusiness.dataprocess.AlarmTypeCatalog;
import cn.cheers.x.module.dynamicbusiness.framework.security.PageAccess;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import cn.cheers.x.module.dynamicbusiness.service.entity.relation.EntityCategoryRelationService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 告警名单按业务大类看。
 *
 * 管什么：安防、设备、巡检各自的页面权限决定能看哪一个大类。名单必须带上这个大类或它下面的分类。
 * 不管什么：不新建分类，不把没挂分类的告警塞进某一类，不算数据范围。
 * 禁止：不选大类就列出全部告警。
 */
@Service
public class AlarmRecordAccess {

    /** 名单被收成空页。 */
    public record Constraint(boolean nothing, List<Long> categoryIds, List<CategoryIdGroupReqVO> groups) {
    }

    /** 页上的一栏。名称用分类树上已经有的名字。 */
    public record Tab(long id, String name, String code) {
    }

    private static final Map<String, List<String>> PAGES = Map.of(
            "alarm-security", List.of(
                    "security-alarm", "security-video", "security-perimeter", "security-intrusion",
                    "security-access", "security-access-grant", "security-broadcast", "security-person",
                    "security-vehicle", "security-behavior", "security-phone", "security-radio",
                    "security-vehicle-gate", "security-vehicle-pass"),
            "alarm-device", List.of("equipment-ledger", "spare-parts"),
            "alarm-device-fault", List.of("equipment-ledger", "spare-parts"),
            "alarm-asset", List.of("equipment-ledger", "spare-parts"),
            "alarm-task", List.of("inspection-task", "inspection-schedule"));

    @Resource
    private CategoryService categoryService;
    @Resource
    private PageAccess pageAccess;
    @Resource
    private EntityCategoryRelationService entityCategoryRelationService;

    /** 这个人能看的业务大类。没有对应页面权限的不出现。分类树上没有的大类也不出现。 */
    public List<Tab> visibleTabs() {
        List<Tab> tabs = new ArrayList<>();
        for (CategoryTreeRespVO node : flatten(categoryService.getCategoryTreeByType(AlarmTypeCatalog.DIRECTORY, null))) {
            List<String> pages = PAGES.get(node.getCode());
            if (pages == null || node.getId() == null) {
                continue;
            }
            if (!pageAccess.allowAny("view", pages.toArray(String[]::new))) {
                continue;
            }
            tabs.add(new Tab(node.getId(), node.getName(), node.getCode()));
        }
        return tabs;
    }

    /**
     * 名单上的分类必须落在能看的大类里面。
     * 一个都没带，或带的都不在范围内，名单为空。
     */
    public Constraint constrainList(List<Long> categoryIds, List<CategoryIdGroupReqVO> groups) {
        Set<Long> allowed = allowedCategoryIds();
        boolean hasIds = hasPositive(categoryIds);
        boolean hasGroups = hasGroupIds(groups);
        if (!hasIds && !hasGroups) {
            return new Constraint(true, List.of(), List.of());
        }
        List<Long> keptIds = hasIds ? keepRequested(categoryIds, allowed) : categoryIds;
        if (hasIds && keptIds.isEmpty()) {
            return new Constraint(true, List.of(), List.of());
        }
        if (!hasGroups) {
            return new Constraint(false, keptIds, groups);
        }
        List<CategoryIdGroupReqVO> keptGroups = new ArrayList<>();
        for (CategoryIdGroupReqVO group : groups) {
            String type = group.getCategoryTypeCode();
            if (type != null && !type.isBlank() && !AlarmTypeCatalog.DIRECTORY.equals(type.trim())) {
                keptGroups.add(group);
                continue;
            }
            List<Long> kept = keepRequested(group.getCategoryIds(), allowed);
            if (kept.isEmpty()) {
                return new Constraint(true, List.of(), List.of());
            }
            CategoryIdGroupReqVO copy = new CategoryIdGroupReqVO();
            copy.setCategoryTypeCode(group.getCategoryTypeCode());
            copy.setCategoryIds(kept);
            keptGroups.add(copy);
        }
        return new Constraint(false, keptIds, keptGroups);
    }

    /** 打开一条告警时，它挂上的分类必须有一栏是这个人能看的。没挂分类的仍可打开。 */
    public void assertDetail(Long entityId) {
        if (entityId == null) {
            return;
        }
        List<Long> hung = entityCategoryRelationService.listCategoryIdsByEntityId(entityId, AlarmTypeCatalog.DIRECTORY);
        if (hung == null || hung.isEmpty()) {
            return;
        }
        if (keepRequested(hung, allowedCategoryIds()).isEmpty()) {
            throw new ServiceException(403, "没有这类告警的查看权限");
        }
    }

    /** 请求里的分类，只留下落在允许范围内的。空请求得到空列表。 */
    static List<Long> keepRequested(List<Long> requested, Set<Long> allowed) {
        if (requested == null || requested.isEmpty() || allowed == null || allowed.isEmpty()) {
            return List.of();
        }
        List<Long> kept = new ArrayList<>();
        for (Long id : requested) {
            if (id != null && id > 0 && allowed.contains(id)) {
                kept.add(id);
            }
        }
        return kept;
    }

    private Set<Long> allowedCategoryIds() {
        List<Long> roots = visibleTabs().stream().map(Tab::id).toList();
        if (roots.isEmpty()) {
            return Set.of();
        }
        Set<Long> ids = new LinkedHashSet<>(roots);
        Map<Long, List<Long>> expanded = categoryService.getAllCategoryIdsIncludingChildrenBatch(
                roots, AlarmTypeCatalog.DIRECTORY);
        if (expanded != null) {
            for (List<Long> children : expanded.values()) {
                if (children != null) {
                    ids.addAll(children);
                }
            }
        }
        return ids;
    }

    private static boolean hasPositive(List<Long> ids) {
        if (ids == null) {
            return false;
        }
        for (Long id : ids) {
            if (id != null && id > 0) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasGroupIds(List<CategoryIdGroupReqVO> groups) {
        if (groups == null) {
            return false;
        }
        for (CategoryIdGroupReqVO group : groups) {
            if (hasPositive(group.getCategoryIds())) {
                return true;
            }
        }
        return false;
    }

    private static List<CategoryTreeRespVO> flatten(List<CategoryTreeRespVO> nodes) {
        List<CategoryTreeRespVO> flat = new ArrayList<>();
        if (nodes == null) {
            return flat;
        }
        for (CategoryTreeRespVO node : nodes) {
            flat.add(node);
            flat.addAll(flatten(node.getChildren()));
        }
        return flat;
    }
}
