package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.UserDataScopeDO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.UserDataScopeMapper;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.system.api.user.AdminUserApi;
import cn.cheers.x.system.api.user.dto.AdminUserRespDTO;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 读取一个人能访问哪些管理单元。
 * 范围记在用户目录的人员上。选了集团，就含集团下面的组织，以及所属组织落在这些组织上的场站和管线。
 * 登录编号和人员编号不是同一个。登录后用登录账号对上人员账号，再读这个人的范围。
 * 不负责：写入；不负责：按人所在部门推断范围；不负责：登录账号对不上人员时另造一份范围。
 */
@Service
public class UserDataScopeQueryService {

    /** 用户目录。登录账号和这里的人员账号相同，才是同一个人。 */
    private static final String USER_ACCOUNT = "user_account";

    @Resource
    private UserDataScopeMapper userDataScopeMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;
    @Resource
    private AdminUserApi adminUserApi;
    @Resource
    private EntityRepository entityRepository;

    public record UnitView(Long id, Long userId, String registryCode, Long unitId, String unitName) {
    }

    public List<UserDataScopeDO> listByUser(Long userId) {
        if (userId == null) {
            return List.of();
        }
        return userDataScopeMapper.selectListByUserId(userId);
    }

    /**
     * 登录人能访问哪些管理单元。
     * 先用登录账号找到用户目录里账号相同的人员，再读这个人已经保存的范围。
     * 对不上人员时返回空：受约束的目录一条也看不到。不把登录编号当成人员编号再查一遍。
     */
    public List<UserDataScopeDO> listForLoginUser(Long loginUserId) {
        Long personId = personIdForLogin(loginUserId);
        if (personId == null) {
            return List.of();
        }
        return listByUser(personId);
    }

    private Long personIdForLogin(Long loginUserId) {
        if (loginUserId == null) {
            return null;
        }
        AdminUserRespDTO login = adminUserApi.getUser(loginUserId).getCheckedData();
        if (login == null || !StringUtils.hasText(login.getUsername())) {
            return null;
        }
        List<EntityDO> people = entityRepository.findByCodes(List.of(login.getUsername().trim()), USER_ACCOUNT);
        if (people == null || people.isEmpty() || people.get(0).getId() == null) {
            return null;
        }
        return people.get(0).getId();
    }

    public List<UnitView> listViewsByUser(Long userId) {
        List<UserDataScopeDO> rows = listByUser(userId);
        if (rows.isEmpty()) {
            return List.of();
        }
        List<Map.Entry<String, Long>> refs = rows.stream()
                .map(row -> Map.entry(row.getRegistryCode(), row.getUnitId()))
                .toList();
        Map<String, Map<Long, String>> names = conditionResolver.selectNamesByCatalog(refs);
        return rows.stream().map(row -> new UnitView(
                row.getId(),
                row.getUserId(),
                row.getRegistryCode(),
                row.getUnitId(),
                names.getOrDefault(row.getRegistryCode(), Map.of()).get(row.getUnitId()))).toList();
    }
}
