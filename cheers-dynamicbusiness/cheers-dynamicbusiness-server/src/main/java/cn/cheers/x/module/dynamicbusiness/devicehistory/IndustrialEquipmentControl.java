package cn.cheers.x.module.dynamicbusiness.devicehistory;

import cn.cheers.x.framework.common.exception.ServiceException;
import org.springframework.stereotype.Service;

/**
 * 按设备动作下发工业设备的写点。
 * 先对上点位对照，再交给当前这条 OPC UA 会话。
 * 不订写点，不猜节点，不把应答当成设备已经打开。巡检和管廊走同一段。
 */
@Service
public class IndustrialEquipmentControl {

    private final IndustrialPointBooks books;
    private final GatewaySessionWrites session;
    private final IndustrialEquipmentBoard board;

    public IndustrialEquipmentControl(IndustrialPointBooks books, GatewaySessionWrites session,
                                      IndustrialEquipmentBoard board) {
        this.books = books;
        this.session = session;
        this.board = board;
    }

    /**
     * 对这台设备执行一条设备动作。
     * 会话不在时先拒绝，不把空浏览表说成写点没有节点标识。
     * 动作没对上写点、型号不对、没有节点标识、不在远程、还没有远程读数，都拒绝。
     * 禁止编一个节点标识。
     */
    public void submit(long tenantId, String actionCode, long equipmentId) {
        if (actionCode == null || actionCode.isBlank()) {
            throw new ServiceException(400, "要做哪一个动作");
        }
        IndustrialPointBooks.ActionRow action = books.action(tenantId, actionCode.trim());
        if (action == null) {
            throw new ServiceException(404, "没有这个动作");
        }
        if (!"DEVICE".equals(action.domain())) {
            throw new ServiceException(400, "这条动作不是设备动作");
        }
        String modelCode = books.equipmentModelCode(tenantId, equipmentId);
        if (modelCode == null) {
            throw new ServiceException(404, "没有这台设备");
        }
        if (!session.attached()) {
            throw new ServiceException(400, GatewaySessionWrites.OPC_UA_SERVER_OFFLINE);
        }
        IndustrialWritePlan.Plan plan = IndustrialWritePlan.plan(
                IndustrialWriteRecipe.parse(action.attrsJson()),
                modelCode,
                books.writeRows(tenantId, equipmentId),
                session.nodeByBinding());
        if (plan.refusal() != null) {
            throw new ServiceException(400, plan.refusal());
        }
        board.requireControl(tenantId, equipmentId);
        session.write(plan.writes());
    }
}
