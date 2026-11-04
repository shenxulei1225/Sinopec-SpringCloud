package cn.cheers.x.module.dynamicbusiness.controller.admin.strategy.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 一次判定的返回。matched 交给调用方执行。skipped 说明哪条启用的策略没法参与。
 */
@Data
public class StrategyDecideRespVO {

    private List<Matched> matched = new ArrayList<>();
    private List<Skipped> skipped = new ArrayList<>();

    @Data
    public static class Matched {
        private Long id;
        private String name;
        private String actionMode;
        private List<String> actionIds;
    }

    @Data
    public static class Skipped {
        private Long id;
        private String name;
        private String reason;
    }
}
