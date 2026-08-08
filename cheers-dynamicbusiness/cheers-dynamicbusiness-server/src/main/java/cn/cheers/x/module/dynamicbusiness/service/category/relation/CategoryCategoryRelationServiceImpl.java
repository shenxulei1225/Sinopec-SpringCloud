package cn.cheers.x.module.dynamicbusiness.service.category.relation;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.category.vo.CategoryCategoryAssociationRespVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryCategoryRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.category.CategoryDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryCategoryRelationMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.category.CategoryMapper;
import cn.cheers.x.module.dynamicbusiness.framework.tenant.TenantAssociationTableService;
import cn.cheers.x.module.dynamicbusiness.service.category.CategoryService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * 分类—分类跨种类关联实现。
 */
@Service
@Validated
@Slf4j
public class CategoryCategoryRelationServiceImpl implements CategoryCategoryRelationService {

    private static final int SORT_STEP = 1024;

    @Resource
    private CategoryCategoryRelationMapper relationMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private CategoryService categoryService;

    @Resource
    private TenantAssociationTableService tenantAssociationTableService;

    private void ensureTenantTable() {
        tenantAssociationTableService.ensureCurrentTenantAssociationTables();
    }

    private String normalizeType(String categoryTypeCode, String label) {
        if (!StringUtils.hasText(categoryTypeCode)) {
            throw new ServiceException(400, label + " 不能为空");
        }
        return categoryTypeCode.trim();
    }

    /** 同种类禁止：层级用 parent_id，不得进本表。 */
    private void requireCrossType(String hostType, String memberType) {
        if (hostType.equalsIgnoreCase(memberType)) {
            throw new ServiceException(400,
                    "禁止同分类种类互相关联；同种类层级请使用 parent_id（host="
                            + hostType + ", member=" + memberType + "）");
        }
    }

    /**
     * 校验分类存在且种类匹配，返回库中实际种类编码。
     */
    private String requireCategory(Long categoryId, String categoryTypeCode, String label) {
        if (categoryId == null) {
            throw new ServiceException(400, label + "Id 不能为空");
        }
        String type = normalizeType(categoryTypeCode, label + "TypeCode");
        CategoryDO cat = categoryMapper.selectByIdAndCategoryTypeCode(categoryId, type);
        if (cat == null || !StringUtils.hasText(cat.getCategoryTypeCode())) {
            throw new ServiceException(404, label + " 不存在或不属于种类 " + type);
        }
        String actual = cat.getCategoryTypeCode().trim();
        if (!actual.equalsIgnoreCase(type)) {
            throw new ServiceException(404, label + " 不存在或不属于种类 " + type);
        }
        return actual;
    }

    private int nextSort(Long hostCategoryId, String hostType, String memberType) {
        Integer max = relationMapper.maxSort(hostCategoryId, hostType, memberType);
        return (max == null ? 0 : max) + SORT_STEP;
    }

    private List<Long> normalizeIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        Set<Long> seen = new LinkedHashSet<>();
        for (Long id : ids) {
            if (id != null) {
                seen.add(id);
            }
        }
        return new ArrayList<>(seen);
    }

    private CategoryCategoryAssociationRespVO ok(String op, Long hostId, Long memberId,
                                                 int success, int fail,
                                                 List<Long> okIds, List<String> fails, long start) {
        int total = success + fail;
        return CategoryCategoryAssociationRespVO.builder()
                .operationType(op)
                .hostCategoryId(hostId)
                .memberCategoryId(memberId)
                .successCount(success)
                .failCount(fail)
                .totalCount(total)
                .successMemberCategoryIds(okIds == null ? List.of() : okIds)
                .failMessages(fails == null ? List.of() : fails)
                .executionTime(System.currentTimeMillis() - start)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryCategoryAssociationRespVO associate(Long hostCategoryId, Long memberCategoryId,
                                                       String hostCategoryTypeCode,
                                                       String memberCategoryTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String hostType = requireCategory(hostCategoryId, hostCategoryTypeCode, "宿主分类");
        String memberType = requireCategory(memberCategoryId, memberCategoryTypeCode, "成员分类");
        requireCrossType(hostType, memberType);

        if (existsRelation(hostCategoryId, memberCategoryId, hostType, memberType)) {
            return ok("ASSOCIATE", hostCategoryId, memberCategoryId, 1, 0,
                    List.of(memberCategoryId), List.of(), start);
        }

        int sort = nextSort(hostCategoryId, hostType, memberType);
        int restored = relationMapper.restoreDeleted(
                hostCategoryId, memberCategoryId, hostType, memberType, sort);
        if (restored > 0) {
            log.info("恢复分类—分类关联: hostId={}, memberId={}, hostType={}, memberType={}",
                    hostCategoryId, memberCategoryId, hostType, memberType);
            return ok("ASSOCIATE", hostCategoryId, memberCategoryId, 1, 0,
                    List.of(memberCategoryId), List.of(), start);
        }

        CategoryCategoryRelationDO row = CategoryCategoryRelationDO.builder()
                .hostCategoryId(hostCategoryId)
                .hostCategoryTypeCode(hostType)
                .memberCategoryId(memberCategoryId)
                .memberCategoryTypeCode(memberType)
                .sort(sort)
                .build();
        relationMapper.insert(row);
        log.info("创建分类—分类关联: hostId={}, memberId={}, hostType={}, memberType={}",
                hostCategoryId, memberCategoryId, hostType, memberType);
        return ok("ASSOCIATE", hostCategoryId, memberCategoryId, 1, 0,
                List.of(memberCategoryId), List.of(), start);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryCategoryAssociationRespVO disassociate(Long hostCategoryId, Long memberCategoryId,
                                                          String hostCategoryTypeCode,
                                                          String memberCategoryTypeCode,
                                                          boolean includeDescendantMembers) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String hostType = normalizeType(hostCategoryTypeCode, "hostCategoryTypeCode");
        String memberType = normalizeType(memberCategoryTypeCode, "memberCategoryTypeCode");
        if (hostCategoryId == null || memberCategoryId == null) {
            return ok("DISASSOCIATE", hostCategoryId, memberCategoryId, 0, 1, List.of(),
                    List.of("hostCategoryId/memberCategoryId 不能为空"), start);
        }
        requireCrossType(hostType, memberType);

        List<Long> memberIds;
        if (includeDescendantMembers) {
            // 与浏览「点上级含下级」对称：服务端按 parent_id 展开成员子树
            memberIds = categoryService.getAllCategoryIdsIncludingChildren(memberCategoryId, memberType);
            if (memberIds == null || memberIds.isEmpty()) {
                memberIds = List.of(memberCategoryId);
            }
        } else {
            memberIds = List.of(memberCategoryId);
        }

        // 先查出实际存在的边，再软删，避免「父节点无边却报成功」
        List<Long> removedMemberIds = relationMapper
                .selectByHostAndMembers(hostCategoryId, memberIds, hostType, memberType)
                .stream()
                .map(CategoryCategoryRelationDO::getMemberCategoryId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (removedMemberIds.isEmpty()) {
            return ok("DISASSOCIATE", hostCategoryId, memberCategoryId, 0, 1, List.of(),
                    List.of("当前成员分类及其下级与该宿主无关联"), start);
        }

        int deleted = relationMapper.softDeleteByHostAndMembers(
                hostCategoryId, removedMemberIds, hostType, memberType);
        log.info("解除分类—分类关联: hostId={}, memberRoot={}, includeDescendants={}, removed={}, rows={}",
                hostCategoryId, memberCategoryId, includeDescendantMembers, removedMemberIds, deleted);
        return ok("DISASSOCIATE", hostCategoryId, memberCategoryId, removedMemberIds.size(), 0,
                removedMemberIds, List.of(), start);
    }

    @Override
    public boolean existsRelation(Long hostCategoryId, Long memberCategoryId,
                                  String hostCategoryTypeCode, String memberCategoryTypeCode) {
        if (hostCategoryId == null || memberCategoryId == null
                || !StringUtils.hasText(hostCategoryTypeCode)
                || !StringUtils.hasText(memberCategoryTypeCode)) {
            return false;
        }
        ensureTenantTable();
        String hostType = normalizeType(hostCategoryTypeCode, "hostCategoryTypeCode");
        String memberType = normalizeType(memberCategoryTypeCode, "memberCategoryTypeCode");
        if (hostType.equalsIgnoreCase(memberType)) {
            return false;
        }
        return relationMapper.selectActive(hostCategoryId, memberCategoryId, hostType, memberType) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryCategoryAssociationRespVO batchAssociateMembersToHost(
            Long hostCategoryId, List<Long> memberCategoryIds,
            String hostCategoryTypeCode, String memberCategoryTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String hostType = requireCategory(hostCategoryId, hostCategoryTypeCode, "宿主分类");
        String memberType = normalizeType(memberCategoryTypeCode, "memberCategoryTypeCode");
        requireCrossType(hostType, memberType);

        List<Long> ids = normalizeIds(memberCategoryIds);
        if (ids.isEmpty()) {
            return ok("BATCH_ASSOCIATE", hostCategoryId, null, 0, 0, List.of(), List.of(), start);
        }

        List<Long> okIds = new ArrayList<>();
        List<String> fails = new ArrayList<>();
        for (Long memberId : ids) {
            try {
                associate(hostCategoryId, memberId, hostType, memberType);
                okIds.add(memberId);
            } catch (ServiceException ex) {
                fails.add(Objects.toString(ex.getMessage(), "关联失败: " + memberId));
            } catch (Exception ex) {
                fails.add("关联失败: " + memberId + " / " + ex.getMessage());
            }
        }
        return ok("BATCH_ASSOCIATE", hostCategoryId, null, okIds.size(), fails.size(), okIds, fails, start);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CategoryCategoryAssociationRespVO batchDisassociateMembersFromHost(
            Long hostCategoryId, List<Long> memberCategoryIds,
            String hostCategoryTypeCode, String memberCategoryTypeCode) {
        long start = System.currentTimeMillis();
        ensureTenantTable();
        String hostType = normalizeType(hostCategoryTypeCode, "hostCategoryTypeCode");
        String memberType = normalizeType(memberCategoryTypeCode, "memberCategoryTypeCode");
        requireCrossType(hostType, memberType);
        if (hostCategoryId == null) {
            return ok("BATCH_DISASSOCIATE", null, null, 0, 1, List.of(),
                    List.of("hostCategoryId 不能为空"), start);
        }
        List<Long> excludeRoots = normalizeIds(memberCategoryIds);
        if (excludeRoots.isEmpty()) {
            return ok("BATCH_DISASSOCIATE", hostCategoryId, null, 0, 0, List.of(), List.of(), start);
        }

        LinkedHashSet<Long> removedMemberIds = new LinkedHashSet<>();
        LinkedHashSet<Long> reassociatedIds = new LinkedHashSet<>();
        List<String> fails = new ArrayList<>();
        for (Long excludeId : excludeRoots) {
            try {
                ExcludeSplitResult one = excludeMemberWithSplit(
                        hostCategoryId, excludeId, hostType, memberType);
                removedMemberIds.addAll(one.removedMemberIds());
                reassociatedIds.addAll(one.reassociatedMemberIds());
                if (one.removedMemberIds().isEmpty() && one.reassociatedMemberIds().isEmpty()) {
                    fails.add("成员 " + excludeId + " 相对该宿主无覆盖性关联可排除");
                }
            } catch (ServiceException ex) {
                fails.add(Objects.toString(ex.getMessage(), "排除失败: " + excludeId));
            } catch (Exception ex) {
                fails.add("排除失败: " + excludeId + " / " + ex.getMessage());
            }
        }

        if (removedMemberIds.isEmpty() && reassociatedIds.isEmpty()) {
            return ok("BATCH_DISASSOCIATE", hostCategoryId, null, 0, Math.max(1, fails.size()),
                    List.of(),
                    fails.isEmpty() ? List.of("所选成员相对该宿主无关联可排除") : fails,
                    start);
        }

        log.info("批量排除成员并拆分祖先行: hostId={}, exclude={}, removed={}, reassociated={}",
                hostCategoryId, excludeRoots, removedMemberIds, reassociatedIds);
        // successCount 以删掉的覆盖行数为主，便于前端提示
        return ok("BATCH_DISASSOCIATE", hostCategoryId, null, removedMemberIds.size(), fails.size(),
                new ArrayList<>(removedMemberIds), fails, start);
    }

    private record ExcludeSplitResult(List<Long> removedMemberIds, List<Long> reassociatedMemberIds) {}

    /**
     * 排除成员 T：删 T 子树挂靠；若祖先有挂靠则删祖先行并沿路径拆分重挂其余兄弟支。
     */
    private ExcludeSplitResult excludeMemberWithSplit(
            Long hostCategoryId, Long excludeId, String hostType, String memberType) {
        if (excludeId == null || excludeId <= 0) {
            return new ExcludeSplitResult(List.of(), List.of());
        }

        List<Long> subtree = categoryService.getAllCategoryIdsIncludingChildren(excludeId, memberType);
        if (subtree == null || subtree.isEmpty()) {
            subtree = List.of(excludeId);
        }

        List<Long> ancestorsRootToNear = listAncestorsRootToNear(excludeId, memberType);
        List<Long> covering = new ArrayList<>();
        for (Long ancestorId : ancestorsRootToNear) {
            if (existsRelation(hostCategoryId, ancestorId, hostType, memberType)) {
                covering.add(ancestorId);
            }
        }

        LinkedHashSet<Long> toRemove = new LinkedHashSet<>();
        List<Long> subtreeRows = relationMapper
                .selectByHostAndMembers(hostCategoryId, subtree, hostType, memberType)
                .stream()
                .map(CategoryCategoryRelationDO::getMemberCategoryId)
                .filter(Objects::nonNull)
                .toList();
        toRemove.addAll(subtreeRows);
        toRemove.addAll(covering);

        if (!toRemove.isEmpty()) {
            relationMapper.softDeleteByHostAndMembers(
                    hostCategoryId, new ArrayList<>(toRemove), hostType, memberType);
        }

        LinkedHashSet<Long> reassociated = new LinkedHashSet<>();
        if (!covering.isEmpty()) {
            Long top = covering.get(0);
            distributeExcept(hostCategoryId, top, excludeId, hostType, memberType, reassociated);
        }

        return new ExcludeSplitResult(new ArrayList<>(toRemove), new ArrayList<>(reassociated));
    }

    /** 从根到父（不含自身）的祖先链。 */
    private List<Long> listAncestorsRootToNear(Long categoryId, String memberType) {
        List<Long> nearToRoot = new ArrayList<>();
        CategoryDO cur = categoryMapper.selectByIdAndCategoryTypeCode(categoryId, memberType);
        if (cur == null) {
            return List.of();
        }
        Long parentId = cur.getParentId();
        int guard = 0;
        while (parentId != null && parentId > 0 && guard++ < 64) {
            nearToRoot.add(parentId);
            CategoryDO parent = categoryMapper.selectByIdAndCategoryTypeCode(parentId, memberType);
            if (parent == null) {
                break;
            }
            parentId = parent.getParentId();
        }
        java.util.Collections.reverse(nearToRoot);
        return nearToRoot;
    }

    /**
     * node 本身不挂靠；给「除通向 exclude 的那一支外」的直接子挂靠，路径中段继续拆分。
     */
    private void distributeExcept(
            Long hostCategoryId,
            Long nodeId,
            Long excludeId,
            String hostType,
            String memberType,
            Set<Long> reassociatedOut) {
        List<Long> pathRootToExclude = new ArrayList<>(listAncestorsRootToNear(excludeId, memberType));
        pathRootToExclude.add(excludeId);
        int idx = pathRootToExclude.indexOf(nodeId);
        Long pathChild = (idx >= 0 && idx + 1 < pathRootToExclude.size())
                ? pathRootToExclude.get(idx + 1)
                : null;

        List<CategoryDO> children =
                categoryMapper.selectByParentIdAndCategoryTypeCode(nodeId, memberType);
        if (children == null || children.isEmpty()) {
            return;
        }
        for (CategoryDO child : children) {
            if (child == null || child.getId() == null) {
                continue;
            }
            Long childId = child.getId();
            if (pathChild != null && childId.equals(pathChild)) {
                if (childId.equals(excludeId)) {
                    continue;
                }
                distributeExcept(hostCategoryId, childId, excludeId, hostType, memberType, reassociatedOut);
            } else {
                associate(hostCategoryId, childId, hostType, memberType);
                reassociatedOut.add(childId);
            }
        }
    }

    @Override
    public List<Long> listMemberCategoryIdsByHost(Long hostCategoryId, String hostCategoryTypeCode,
                                                  String memberCategoryTypeCode,
                                                  boolean includeDescendantHosts) {
        if (hostCategoryId == null || !StringUtils.hasText(hostCategoryTypeCode)
                || !StringUtils.hasText(memberCategoryTypeCode)) {
            return List.of();
        }
        ensureTenantTable();
        String hostType = normalizeType(hostCategoryTypeCode, "hostCategoryTypeCode");
        String memberType = normalizeType(memberCategoryTypeCode, "memberCategoryTypeCode");
        if (hostType.equalsIgnoreCase(memberType)) {
            return List.of();
        }

        List<Long> hostIds;
        if (includeDescendantHosts) {
            // 与点分类查型号/实体一致：按 parent_id 展开宿主子树，服务端汇总，前端不必自拼 id
            hostIds = categoryService.getAllCategoryIdsIncludingChildren(hostCategoryId, hostType);
            if (hostIds == null || hostIds.isEmpty()) {
                hostIds = List.of(hostCategoryId);
            }
        } else {
            hostIds = List.of(hostCategoryId);
        }

        Set<Long> memberIds = new LinkedHashSet<>();
        for (CategoryCategoryRelationDO row : relationMapper.selectByHosts(hostIds, hostType, memberType)) {
            if (row.getMemberCategoryId() != null) {
                memberIds.add(row.getMemberCategoryId());
            }
        }
        return new ArrayList<>(memberIds);
    }

    @Override
    public List<Long> listHostCategoryIdsByMember(Long memberCategoryId, String memberCategoryTypeCode,
                                                  String hostCategoryTypeCode) {
        if (memberCategoryId == null || !StringUtils.hasText(memberCategoryTypeCode)) {
            return List.of();
        }
        ensureTenantTable();
        String memberType = normalizeType(memberCategoryTypeCode, "memberCategoryTypeCode");
        String hostType = StringUtils.hasText(hostCategoryTypeCode)
                ? normalizeType(hostCategoryTypeCode, "hostCategoryTypeCode")
                : null;
        if (hostType != null && hostType.equalsIgnoreCase(memberType)) {
            return List.of();
        }
        return relationMapper.selectByMember(memberCategoryId, memberType, hostType).stream()
                .map(CategoryCategoryRelationDO::getHostCategoryId)
                .filter(Objects::nonNull)
                .toList();
    }
}
