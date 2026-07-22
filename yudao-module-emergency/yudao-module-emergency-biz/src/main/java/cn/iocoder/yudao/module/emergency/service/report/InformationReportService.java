package cn.iocoder.yudao.module.emergency.service.report;

import cn.cheers.x.framework.common.pojo.PageResult;
import cn.iocoder.yudao.module.emergency.controller.admin.report.vo.*;

/**
 * 信息报送流程管理 Service 接口
 *
 * @author 芋道源码
 */
public interface InformationReportService {

    /**
     * 创建信息报送记录
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInformationReport(InformationReportCreateReqVO createReqVO);

    /**
     * 更新信息报送记录
     *
     * @param updateReqVO 更新信息
     */
    void updateInformationReport(InformationReportUpdateReqVO updateReqVO);

    /**
     * 删除信息报送记录
     *
     * @param id 编号
     */
    void deleteInformationReport(Long id);

    /**
     * 获得信息报送记录
     *
     * @param id 编号
     * @return 信息报送记录
     */
    InformationReportRespVO getInformationReport(Long id);

    /**
     * 获得信息报送记录分页
     *
     * @param pageReqVO 分页查询
     * @return 信息报送记录分页
     */
    PageResult<InformationReportRespVO> getInformationReportPage(InformationReportPageReqVO pageReqVO);

    /**
     * 提交报送（接报 -> 呈报 -> 上报）
     *
     * @param id 报送记录ID
     * @param submitReqVO 提交信息
     */
    void submitReport(Long id, InformationReportSubmitReqVO submitReqVO);

    /**
     * 确认报送
     *
     * @param id 报送记录ID
     * @param confirmReqVO 确认信息
     */
    void confirmReport(Long id, InformationReportConfirmReqVO confirmReqVO);

    /**
     * 检查报送时限
     *
     * @param eventId 事件ID
     * @param reportType 报送类型
     * @return 是否超时
     */
    boolean checkReportTimeout(Long eventId, String reportType);

    /**
     * 获取报送时限要求（分钟）
     *
     * @param eventLevel 事件级别
     * @return 时限（分钟）
     */
    int getReportTimeLimit(String eventLevel);

    /**
     * 根据事件级别、报送类型和报送对象获取具体的报送时限（分钟）
     * BR-011：不同级别的事件必须遵循不同的信息报送时限
     *
     * @param eventLevel 事件级别
     * @param reportType 报送类型（INITIAL初报/SUPPLEMENT续报/FINAL终报）
     * @param reportTarget 报送对象（INTERNAL内部/EXTERNAL外部/GROUP集团公司/LOCAL_GOV地方政府）
     * @return 时限（分钟）
     */
    int getReportTimeLimitByType(String eventLevel, String reportType, String reportTarget);
}




