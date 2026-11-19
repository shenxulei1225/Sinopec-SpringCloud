package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.CatalogOwnershipMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementMethodMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 归属声明的读取，以及「这本目录怎样逐跳归到某本目录」。
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>「数据资源管理」页「数据管理方式」标签打开时，列出每本目录按什么管理、整条链怎么走 → {@link #listOverview}</li>
 *   <li>授权、范围计算要知道一本目录受不受约束、能不能到达管理单元 → {@link #isRestricted} / {@link #reachesManagementUnit}</li>
     *   <li>数据页要知道这本目录勾了按哪些目录管理、用的哪个字段 → {@link #listManagementTargets}</li>
 * </ul>
 *
 * 不负责：写声明（见 {@link CatalogOwnershipService}）；算可见记录（见 {@link DataScopeQueryService}）；
 * 推出管理单元目录（见 {@link ManagementUnitCatalogQueryService}）。
 * 禁止：按具体目录编码写分支。哪本目录受限只看管理单元目录和归属声明。
 */
@Service
public class CatalogOwnershipQueryService {

    /** 声明链最多走几跳。超过说明配成了环，按配置错误报出。 */
    static final int MAX_HOPS = 8;

    @Resource
    private CatalogOwnershipMapper catalogOwnershipMapper;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private ManagementMethodMapper managementMethodMapper;

    /** 归属链上的一跳：本目录用哪个字段，归到哪本目录。 */
    public record OwnershipHop(String fieldCode, String fieldName, String targetRegistryCode, String targetName) {
    }

    /**
     * 一条归属声明，以及从它出发到管理单元目录的整条链。
     *
     * @param path 第一跳就是这条声明本身，后面按上级目录自己的声明走最短的一条；到不了管理单元目录时为空列表
     */
    public record OwnershipDeclarationView(OwnershipHop hop, boolean required, boolean multiValued,
                                           List<OwnershipHop> path) {
    }

    /**
     * 一本目录在「数据管理方式」表格里的一行。
     *
     * @param groupName              数据目录的分组，表格按它分段，空表示未分组
     * @param managementUnitCatalog  这本就是管理单元目录：它是范围的起点，不用声明归属
     * @param reachesManagementUnit  任一条声明能到达管理单元目录（或自己就是）
     * @param allowedMethods         这一行能勾的方式（目录注册编码）：去掉本目录自己、会绕回本目录的、对应不到单位的
     */
    public record CatalogOwnershipView(String registryCode, String name, String groupName, boolean managementUnitCatalog,
                                       boolean reachesManagementUnit, List<OwnershipDeclarationView> declarations,
                                       List<String> allowedMethods) {
    }

    /**
     * 一种可选的管理方式，「数据管理方式」表格里的一列。
     *
     * @param reachesManagementUnit 这本目录能对应到单位。对应不到时整列不能勾，已勾的目录谁都看不到
     * @param usageCount            有几本目录在用这种方式
     */
    public record ManagementMethodView(String registryCode, String name, boolean reachesManagementUnit, int usageCount) {
    }

    /**
     * 整个租户的数据管理方式配置。managementUnitRegistryCode 为空表示还没登记管理单元类型。
     *
     * @param methods 可选的管理方式，按列顺序
     */
    public record OwnershipOverview(String managementUnitRegistryCode, List<ManagementMethodView> methods,
                                    List<CatalogOwnershipView> catalogs) {
    }


    public List<CatalogOwnershipDO> listByRegistryCode(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            return List.of();
        }
        return catalogOwnershipMapper.selectListByRegistryCode(registryCode.trim());
    }

    /**
     * 接报、数据页要填的直接归属。每一条是这本目录已经声明的一跳：字段上配置的名字、指向哪本目录、保存时是否必填。
     * 没有声明就返回空列表。不补场站，也不补管线。
     */
    public List<OwnershipDeclarationView> listDirectDeclarations(String registryCode) {
        List<CatalogOwnershipDO> rows = listByRegistryCode(registryCode);
        if (rows.isEmpty()) {
            return List.of();
        }
        Map<String, String> names = new HashMap<>();
        for (EntityTypeDO catalog : entityTypeMapper.selectAllList()) {
            names.put(catalog.getCode(), StringUtils.hasText(catalog.getName()) ? catalog.getName().trim() : catalog.getCode());
        }
        Map<String, Map<String, EntityTypeBaseFieldDO>> fieldsByStorage = new HashMap<>();
        List<OwnershipDeclarationView> views = new ArrayList<>();
        for (CatalogOwnershipDO row : rows) {
            views.add(new OwnershipDeclarationView(toHop(row, names, fieldsByStorage),
                    Boolean.TRUE.equals(row.getRequired()), Boolean.TRUE.equals(row.getMultiValued()), List.of()));
        }
        return List.copyOf(views);
    }

    /**
     * 数据管理方式里这本目录勾了按哪些目录管理，以及用的哪个归属字段。
     * 只返回直接勾上的那一跳，不把上一级再展开。没勾返回空列表。
     * 数据页靠字段编码区分「按场站」和「按管线段」：两本目录都可以指向管线，只有所属管线段才在左边列管线段。
     */
    public List<CatalogManagementTarget> listManagementTargets(String registryCode) {
        List<CatalogManagementTarget> targets = new ArrayList<>();
        for (CatalogOwnershipDO row : listByRegistryCode(registryCode)) {
            if (row == null || !StringUtils.hasText(row.getTargetRegistryCode())) {
                continue;
            }
            String fieldCode = row.getFieldCode() == null ? "" : row.getFieldCode().trim();
            targets.add(new CatalogManagementTarget(row.getTargetRegistryCode().trim(), fieldCode));
        }
        return List.copyOf(targets);
    }

    /**
     * 「数据管理方式」标签的总览：可选的方式（表格的列），每本目录一行，列出它的声明、每条声明到管理单元目录的整条链，
     * 以及这一行能勾哪几种方式。
     * 链只按已存的声明算，不猜字段、不补声明。声明链断了就给空链，页面标成到不了。
     */
    public OwnershipOverview listOverview() {
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        List<EntityTypeDO> catalogs = entityTypeMapper.selectAllList();
        Map<String, String> names = new HashMap<>();
        for (EntityTypeDO catalog : catalogs) {
            names.put(catalog.getCode(), StringUtils.hasText(catalog.getName()) ? catalog.getName().trim() : catalog.getCode());
        }
        Map<String, List<CatalogOwnershipDO>> byCatalog = loadAllDeclarations();
        List<String> methodCodes = managementMethodMapper.selectRegistryCodesOrdered();
        // 每种方式往上能走到哪些目录（含自己）：判断能否对应到单位、会不会绕回某一行，都只看这份
        Map<String, Set<String>> upwardByMethod = new LinkedHashMap<>();
        methodCodes.forEach(method -> upwardByMethod.put(method, upwardFrom(method, byCatalog)));
        Map<String, Map<String, EntityTypeBaseFieldDO>> fieldsByStorage = new HashMap<>();
        List<CatalogOwnershipView> rows = new ArrayList<>();
        for (EntityTypeDO catalog : catalogs) {
            String code = catalog.getCode();
            List<OwnershipDeclarationView> declarations = new ArrayList<>();
            for (CatalogOwnershipDO row : byCatalog.getOrDefault(code, List.of())) {
                OwnershipHop first = toHop(row, names, fieldsByStorage);
                List<OwnershipHop> path = List.of();
                List<CatalogOwnershipDO> rest = unitCatalog == null ? null
                        : shortestChain(row.getTargetRegistryCode(), unitCatalog, byCatalog);
                if (rest != null) {
                    List<OwnershipHop> hops = new ArrayList<>();
                    hops.add(first);
                    rest.forEach(next -> hops.add(toHop(next, names, fieldsByStorage)));
                    path = hops;
                }
                declarations.add(new OwnershipDeclarationView(first, Boolean.TRUE.equals(row.getRequired()),
                        Boolean.TRUE.equals(row.getMultiValued()), path));
            }
            boolean isUnit = code.equals(unitCatalog);
            boolean reaches = isUnit || declarations.stream().anyMatch(item -> !item.path().isEmpty());
            List<String> allowed = isUnit ? List.of() : upwardByMethod.entrySet().stream()
                    .filter(entry -> unitCatalog != null && entry.getValue().contains(unitCatalog))
                    .filter(entry -> !entry.getValue().contains(code))
                    .map(Map.Entry::getKey).toList();
            String group = StringUtils.hasText(catalog.getGroupName()) ? catalog.getGroupName().trim() : null;
            rows.add(new CatalogOwnershipView(code, names.get(code), group, isUnit, reaches, declarations, allowed));
        }
        List<ManagementMethodView> methods = methodCodes.stream().map(method -> new ManagementMethodView(method,
                names.getOrDefault(method, method),
                unitCatalog != null && upwardByMethod.get(method).contains(unitCatalog),
                (int) byCatalog.values().stream().filter(list -> list.stream()
                        .anyMatch(row -> method.equals(row.getTargetRegistryCode()))).count())).toList();
        return new OwnershipOverview(unitCatalog, methods, rows);
    }

    /** 从某本目录顺着已存的声明往上能走到的全部目录，含它自己。 */
    private static Set<String> upwardFrom(String from, Map<String, List<CatalogOwnershipDO>> byCatalog) {
        Set<String> seen = new LinkedHashSet<>(Set.of(from));
        Deque<String> queue = new ArrayDeque<>(List.of(from));
        while (!queue.isEmpty()) {
            for (CatalogOwnershipDO next : byCatalog.getOrDefault(queue.poll(), List.of())) {
                if (seen.add(next.getTargetRegistryCode())) {
                    queue.add(next.getTargetRegistryCode());
                }
            }
        }
        return seen;
    }


    /**
     * 这些目录里有没有哪条归属声明用到这个字段。删基础字段前核对：用到了就不能删，先在「数据资源管理」的「数据管理方式」里改掉。
     */
    public boolean isFieldDeclared(Collection<String> registryCodes, String fieldCode) {
        if (registryCodes == null || !StringUtils.hasText(fieldCode)) {
            return false;
        }
        String code = fieldCode.trim();
        for (String registryCode : registryCodes) {
            for (CatalogOwnershipDO declaration : listByRegistryCode(registryCode)) {
                if (code.equals(declaration.getFieldCode())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 这本目录受不受数据范围约束。
     * 它就是管理单元目录，或者写了归属声明，就受约束：只有任职能看，按任职所在单元算范围。
     * 声明了却到不了管理单元目录（还没登记管理单元类型、或声明链断了），仍受约束，谁都看不到，缺口可见。
     * 两样都没有，说明这本目录不按单元分（检查项、标准作业程序这类共用资料），有功能就能看全部。
     */
    public boolean isRestricted(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            return false;
        }
        String code = registryCode.trim();
        return code.equals(managementUnitCatalogQueryService.findManagementUnitRegistryCode())
                || !listByRegistryCode(code).isEmpty();
    }

    /** 从这本目录逐跳往上能不能到达管理单元目录（自己就是也算）。租户还没有管理单元目录时为 false。 */
    public boolean reachesManagementUnit(String registryCode) {
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        return unitCatalog != null && hopsToward(registryCode, unitCatalog, new HashSet<>()) >= 0;
    }

    /**
     * 从某本目录走到目标目录要几跳；到不了返回 -1。
     * 自己就是目标目录时是 0 跳。
     */
    int hopsToward(String fromRegistryCode, String scopeRegistryCode, Set<String> visited) {
        if (!StringUtils.hasText(fromRegistryCode) || !StringUtils.hasText(scopeRegistryCode)) {
            return -1;
        }
        String from = fromRegistryCode.trim();
        if (from.equals(scopeRegistryCode.trim())) {
            return 0;
        }
        if (!visited.add(from) || visited.size() > MAX_HOPS) {
            return -1;
        }
        int best = -1;
        for (CatalogOwnershipDO declaration : listByRegistryCode(from)) {
            int next = hopsToward(declaration.getTargetRegistryCode(), scopeRegistryCode, new HashSet<>(visited));
            if (next >= 0 && (best < 0 || next + 1 < best)) {
                best = next + 1;
            }
        }
        return best;
    }

    /** 字段登记在哪本目录上：子数据类型、划分数据、使用已有数据的字段在底座上。 */
    String storageCodeOf(String code) {
        EntityTypeDO type = entityTypeMapper.selectByCode(code);
        return type != null && StringUtils.hasText(type.getBaseEntityTypeCode())
                ? type.getBaseEntityTypeCode().trim() : code;
    }

    private Map<String, List<CatalogOwnershipDO>> loadAllDeclarations() {
        Map<String, List<CatalogOwnershipDO>> byCatalog = new LinkedHashMap<>();
        catalogOwnershipMapper.selectList().stream()
                .sorted(Comparator.comparing(CatalogOwnershipDO::getId))
                .forEach(row -> byCatalog.computeIfAbsent(row.getRegistryCode(), key -> new ArrayList<>()).add(row));
        return byCatalog;
    }

    /**
     * 从 from 目录按声明走到管理单元目录的最短一条，返回经过的声明（不含 from 之前那一跳）。
     * from 自己就是管理单元目录时返回空列表。到不了返回 null。
     */
    private List<CatalogOwnershipDO> shortestChain(String from, String unitCatalog,
                                                   Map<String, List<CatalogOwnershipDO>> byCatalog) {
        Map<String, CatalogOwnershipDO> reachedBy = new HashMap<>();
        Set<String> seen = new HashSet<>(Set.of(from));
        Deque<String> queue = new ArrayDeque<>(List.of(from));
        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(unitCatalog)) {
                List<CatalogOwnershipDO> chain = new ArrayList<>();
                for (String step = current; reachedBy.containsKey(step); step = reachedBy.get(step).getRegistryCode()) {
                    chain.add(0, reachedBy.get(step));
                }
                return chain;
            }
            for (CatalogOwnershipDO next : byCatalog.getOrDefault(current, List.of())) {
                if (seen.add(next.getTargetRegistryCode())) {
                    reachedBy.put(next.getTargetRegistryCode(), next);
                    queue.add(next.getTargetRegistryCode());
                }
            }
        }
        return null;
    }

    private OwnershipHop toHop(CatalogOwnershipDO row, Map<String, String> names,
                               Map<String, Map<String, EntityTypeBaseFieldDO>> fieldsByStorage) {
        String storage = storageCodeOf(row.getRegistryCode());
        EntityTypeBaseFieldDO field = fieldsByStorage
                .computeIfAbsent(storage, entityDedicatedColumnService::loadEnabledBaseFieldMeta)
                .get(row.getFieldCode());
        String target = row.getTargetRegistryCode();
        return new OwnershipHop(row.getFieldCode(), field == null ? row.getFieldCode() : fieldNameOf(field),
                target, names.getOrDefault(target, target));
    }

    private static String fieldNameOf(EntityTypeBaseFieldDO field) {
        return StringUtils.hasText(field.getFieldName()) ? field.getFieldName().trim() : field.getFieldCode();
    }
}
