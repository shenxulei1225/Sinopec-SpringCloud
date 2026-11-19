package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.CatalogOwnershipDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeBaseFieldDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.CatalogOwnershipMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entitytype.vo.EntityTypeBaseFieldSaveReqVO;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.FieldPlan;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogManagementQueryService.ManagementChoice;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entitytype.EntityTypeBaseFieldService;
import cn.cheers.x.module.dynamicbusiness.service.field.FieldService;
import org.springframework.context.annotation.Lazy;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 「这本目录的数据怎么管」的保存。用户先选按哪本目录管理（数据资源管理「数据管理方式」标签、新建数据目录表单、
 * 业务开通），这里一次做完：目录上缺管理所需的字段就挂上（字段库也没有就新建），再整份替换这本目录的归属声明。
 * 负责：保存前核对字段是引用字段、指向的目录对得上、能逐跳到达管理单元目录、不成环、每个上级目录只有一条；
 * 删除数据目录时删掉它自己的声明，别的目录还归属到它时拒绝删除。
 * 不负责：算可见范围（见 {@link DataScopeQueryService}）；给记录补归属值；新建字段（字段走基础字段分配）。
 * 禁止：按具体目录编码写分支；核对不过时替它修路径或改字段。
 */
@Service
public class CatalogOwnershipService {

    @Resource
    private CatalogOwnershipMapper catalogOwnershipMapper;
    @Resource
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityDedicatedColumnService entityDedicatedColumnService;
    @Resource
    private CatalogManagementQueryService catalogManagementQueryService;
    @Resource
    @Lazy
    private EntityTypeBaseFieldService entityTypeBaseFieldService;
    @Resource
    @Lazy
    private FieldService fieldService;

    /** 一条声明的输入：字段、指向的目录、是否必填、是否多值。 */
    public record Declaration(String fieldCode, String targetRegistryCode, boolean required, boolean multiValued) {
    }

    /**
     * 按用户选的管理方式保存这本目录：传空列表表示「不分单位，公开」。
     * 每种方式先按 {@link CatalogManagementQueryService#planField} 落到字段：已有就用，缺了就挂字段库的「目录编码_id」，
     * 字段库也没有就新建「所属某目录」。挂上的字段必填。最后整份替换归属声明，所有声明都按必填写。
     * 目录上有几个字段都指向同一本目录、用户没挑时报 400，不替他挑。
     * 改管理方式后原来的字段留在目录上，不删数据。
     */
    @Transactional(rollbackFor = Exception.class)
    public void applyManagement(String registryCode, List<ManagementChoice> choices) {
        EntityTypeDO catalog = requireCatalog(registryCode);
        String code = catalog.getCode().trim();
        List<Declaration> declarations = new ArrayList<>();
        for (ManagementChoice choice : choices == null ? List.<ManagementChoice>of() : choices) {
            FieldPlan plan = catalogManagementQueryService.planManagement(code, choice);
            String fieldCode = writeField(code, plan);
            declarations.add(new Declaration(fieldCode, plan.targetRegistryCode(), true, plan.multiValued()));
        }
        replaceDeclarations(code, declarations);
    }

    /**
     * 只把「按某目录管理」所需的字段挂到目录上，不写声明。
     * 业务开通时用：目标目录还对应不到单位（租户还没登记管理单元类型），记录上仍要有这个字段。
     */
    @Transactional(rollbackFor = Exception.class)
    public void attachManagementField(String registryCode, String targetRegistryCode) {
        String code = requireCatalog(registryCode).getCode().trim();
        writeField(code, catalogManagementQueryService.planField(code, targetRegistryCode, null));
    }

    /** 按规划写字段，返回这本目录上管理用的字段编码。 */
    private String writeField(String registryCode, FieldPlan plan) {
        switch (plan.action()) {
            case USE_EXISTING:
                return plan.fieldCode();
            case CHOOSE:
                throw new ServiceException(400, "这本目录有几个字段都指向「" + plan.targetName() + "」："
                        + String.join("、", plan.candidates().stream().map(CatalogManagementQueryService.FieldRef::fieldName).toList())
                        + "。请选按哪一个管理");
            case CREATE_LIBRARY:
                attachBaseField(registryCode, fieldService.createEntityRefField(plan.fieldCode(), plan.fieldName(),
                        plan.targetRegistryCode()), plan.fieldName());
                return plan.fieldCode();
            case ATTACH_LIBRARY:
                attachBaseField(registryCode, plan.libraryFieldId(), plan.fieldName());
                return plan.fieldCode();
            default:
                throw new IllegalStateException("未知的字段动作：" + plan.action());
        }
    }

    private void attachBaseField(String registryCode, Long libraryFieldId, String fieldName) {
        EntityTypeBaseFieldSaveReqVO req = new EntityTypeBaseFieldSaveReqVO();
        req.setEntityTypeCode(registryCode);
        req.setLibraryFieldId(libraryFieldId);
        req.setRequired(true);
        req.setIsSearchable(false);
        req.setIsFilterable(true);
        req.setIsSortable(true);
        req.setFieldName(fieldName);
        entityTypeBaseFieldService.createBaseField(req);
    }

    /**
     * 整份替换这本目录的归属声明。传空列表表示这本目录不再按管理单元分。
     * 只由 {@link #applyManagement} 调用，字段此时已在目录上。
     */
    @Transactional(rollbackFor = Exception.class)
    void replaceDeclarations(String registryCode, List<Declaration> declarations) {
        EntityTypeDO catalog = requireCatalog(registryCode);
        String code = catalog.getCode().trim();
        List<Declaration> items = declarations == null ? List.of() : declarations;
        Set<String> fields = new HashSet<>();
        Set<String> targets = new HashSet<>();
        for (Declaration item : items) {
            validate(code, item);
            if (!fields.add(item.fieldCode().trim())) {
                throw new ServiceException(400, "同一个字段只能声明一次：" + item.fieldCode());
            }
            if (!targets.add(item.targetRegistryCode().trim())) {
                throw new ServiceException(400, "一本目录往同一本上级目录只能有一条归属：" + item.targetRegistryCode());
            }
        }
        catalogOwnershipMapper.deleteByRegistryCode(code);
        for (Declaration item : items) {
            CatalogOwnershipDO row = new CatalogOwnershipDO();
            row.setRegistryCode(code);
            row.setFieldCode(item.fieldCode().trim());
            row.setTargetRegistryCode(item.targetRegistryCode().trim());
            row.setRequired(item.required());
            row.setMultiValued(item.multiValued());
            catalogOwnershipMapper.insert(row);
        }
    }

    /**
     * 删除数据目录时调用。
     * 别的目录的归属声明还指向这本目录时拒绝删除：删了那些目录的记录就归不到管理单元，要先改那些目录的数据归属。
     * 否则删掉这本目录自己的声明。
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteDeclarationsOfDeletedCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "缺少数据目录注册编码");
        }
        String code = registryCode.trim();
        List<String> dependents = catalogOwnershipMapper.selectListByTargetRegistryCode(code).stream()
                .map(CatalogOwnershipDO::getRegistryCode)
                .filter(source -> !code.equals(source))
                .distinct()
                .toList();
        if (!dependents.isEmpty()) {
            throw new ServiceException(400, "这些目录的数据归属指向它，先到「数据资源管理」的「数据管理方式」里改那些目录：" + String.join("、", dependents));
        }
        catalogOwnershipMapper.deleteByRegistryCode(code);
    }

    private void validate(String code, Declaration item) {
        if (item == null || !StringUtils.hasText(item.fieldCode()) || !StringUtils.hasText(item.targetRegistryCode())) {
            throw new ServiceException(400, "归属声明要选字段和指向的目录");
        }
        String field = item.fieldCode().trim();
        String target = item.targetRegistryCode().trim();
        if (target.equals(code) || catalogOwnershipQueryService.hopsToward(target, code, new HashSet<>()) >= 0) {
            throw new ServiceException(400, "归属声明不能绕回本目录：" + target);
        }
        if (!catalogOwnershipQueryService.reachesManagementUnit(target)) {
            throw new ServiceException(400, "指向的目录 " + target + " 到不了管理单元目录");
        }
        String storage = catalogOwnershipQueryService.storageCodeOf(code);
        Map<String, EntityTypeBaseFieldDO> meta = entityDedicatedColumnService.loadEnabledBaseFieldMeta(storage);
        EntityTypeBaseFieldDO baseField = meta.get(field);
        if (baseField == null || !EntityDedicatedColumnService.isRefType(baseField.getDataType())) {
            throw new ServiceException(400, "归属字段必须是这本目录上启用的引用字段：" + field);
        }
        String refTarget = entityDedicatedColumnService.loadRefTargetsForBaseFieldMeta(Map.of(field, baseField))
                .get(baseField.getLibraryFieldId());
        if (!target.equals(refTarget)) {
            throw new ServiceException(400, "字段 " + field + " 指向的是 " + refTarget + "，不是 " + target);
        }
        boolean multi = EntityDedicatedColumnService.isMultiRefType(baseField.getDataType());
        if (multi != item.multiValued()) {
            throw new ServiceException(400, "字段 " + field + (multi ? " 是多选引用，要声明为多值" : " 是单选引用，不能声明为多值"));
        }
    }

    private EntityTypeDO requireCatalog(String registryCode) {
        if (!StringUtils.hasText(registryCode)) {
            throw new ServiceException(400, "请选择数据目录");
        }
        EntityTypeDO catalog = entityTypeMapper.selectByCode(registryCode.trim());
        if (catalog == null) {
            throw new ServiceException(400, "数据目录不存在：" + registryCode);
        }
        return catalog;
    }
}
