package cn.cheers.x.module.dynamicbusiness.service.linkage;

/**
 * 联动方式里的一对：来源设备对上当前事件后，对目标设备执行动作。
 *
 * @param index 这一对在联动方式里的位置，从 1 起。执行项用它区分，不是绑定子实体编号。
 * @param sourceEquipmentId 来源设备。空表示这条对当前事件来源都适用。
 * @param targetEquipmentId 动作作用的目标设备。
 * @param actionId 这一对要调用的动作。
 */
public record LinkagePair(long index, Long sourceEquipmentId, long targetEquipmentId, long actionId) {
}
