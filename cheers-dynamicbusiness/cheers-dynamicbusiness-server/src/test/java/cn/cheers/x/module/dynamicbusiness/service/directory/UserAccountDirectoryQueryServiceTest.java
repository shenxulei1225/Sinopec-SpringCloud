package cn.cheers.x.module.dynamicbusiness.service.directory;

import cn.cheers.x.framework.common.pojo.PageParam;
import cn.cheers.x.framework.common.pojo.PageResult;
import cn.cheers.x.module.dynamicbusiness.api.directory.dto.UserAccountDTO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entity.EntityDO;
import cn.cheers.x.module.dynamicbusiness.dal.repository.entity.EntityRepository;
import cn.cheers.x.module.dynamicbusiness.service.entity.EntityDedicatedColumnService;
import cn.cheers.x.module.dynamicbusiness.service.entity.core.EntityCoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserAccountDirectoryQueryServiceTest {

    private EntityCoreService entityCoreService;
    private EntityRepository entityRepository;
    private EntityDedicatedColumnService dedicatedColumnService;
    private UserAccountDirectoryQueryService service;

    @BeforeEach
    void setUp() {
        entityCoreService = mock(EntityCoreService.class);
        entityRepository = mock(EntityRepository.class);
        dedicatedColumnService = mock(EntityDedicatedColumnService.class);
        service = new UserAccountDirectoryQueryService(
                entityCoreService, entityRepository, dedicatedColumnService);
    }

    @Test
    void listUserAccountsByIds_returnsStrongTypedDirectoryFields() {
        EntityDO first = user(1L, "alice", "艾丽丝", "13800000001", 10L);
        EntityDO second = user(2L, "bob", "鲍勃", "13800000002", 20L);
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(1L, 2L), "user_account", false)).thenReturn(List.of(first, second));

        List<UserAccountDTO> result = service.listUserAccountsByIds(List.of(1L, 2L));

        assertEquals(List.of("alice", "bob"), result.stream().map(UserAccountDTO::getAccount).toList());
        assertEquals(20L, result.get(1).getDepartmentId());
        assertEquals("13800000002", result.get(1).getMobile());
    }

    @Test
    void getUserAccountByMobile_filtersPhysicalColumnThenLoadsDirectoryFields() {
        EntityDO matched = user(4L, "dave", "戴维", "13800000004", 30L);
        when(dedicatedColumnService.resolvePhysicalColumn("user_account", "mobile"))
                .thenReturn("mobile");
        when(entityCoreService.listEntities("user_account", null, null))
                .thenReturn(List.of(matched));
        when(entityRepository.retainOrderedIdsByPhysicalFilters(
                eq(List.of(4L)), eq("user_account"), anyList()))
                .thenReturn(List.of(4L));
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(4L), "user_account", false)).thenReturn(List.of(matched));

        UserAccountDTO result = service.getUserAccountByMobile(" 13800000004 ");

        assertEquals(4L, result.getId());
        assertEquals("13800000004", result.getMobile());
    }

    @Test
    void listUserAccountsByIds_parsesVisitTenantIdsFromDedicatedColumn() {
        EntityDO matched = user(5L, "erin", "艾琳", null, null);
        matched.setDedicatedBaseFieldValues(Map.of("visit_tenant_ids", "1, 3,5"));
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(5L), "user_account", false)).thenReturn(List.of(matched));

        UserAccountDTO result = service.listUserAccountsByIds(List.of(5L)).get(0);

        assertEquals(List.of(1L, 3L, 5L), result.getVisitTenantIds());
    }

    @Test
    void listUserAccountsByNickname_loadsMatchedDirectoryRowsOnce() {
        EntityDO matched = user(6L, "frank", "弗兰克", null, null);
        when(entityCoreService.pageEntities(
                "user_account", null, null, "弗兰克", 1, PageParam.PAGE_SIZE_NONE))
                .thenReturn(new PageResult<>(List.of(matched), 1L));
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(6L), "user_account", false)).thenReturn(List.of(matched));

        List<UserAccountDTO> result = service.listUserAccountsByNickname(" 弗兰克 ");

        assertEquals(List.of(6L), result.stream().map(UserAccountDTO::getId).toList());
        assertEquals("弗兰克", result.get(0).getNickname());
    }

    @Test
    void getUserAccountByAccount_resolvesCodeThenLoadsDedicatedFieldsOnce() {
        EntityDO matched = user(3L, "carol", "卡罗尔", null, null);
        when(entityCoreService.listByCodes(List.of("carol"), "user_account"))
                .thenReturn(List.of(matched));
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(3L), "user_account", false)).thenReturn(List.of(matched));

        UserAccountDTO result = service.getUserAccountByAccount(" carol ");

        assertEquals(3L, result.getId());
        assertEquals("卡罗尔", result.getNickname());
    }

    private static EntityDO user(
            Long id,
            String account,
            String nickname,
            String mobile,
            Long departmentId) {
        EntityDO row = EntityDO.builder()
                .id(id)
                .entityTypeCode("user_account")
                .code(account)
                .name(nickname)
                .status(1)
                .build();
        if (mobile != null || departmentId != null) {
            java.util.LinkedHashMap<String, Object> fields = new java.util.LinkedHashMap<>();
            if (mobile != null) {
                fields.put("mobile", mobile);
            }
            if (departmentId != null) {
                fields.put("department_id", departmentId);
            }
            row.setDedicatedBaseFieldValues(Map.copyOf(fields));
        }
        return row;
    }
}
