package cn.cheers.x.intelligentanalysis.service;

import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobRespDTO;
import cn.cheers.x.intelligentanalysis.api.dto.AnalysisJobSubmitReqDTO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisCapabilityRespVO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisCapabilitySaveReqVO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisProviderRespVO;
import cn.cheers.x.intelligentanalysis.controller.admin.vo.AnalysisProviderSaveReqVO;

import java.util.List;

/**
 * 智能分析能力目录、服务接入和任务账。
 */
public interface IntelligentAnalysisService {

    List<AnalysisCapabilityRespVO> listCapabilities(boolean onlyAvailable);

    /** 动作创建时确认所选能力当前可以受理任务。 */
    void assertCapabilityAvailable(Long capabilityId);

    Long saveCapability(AnalysisCapabilitySaveReqVO request);

    List<AnalysisProviderRespVO> listProviders();

    Long saveProvider(AnalysisProviderSaveReqVO request);

    AnalysisJobRespDTO submitJob(AnalysisJobSubmitReqDTO request);

    AnalysisJobRespDTO getJob(String jobNo);

    List<AnalysisJobRespDTO> listRecentJobs(int limit);

    void applyLegacyCallback(String body);
}
