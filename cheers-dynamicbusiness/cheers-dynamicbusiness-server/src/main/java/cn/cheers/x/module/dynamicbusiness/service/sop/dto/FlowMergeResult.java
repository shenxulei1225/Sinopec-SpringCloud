package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * merge 结果：成功返回 effective；缺口返回 gapCodes（不静默补参）。
 */
@Data
public class FlowMergeResult {

    private boolean ok;
    private FlowEffectiveConfig effective;
    private List<String> gapCodes = new ArrayList<>();

    public static FlowMergeResult success(FlowEffectiveConfig effective) {
        FlowMergeResult r = new FlowMergeResult();
        r.setOk(true);
        r.setEffective(effective);
        return r;
    }

    public static FlowMergeResult failure(List<String> gapCodes) {
        FlowMergeResult r = new FlowMergeResult();
        r.setOk(false);
        r.setGapCodes(gapCodes != null ? gapCodes : List.of());
        return r;
    }
}
