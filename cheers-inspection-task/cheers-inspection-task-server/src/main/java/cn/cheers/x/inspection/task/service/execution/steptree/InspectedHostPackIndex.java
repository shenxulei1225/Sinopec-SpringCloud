package cn.cheers.x.inspection.task.service.execution.steptree;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import cn.cheers.x.inspection.task.model.task.InspectionContent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 按任务上勾的被检查对象和检查项，索引「检查项 → 该被检设备的参数包」。
 * <p>禁止：用执行设备（机器人/无人机）的包冒充被检设备；步骤上存参。
 */
public final class InspectedHostPackIndex {

    private final Map<Long, HostSopParamPack> packByItem = new LinkedHashMap<>();

    private InspectedHostPackIndex() {
    }

    public static InspectedHostPackIndex from(
            InspectionContent content,
            Function<Long, HostSopParamPack> loadByEquipmentId
    ) {
        InspectedHostPackIndex index = new InspectedHostPackIndex();
        if (content == null) {
            throw ServiceExceptionUtil.invalidParamException("任务没有勾选被检查对象和检查项");
        }
        int objectCount = 0;
        objectCount += indexObjects(content.getCustomObjects(), loadByEquipmentId, index);
        if (content.getGroups() != null) {
            for (InspectionContent.ObjectGroup group : content.getGroups()) {
                if (group != null) {
                    objectCount += indexObjects(group.getObjects(), loadByEquipmentId, index);
                }
            }
        }
        if (objectCount == 0) {
            throw ServiceExceptionUtil.invalidParamException("任务没有勾选被检查对象和检查项");
        }
        return index;
    }

    public HostSopParamPack packForItem(Long inspectionItemId) {
        if (inspectionItemId == null) {
            return HostSopParamPack.empty();
        }
        HostSopParamPack pack = packByItem.get(inspectionItemId);
        return pack == null ? HostSopParamPack.empty() : pack;
    }

    private static int indexObjects(
            java.util.List<InspectionContent.ObjectContent> objects,
            Function<Long, HostSopParamPack> loadByEquipmentId,
            InspectedHostPackIndex index
    ) {
        if (objects == null) {
            return 0;
        }
        int count = 0;
        for (InspectionContent.ObjectContent object : objects) {
            if (object == null || object.getObjectId() == null) {
                continue;
            }
            count++;
            HostSopParamPack pack = loadByEquipmentId.apply(object.getObjectId());
            if (object.getItems() == null) {
                continue;
            }
            for (InspectionContent.ItemContent item : object.getItems()) {
                if (item != null && item.getItemId() != null) {
                    index.packByItem.put(item.getItemId(), pack);
                }
            }
        }
        return count;
    }
}
