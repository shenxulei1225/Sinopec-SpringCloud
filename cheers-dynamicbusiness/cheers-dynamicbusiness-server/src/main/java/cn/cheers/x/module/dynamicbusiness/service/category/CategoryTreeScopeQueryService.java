package cn.cheers.x.module.dynamicbusiness.service.category;

import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryTreeRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryEntityLinkDO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 分类树按登录人的数据范围裁剪。
 *
 * <p>界面：台账上的分类树里，节点本身是某本目录里的一条记录时，登录人看不到的旁支不出现。
 * 下级记录能看、它的上级本身不在任职范围里时，上级仍留在树上，否则下级会跟着被拿掉，左边只剩空文件夹。
 * 场站树列出这个人能看的全部场站，不跟右上角收成一座。
 * 部门这类挂在场站上的记录，右上角选了站就只留这座站的，避免各站的同名部门铺在一起。</p>
 *
 * <p>权威：节点挂的是哪条记录、哪本目录，只认分类与实体的挂接（{@code CategoryEntityLinkDO}）。
 * 看得见哪些认 {@link DataScopeQueryService#retainCoveredInBrowseFacility}。</p>
 *
 * <p>不负责：拼树、缓存（见 {@link CategoryService#getCategoryTreeByType}）；不挂实体的普通分类节点照常显示。
 * 禁止：改写缓存里的树；为看不到的记录造占位节点。</p>
 */
@Service
public class CategoryTreeScopeQueryService {

    @Resource
    private CategoryEntityLinkService categoryEntityLinkService;
    @Resource
    private DataScopeQueryService dataScopeQueryService;

    /**
     * 返回裁剪后的新树，入参不改。
     * 挂了实体但登录人看不到、下面也没有能看的记录时，这一枝去掉。
     * 下面还有能看的记录时，这条上级留着，人才能点到下级。没有挂实体的节点保留。
     */
    public List<CategoryTreeRespVO> retainVisibleEntityNodes(List<CategoryTreeRespVO> tree) {
        if (tree == null || tree.isEmpty()) {
            return List.of();
        }
        List<Long> categoryIds = new ArrayList<>();
        collectEntityCategoryIds(tree, categoryIds);
        if (categoryIds.isEmpty()) {
            return tree;
        }
        Set<Long> hiddenCategoryIds = hiddenCategoryIds(categoryEntityLinkService.getLinksByCategoryIds(categoryIds));
        keepPathToVisibleNodes(tree, hiddenCategoryIds);
        return hiddenCategoryIds.isEmpty() ? tree : prune(tree, hiddenCategoryIds);
    }

    /**
     * 从叶子往上：这条记录不在任职范围里，但有一个下级会留在树上，就把这条从隐藏名单拿掉。
     */
    private static void keepPathToVisibleNodes(List<CategoryTreeRespVO> nodes, Set<Long> hidden) {
        for (CategoryTreeRespVO node : nodes) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                keepPathToVisibleNodes(node.getChildren(), hidden);
            }
            if (node.getId() == null || !hidden.contains(node.getId()) || node.getChildren() == null) {
                continue;
            }
            boolean childStays = node.getChildren().stream()
                    .anyMatch(child -> child.getId() != null && !hidden.contains(child.getId()));
            if (childStays) {
                hidden.remove(node.getId());
            }
        }
    }

    /**
     * 场站节点留全部能看的站。
     * 能按场站归属的记录，再收到右上角选中的站。
     */
    private List<Long> visibleEntityIds(String catalog, List<Long> entityIds) {
        return dataScopeQueryService.retainCoveredInBrowseFacility(catalog, entityIds);
    }

    /** 按挂接的目录分组核对，返回登录人看不到的分类节点编号。 */
    private Set<Long> hiddenCategoryIds(List<CategoryEntityLinkDO> links) {
        Map<String, List<CategoryEntityLinkDO>> byCatalog = new HashMap<>();
        for (CategoryEntityLinkDO link : links) {
            if (link == null || link.getCategoryId() == null || link.getEntityId() == null
                    || !StringUtils.hasText(link.getEntityTypeCode())) {
                continue;
            }
            byCatalog.computeIfAbsent(link.getEntityTypeCode().trim(), key -> new ArrayList<>()).add(link);
        }
        Set<Long> hidden = new HashSet<>();
        byCatalog.forEach((catalog, rows) -> {
            List<Long> entityIds = rows.stream().map(CategoryEntityLinkDO::getEntityId).distinct().toList();
            Set<Long> visible = new HashSet<>(visibleEntityIds(catalog, entityIds));
            rows.stream()
                    .filter(link -> !visible.contains(link.getEntityId()))
                    .map(CategoryEntityLinkDO::getCategoryId)
                    .forEach(hidden::add);
        });
        return hidden;
    }

    private static void collectEntityCategoryIds(List<CategoryTreeRespVO> nodes, List<Long> out) {
        for (CategoryTreeRespVO node : nodes) {
            if (Boolean.TRUE.equals(node.getIsEntity()) && node.getId() != null) {
                out.add(node.getId());
            }
            if (node.getChildren() != null) {
                collectEntityCategoryIds(node.getChildren(), out);
            }
        }
    }

    private static List<CategoryTreeRespVO> prune(List<CategoryTreeRespVO> nodes, Set<Long> hiddenCategoryIds) {
        return nodes.stream()
                .filter(node -> !hiddenCategoryIds.contains(node.getId()))
                .map(node -> copyWithChildren(node, hiddenCategoryIds))
                .toList();
    }

    private static CategoryTreeRespVO copyWithChildren(CategoryTreeRespVO node, Set<Long> hiddenCategoryIds) {
        CategoryTreeRespVO copy = new CategoryTreeRespVO();
        BeanUtils.copyProperties(node, copy, "children");
        copy.setChildren(node.getChildren() == null ? null : prune(node.getChildren(), hiddenCategoryIds));
        return copy;
    }
}
