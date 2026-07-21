package cn.cheers.x.inspection.inspection_content.service.profile;

/**
 * 对象巡检类型台账写服务。
 */
public interface ObjectProfileService {

    void upsert(Long facilityId, Long objectId, String inspectionType, Integer defaultWorkMinutes);
}
