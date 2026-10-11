package cn.cheers.x.module.dynamicbusiness.service.entity.mount;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.framework.hierarchy.IdTreeHierarchy;
import cn.cheers.x.module.dynamicbusiness.service.category.hierarchy.AdvancedCategoryEntityHierarchyService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 保存主机的挂载点时，改被选设备的上级。
 *
 * 负责：这一台主机的挂载点写进库之后，把选中设备的上级写成这台主机；从行里去掉的、上级正是这台主机的，上级清掉。
 * 不负责：画详情上的行；摄像机型号上的主类型、子类型。
 * 禁止：人在这里改上级的同时，再从关系图拖一次同一台设备。
 */
@Component
public class PayloadMountParentSync {

    @Resource
    private EntityCoreService entityCoreService;
    @Resource
    private AdvancedCategoryEntityHierarchyService advancedCategoryEntityHierarchyService;

    /**
     * 请求里带了挂载点才调用。previous 是保存前的值，next 是这次要保存的值。
     */
    public void sync(String entityTypeCode, long hostId, Object previous, Object next) {
        if (entityTypeCode == null || entityTypeCode.isBlank() || hostId <= 0) {
            return;
        }
        List<PayloadMounts.Row> before = PayloadMounts.parse(previous);
        List<PayloadMounts.Row> after = PayloadMounts.parse(next);
        Set<Long> ids = new LinkedHashSet<>();
        ids.addAll(PayloadMounts.equipmentIds(before));
        ids.addAll(PayloadMounts.equipmentIds(after));
        Map<Long, Long> parents = new LinkedHashMap<>();
        for (Long id : ids) {
            EntityDO entity = entityCoreService.get(id, entityTypeCode);
            if (entity == null) {
                continue;
            }
            parents.put(id, IdTreeHierarchy.normalizeParentId(entity.getParentId()));
        }
        List<PayloadMounts.Move> moves = PayloadMounts.plan(hostId, before, after, parents);
        for (PayloadMounts.Move move : moves) {
            entityCoreService.moveEntity(move.equipmentId(), entityTypeCode, move.newParentId());
            advancedCategoryEntityHierarchyService.syncCategoryAfterEntityMove(
                    move.equipmentId(), entityTypeCode, move.newParentId());
        }
    }
}
