package cn.cheers.x.module.dynamicbusiness.service.strategy;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.framework.tenant.core.context.TenantContextHolder;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.dataprocess.StrategyRecordFields;
import cn.cheers.x.strategy.PublishedStrategy;
import cn.cheers.x.strategy.StrategyCombine;
import cn.cheers.x.strategy.StrategyMatch;
import cn.cheers.x.strategy.StrategyPublication;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 从动态业务读出某个域已经发布的策略。
 *
 * 管什么：把通用策略型号上的记录收成判定模块要的发布包。
 * 不管什么：不判定，不执行动作，不读告警策略型号。
 * 禁止：缺列或缺域记录时编一个默认域。
 */
@Service
public class StrategyPublicationReader {

    @Resource
    private ModelMapper modelMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityRepository entityRepository;

    public StrategyPublication load(String domainCode) {
        if (domainCode == null || domainCode.isBlank()) {
            throw new ServiceException(400, "没有指定域");
        }
        ModelDO model = modelMapper.selectByNameAndEntityTypeCode(
                StrategyRecordFields.MODEL, StrategyRecordFields.DIRECTORY);
        if (model == null || model.getId() == null) {
            throw new ServiceException(400, "通用策略还没有准备好");
        }
        Map<String, String> codes = fieldCodes();
        List<EntityDO> rows = entityRepository.findByModelId(model.getId(), StrategyRecordFields.DIRECTORY);
        EntityDO domain = null;
        List<PublishedStrategy> strategies = new ArrayList<>();
        for (EntityDO row : rows) {
            if (row == null || row.getId() == null || !Integer.valueOf(1).equals(row.getStatus())) {
                continue;
            }
            String kind = text(row, codes.get(StrategyRecordFields.KIND));
            if (StrategyRecordFields.KIND_DOMAIN.equals(kind) && domainCode.equals(row.getCode())) {
                domain = row;
                continue;
            }
            if (!StrategyRecordFields.KIND_STRATEGY.equals(kind)) {
                continue;
            }
            if (!domainCode.equals(text(row, codes.get(StrategyRecordFields.DOMAIN_CODE)))) {
                continue;
            }
                    strategies.add(new PublishedStrategy(
                            row.getId(),
                            row.getName(),
                            true,
                            row.getSort() == null ? 0 : row.getSort(),
                            matchOf(text(row, codes.get(StrategyRecordFields.MATCH))),
                            values(row, codes.get(StrategyRecordFields.CONDITION_IDS)),
                            text(row, codes.get(StrategyRecordFields.ACTION_MODE)),
                            values(row, codes.get(StrategyRecordFields.ACTION_IDS))));
        }
        if (domain == null) {
            throw new ServiceException(400, "域「" + domainCode + "」还没有发布");
        }
        return new StrategyPublication(
                domainCode,
                combineOf(text(domain, codes.get(StrategyRecordFields.COMBINE))),
                values(domain, codes.get(StrategyRecordFields.ALLOWED_MODES)),
                strategies);
    }

    private Map<String, String> fieldCodes() {
        Map<String, String> codes = new LinkedHashMap<>();
        for (String name : List.of(
                StrategyRecordFields.KIND,
                StrategyRecordFields.CONDITION_IDS,
                StrategyRecordFields.MATCH,
                StrategyRecordFields.ACTION_IDS,
                StrategyRecordFields.ACTION_MODE,
                StrategyRecordFields.DOMAIN_CODE,
                StrategyRecordFields.COMBINE,
                StrategyRecordFields.ALLOWED_MODES)) {
            codes.put(name, requireCode(name));
        }
        return codes;
    }

    private String requireCode(String name) {
        Long tenantId = TenantContextHolder.getRequiredTenantId();
        List<FieldDO> rows = fieldMapper.selectList(new LambdaQueryWrapperX<FieldDO>()
                .eq(FieldDO::getName, name)
                .eq(FieldDO::getTenantId, tenantId)
                .eq(FieldDO::getDeleted, false));
        if (rows.size() != 1 || rows.get(0).getCode() == null) {
            throw new ServiceException(400, "策略字段「" + name + "」还没有准备好");
        }
        return rows.get(0).getCode();
    }

    private static StrategyMatch matchOf(String raw) {
        if ("ALL".equals(raw)) {
            return StrategyMatch.ALL;
        }
        if ("ANY".equals(raw)) {
            return StrategyMatch.ANY;
        }
        return null;
    }

    private static StrategyCombine combineOf(String raw) {
        if ("FIRST".equals(raw)) {
            return StrategyCombine.FIRST;
        }
        if ("ALL".equals(raw)) {
            return StrategyCombine.ALL;
        }
        return null;
    }

    public String fieldCode(String name) {
        return requireCode(name);
    }

    /**
     * 条件项上写下的判定编号。策略勾的是条件项的记录编号，告警调用方用这个编号判断点位或告警是否成立。
     * 传入的已经是 open: 或 bit: 时原样返回。记录不存在就返回空。
     */
    public String conditionToken(String conditionId) {
        if (conditionId == null || conditionId.isBlank()) {
            return "";
        }
        String raw = conditionId.trim();
        if (raw.startsWith("open:") || raw.startsWith("bit:") || raw.startsWith("state:")) {
            return raw;
        }
        long id;
        try {
            id = Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            return "";
        }
        EntityDO row = entityRepository.findById(id, StrategyRecordFields.DIRECTORY);
        if (row == null) {
            return "";
        }
        return text(row, requireCode(StrategyRecordFields.CONDITION_IDS));
    }

    private static List<String> values(EntityDO row, String fieldCode) {
        if (row.getCustomFields() == null || fieldCode == null) {
            return List.of();
        }
        Object value = row.getCustomFields().get(fieldCode);
        if (value instanceof Collection<?> collection) {
            List<String> ids = new ArrayList<>();
            for (Object item : collection) {
                if (item == null) {
                    continue;
                }
                if (item instanceof Map<?, ?> map && map.get("id") != null) {
                    ids.add(String.valueOf(map.get("id")).trim());
                    continue;
                }
                String text = String.valueOf(item).trim();
                if (!text.isEmpty()) {
                    ids.add(text);
                }
            }
            return ids;
        }
        return splitIds(value == null ? "" : String.valueOf(value));
    }

    private static String text(EntityDO row, String fieldCode) {
        if (row.getCustomFields() == null || fieldCode == null) {
            return "";
        }
        Object value = row.getCustomFields().get(fieldCode);
        return value == null ? "" : String.valueOf(value).trim();
    }

    private static List<String> splitIds(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        List<String> ids = new ArrayList<>();
        for (String part : raw.split("[\\n,，、]")) {
            String id = part.trim();
            if (!id.isEmpty()) {
                ids.add(id);
            }
        }
        return ids;
    }
}
