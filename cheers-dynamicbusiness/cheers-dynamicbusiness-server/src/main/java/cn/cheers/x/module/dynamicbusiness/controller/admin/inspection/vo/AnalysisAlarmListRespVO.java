package cn.cheers.x.module.dynamicbusiness.controller.admin.inspection.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 跨任务的分析告警清单。只含已经写成异常记录的告警。
 */
@Data
public class AnalysisAlarmListRespVO {

    private List<AnalysisAlarmItemRespVO> list;

    @Schema(description = "还有更早的告警没有列入本次结果")
    private boolean truncated;
}
