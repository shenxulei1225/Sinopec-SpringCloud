package cn.cheers.x.module.platform.routing.planner;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsGivenOrderStrategy implements StopOrderStrategy {

    @Override
    public List<String> order(List<String> stopIds, CostMatrix matrix) {
        return List.copyOf(stopIds);
    }

}
