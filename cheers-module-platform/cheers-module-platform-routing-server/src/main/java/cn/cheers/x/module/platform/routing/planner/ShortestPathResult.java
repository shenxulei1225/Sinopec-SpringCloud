package cn.cheers.x.module.platform.routing.planner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShortestPathResult {

    private List<String> nodeIds;
    private List<String> edgeIds;
    private double totalCost;
}
