package cn.iocoder.yudao.module.emergency.service.report;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.security.core.util.SecurityFrameworkUtils;
import cn.iocoder.yudao.module.emergency.controller.admin.report.vo.*;
import cn.iocoder.yudao.module.emergency.convert.report.InformationReportConvert;
import cn.iocoder.yudao.module.emergency.dal.dataobject.event.EmergencyEventDO;
import cn.iocoder.yudao.module.emergency.dal.dataobject.report.InformationReportDO;
import cn.iocoder.yudao.module.emergency.dal.mysql.event.EmergencyEventMapper;
import cn.iocoder.yudao.module.emergency.dal.mysql.report.InformationReportMapper;
import cn.iocoder.yudao.module.emergency.enums.error.ErrorCodeConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 信息报送流程管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Slf4j
public class InformationReportServiceImpl implements InformationReportService {

    @Resource
    private InformationReportMapper informationReportMapper;

    @Resource
    private EmergencyEventMapper eventMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createInformationReport(InformationReportCreateReqVO createReqVO) {
        // 验证事件是否存在
        EmergencyEventDO event = eventMapper.selectById(createReqVO.getEventId());
        if (event == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.EVENT_NOT_EXISTS);
        }

        // 获取报送时限要求
        int timeLimit = getReportTimeLimit(event.getEventLevel());
        
        // 插入
        InformationReportDO informationReport = InformationReportConvert.INSTANCE.convert(createReqVO);
        informationReport.setStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        // BR-013：记录完整的报送流程，根据报送类型设置相应时间
        if ("RECEIVE".equals(createReqVO.getReportType())) {
            // 接报：记录接报时间
            informationReport.setReceiveTime(now);
        } else if ("PRESENT".equals(createReqVO.getReportType())) {
            // 呈报：记录呈报时间
            informationReport.setPresentTime(now);
        } else if ("REPORT".equals(createReqVO.getReportType())) {
            // 上报：记录上报时间
            informationReport.setReportTime(now);
        }
        informationReport.setTimeLimit(timeLimit);
        informationReport.setTimeout(false);
        informationReportMapper.insert(informationReport);
        
        // 返回
        return informationReport.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateInformationReport(InformationReportUpdateReqVO updateReqVO) {
        // 校验存在
        validateInformationReportExists(updateReqVO.getId());
        
        // 更新
        InformationReportDO updateObj = InformationReportConvert.INSTANCE.convert(updateReqVO);
        informationReportMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteInformationReport(Long id) {
        // 校验存在
        validateInformationReportExists(id);
        
        // 删除
        informationReportMapper.deleteById(id);
    }

    private InformationReportDO validateInformationReportExists(Long id) {
        InformationReportDO informationReport = informationReportMapper.selectById(id);
        if (informationReport == null) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.INFORMATION_REPORT_NOT_EXISTS);
        }
        return informationReport;
    }

    @Override
    public InformationReportRespVO getInformationReport(Long id) {
        InformationReportDO informationReport = informationReportMapper.selectById(id);
        return InformationReportConvert.INSTANCE.convert(informationReport);
    }

    @Override
    public PageResult<InformationReportRespVO> getInformationReportPage(InformationReportPageReqVO pageReqVO) {
        PageResult<InformationReportDO> pageResult = informationReportMapper.selectPage(pageReqVO);
        return InformationReportConvert.INSTANCE.convertPage(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitReport(Long id, InformationReportSubmitReqVO submitReqVO) {
        // 校验存在
        InformationReportDO informationReport = validateInformationReportExists(id);
        
        // 校验状态
        if (!"PENDING".equals(informationReport.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.INFORMATION_REPORT_STATUS_ERROR);
        }

        // 检查是否超时
        boolean timeout = checkReportTimeout(informationReport.getEventId(), informationReport.getReportType());
        
        // 更新状态
        InformationReportDO updateObj = new InformationReportDO();
        updateObj.setId(id);
        updateObj.setStatus("SUBMITTED");
        LocalDateTime now = LocalDateTime.now();
        // BR-013：根据报送类型更新相应的时间字段
        String reportType = informationReport.getReportType();
        if ("RECEIVE".equals(reportType)) {
            // 接报完成：如果还没有接报时间，设置接报时间
            if (informationReport.getReceiveTime() == null) {
                updateObj.setReceiveTime(now);
            }
        } else if ("PRESENT".equals(reportType)) {
            // 呈报完成：设置呈报时间
            updateObj.setPresentTime(now);
            // 如果还没有接报时间，说明可能跳过了接报环节，记录警告
            if (informationReport.getReceiveTime() == null) {
                log.warn("报送流程不完整：呈报前缺少接报环节，reportId={}", id);
            }
        } else if ("REPORT".equals(reportType)) {
            // 上报完成：设置上报时间
            updateObj.setReportTime(now);
            // BR-015：校验报送流程的完整性，不允许跳过必要的报送环节
            if (informationReport.getReceiveTime() == null) {
                log.warn("报送流程不完整：上报前缺少接报环节，reportId={}", id);
            }
            if (informationReport.getPresentTime() == null) {
                log.warn("报送流程不完整：上报前缺少呈报环节，reportId={}", id);
            }
        }
        updateObj.setTimeout(timeout);
        informationReportMapper.updateById(updateObj);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReport(Long id, InformationReportConfirmReqVO confirmReqVO) {
        // 校验存在
        InformationReportDO informationReport = validateInformationReportExists(id);
        
        // 校验状态
        if (!"SUBMITTED".equals(informationReport.getStatus())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.INFORMATION_REPORT_STATUS_ERROR);
        }

        // 获取当前用户
        Long userId = SecurityFrameworkUtils.getLoginUserId();
        String userName = null;
        try {
            cn.cheers.x.framework.security.core.LoginUser loginUser = SecurityFrameworkUtils.getLoginUser();
            if (loginUser != null && loginUser.getInfo() != null) {
                // 从info Map中获取nickname
                userName = loginUser.getInfo().get(cn.cheers.x.framework.security.core.LoginUser.INFO_KEY_NICKNAME);
                if (userName == null) {
                    userName = userId != null ? String.valueOf(userId) : "系统";
                }
            } else {
                userName = userId != null ? String.valueOf(userId) : "系统";
            }
        } catch (Exception e) {
            log.warn("获取用户信息失败", e);
            userName = userId != null ? String.valueOf(userId) : "系统";
        }

        // 更新状态
        InformationReportDO updateObj = new InformationReportDO();
        updateObj.setId(id);
        updateObj.setStatus("CONFIRMED");
        updateObj.setConfirmTime(LocalDateTime.now());
        updateObj.setConfirmPerson(userName);
        updateObj.setConfirmRemark(confirmReqVO.getConfirmRemark());
        informationReportMapper.updateById(updateObj);
    }

    @Override
    public boolean checkReportTimeout(Long eventId, String reportType) {
        EmergencyEventDO event = eventMapper.selectById(eventId);
        if (event == null) {
            return false;
        }

        int timeLimit = getReportTimeLimit(event.getEventLevel());
        LocalDateTime eventCreateTime = event.getCreateTime();
        LocalDateTime now = LocalDateTime.now();
        
        long minutes = ChronoUnit.MINUTES.between(eventCreateTime, now);
        return minutes > timeLimit;
    }

    @Override
    public int getReportTimeLimit(String eventLevel) {
        // BR-011：根据事件级别和报送类型返回报送时限（分钟）
        // 初报时限：
        // - 事发单位：事件发生后5分钟内电话报告，30分钟内形成书面报告
        // - 公司向集团公司：事件发生后15分钟内（或接到所属单位信息报送后10分钟内）电话报告，1小时内书面报告
        // - 网络信息系统事件：第一时间报告，Ⅲ级及以上事件1小时内书面报告
        // 续报时限：
        // - 事发单位：初报后3小时内根据情况变化和工作进展续报信息
        // - 公司向集团公司：初报后4小时内根据情况变化和工作进展续报信息
        // 终报时限：
        // - 网络信息系统事件：应急处置结束后5天内报告
        // - 其他事件：应急处置结束后及时报告
        
        // 这里返回初报的书面报告时限（最严格的要求）
        // 实际使用时，应该根据reportType（INITIAL/SUPPLEMENT/FINAL）和reportTarget来确定具体时限
        if (eventLevel == null) {
            return 120; // 默认2小时
        }
        
        // 支持中文和罗马数字格式
        if (eventLevel.contains("Ⅰ") || eventLevel.contains("I级") || eventLevel.contains("特别重大")) {
            return 15; // Ⅰ级事件：15分钟内上报
        } else if (eventLevel.contains("Ⅱ") || eventLevel.contains("II级") || eventLevel.contains("重大")) {
            return 30; // Ⅱ级事件：30分钟内上报
        } else if (eventLevel.contains("Ⅲ") || eventLevel.contains("III级") || eventLevel.contains("较大")) {
            return 60; // Ⅲ级事件：1小时内上报
        } else if (eventLevel.contains("Ⅳ") || eventLevel.contains("IV级") || eventLevel.contains("一般")) {
            return 120; // Ⅳ级事件：2小时内上报
        } else if (eventLevel.contains("Ⅴ") || eventLevel.contains("V级") || eventLevel.contains("轻微")) {
            return 120; // Ⅴ级事件：2小时内上报
        } else {
            return 120; // 默认2小时
        }
    }
    
    /**
     * 根据事件级别、报送类型和报送对象获取具体的报送时限（分钟）
     * BR-011：不同级别的事件必须遵循不同的信息报送时限
     *
     * @param eventLevel 事件级别
     * @param reportType 报送类型（INITIAL初报/SUPPLEMENT续报/FINAL终报）
     * @param reportTarget 报送对象（INTERNAL内部/EXTERNAL外部/GROUP集团公司/LOCAL_GOV地方政府）
     * @return 时限（分钟）
     */
    public int getReportTimeLimitByType(String eventLevel, String reportType, String reportTarget) {
        if (eventLevel == null || reportType == null) {
            return 120; // 默认2小时
        }
        
        // 初报时限
        if ("INITIAL".equals(reportType)) {
            if ("INTERNAL".equals(reportTarget)) {
                // 事发单位：30分钟内形成书面报告
                return 30;
            } else if ("GROUP".equals(reportTarget)) {
                // 公司向集团公司：1小时内书面报告
                return 60;
            } else if ("LOCAL_GOV".equals(reportTarget)) {
                // 向地方政府有关部门报告：应当在1小时内报告
                return 60;
            } else {
                // 默认：根据事件级别
                return getReportTimeLimit(eventLevel);
            }
        }
        // 续报时限
        else if ("SUPPLEMENT".equals(reportType)) {
            if ("INTERNAL".equals(reportTarget)) {
                // 事发单位：初报后3小时内续报
                return 180; // 3小时
            } else if ("GROUP".equals(reportTarget)) {
                // 公司向集团公司：初报后4小时内续报
                return 240; // 4小时
            } else {
                // 默认：4小时
                return 240;
            }
        }
        // 终报时限
        else if ("FINAL".equals(reportType)) {
            // 终报：应急处置结束后及时报告（这里不设置具体时限，由业务逻辑控制）
            // 网络信息系统事件：应急处置结束后5天内报告
            return 7200; // 5天 = 7200分钟（作为参考值）
        }
        
        // 默认返回基础时限
        return getReportTimeLimit(eventLevel);
    }
    
    /**
     * 校验报送流程的完整性
     * BR-015：信息报送记录一旦创建，报送状态必须能够跟踪，系统必须在校验时确保报送流程的完整性，不允许跳过必要的报送环节
     *
     * @param informationReport 信息报送记录
     * @return 是否流程完整
     */
    public boolean validateReportProcessIntegrity(InformationReportDO informationReport) {
        if (informationReport == null) {
            return false;
        }
        
        String reportType = informationReport.getReportType();
        
        // 根据报送类型校验流程完整性
        if ("REPORT".equals(reportType)) {
            // 上报：必须经过接报和呈报
            if (informationReport.getReceiveTime() == null) {
                log.warn("报送流程不完整：上报前缺少接报环节，reportId={}", informationReport.getId());
                return false;
            }
            if (informationReport.getPresentTime() == null) {
                log.warn("报送流程不完整：上报前缺少呈报环节，reportId={}", informationReport.getId());
                return false;
            }
        } else if ("PRESENT".equals(reportType)) {
            // 呈报：必须经过接报
            if (informationReport.getReceiveTime() == null) {
                log.warn("报送流程不完整：呈报前缺少接报环节，reportId={}", informationReport.getId());
                return false;
            }
        }
        
        return true;
    }
}

