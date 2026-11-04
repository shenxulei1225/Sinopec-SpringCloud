package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import org.eclipse.milo.opcua.stack.core.types.builtin.StatusCode;
import org.eclipse.milo.opcua.stack.core.types.builtin.Variant;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 采集那条 OPC UA 会话上的写入。
 * 节点只来自这次浏览对上的写点。不建订阅，不另开会话。
 * 写入应答只说明值有没有交给现场网关。
 */
@Component
public class GatewaySessionWrites {

    /** 采集会话没连上时，指令没有发出去。不是联动没触发。 */
    public static final String OPC_UA_SERVER_OFFLINE = "OPC UA 服务器不在线，指令发送失败";

    private final Object lock = new Object();
    private OpcUaClient client;
    private Map<String, String> nodeByBinding = Map.of();

    /** 会话连上后记下写点的节点。写点不进入订阅。 */
    public void attach(OpcUaClient session, Map<String, GatewayPointRef> writeMatches) {
        Map<String, String> next = new LinkedHashMap<>();
        for (Map.Entry<String, GatewayPointRef> entry : writeMatches.entrySet()) {
            next.put(entry.getValue().getBindingCode(), entry.getKey());
        }
        synchronized (lock) {
            client = session;
            nodeByBinding = Map.copyOf(next);
        }
    }

    /** 会话断开后不能再写。下一次连上再记节点。 */
    public void detach() {
        synchronized (lock) {
            client = null;
            nodeByBinding = Map.of();
        }
    }

    /** 点位编码到浏览得到的节点标识。没有就是还没对上。 */
    public Map<String, String> nodeByBinding() {
        synchronized (lock) {
            return nodeByBinding;
        }
    }

    /** 当前这条采集会话是否还连着。没连着就不能写，也不去看浏览表。 */
    public boolean attached() {
        synchronized (lock) {
            return client != null;
        }
    }

    /**
     * 把已经定好的节点和开关值交给现场网关。
     * 会话不在，或现场网关没收下，直接拒绝。
     */
    public void write(List<PlannedWrite> writes) {
        if (writes == null || writes.isEmpty()) {
            throw new ServiceException(400, "这条动作还没对上写点");
        }
        OpcUaClient session = openSession();
        List<NodeId> nodes = new ArrayList<>();
        List<DataValue> values = new ArrayList<>();
        for (PlannedWrite write : writes) {
            nodes.add(NodeId.parse(write.nodeId()));
            values.add(DataValue.valueOnly(new Variant(write.on())));
        }
        List<StatusCode> statuses = send(session, nodes, values);
        for (StatusCode status : statuses) {
            if (status == null || !status.isGood()) {
                throw new ServiceException(400, "现场网关没有收下这次写入");
            }
        }
    }

    private OpcUaClient openSession() {
        synchronized (lock) {
            if (client == null) {
                throw new ServiceException(400, OPC_UA_SERVER_OFFLINE);
            }
            return client;
        }
    }

    private static List<StatusCode> send(OpcUaClient session, List<NodeId> nodes, List<DataValue> values) {
        try {
            return session.writeValues(nodes, values).get(15, TimeUnit.SECONDS);
        } catch (Exception ex) {
            throw new ServiceException(400, "现场网关没有收下这次写入");
        }
    }
}
