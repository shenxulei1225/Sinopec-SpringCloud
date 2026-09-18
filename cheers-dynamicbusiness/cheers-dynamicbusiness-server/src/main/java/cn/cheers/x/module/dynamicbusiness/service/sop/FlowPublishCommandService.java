package cn.cheers.x.module.dynamicbusiness.service.sop;

/**
 * SOP 发布命令服务。
 *
 * <p><b>负责</b>：按指定版本号执行发布。</p>
 * <p><b>不负责</b>：创建新版本内容；步骤编辑保存。</p>
 */
public interface FlowPublishCommandService {

    /**
     * 按版本号发布 SOP。
     *
     * @param currentFlowId 当前操作行 id（用于锁定同 code 发布范围）
     * @param versionNo 指定发布版本号
     */
    void publishByVersion(long currentFlowId, int versionNo);

    /**
     * 将当前版本内容保存到指定版本号。
     *
     * <p>行为：</p>
     * <ul>
     *   <li>目标版本不存在：新建未发布版本；</li>
     *   <li>目标版本已存在且未发布：覆盖该版本内容；</li>
     *   <li>目标版本已发布：拒绝覆盖。</li>
     * </ul>
     *
     * @param currentFlowId 当前操作行 id（作为源版本）
     * @param versionNo 目标版本号
     */
    void saveToVersion(long currentFlowId, int versionNo);

    /**
     * 撤回当前 SOP 的发布状态（同 code 所有版本标记为未发布）。
     *
     * @param currentFlowId 当前操作行 id（用于锁定同 code 范围）
     */
    void unpublish(long currentFlowId);
}

