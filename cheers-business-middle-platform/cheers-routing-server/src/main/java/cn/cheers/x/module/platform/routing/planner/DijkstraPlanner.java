package cn.cheers.x.module.platform.routing.planner;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_UNREACHABLE;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;

@Component
public class DijkstraPlanner {

    public ShortestPathResult shortestPath(String from, String to, GraphView view) {
        if (view.getNode(from) == null || view.getNode(to) == null) {
            throw exception(ROUTE_UNREACHABLE);
        }
        if (from.equals(to)) {
            return ShortestPathResult.builder()
                    .nodeIds(List.of(from))
                    .edgeIds(List.of())
                    .totalCost(0D)
                    .build();
        }

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prevNode = new HashMap<>();
        Map<String, String> prevEdge = new HashMap<>();
        PriorityQueue<NodeCost> queue = new PriorityQueue<>(Comparator.comparingDouble(NodeCost::cost));

        dist.put(from, 0D);
        queue.offer(new NodeCost(from, 0D));

        while (!queue.isEmpty()) {
            NodeCost current = queue.poll();
            String nodeId = current.nodeId();
            double cost = current.cost();
            if (cost > dist.getOrDefault(nodeId, Double.POSITIVE_INFINITY)) {
                continue;
            }
            if (nodeId.equals(to)) {
                break;
            }
            for (GraphView.AdjacencyEdge edge : view.outgoing(nodeId)) {
                String next = edge.getToNodeId();
                double nextCost = cost + edge.getWeight();
                if (nextCost < dist.getOrDefault(next, Double.POSITIVE_INFINITY)) {
                    dist.put(next, nextCost);
                    prevNode.put(next, nodeId);
                    prevEdge.put(next, edge.getEdgeId());
                    queue.offer(new NodeCost(next, nextCost));
                }
            }
        }

        if (!dist.containsKey(to)) {
            throw exception(ROUTE_UNREACHABLE);
        }

        List<String> nodeIds = new ArrayList<>();
        List<String> edgeIds = new ArrayList<>();
        String cursor = to;
        while (cursor != null) {
            nodeIds.add(cursor);
            String edgeId = prevEdge.get(cursor);
            if (edgeId != null) {
                edgeIds.add(edgeId);
            }
            cursor = prevNode.get(cursor);
        }
        Collections.reverse(nodeIds);
        Collections.reverse(edgeIds);

        return ShortestPathResult.builder()
                .nodeIds(nodeIds)
                .edgeIds(edgeIds)
                .totalCost(dist.get(to))
                .build();
    }

    private record NodeCost(String nodeId, double cost) {
    }
}
