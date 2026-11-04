package cn.cheers.x.module.dynamicbusiness.service.push;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.api.entity.dto.EntityRespDTO;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityRpcFacadeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询事件主题目录和事件订阅目录里已经写下的内容。
 * 管什么：字段编码、订阅型号、按对象编码读名字规则、这场页面主题能不能订。
 * 不管什么：不建目录、不建型号、不建分类、不补主题行。
 */
@Service
public class PushCatalogQueryService {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityRpcFacadeService entityRpcFacadeService;

    /**
     * 按数据管理里看到的列名找回文本字段编码。
     * 缺了就报错，不另建一列。
     */
    public String fieldCode(String name) {
        FieldDO field = fieldOf(name);
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "推送目录缺少字段「" + name + "」");
        }
        if (!"TEXT".equalsIgnoreCase(field.getType())) {
            throw new ServiceException(400, "字段「" + name + "」已经存在，但不是文本");
        }
        return field.getCode();
    }

    /**
     * 按列名找回多选引用的字段编码。详情栏用它列出订了哪些事件主题。
     */
    public String refFieldCode(String name) {
        FieldDO field = fieldOf(name);
        if (field == null || !StringUtils.hasText(field.getCode())) {
            throw new ServiceException(500, "推送目录缺少字段「" + name + "」");
        }
        if (!"REF_Multi".equalsIgnoreCase(field.getType())) {
            throw new ServiceException(400, "字段「" + name + "」已经存在，但不是多选引用");
        }
        return field.getCode();
    }

    public boolean topicCatalogExists() {
        return catalog(PushCatalogCodes.TOPIC_TYPE) != null;
    }

    public boolean subscriptionCatalogExists() {
        return catalog(PushCatalogCodes.SUBSCRIPTION_TYPE) != null;
    }

    /**
     * 事件订阅目录当前那一条型号。不是一条就报错，不猜。
     */
    public Long subscriptionModelId() {
        List<ModelDO> models = modelMapper.selectByEntityTypeCode(PushCatalogCodes.SUBSCRIPTION_TYPE);
        if (models == null || models.size() != 1 || models.get(0) == null || models.get(0).getId() == null) {
            throw new ServiceException(500, "事件订阅目录的型号不是一条");
        }
        return models.get(0).getId();
    }

    /**
     * 按事件主题实体编号取出名字规则。
     * 目录没有这一行或这一行没有规则，返回空。不补建。
     */
    public String namePatternByTopicId(long topicId) {
        if (topicId <= 0 || !topicCatalogExists()) {
            return null;
        }
        EntityRespDTO row = entityRpcFacadeService.get(topicId, PushCatalogCodes.TOPIC_TYPE);
        if (row == null) {
            return null;
        }
        return text(row, fieldCode(PushCatalogCodes.NAME_PATTERN));
    }

    /**
     * 业务域是告警的事件主题已经写下的名字规则。
     * 联动订消息通道时用这些规则，不另编主题。
     */
    public List<String> alarmEventNamePatterns() {
        if (!topicCatalogExists()) {
            return List.of();
        }
        List<EntityRespDTO> topics = entityRpcFacadeService.list(PushCatalogCodes.TOPIC_TYPE, null);
        if (topics == null || topics.isEmpty()) {
            return List.of();
        }
        String patternField = fieldCode(PushCatalogCodes.NAME_PATTERN);
        List<String> patterns = new ArrayList<>();
        for (EntityRespDTO row : topics) {
            if (row == null || !"alarm".equals(text(row, "domain"))) {
                continue;
            }
            String pattern = text(row, patternField);
            if (pattern != null) {
                patterns.add(pattern);
            }
        }
        return List.copyOf(patterns);
    }

    /**
     * 按事件主题已经写下的对象编码，取出名字规则。
     * 目录没有、这一行没有、或同一编码出现多行，都返回空。不补建。
     */
    public String namePatternByTopicCode(String topicCode) {
        EntityRespDTO row = uniqueTopicByCode(topicCode);
        if (row == null) {
            return null;
        }
        return text(row, fieldCode(PushCatalogCodes.NAME_PATTERN));
    }

    /**
     * 把已有名字规则里的占位换成这场编号。
     * 目录没有这一行、规则对不上占位、编号空着，都不发，也不另编主题。
     */
    public String filledTopic(String topicCode, String placeholder, long id) {
        return fillPattern(namePatternByTopicCode(topicCode), placeholder, id);
    }

    static String fillPattern(String pattern, String placeholder, long id) {
        if (pattern == null || placeholder == null || !pattern.contains(placeholder) || id <= 0) {
            return null;
        }
        return pattern.replace(placeholder, Long.toString(id));
    }

    private EntityRespDTO uniqueTopicByCode(String topicCode) {
        if (!StringUtils.hasText(topicCode) || !topicCatalogExists()) {
            return null;
        }
        List<EntityRespDTO> topics = entityRpcFacadeService.list(PushCatalogCodes.TOPIC_TYPE, null);
        if (topics == null || topics.isEmpty()) {
            return null;
        }
        EntityRespDTO found = null;
        for (EntityRespDTO row : topics) {
            if (row == null) {
                continue;
            }
            if (topicCode.equals(text(row, "code"))) {
                if (found != null) {
                    return null;
                }
                found = row;
            }
        }
        return found;
    }

    /**
     * 这场页面主题能不能写入订阅。
     * 只认事件主题上已经写下的名字规则。目录没有、规则对不上，都不能订。
     */
    public boolean pageTopicAllowed(String topic) {
        if (topic == null || topic.isBlank()) {
            return false;
        }
        if (!topicCatalogExists()) {
            return false;
        }
        List<EntityRespDTO> topics = entityRpcFacadeService.list(PushCatalogCodes.TOPIC_TYPE, null);
        if (topics == null || topics.isEmpty()) {
            return false;
        }
        return !PushSubscriptionTopicLinks.coveredIds(
                topic.trim(), fieldCode(PushCatalogCodes.NAME_PATTERN), topics).isEmpty();
    }

    /**
     * 当前租户这一列。同名列每个租户各有一条，不能用 selectOne 扫全库。
     */
    private FieldDO fieldOf(String name) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<FieldDO> rows = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getDeleted, false));
        FieldDO found = null;
        for (FieldDO row : rows == null ? List.<FieldDO>of() : rows) {
            if (row == null || !tenantId.equals(row.getTenantId())) {
                continue;
            }
            if (found != null) {
                throw new ServiceException(500, "推送目录字段「" + name + "」不是一条");
            }
            found = row;
        }
        return found;
    }

    private EntityTypeDO catalog(String code) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<EntityTypeDO> rows = entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getCode, code)
                .eq(EntityTypeDO::getDeleted, false));
        EntityTypeDO found = null;
        for (EntityTypeDO row : rows == null ? List.<EntityTypeDO>of() : rows) {
            if (row == null || !tenantId.equals(row.getTenantId())) {
                continue;
            }
            if (found != null) {
                throw new ServiceException(500, "事件主题目录不是一条");
            }
            found = row;
        }
        return found;
    }

    private static String text(EntityRespDTO row, String field) {
        String fromBase = text(row.getBaseFields(), field);
        if (fromBase != null) {
            return fromBase;
        }
        return text(row.getCustomFields(), field);
    }

    private static String text(java.util.Map<String, Object> fields, String field) {
        if (fields == null || fields.get(field) == null) {
            return null;
        }
        String value = String.valueOf(fields.get(field)).trim();
        return value.isEmpty() ? null : value;
    }
}
