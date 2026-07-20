package cn.cheers.x.module.platform.topology.dal.mysql;

import cn.cheers.x.module.platform.topology.dal.dataobject.PathNetworkDO;
import cn.cheers.x.framework.mybatis.core.mapper.BaseMapperX;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PathNetworkMapper extends BaseMapperX<PathNetworkDO> {

    /**
     * 按设施+种类取草稿。多实例时优先旧槽位 id（net_{fid}_{kind}_draft），
     * 否则取节点/边 JSON 更长的一条，避免 selectOne 多行异常或误取空草稿。
     */
    default PathNetworkDO selectDraftByFacilityIdAndKind(Long facilityId, String networkKind) {
        List<PathNetworkDO> rows = selectList(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .eq(PathNetworkDO::getNetworkKind, networkKind)
                .eq(PathNetworkDO::getStatus, "DRAFT"));
        if (rows == null || rows.isEmpty()) {
            return null;
        }
        if (rows.size() == 1) {
            return rows.get(0);
        }
        String legacyId = "net_" + facilityId + "_" + networkKind.toLowerCase() + "_draft";
        for (PathNetworkDO row : rows) {
            if (legacyId.equals(row.getId())) {
                return row;
            }
        }
        return rows.stream()
                .max((a, b) -> Integer.compare(geomLen(a), geomLen(b)))
                .orElse(rows.get(0));
    }

    private static int geomLen(PathNetworkDO row) {
        int n = row.getNodes() == null ? 0 : row.getNodes().length();
        int e = row.getEdges() == null ? 0 : row.getEdges().length();
        return n + e;
    }

    default PathNetworkDO selectLatestPublishedByFacilityIdAndKind(Long facilityId, String networkKind) {
        return selectOne(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .eq(PathNetworkDO::getNetworkKind, networkKind)
                .eq(PathNetworkDO::getStatus, "PUBLISHED")
                .orderByDesc(PathNetworkDO::getVersion)
                .last("LIMIT 1"));
    }

    default List<PathNetworkDO> selectPublishedByFacilityIdAndKind(Long facilityId, String networkKind) {
        return selectList(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .eq(PathNetworkDO::getNetworkKind, networkKind)
                .eq(PathNetworkDO::getStatus, "PUBLISHED"));
    }

    /** 设施下全部路网实例（草稿 + 已发布正式版） */
    default List<PathNetworkDO> selectAllByFacilityId(Long facilityId) {
        return selectList(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .in(PathNetworkDO::getStatus, "DRAFT", "PUBLISHED")
                .last("ORDER BY CASE WHEN status = 'DRAFT' THEN 0 ELSE 1 END, update_time DESC NULLS LAST, id DESC"));
    }

    /** 设施下全部草稿路网实例（多实例列表） */
    default List<PathNetworkDO> selectDraftsByFacilityId(Long facilityId) {
        return selectList(new LambdaQueryWrapperX<PathNetworkDO>()
                .eq(PathNetworkDO::getFacilityId, facilityId)
                .eq(PathNetworkDO::getStatus, "DRAFT")
                .last("ORDER BY update_time DESC NULLS LAST, id DESC"));
    }
}
