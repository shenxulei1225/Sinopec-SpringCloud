package cn.cheers.x.module.dynamicbusiness.service.entity.refroute;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.refroute.EntityRefLookupQueryService.InboundRefCount;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 删除前拦截：记录还被别的数据的引用字段指着（例：漳州站被 12 台设备的「所属场站」引用），就不能删，强制删除也不行。
 *
 * <p>管什么：按目录汇总引用条数，给单删、批删、删除预览、删分类绑定记录用同一句提示。</p>
 * <p>不管什么：不清引用、不改被引用方；手工建的无字段关联不在这里拦（仍由强制删除决定是否解除）。</p>
 */
@Service
public class EntityInboundRefDeleteGuard {

    @Resource
    private EntityRefLookupQueryService entityRefLookupQueryService;

    @Resource
    private EntityTypeMapper entityTypeMapper;

    /**
     * 这条记录被哪些目录引用了多少条。
     *
     * @return 例「设备 12 条、管道 3 条」；没人引用返回空串
     */
    public String describeInboundRefs(String entityTypeCode, Long id) {
        if (!StringUtils.hasText(entityTypeCode) || id == null) {
            return "";
        }
        List<InboundRefCount> counts = entityRefLookupQueryService.countInbound(entityTypeCode.trim(), List.of(id));
        Map<String, Long> byCatalog = new LinkedHashMap<>();
        for (InboundRefCount count : counts) {
            byCatalog.merge(catalogName(count.subjectTypeCode()), count.count(), Long::sum);
        }
        return byCatalog.entrySet().stream()
                .map(entry -> entry.getKey() + " " + entry.getValue() + " 条")
                .collect(Collectors.joining("、"));
    }

    /** 被引用就抛 400，提示先去改掉这些数据里的引用。 */
    public void assertNotReferenced(String entityTypeCode, Long id) {
        String refs = describeInboundRefs(entityTypeCode, id);
        if (!refs.isEmpty()) {
            throw new ServiceException(400, rejectMessage(refs));
        }
    }

    public static String rejectMessage(String refs) {
        return "仍被其它数据引用，不能删除：" + refs + "。请先改掉这些数据里的引用";
    }

    private String catalogName(String typeCode) {
        EntityTypeDO type = StringUtils.hasText(typeCode) ? entityTypeMapper.selectByCode(typeCode) : null;
        return type != null && StringUtils.hasText(type.getName()) ? type.getName() : typeCode;
    }
}
