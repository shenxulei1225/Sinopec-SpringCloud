package cn.cheers.x.module.platform.runtime.service;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionAppendReqDTO;
import cn.cheers.x.module.platform.runtime.api.dto.ProcessTimelineActionRespDTO;
import cn.cheers.x.module.platform.runtime.dal.dataobject.ProcessTimelineActionDO;
import cn.cheers.x.module.platform.runtime.dal.mysql.ProcessTimelineActionMapper;
import cn.cheers.x.module.platform.runtime.enums.ErrorCodeConstants;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProcessTimelineServiceImpl implements ProcessTimelineService {

    @Resource
    private ProcessTimelineActionMapper processTimelineActionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long append(ProcessTimelineActionAppendReqDTO request) {
        if (request == null
                || !StringUtils.hasText(request.getTargetType())
                || !StringUtils.hasText(request.getTargetId())
                || request.getOccurredAt() == null
                || !StringUtils.hasText(request.getActionCode())
                || !StringUtils.hasText(request.getHowSummary())) {
            throw ServiceExceptionUtil.exception(ErrorCodeConstants.PROCESS_TIMELINE_APPEND_INVALID);
        }
        ProcessTimelineActionDO row = ProcessTimelineActionDO.builder()
                .siteId(request.getSiteId())
                .targetType(request.getTargetType().trim())
                .targetId(request.getTargetId().trim())
                .occurredAt(request.getOccurredAt())
                .actorId(blankToNull(request.getActorId()))
                .actorName(blankToNull(request.getActorName()))
                .actionCode(request.getActionCode().trim())
                .howSummary(request.getHowSummary().trim())
                .payloadJson(blankToNull(request.getPayloadJson()))
                .build();
        processTimelineActionMapper.insert(row);
        return row.getId();
    }

    @Override
    public PageResult<ProcessTimelineActionRespDTO> pageByTarget(
            String targetType, String targetId, Integer pageNo, Integer pageSize) {
        if (!StringUtils.hasText(targetType) || !StringUtils.hasText(targetId)) {
            return PageResult.empty();
        }
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(pageNo == null || pageNo < 1 ? 1 : pageNo);
        pageParam.setPageSize(pageSize == null || pageSize < 1 ? 20 : pageSize);

        PageResult<ProcessTimelineActionDO> page = processTimelineActionMapper.selectPage(
                pageParam,
                new LambdaQueryWrapperX<ProcessTimelineActionDO>()
                        .eq(ProcessTimelineActionDO::getTargetType, targetType.trim())
                        .eq(ProcessTimelineActionDO::getTargetId, targetId.trim())
                        .orderByDesc(ProcessTimelineActionDO::getOccurredAt)
                        .orderByDesc(ProcessTimelineActionDO::getId));

        List<ProcessTimelineActionRespDTO> list = page.getList().stream().map(this::toResp).toList();
        return new PageResult<>(list, page.getTotal());
    }

    private ProcessTimelineActionRespDTO toResp(ProcessTimelineActionDO row) {
        return ProcessTimelineActionRespDTO.builder()
                .id(row.getId())
                .targetType(row.getTargetType())
                .targetId(row.getTargetId())
                .occurredAt(row.getOccurredAt())
                .actorId(row.getActorId())
                .actorName(row.getActorName())
                .actionCode(row.getActionCode())
                .howSummary(row.getHowSummary())
                .payloadJson(row.getPayloadJson())
                .siteId(row.getSiteId())
                .source("platform")
                .build();
    }

    private static String blankToNull(String v) {
        return StringUtils.hasText(v) ? v.trim() : null;
    }
}
