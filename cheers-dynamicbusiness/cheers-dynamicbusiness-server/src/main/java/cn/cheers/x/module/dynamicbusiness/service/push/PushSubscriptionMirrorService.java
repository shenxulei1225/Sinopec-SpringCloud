package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把页面连接的订阅、取消、断开写进事件订阅目录。
 * 调用前目录种类必须能按固定规则准备好。
 * 不负责把消息送到浏览器，也不在发消息时查询这本目录。
 */
@Service
public class PushSubscriptionMirrorService {

    @Resource
    private PushCatalogEnsure pushCatalogEnsure;
    @Resource
    private EntityService entityService;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;

    /**
     * 页面订了一个主题。主题不在目录种类里则拒绝，不建行。
     * 同一条连接是一个订阅方。对象列写订阅方名称。
     * 这次听的事件主题挂到这一行的引用上，详情栏读这些引用。
     */
    @Transactional(rollbackFor = Exception.class)
    public void onSubscribed(String clientId, long userId, String topic, String subscriberName) {
        requirePageClient(clientId);
        requireLengths(clientId, topic);
        if (subscriberName == null || subscriberName.isBlank()) {
            throw new ServiceException(400, "这场订阅还写不出订阅方，没有写入订阅");
        }
        if (!PushTopicKinds.matches(topic)) {
            throw new ServiceException(400, "这个主题不在推送目录的种类里，没有写入订阅");
        }
        pushCatalogEnsure.ensure();
        String trimmedClient = clientId.trim();
        String trimmedTopic = topic.trim();
        EntityRespDTO row = findByClient(trimmedClient);
        if (row == null) {
            createSubscriber(trimmedClient, userId, trimmedTopic, subscriberName.trim());
            return;
        }
        String current = field(row, pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME));
        String names = appendTopicName(current, trimmedTopic);
        refreshSubscriber(row, subscriberName.trim(), names);
    }

    /**
     * 页面取消一个主题。从这一行的引用里拿掉对应的事件主题。
     * 这一行已经没有主题时，删掉这个订阅方。
     */
    @Transactional(rollbackFor = Exception.class)
    public void onUnsubscribed(String clientId, String topic) {
        requirePageClient(clientId);
        if (topic == null || topic.isBlank() || !PushTopicKinds.matches(topic)) {
            throw new ServiceException(400, "这个主题不在推送目录的种类里，没有删除订阅");
        }
        if (!pushCatalogEnsure.subscriptionCatalogExists()) {
            return;
        }
        EntityRespDTO row = findByClient(clientId.trim());
        if (row == null || row.getId() == null) {
            return;
        }
        String remaining = removeTopicName(
                field(row, pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME)), topic.trim());
        if (remaining.isBlank()) {
            deleteRow(row.getId());
            return;
        }
        refreshSubscriber(row, PushCatalogCodes.STATION_LIVE_MONITOR, remaining);
    }

    /**
     * 页面连接断开。删掉这条连接在目录里的全部订阅行。
     * 目录还没建过时，没有行可删。
     */
    @Transactional(rollbackFor = Exception.class)
    public void onDisconnected(String clientId) {
        requirePageClient(clientId);
        if (!pushCatalogEnsure.subscriptionCatalogExists()) {
            return;
        }
        List<EntityRespDTO> rows = entityRpcFacadeService.list(PushCatalogCodes.SUBSCRIPTION_TYPE, null);
        if (rows == null || rows.isEmpty()) {
            return;
        }
        String expected = clientId.trim();
        for (EntityRespDTO row : rows) {
            if (row == null || row.getId() == null) {
                continue;
            }
            if (expected.equals(field(row, pushCatalogEnsure.fieldCode(PushCatalogCodes.CLIENT_ID)))) {
                deleteRow(row.getId());
            }
        }
    }

    /**
     * 已经写过的订阅行改成订阅方名称，并补上事件主题引用。
     * 服务准备目录之后调用。没有订阅行时什么都不做。
     */
    @Transactional(rollbackFor = Exception.class)
    public void repairSubscriberRows() {
        if (!pushCatalogEnsure.subscriptionCatalogExists()) {
            return;
        }
        List<EntityRespDTO> rows = entityRpcFacadeService.list(PushCatalogCodes.SUBSCRIPTION_TYPE, null);
        if (rows == null) {
            return;
        }
        for (EntityRespDTO row : rows) {
            if (row == null || row.getId() == null) {
                continue;
            }
            String topic = field(row, pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME));
            if (topic == null) {
                continue;
            }
            refreshSubscriber(row, PushCatalogCodes.STATION_LIVE_MONITOR, topic);
        }
    }

    private void createSubscriber(String clientId, long userId, String topic, String subscriberName) {
        EntityCreateReqVO request = new EntityCreateReqVO();
        request.setRegistryCode(PushCatalogCodes.SUBSCRIPTION_TYPE);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", PushCatalogCodes.SUBSCRIPTION_TYPE);
        baseFields.put("modelId", pushCatalogEnsure.subscriptionModelId());
        baseFields.put("name", subscriberName);
        baseFields.put("code", PushCatalogCodes.subscriberCode(clientId));
        baseFields.put("status", 1);
        request.setBaseFields(baseFields);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.CLIENT_ID), clientId);
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME), topic);
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.LISTENER_USER_ID), Long.toString(userId));
        fields.put(pushCatalogEnsure.refFieldCode(PushCatalogCodes.SUBSCRIBED_TOPICS), refValues(coveredTopicIds(topic)));
        request.setCustomFields(fields);
        entityService.create(request);
    }

    private void refreshSubscriber(EntityRespDTO row, String subscriberName, String topicNames) {
        EntityUpdateReqVO request = new EntityUpdateReqVO();
        request.setId(row.getId());
        request.setRegistryCode(PushCatalogCodes.SUBSCRIPTION_TYPE);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        if (row.getBaseFields() != null) {
            baseFields.putAll(row.getBaseFields());
        }
        baseFields.put("entityTypeCode", PushCatalogCodes.SUBSCRIPTION_TYPE);
        baseFields.put("name", subscriberName);
        baseFields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME), topicNames);
        baseFields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.ORIGIN_TEXT), "");
        request.setBaseFields(baseFields);
        Map<String, Object> fields = new LinkedHashMap<>();
        if (row.getCustomFields() != null) {
            fields.putAll(row.getCustomFields());
        }
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME), topicNames);
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.ORIGIN_TEXT), "");
        fields.put(pushCatalogEnsure.refFieldCode(PushCatalogCodes.SUBSCRIBED_TOPICS), refValues(coveredTopicIds(topicNames)));
        request.setCustomFields(fields);
        entityService.update(request);
    }

    private List<Long> coveredTopicIds(String topicNames) {
        List<Long> ids = new ArrayList<>();
        if (topicNames == null || topicNames.isBlank()) {
            return ids;
        }
        List<EntityRespDTO> topics = entityRpcFacadeService.list(PushCatalogCodes.TOPIC_TYPE, null);
        String patternField = pushCatalogEnsure.fieldCode(PushCatalogCodes.NAME_PATTERN);
        for (String line : topicNames.split("\n")) {
            if (line.isBlank()) {
                continue;
            }
            for (Long id : PushSubscriptionTopicLinks.coveredIds(line.trim(), patternField, topics)) {
                if (!ids.contains(id)) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }

    private static String appendTopicName(String current, String topic) {
        if (current == null || current.isBlank()) {
            return topic;
        }
        for (String line : current.split("\n")) {
            if (line.trim().equals(topic)) {
                return current;
            }
        }
        String joined = current.trim() + "\n" + topic;
        return joined.length() > 255 ? current : joined;
    }

    private EntityRespDTO findByClient(String clientId) {
        List<EntityRespDTO> rows = entityRpcFacadeService.list(PushCatalogCodes.SUBSCRIPTION_TYPE, null);
        if (rows == null) {
            return null;
        }
        String clientField = pushCatalogEnsure.fieldCode(PushCatalogCodes.CLIENT_ID);
        for (EntityRespDTO row : rows) {
            if (row != null && clientId.equals(field(row, clientField))) {
                return row;
            }
        }
        return null;
    }

    private static List<Map<String, Object>> refValues(List<Long> topicIds) {
        List<Map<String, Object>> refs = new ArrayList<>();
        for (Long topicId : topicIds) {
            Map<String, Object> ref = new LinkedHashMap<>();
            ref.put("entityTypeCode", PushCatalogCodes.TOPIC_TYPE);
            ref.put("id", topicId);
            refs.add(ref);
        }
        return refs;
    }

    private static String removeTopicName(String current, String topic) {
        if (current == null || current.isBlank()) {
            return "";
        }
        List<String> kept = new ArrayList<>();
        for (String line : current.split("\n")) {
            if (!line.trim().isEmpty() && !line.trim().equals(topic)) {
                kept.add(line.trim());
            }
        }
        return String.join("\n", kept);
    }

    private void deleteRow(Long id) {
        EntityDeleteReqVO request = new EntityDeleteReqVO();
        request.setId(id);
        request.setEntityTypeCode(PushCatalogCodes.SUBSCRIPTION_TYPE);
        request.setRegistryCode(PushCatalogCodes.SUBSCRIPTION_TYPE);
        request.setForceDelete(false);
        entityService.delete(request);
    }

    private static void requirePageClient(String clientId) {
        if (clientId == null || !clientId.startsWith("page-")) {
            throw new ServiceException(400, "只有页面连接会写入事件订阅目录");
        }
    }

    private static void requireLengths(String clientId, String topic) {
        try {
            PushCatalogCodes.requireTextLength("连接编号", clientId);
            PushCatalogCodes.requireTextLength("主题名", topic);
        } catch (IllegalArgumentException ex) {
            throw new ServiceException(400, ex.getMessage());
        }
    }

    private static String field(EntityRespDTO row, String code) {
        String fromBase = text(row.getBaseFields(), code);
        if (fromBase != null) {
            return fromBase;
        }
        return text(row.getCustomFields(), code);
    }

    private static String text(Map<String, Object> fields, String code) {
        if (fields == null || fields.get(code) == null) {
            return null;
        }
        String value = String.valueOf(fields.get(code)).trim();
        return value.isEmpty() ? null : value;
    }
}
