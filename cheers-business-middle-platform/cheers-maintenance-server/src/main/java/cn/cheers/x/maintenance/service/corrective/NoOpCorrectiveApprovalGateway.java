package cn.cheers.x.maintenance.service.corrective;

import org.springframework.stereotype.Component;

@Component
public class NoOpCorrectiveApprovalGateway implements CorrectiveApprovalGateway {
    @Override
    public String startApproval(Long caseId, String title) {
        return null;
    }
}
