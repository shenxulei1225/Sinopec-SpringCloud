package cn.cheers.x.module.dynamicbusiness.event.listener;

import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityRelationDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entity.EntityRelationMapper;
import cn.cheers.x.module.dynamicbusiness.event.EntityNameChangedEvent;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体名称变更监听器
 * 
 * <p>监听 EntityNameChangedEvent 事件，当目标实体的名称发生变更时，
 * 更新所有引用该实体的源实体的 customFields 中的名称字段。</p>
 * 
 * <h3>处理逻辑</h3>
 * <ol>
 *   <li>查询所有引用该实体的关联记录（EntityRelationDO）</li>
 *   <li>对于每个关联记录，获取源实体的 customFields</li>
 *   <li>根据字段类型（单选/多选）更新名称字段：
 *     <ul>
 *       <li>单选关联：更新 `{fieldCode}_name` 字段</li>
 *       <li>多选关联：更新 `{fieldCode}_names` 数组中对应的名称</li>
 *     </ul>
 *   </li>
 *   <li>保存更新后的 customFields</li>
 * </ol>
 * 
 * <h3>存储格式</h3>
 * <ul>
 *   <li>单选关联：{ "device_id": 301, "device_id_name": "挖掘机" }</li>
 *   <li>多选关联：{ "devices": [301, 302], "devices_names": ["挖掘机", "吊车"] }</li>
 * </ul>
 * 
 * <h3>需求引用</h3>
 * <ul>
 *   <li>数据一致性：确保关联展示的名称与实际名称保持同步</li>
 * </ul>
 * 
 * @author 基础服务模块
 */
@Component
@Slf4j
public class EntityNameChangedListener {

    @Resource
    private EntityRelationMapper entityRelationMapper;

    @Resource
    private EntityMapper entityMapper;

    /**
     * 单选关联类型
     */
    private static final String RELATION_TYPE_ENTITY_REF = "ENTITY_REF";

    /**
     * 多选关联类型
     */
    private static final String RELATION_TYPE_ENTITY_REF_MULTI = "ENTITY_REF_MULTI";

    /**
     * 处理实体名称变更事件
     * 
     * @param event 实体名称变更事件
     */
    @EventListener
    @Async // 异步处理，不阻塞主流程
    public void onEntityNameChanged(EntityNameChangedEvent event) {
        log.info("[onEntityNameChanged][收到实体名称变更事件: {}]", event);

        // 检查名称是否真正发生了变更
        if (!event.isNameActuallyChanged()) {
            log.debug("[onEntityNameChanged][名称未发生变更，跳过处理]");
            return;
        }

        Long tenantId = event.getTenantId();
        if (tenantId == null) {
            // 如果没有租户 ID，直接处理
            processNameChange(event);
            return;
        }

        // 在租户上下文中执行
        TenantUtils.execute(tenantId, () -> {
            try {
                processNameChange(event);
            } catch (Exception e) {
                log.error("[onEntityNameChanged][处理名称变更失败: {}]", event, e);
                // 不抛出异常，避免影响主流程
            }
        });
    }

    /**
     * 处理名称变更
     * 
     * @param event 名称变更事件
     */
    @Transactional(rollbackFor = Exception.class)
    protected void processNameChange(EntityNameChangedEvent event) {
        Long entityId = event.getEntityId();
        String newName = event.getNewName();

        log.debug("[processNameChange][开始处理名称变更: entityId={}, newName={}]", entityId, newName);

        // 查询所有引用该实体的关联记录
        List<EntityRelationDO> relations = entityRelationMapper.selectByTargetEntityId(entityId);
        if (CollectionUtils.isEmpty(relations)) {
            log.debug("[processNameChange][没有找到引用该实体的关联记录，跳过处理]");
            return;
        }

        log.info("[processNameChange][找到 {} 个引用该实体的关联记录]", relations.size());

        int successCount = 0;
        int failCount = 0;

        // 遍历每个关联记录，更新源实体的 customFields
        for (EntityRelationDO relation : relations) {
            try {
                boolean updated = updateSourceEntityName(relation, entityId, newName);
                if (updated) {
                    successCount++;
                }
            } catch (Exception e) {
                failCount++;
                log.warn("[processNameChange][更新源实体名称失败: sourceEntityId={}, fieldCode={}]",
                        relation.getSourceEntityId(), relation.getFieldCode(), e);
            }
        }

        log.info("[processNameChange][名称变更处理完成: entityId={}, successCount={}, failCount={}]",
                entityId, successCount, failCount);
    }

    /**
     * 更新源实体的 customFields 中的名称字段
     * 
     * @param relation 关联记录
     * @param targetEntityId 目标实体 ID
     * @param newName 新名称
     * @return 是否更新成功
     */
    private boolean updateSourceEntityName(EntityRelationDO relation, Long targetEntityId, String newName) {
        Long sourceEntityId = relation.getSourceEntityId();
        String fieldCode = relation.getFieldCode();
        String relationType = relation.getRelationType();

        // 获取源实体
        EntityDO sourceEntity = entityMapper.selectById(sourceEntityId);
        if (sourceEntity == null) {
            log.warn("[updateSourceEntityName][源实体不存在: sourceEntityId={}]", sourceEntityId);
            return false;
        }

        // 解析 customFields
        Map<String, Object> customFields = sourceEntity.getCustomFields();
        if (customFields == null || customFields.isEmpty()) {
            log.debug("[updateSourceEntityName][源实体 customFields 为空，跳过更新]");
            return false;
        }

        Map<String, Object> mutableFields = new HashMap<>(customFields);

        boolean updated;
        if (RELATION_TYPE_ENTITY_REF_MULTI.equals(relationType)) {
            updated = updateMultiRefName(mutableFields, fieldCode, targetEntityId, newName);
        } else {
            updated = updateSingleRefName(mutableFields, fieldCode, newName);
        }

        if (!updated) {
            return false;
        }

        EntityDO update = new EntityDO();
        update.setId(sourceEntityId);
        update.setCustomFields(mutableFields);
        entityMapper.updateById(update);

        log.debug("[updateSourceEntityName][更新源实体名称成功: sourceEntityId={}, fieldCode={}]",
                sourceEntityId, fieldCode);
        return true;
    }

    /**
     * 更新单选关联字段的名称
     * 
     * <p>存储格式：{ "device_id": 301, "device_id_name": "挖掘机" }</p>
     * 
     * @param customFields customFields JSON 对象
     * @param fieldCode 字段编码
     * @param newName 新名称
     * @return 是否更新成功
     */
    private boolean updateSingleRefName(Map<String, Object> customFields, String fieldCode, String newName) {
        String nameFieldCode = fieldCode + "_name";
        
        // 检查名称字段是否存在
        if (!customFields.containsKey(nameFieldCode)) {
            log.debug("[updateSingleRefName][名称字段不存在: {}]", nameFieldCode);
            return false;
        }

        // 更新名称
        customFields.put(nameFieldCode, newName);
        return true;
    }

    /**
     * 更新多选关联字段的名称
     * 
     * <p>存储格式：{ "devices": [301, 302], "devices_names": ["挖掘机", "吊车"] }</p>
     * 
     * @param customFields customFields JSON 对象
     * @param fieldCode 字段编码
     * @param targetEntityId 目标实体 ID
     * @param newName 新名称
     * @return 是否更新成功
     */
    private boolean updateMultiRefName(Map<String, Object> customFields, String fieldCode,
                                       Long targetEntityId, String newName) {
        String idsFieldCode = fieldCode;
        String namesFieldCode = fieldCode + "_names";

        // 获取 ID 数组
        Object idsObj = customFields.get(idsFieldCode);
        if (idsObj == null) {
            log.debug("[updateMultiRefName][ID 数组字段不存在: {}]", idsFieldCode);
            return false;
        }

        // 获取名称数组
        Object namesObj = customFields.get(namesFieldCode);
        if (namesObj == null) {
            log.debug("[updateMultiRefName][名称数组字段不存在: {}]", namesFieldCode);
            return false;
        }

        // 解析 ID 数组
        List<Long> ids;
        try {
            if (idsObj instanceof List) {
                ids = new ArrayList<>();
                for (Object item : (List<?>) idsObj) {
                    if (item instanceof Number) {
                        ids.add(((Number) item).longValue());
                    }
                }
            } else {
                log.warn("[updateMultiRefName][ID 数组格式不正确: {}]", idsObj);
                return false;
            }
        } catch (Exception e) {
            log.warn("[updateMultiRefName][解析 ID 数组失败]", e);
            return false;
        }

        // 解析名称数组
        List<String> names;
        try {
            if (namesObj instanceof List) {
                names = new ArrayList<>();
                for (Object item : (List<?>) namesObj) {
                    names.add(item != null ? item.toString() : null);
                }
            } else {
                log.warn("[updateMultiRefName][名称数组格式不正确: {}]", namesObj);
                return false;
            }
        } catch (Exception e) {
            log.warn("[updateMultiRefName][解析名称数组失败]", e);
            return false;
        }

        // 查找目标实体 ID 在数组中的位置
        int index = ids.indexOf(targetEntityId);
        if (index < 0) {
            log.debug("[updateMultiRefName][目标实体 ID 不在数组中: targetEntityId={}]", targetEntityId);
            return false;
        }

        // 确保名称数组长度足够
        while (names.size() <= index) {
            names.add(null);
        }

        // 更新名称
        names.set(index, newName);
        customFields.put(namesFieldCode, names);

        return true;
    }
}
