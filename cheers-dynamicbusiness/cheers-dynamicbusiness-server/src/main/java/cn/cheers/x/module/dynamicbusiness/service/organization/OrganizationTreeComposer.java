package cn.cheers.x.module.dynamicbusiness.service.organization;

import cn.cheers.x.module.dynamicbusiness.controller.admin.organization.vo.OrganizationTreeNodeRespVO;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 组织架构页左侧的组织树：组织机构按上下级展开（集团 → 省公司 → 分公司 → 作业区）。
 * 只生成这一次查询的结果，不写任何数据。
 *
 * 不负责：场站。场站是另一本数据目录，靠场站上的「所属组织」关联到作业区，不是组织树的下级。
 * 上级找不到的节点按最上层处理，因为它本身是组织机构里的记录。
 */
public final class OrganizationTreeComposer {

    public static final String KIND_ORGANIZATION = "organization";

    private OrganizationTreeComposer() {
    }

    public static List<OrganizationTreeNodeRespVO> compose(List<OrganizationNodeRow> rows) {
        return compose(KIND_ORGANIZATION, rows);
    }

    /** kind 是这本目录的注册编码，写进节点上，方便调用方分辨树从哪本目录来。 */
    public static List<OrganizationTreeNodeRespVO> compose(String kind, List<OrganizationNodeRow> rows) {
        String nodeKind = kind == null || kind.isBlank() ? KIND_ORGANIZATION : kind.trim();
        Map<Long, OrganizationTreeNodeRespVO> byId = new LinkedHashMap<>();
        for (OrganizationNodeRow row : rows) {
            byId.put(row.entityId(), node(nodeKind, row.entityId(), row.name(), row.modelId()));
        }
        List<OrganizationTreeNodeRespVO> roots = new ArrayList<>();
        for (OrganizationNodeRow row : rows) {
            OrganizationTreeNodeRespVO self = byId.get(row.entityId());
            OrganizationTreeNodeRespVO parent = row.parentEntityId() == null
                    ? null : byId.get(row.parentEntityId());
            if (parent == null || parent == self) {
                roots.add(self);
            } else {
                parent.getChildren().add(self);
            }
        }
        return roots;
    }

    private static OrganizationTreeNodeRespVO node(String kind, long entityId, String name, Long modelId) {
        OrganizationTreeNodeRespVO node = new OrganizationTreeNodeRespVO();
        node.setKey(kind + ":" + entityId);
        node.setKind(kind);
        node.setEntityId(entityId);
        node.setName(name);
        node.setModelId(modelId);
        node.setChildren(new ArrayList<>());
        return node;
    }
}
