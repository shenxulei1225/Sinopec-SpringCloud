package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytypescope.EntityTypeScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytypescope.EntityTypeScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.model.ModelMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.framework.entitytype.EntityTypeScopeContext;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 实体接口上「这次实际读写哪本数据目录」的核对。
 * <p>
 * 用户打开的是本页数据目录（请求里的目录注册编码）。请求另带的类型编码有两种情况：
 * <ul>
 *   <li>就是本页，或者是本页复用的底座类型：这次读写按本页算。子数据类型强制按本页业务域，
 *       划分数据强制按本页成员，请求不能借底座类型读到别的业务域或非成员。</li>
 *   <li>是另一本目录（例如检查项页里的设备栏）：这次读写按那本目录算，那本目录上也要有同一项操作的授权。
 *       页面布局里放了哪些栏不扩大授权。</li>
 * </ul>
 * 权威见 docs/中台/能力定义/数据权限能力/数据权限能力（权威）.md 第 2.4 节。
 * <p>
 * 不负责：判断角色有没有某本目录的某项操作（{@link CatalogRoleGrantService}）；
 * 记录落在谁的数据范围里（{@link cn.cheers.x.module.dynamicbusiness.service.datapermission.DataScopeQueryService}）。
 * 禁止：在这里补业务域、补成员、猜目录。不一致一律拒绝。
 */
@Service
public class CatalogEntryAccessService {

    @Resource
    private EntityTypeMapper entityTypeMapper;
    @Resource
    private EntityTypeScopeMapper entityTypeScopeMapper;
    @Resource
    private ModelMapper modelMapper;
    @Resource
    private EntityRepository entityRepository;

    /**
     * 这次实际读写的目录。
     *
     * @param pageEntry 用户打开的本页目录
     * @param entry     实际读写的目录入口（本页，或另一本目录）
     * @param samePage  true：按本页算；false：读写的是另一本目录，要另核对那本目录的授权
     */
    public record Target(EntityTypeScopeContext pageEntry, EntityTypeScopeContext entry, boolean samePage) {
    }

    /**
     * 列表查询实际使用的类型编码和业务域。
     *
     * @param scopeRegistryCode 这次实际读的目录注册编码，数据范围按它算（本页，或另一本目录）
     */
    public record QueryBinding(String entityTypeCode, String domain, String scopeRegistryCode) {
    }

    /**
     * 解析这次实际读写的目录。
     * 本页目录不存在、请求的类型不存在时报错，不猜。
     */
    public Target resolve(String registryCode, String requestedTypeCode) {
        EntityTypeScopeContext page = requireEntry(registryCode, "请求缺少本页数据目录");
        String requested = trimToNull(requestedTypeCode);
        if (requested == null || requested.equals(page.getRegistryCode())) {
            return new Target(page, page, true);
        }
        boolean reusesStorage = !page.getStorageEntityTypeCode().equals(page.getRegistryCode());
        if (reusesStorage && requested.equals(page.getStorageEntityTypeCode())) {
            return new Target(page, page, true);
        }
        EntityTypeScopeContext other = requireEntry(requested, "数据类型不存在：" + requested);
        return new Target(page, other, false);
    }

    /**
     * 实际读写另一本目录时，返回那本目录的注册编码，调用方据此再核对一次授权；按本页算时返回 null。
     */
    public String otherCatalogCode(String registryCode, String requestedTypeCode) {
        Target target = resolve(registryCode, requestedTypeCode);
        return target.samePage() ? null : target.entry().getRegistryCode();
    }

    /**
     * 列表查询：按本页算时把类型和业务域收回到本页。
     * <ul>
     *   <li>子数据类型页：业务域强制为本页业务域；请求另传别的业务域 → 拒绝。</li>
     *   <li>划分数据页：类型改为本页注册编码，查询按本页成员收窄。</li>
     *   <li>另一本目录：原样交给查询，由查询按那本目录自己的入口解析。</li>
     * </ul>
     */
    public QueryBinding bindQuery(String registryCode, String requestedTypeCode, String requestedDomain) {
        Target target = resolve(registryCode, requestedTypeCode);
        String domain = EntityTypeScopeContext.normalizeDomain(requestedDomain);
        String scope = target.entry().getRegistryCode();
        if (!target.samePage()) {
            return new QueryBinding(requestedTypeCode, domain, scope);
        }
        EntityTypeScopeContext page = target.pageEntry();
        String typeCode = StringUtils.hasText(requestedTypeCode) ? requestedTypeCode.trim() : page.getRegistryCode();
        if (page.isDomainEntry()) {
            if (domain != null && !EntityTypeScopeContext.domainsEqual(domain, page.getDomain())) {
                throw new ServiceException(403, "请求业务域与本页数据目录不一致");
            }
            return new QueryBinding(typeCode, page.getDomain(), scope);
        }
        if (page.isScopeEntry()) {
            return new QueryBinding(page.getRegistryCode(), domain, scope);
        }
        return new QueryBinding(typeCode, domain, scope);
    }

    /**
     * 按编号读写已有记录前：这些记录必须属于这次实际读写的目录。
     * 子数据类型要求业务域一致；划分数据要求是成员。记录不存在也拒绝，不跳过。
     */
    public void assertRecordsInTarget(String registryCode, String requestedTypeCode, Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        EntityTypeScopeContext entry = resolve(registryCode, requestedTypeCode).entry();
        if (!entry.isDomainEntry() && !entry.isScopeEntry()) {
            return;
        }
        List<Long> distinct = new ArrayList<>(new LinkedHashSet<>(ids));
        if (entry.isScopeEntry()) {
            Set<Long> members = new LinkedHashSet<>();
            for (EntityTypeScopeDO row : entityTypeScopeMapper.selectByCodeAndEntityIds(entry.getRegistryCode(), distinct)) {
                members.add(row.getEntityId());
            }
            if (!members.containsAll(distinct)) {
                throw new ServiceException(403, "记录不在本页数据目录的成员里");
            }
            return;
        }
        List<EntityDO> stored = entityRepository.findByIds(distinct, entry.getStorageEntityTypeCode());
        if (stored.size() != distinct.size()) {
            throw new ServiceException(403, "记录不在本页数据目录里");
        }
        for (EntityDO row : stored) {
            if (!EntityTypeScopeContext.domainsEqual(row.getDomain(), entry.getDomain())) {
                throw new ServiceException(403, "记录不在本页数据目录里");
            }
        }
    }

    /**
     * 按分类等条件查出一批编号后：只留下属于这次读写目录的记录，保持原顺序。
     * 子数据类型留本业务域的，划分数据留成员；原生目录全部保留。
     * 这是按授权收窄结果，不是补数据：不属于本页的记录本来就不该让本页看到。
     */
    public List<Long> retainRecordsInTarget(String registryCode, String requestedTypeCode, List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        EntityTypeScopeContext entry = resolve(registryCode, requestedTypeCode).entry();
        if (!entry.isDomainEntry() && !entry.isScopeEntry()) {
            return ids;
        }
        List<Long> distinct = new ArrayList<>(new LinkedHashSet<>(ids));
        Set<Long> kept = new LinkedHashSet<>();
        if (entry.isScopeEntry()) {
            for (EntityTypeScopeDO row : entityTypeScopeMapper.selectByCodeAndEntityIds(entry.getRegistryCode(), distinct)) {
                kept.add(row.getEntityId());
            }
        } else {
            for (EntityDO row : entityRepository.findByIds(distinct, entry.getStorageEntityTypeCode())) {
                if (EntityTypeScopeContext.domainsEqual(row.getDomain(), entry.getDomain())) {
                    kept.add(row.getId());
                }
            }
        }
        return ids.stream().filter(kept::contains).toList();
    }

    /**
     * 新建或换型号时：实体只能用这次读写目录的型号。
     * 实体的业务域随型号写入，所以型号不对就会写到别的业务域，这里先拒绝。
     */
    public void assertModelInTarget(String registryCode, String requestedTypeCode, Long modelId) {
        if (modelId == null) {
            return;
        }
        assertModelsInTarget(registryCode, requestedTypeCode, List.of(modelId));
    }

    /**
     * 按编号读写型号前：这些型号必须属于这次实际读写的目录。
     * 型号的类型编码要等于目录的底座类型；子数据类型还要求业务域一致。型号不存在也拒绝，不跳过。
     * 划分数据按底座类型的型号算，型号本身不分成员。
     */
    public void assertModelsInTarget(String registryCode, String requestedTypeCode, Collection<Long> modelIds) {
        if (modelIds == null || modelIds.isEmpty()) {
            return;
        }
        EntityTypeScopeContext entry = resolve(registryCode, requestedTypeCode).entry();
        List<Long> distinct = new ArrayList<>(new LinkedHashSet<>(modelIds));
        List<ModelDO> models = modelMapper.selectByIds(distinct);
        if (models.size() != distinct.size()) {
            throw new ServiceException(400, "型号不存在");
        }
        for (ModelDO model : models) {
            assertModelMatchesEntry(entry, model.getEntityTypeCode(), model.getDomain());
        }
    }

    /**
     * 新建型号前：请求里写的类型和业务域必须落在这次实际读写的目录里。
     * 子数据类型页不传业务域时拒绝，不替它填本页业务域。
     */
    public void assertNewModelInTarget(String registryCode, String requestedTypeCode, String modelDomain) {
        EntityTypeScopeContext entry = resolve(registryCode, requestedTypeCode).entry();
        assertModelMatchesEntry(entry, entry.getStorageEntityTypeCode(), modelDomain);
    }

    /**
     * 型号列表：查询实际使用的底座类型和业务域。
     * 型号按底座类型存，所以划分数据页和子数据类型页都换成底座类型；子数据类型再强制本页业务域，
     * 请求另传别的业务域就拒绝。
     */
    public QueryBinding bindModelQuery(String registryCode, String requestedTypeCode, String requestedDomain) {
        EntityTypeScopeContext entry = resolve(registryCode, requestedTypeCode).entry();
        String domain = EntityTypeScopeContext.normalizeDomain(requestedDomain);
        if (entry.isDomainEntry()) {
            if (domain != null && !EntityTypeScopeContext.domainsEqual(domain, entry.getDomain())) {
                throw new ServiceException(403, "请求业务域与本页数据目录不一致");
            }
            return new QueryBinding(entry.getStorageEntityTypeCode(), entry.getDomain(), entry.getRegistryCode());
        }
        return new QueryBinding(entry.getStorageEntityTypeCode(), domain, entry.getRegistryCode());
    }

    private static void assertModelMatchesEntry(EntityTypeScopeContext entry, String modelTypeCode, String modelDomain) {
        if (!entry.getStorageEntityTypeCode().equals(trimToNull(modelTypeCode))) {
            throw new ServiceException(403, "型号不属于本页数据目录");
        }
        if (entry.isDomainEntry() && !EntityTypeScopeContext.domainsEqual(modelDomain, entry.getDomain())) {
            throw new ServiceException(403, "型号不属于本页数据目录的业务域");
        }
    }

    /**
     * 按类型列整棵上级树（上级选择）：这棵树按底座类型取数，不带业务域、不按成员收窄。
     * 子数据类型必须带本业务域的型号，划分数据不能这样列，直接拒绝，不放出整张底座表。
     */
    public void assertWholeTypeTreeAllowed(String registryCode, String requestedTypeCode, Long modelId) {
        EntityTypeScopeContext entry = resolve(registryCode, requestedTypeCode).entry();
        if (entry.isScopeEntry()) {
            throw new ServiceException(403, "划分数据不能按类型列上级树");
        }
        if (entry.isDomainEntry()) {
            if (modelId == null) {
                throw new ServiceException(400, "子数据类型列上级树时必须指定型号");
            }
            assertModelInTarget(registryCode, requestedTypeCode, modelId);
        }
    }

    private EntityTypeScopeContext requireEntry(String code, String missingMessage) {
        String trimmed = trimToNull(code);
        if (trimmed == null) {
            throw new ServiceException(400, missingMessage);
        }
        EntityTypeDO type = entityTypeMapper.selectByCode(trimmed);
        if (type == null) {
            throw new ServiceException(400, "数据目录不存在：" + trimmed);
        }
        return EntityTypeScopeContext.from(type);
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
