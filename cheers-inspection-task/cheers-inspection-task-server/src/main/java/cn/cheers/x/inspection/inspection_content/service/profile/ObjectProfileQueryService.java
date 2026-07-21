package cn.cheers.x.inspection.inspection_content.service.profile;

import java.util.Collection;

/**
 * 对象巡检类型台账查询服务。
 */
public interface ObjectProfileQueryService {

    String requireConsistentInspectionType(Long facilityId, Collection<Long> objectIds);
}
