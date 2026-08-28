package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * merge 结果：成功返回 effective；缺口返回 gapCodes（不静默补参）。
 */
@Data
public class SopMergeResult {

    private boolean ok;
    private SopEffectiveConfig effective;
    private List<String> gapCodes = new ArrayList<>();

    public static SopMergeResult success(SopEffectiveConfig effective) {
        SopMergeResult r = new SopMergeResult();
        r.setOk(true);
        r.setEffective(effective);
        return r;
    }

    public static SopMergeResult failure(List<String> gapCodes) {
        SopMergeResult r = new SopMergeResult();
        r.setOk(false);
        r.setGapCodes(gapCodes != null ? gapCodes : List.of());
        return r;
    }
}
