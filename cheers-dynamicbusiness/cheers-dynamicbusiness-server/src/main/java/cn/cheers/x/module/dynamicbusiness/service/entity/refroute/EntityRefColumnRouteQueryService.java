package cn.cheers.x.module.dynamicbusiness.service.entity.refroute;

import cn.cheers.x.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeBaseFieldMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.framework.entity.EntityTableNameHandler;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService.PhysicalFieldSpec;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 引用字段存在哪：哪些单值引用落在记录自己的专用列上。
 *
 * <p>规则（权威：docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md「引用值存在哪」）：</p>
 * <ul>
 *   <li>目录用专用表存数、字段是启用的基础字段、单值引用 → 值只存在专用列上，列上有索引。
 *       不再往实体关联表、分类-实体关联表各抄一行。</li>
 *   <li>多选引用、只存在扩展字段里的引用 → 不在这里，仍以实体关联表为准（被引用的是分类即实体时另写分类-实体关联）。</li>
 * </ul>
 *
 * <p>场景速查：写入时判断要不要抄关联行 → {@link #findRoute}；分类筛选、正查 → {@link #listRoutes(String, String)}；
 * 反查「谁引用了这本目录」→ {@link #listRoutesTo}。</p>
 *
 * 只读元数据，不写任何表，不补字段或列。
 */
@Service
public class EntityRefColumnRouteQueryService {

    private static final Set<String> SINGLE_REF_DATA_TYPES = Set.of("REF", "ENTITY_REF", "REFERENCE", "Ref", "Entity_Ref", "Reference");
    private static final Pattern SAFE_COLUMN = Pattern.compile("^[a-z][a-z0-9_]*$");

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeBaseFieldMapper baseFieldMapper;
    @Resource
    private EntityDedicatedColumnService dedicatedColumnService;
    @Resource
    private EntityTableNameHandler tableNameHandler;

    /**
     * 存数类型：子数据类型、划分数据、使用已有数据的记录存在底座类型上，基础字段也登记在底座类型上。
     * 目录不存在时原样返回，由调用方按「没有路由」处理。
     */
    public String storageCodeOf(String typeCode) {
        if (!StringUtils.hasText(typeCode)) {
            return typeCode;
        }
        String code = typeCode.trim();
        EntityTypeDO type = entityTypeMapper.selectByCode(code);
        if (type == null || !StringUtils.hasText(type.getBaseEntityTypeCode())) {
            return code;
        }
        return type.getBaseEntityTypeCode().trim();
    }

    /**
     * 这个字段是不是落在专用列上的单值引用。
     *
     * @return 是 → 路由；否（多选、扩展字段、未启用、目录不用专用表）→ 空，值以实体关联表为准
     */
    public Optional<RefColumnRoute> findRoute(String subjectTypeCode, String fieldCode) {
        if (!StringUtils.hasText(fieldCode)) {
            return Optional.empty();
        }
        String code = fieldCode.trim();
        return listRoutes(subjectTypeCode).stream()
                .filter(route -> route.fieldCode().equals(code))
                .findFirst();
    }

    /** 这本目录上全部落在专用列上的单值引用字段。 */
    public List<RefColumnRoute> listRoutes(String subjectTypeCode) {
        if (!StringUtils.hasText(subjectTypeCode)) {
            return List.of();
        }
        String storage = storageCodeOf(subjectTypeCode);
        if (!tableNameHandler.isDynamicTable(storage)) {
            return List.of();
        }
        Map<String, EntityTypeBaseFieldDO> meta = dedicatedColumnService.loadEnabledBaseFieldMeta(storage);
        Map<Long, String> targets = dedicatedColumnService.loadRefTargetsForBaseFieldMeta(meta);
        List<RefColumnRoute> routes = new ArrayList<>();
        for (PhysicalFieldSpec spec : dedicatedColumnService.listEnabledPhysicalFields(storage)) {
            EntityTypeBaseFieldDO field = meta.get(spec.fieldCode());
            // 列名会拼进反查 SQL，只认小写字母数字下划线
            if (field == null || !isSingleRef(field.getDataType()) || !SAFE_COLUMN.matcher(spec.columnName()).matches()) {
                continue;
            }
            String target = field.getLibraryFieldId() == null ? null : targets.get(field.getLibraryFieldId());
            // 没声明指向哪本目录的引用保存时就会被拒绝，这里不猜目标
            if (!StringUtils.hasText(target)) {
                continue;
            }
            routes.add(new RefColumnRoute(storage, spec.fieldCode(), spec.columnName(), target.trim()));
        }
        return routes;
    }

    /** 引用方这本目录上，指向被引用目录的专用列引用。例：设备 → 场站只有「所属场站」一列。 */
    public List<RefColumnRoute> listRoutes(String subjectTypeCode, String targetTypeCode) {
        if (!StringUtils.hasText(targetTypeCode)) {
            return List.of();
        }
        Set<String> targetCodes = sameCatalogCodes(targetTypeCode);
        return listRoutes(subjectTypeCode).stream()
                .filter(route -> targetCodes.contains(route.targetTypeCode()))
                .toList();
    }

    /**
     * 所有目录里指向这本目录的专用列引用。删除拦截、「被谁引用」列表用。
     * 先按字段类型筛出可能的引用方目录，再逐本核对，避免扫全部目录。
     */
    public List<RefColumnRoute> listRoutesTo(String targetTypeCode) {
        if (!StringUtils.hasText(targetTypeCode)) {
            return List.of();
        }
        List<EntityTypeBaseFieldDO> candidates = baseFieldMapper.selectList(new LambdaQueryWrapperX<EntityTypeBaseFieldDO>()
                .eq(EntityTypeBaseFieldDO::getStatus, 1)
                .in(EntityTypeBaseFieldDO::getDataType, SINGLE_REF_DATA_TYPES));
        Set<String> subjectCodes = new LinkedHashSet<>();
        for (EntityTypeBaseFieldDO field : candidates) {
            if (StringUtils.hasText(field.getEntityTypeCode())) {
                subjectCodes.add(field.getEntityTypeCode().trim());
            }
        }
        List<RefColumnRoute> routes = new ArrayList<>();
        for (String subject : subjectCodes) {
            routes.addAll(listRoutes(subject, targetTypeCode));
        }
        return routes;
    }

    /** 目录编码和它的存数类型都算同一本：字段声明可能写子目录，也可能写底座类型。 */
    /**
     * 与这本目录共用同一张存储表的全部目录编码（存储目录本身 + 复用它的目录）。
     * 实体关系表里的类型码可能写的是其中任一个，按 id 匹配时必须带上这组码，避免不同表同 id 串号。
     */
    public Set<String> catalogCodesSharingStorage(String typeCode) {
        Set<String> codes = new LinkedHashSet<>();
        if (!StringUtils.hasText(typeCode)) {
            return codes;
        }
        String storage = storageCodeOf(typeCode);
        codes.add(typeCode.trim());
        codes.add(storage);
        for (EntityTypeDO reuse : entityTypeMapper.selectList(new LambdaQueryWrapperX<EntityTypeDO>()
                .eq(EntityTypeDO::getBaseEntityTypeCode, storage))) {
            if (StringUtils.hasText(reuse.getCode())) {
                codes.add(reuse.getCode().trim());
            }
        }
        return codes;
    }

    private Set<String> sameCatalogCodes(String typeCode) {
        Set<String> codes = new LinkedHashSet<>();
        codes.add(typeCode.trim());
        codes.add(storageCodeOf(typeCode));
        return codes;
    }

    /** 字段数据类型是否单值引用（多选 REF_Multi 不算）。建列时据此决定是否建反查索引。 */
    public static boolean isSingleRef(String dataType) {
        return EntityDedicatedColumnService.isRefType(dataType) && !EntityDedicatedColumnService.isMultiRefType(dataType);
    }
}
