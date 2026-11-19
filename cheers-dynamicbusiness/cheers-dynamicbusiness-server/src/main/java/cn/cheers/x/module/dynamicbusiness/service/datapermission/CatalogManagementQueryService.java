package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.field.FieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.ManagementMethodMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.field.FieldMapper;
import cn.cheers.x.module.dynamicbusiness.enums.field.FieldTypeEnum;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 「这本目录的数据怎么管」的读取与规划。用户先选按哪本目录管理（按场站、按管线、按组织机构……），
 * 这里算出要用哪个字段：目录上已有就用，没有就挂字段库里的「目录编码_id」，字段库也没有就新建。
 *
 * 能选的方式只来自租户登记的可选管理方式（{@link ManagementMethodService}），不再从全部目录里挑。
 *
 * <p>场景速查：</p>
 * <ul>
 *   <li>新建目录表单「数据怎么管」下拉里能选哪些方式 → {@link #listOptions}</li>
 *   <li>登记可选管理方式时，还能加哪些目录 → {@link #listMethodCandidates}</li>
 *   <li>保存前提示：会用哪个字段、要不要新加、有多少条记录还没填 → {@link #preview}</li>
 *   <li>保存时按同一份规划写入（见 {@link CatalogOwnershipService#applyManagement}）→ {@link #planField}</li>
 * </ul>
 *
 * 不负责：挂字段、建字段、写声明（见 {@link CatalogOwnershipService}）；算可见记录（见 {@link DataScopeQueryService}）。
 * 禁止：按具体目录编码写分支；按字段名称猜字段。字段只认「指向哪本目录」和字段库编码「目录编码_id」。
 */
@Service
public class CatalogManagementQueryService {

    /** 字段库里「按某目录管理」的标准字段编码 = 目录编码 + 这个后缀，例如 facility_id、pipeline_id。 */
    static final String MANAGEMENT_FIELD_SUFFIX = "_id";
    private static final String ENTITY_PROVIDER_PREFIX = "dynamic-entity:";

    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private FieldMapper fieldMapper;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private DataScopeConditionResolver dataScopeConditionResolver;
    @Resource
    private ManagementMethodMapper managementMethodMapper;

    /** 一种可选的管理方式：按哪本目录管理。 */
    public record ManagementOption(String registryCode, String name, boolean managementUnitCatalog) {
    }

    /** 用户选的一种管理方式。fieldCode 只在目录上有几个字段都指向同一本目录时由用户挑，其余为空。 */
    public record ManagementChoice(String targetRegistryCode, String fieldCode) {
    }

    /** 保存时对这本目录上的字段要做什么。 */
    public enum FieldAction {
        /** 目录上已有指向它的字段，直接用 */
        USE_EXISTING,
        /** 字段库里有「目录编码_id」，挂到这本目录 */
        ATTACH_LIBRARY,
        /** 字段库里也没有，新建「所属某目录」再挂上 */
        CREATE_LIBRARY,
        /** 目录上有几个字段都指向它，要用户挑一个 */
        CHOOSE
    }

    /** 一个字段的编码和名称。 */
    public record FieldRef(String fieldCode, String fieldName) {
    }

    /**
     * 一种管理方式落到字段上的规划。
     *
     * @param libraryFieldId ATTACH_LIBRARY 时要挂的字段库字段编号，其余为空
     * @param candidates     CHOOSE 时让用户挑的字段，其余为空列表
     */
    public record FieldPlan(String targetRegistryCode, String targetName, FieldAction action, String fieldCode,
                            String fieldName, boolean multiValued, Long libraryFieldId, List<FieldRef> candidates) {
    }

    /** 保存前提示用：规划加上「保存后有多少条记录还没填这个字段」。 */
    public record PlanPreview(FieldPlan plan, long recordsWithoutValue) {
    }

    /**
     * 下拉里能选的管理方式：登记过的可选方式，按列顺序，只留能对应到单位的。
     * registryCode 不为空时去掉本目录和会绕回本目录的；新建目录时传空。
     * 还没登记单位或可选方式时返回空列表，页面只剩「不分单位，公开」。
     */
    public List<ManagementOption> listOptions(String registryCode) {
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (unitCatalog == null) {
            return List.of();
        }
        String self = StringUtils.hasText(registryCode) ? registryCode.trim() : null;
        List<ManagementOption> options = new ArrayList<>();
        for (String code : managementMethodMapper.selectRegistryCodesOrdered()) {
            if (code.equals(self) || !catalogOwnershipQueryService.reachesManagementUnit(code)) {
                continue;
            }
            if (self != null && catalogOwnershipQueryService.hopsToward(code, self, new HashSet<>()) >= 0) {
                continue;
            }
            options.add(new ManagementOption(code, nameOf(code), code.equals(unitCatalog)));
        }
        return options;
    }

    /**
     * 登记可选管理方式时还能加哪些目录：能对应到单位、还没登记的目录，单位目录排在最前。
     * 还没登记单位时返回空列表。
     */
    public List<ManagementOption> listMethodCandidates() {
        String unitCatalog = managementUnitCatalogQueryService.findManagementUnitRegistryCode();
        if (unitCatalog == null) {
            return List.of();
        }
        Set<String> registered = new HashSet<>(managementMethodMapper.selectRegistryCodesOrdered());
        List<ManagementOption> unit = new ArrayList<>();
        List<ManagementOption> others = new ArrayList<>();
        for (EntityTypeDO catalog : entityTypeMapper.selectAllList()) {
            String code = catalog.getCode();
            if (registered.contains(code) || !catalogOwnershipQueryService.reachesManagementUnit(code)) {
                continue;
            }
            boolean isUnit = code.equals(unitCatalog);
            (isUnit ? unit : others).add(new ManagementOption(code, nameOf(catalog), isUnit));
        }
        unit.addAll(others);
        return unit;
    }

    /** 保存前提示：每种管理方式会用哪个字段、要不要新加，以及保存后有多少条记录还没填。 */
    public List<PlanPreview> preview(String registryCode, List<ManagementChoice> choices) {
        String code = requireCatalog(registryCode).getCode();
        List<PlanPreview> previews = new ArrayList<>();
        for (ManagementChoice choice : choices == null ? List.<ManagementChoice>of() : choices) {
            FieldPlan plan = planManagement(code, choice);
            long missing = switch (plan.action()) {
                case CHOOSE -> 0L;
                case USE_EXISTING -> dataScopeConditionResolver.countRecordsWithoutValue(code, plan.fieldCode(), plan.multiValued());
                case ATTACH_LIBRARY, CREATE_LIBRARY -> dataScopeConditionResolver.countRecordsWithoutValue(code, null, false);
            };
            previews.add(new PlanPreview(plan, missing));
        }
        return previews;
    }

    /** 一种管理方式的规划：先核对能选，再落到字段。 */
    FieldPlan planManagement(String registryCode, ManagementChoice choice) {
        if (choice == null || !StringUtils.hasText(choice.targetRegistryCode())) {
            throw new ServiceException(400, "请选择按哪本目录管理");
        }
        String target = choice.targetRegistryCode().trim();
        if (!managementMethodMapper.selectRegistryCodesOrdered().contains(target)) {
            throw new ServiceException(400, "「" + nameOf(target) + "」不在可选的管理方式里，先在「数据资源管理」第一个标签里登记");
        }
        if (target.equals(registryCode) || catalogOwnershipQueryService.hopsToward(target, registryCode, new HashSet<>()) >= 0) {
            throw new ServiceException(400, "不能按本目录或它下面的目录管理：" + nameOf(target));
        }
        if (!catalogOwnershipQueryService.reachesManagementUnit(target)) {
            throw new ServiceException(400, "「" + nameOf(target) + "」还对应不到任何组织，不能按它管理");
        }
        return planField(registryCode, target, choice.fieldCode());
    }

    /**
     * 按某本目录管理时，这本目录用哪个字段。不核对目标能否对应到单位（业务开通在单位登记之前也要先挂字段）。
     * <ol>
     *   <li>目录（子数据类型等看底座）上已有指向目标的引用字段：一个就用它；几个就要用户挑（fieldCode 指定了就用它）。</li>
     *   <li>没有：只有自有存储的目录能挂字段。字段库有「目标编码_id」且是指向目标的单选引用，就挂它；
     *       编码被别的字段占了就报错；没有就新建「所属目标名」。</li>
     * </ol>
     */
    FieldPlan planField(String registryCode, String target, String chosenFieldCode) {
        EntityTypeDO catalog = requireCatalog(registryCode);
        String targetName = nameOf(target);
        String storage = catalogOwnershipQueryService.storageCodeOf(catalog.getCode());
        Map<String, EntityTypeBaseFieldDO> meta = entityDedicatedColumnService.loadEnabledBaseFieldMeta(storage);
        Map<Long, String> refTargets = entityDedicatedColumnService.loadRefTargetsForBaseFieldMeta(meta);
        List<EntityTypeBaseFieldDO> existing = meta.values().stream()
                .filter(field -> EntityDedicatedColumnService.isRefType(field.getDataType()))
                .filter(field -> target.equals(refTargets.get(field.getLibraryFieldId())))
                .toList();
        if (StringUtils.hasText(chosenFieldCode)) {
            EntityTypeBaseFieldDO chosen = existing.stream()
                    .filter(field -> field.getFieldCode().equals(chosenFieldCode.trim())).findFirst()
                    .orElseThrow(() -> new ServiceException(400, "字段 " + chosenFieldCode + " 不是指向「" + targetName + "」的字段"));
            return existingPlan(target, targetName, chosen);
        }
        if (existing.size() == 1) {
            return existingPlan(target, targetName, existing.get(0));
        }
        if (existing.size() > 1) {
            List<FieldRef> candidates = existing.stream()
                    .map(field -> new FieldRef(field.getFieldCode(), fieldNameOf(field))).toList();
            return new FieldPlan(target, targetName, FieldAction.CHOOSE, null, null, false, null, candidates);
        }
        if (!storage.equals(catalog.getCode())) {
            throw new ServiceException(400, "「" + nameOf(catalog) + "」的数据存在「" + nameOf(storage) + "」上，「"
                    + nameOf(storage) + "」没有指向「" + targetName + "」的字段。先把「" + nameOf(storage)
                    + "」设成按" + targetName + "管理");
        }
        String libraryCode = target + MANAGEMENT_FIELD_SUFFIX;
        FieldDO library = fieldMapper.selectByCode(libraryCode);
        if (library == null) {
            return new FieldPlan(target, targetName, FieldAction.CREATE_LIBRARY, libraryCode, "所属" + targetName,
                    false, null, List.of());
        }
        boolean singleRefToTarget = FieldTypeEnum.ENTITY_REF.getCode().equals(library.getType())
                && (ENTITY_PROVIDER_PREFIX + target).equals(library.getProviderCode());
        if (!singleRefToTarget) {
            throw new ServiceException(400, "字段库里编码 " + libraryCode + " 的字段「" + library.getName()
                    + "」不是指向「" + targetName + "」的单选引用，不能自动挂上");
        }
        return new FieldPlan(target, targetName, FieldAction.ATTACH_LIBRARY, libraryCode, library.getName(),
                false, library.getId(), List.of());
    }

    private static FieldPlan existingPlan(String target, String targetName, EntityTypeBaseFieldDO field) {
        return new FieldPlan(target, targetName, FieldAction.USE_EXISTING, field.getFieldCode(), fieldNameOf(field),
                EntityDedicatedColumnService.isMultiRefType(field.getDataType()), null, List.of());
    }

    private EntityTypeDO requireCatalog(String registryCode) {
        EntityTypeDO catalog = StringUtils.hasText(registryCode) ? entityTypeMapper.selectByCode(registryCode.trim()) : null;
        if (catalog == null) {
            throw new ServiceException(400, "数据目录不存在：" + registryCode);
        }
        return catalog;
    }

    private String nameOf(String code) {
        EntityTypeDO catalog = entityTypeMapper.selectByCode(code);
        return catalog == null ? code : nameOf(catalog);
    }

    private static String nameOf(EntityTypeDO catalog) {
        return StringUtils.hasText(catalog.getName()) ? catalog.getName().trim() : catalog.getCode();
    }

    private static String fieldNameOf(EntityTypeBaseFieldDO field) {
        return StringUtils.hasText(field.getFieldName()) ? field.getFieldName().trim() : field.getFieldCode();
    }
}
