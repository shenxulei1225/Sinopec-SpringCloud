package cn.iocoder.yudao.module.emergency.service.plan;

/**
 * 预案版本管理 Service 接口
 *
 * @author 芋道源码
 */
public interface PlanVersionService {

    /**
     * 发布新版本并锁定
     * 当预案确定后，管理员可以发布新版本并锁定使用，此时创建响应时保存预案快照
     *
     * @param planId 预案ID
     * @param versionNumber 版本号
     */
    void publishVersion(Long planId, String versionNumber);

    /**
     * 解锁版本（恢复实时引用）
     * 解锁后，创建响应时实时引用最新版本的预案
     *
     * @param planId 预案ID
     */
    void unlockVersion(Long planId);

    /**
     * 获取预案快照（如果版本已锁定）
     * 如果版本未锁定，返回null（使用实时引用）
     *
     * @param planId 预案ID
     * @return 预案快照（JSON格式），如果版本未锁定则返回null
     */
    String getPlanSnapshot(Long planId);
}






