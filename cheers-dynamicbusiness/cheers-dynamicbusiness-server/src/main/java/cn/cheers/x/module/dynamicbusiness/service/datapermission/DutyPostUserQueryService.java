package cn.cheers.x.module.dynamicbusiness.service.datapermission;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.datapermission.DutyPostUserDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.datapermission.DutyPostUserMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 读取一个职务上的人员，并补上用户目录里的姓名。
 * 不负责：写入。不负责：按管理单元过滤。
 */
@Service
public class DutyPostUserQueryService {

    public record DutyPostUserView(DutyPostUserDO row, String userName) {
    }

    @Resource
    private DutyPostUserMapper dutyPostUserMapper;
    @Resource
    private DataScopeConditionResolver conditionResolver;

    /** 这个职务上的全部人员。姓名来自用户目录。 */
    public List<DutyPostUserView> listByDutyPostId(Long dutyPostId) {
        if (dutyPostId == null) {
            return List.of();
        }
        List<DutyPostUserDO> rows = dutyPostUserMapper.selectListByDutyPostId(dutyPostId);
        if (rows.isEmpty()) {
            return List.of();
        }
        List<Map.Entry<String, Long>> refs = new ArrayList<>();
        rows.forEach(row -> refs.add(Map.entry("user_account", row.getUserId())));
        Map<Long, String> names = conditionResolver.selectNamesByCatalog(refs).getOrDefault("user_account", Map.of());
        return rows.stream().map(row -> new DutyPostUserView(row, names.get(row.getUserId()))).toList();
    }
}
