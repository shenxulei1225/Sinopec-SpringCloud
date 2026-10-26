package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.EntityDeleteReqVO;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 把页面连接的订阅、取消、断开写进推送订阅目录。
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
     * 页面订了一个主题。主题不在三种固定规则里则拒绝，不建行。
     * 同一连接重复订同一个主题，目录里仍只有一行。
     */
    @Transactional(rollbackFor = Exception.class)
    public void onSubscribed(String clientId, long userId, String topic) {
        requirePageClient(clientId);
        requireLengths(clientId, topic);
        if (!PushTopicKinds.matches(topic)) {
            throw new ServiceException(400, "这个主题不在推送目录的种类里，没有写入订阅");
        }
        pushCatalogEnsure.ensure();
        String code = PushCatalogCodes.subscriptionCode(clientId, topic);
        if (entityRpcFacadeService.getByCode(code, PushCatalogCodes.SUBSCRIPTION_TYPE) != null) {
            return;
        }
        EntityCreateReqVO request = new EntityCreateReqVO();
        request.setRegistryCode(PushCatalogCodes.SUBSCRIPTION_TYPE);
        Map<String, Object> baseFields = new LinkedHashMap<>();
        baseFields.put("entityTypeCode", PushCatalogCodes.SUBSCRIPTION_TYPE);
        baseFields.put("modelId", pushCatalogEnsure.subscriptionModelId());
        baseFields.put("name", topic.trim());
        baseFields.put("code", code);
        baseFields.put("status", 1);
        request.setBaseFields(baseFields);
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.CLIENT_ID), clientId.trim());
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.TOPIC_NAME), topic.trim());
        fields.put(pushCatalogEnsure.fieldCode(PushCatalogCodes.LISTENER_USER_ID), Long.toString(userId));
        request.setCustomFields(fields);
        entityService.create(request);
    }

    /**
     * 页面取消一个主题。行已经不在时直接返回。
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
        EntityRespDTO row = entityRpcFacadeService.getByCode(
                PushCatalogCodes.subscriptionCode(clientId, topic), PushCatalogCodes.SUBSCRIPTION_TYPE);
        if (row == null || row.getId() == null) {
            return;
        }
        deleteRow(row.getId());
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
            throw new ServiceException(400, "只有页面连接会写入推送订阅目录");
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
