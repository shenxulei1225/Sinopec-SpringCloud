package cn.cheers.x.maintenance.service.corrective;

/**
 * 人机审批网关：Task 7 可接 Zeebe；默认空实现配合 skip-approval。
 */
public interface CorrectiveApprovalGateway {
    /**
     * @return processInstanceKey；skip 时返回 null
     */
    String startApproval(Long caseId, String title);
}
