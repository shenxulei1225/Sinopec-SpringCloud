package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.PostAssignmentQueryService;
import cn.cheers.x.system.api.permission.RoleApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CatalogRoleGrantPostRuleTest {

    private static final long USER = 7L;
    private static final long DIRECT_ROLE = 1L;
    private static final long POST_ROLE = 209L;

    @Mock
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Mock
    private EntityTypeMapper entityTypeMapper;
    @Mock
    private PostAssignmentQueryService postAssignmentQueryService;
    @Mock
    private RoleApi roleApi;
    @InjectMocks
    private CatalogRoleGrantServiceImpl service;

    @BeforeEach
    void setUp() {
        when(entityTypeMapper.selectByCode(anyString())).thenReturn(new EntityTypeDO());
        when(roleApi.getUserRoleIdListByUserId(USER)).thenReturn(CommonResult.success(List.of(DIRECT_ROLE)));
        when(postAssignmentQueryService.listActivePostRoleIds(USER)).thenReturn(Set.of());
        when(catalogRoleGrantMapper.existsGrant(any(), anyString(), anyString(), anyString())).thenReturn(false);
        when(catalogRoleGrantMapper.existsGrant(argThat(CatalogRoleGrantPostRuleTest::containsDirectRole),
                anyString(), eq("entity"), eq("query"))).thenReturn(true);
        when(catalogRoleGrantMapper.existsGrant(argThat(CatalogRoleGrantPostRuleTest::containsPostRole),
                anyString(), eq("entity"), eq("query"))).thenReturn(true);
    }

    @Test
    void directRoleAllowsCatalogOperation() {
        assertThat(service.allow(USER, "organization", "entity", "query")).isTrue();
    }

    @Test
    void dutyConvertedRoleAllowsWithoutLookingAtTheUnit() {
        when(roleApi.getUserRoleIdListByUserId(USER)).thenReturn(CommonResult.success(List.of()));
        when(postAssignmentQueryService.listActivePostRoleIds(USER)).thenReturn(Set.of(POST_ROLE));

        assertThat(service.allow(USER, "organization", "entity", "query")).isTrue();
    }

    @Test
    void missingRoleGrantRejects() {
        when(catalogRoleGrantMapper.existsGrant(any(), anyString(), anyString(), anyString())).thenReturn(false);

        assertThat(service.allow(USER, "organization", "entity", "delete")).isFalse();
    }

    private static boolean containsDirectRole(Collection<Long> roleIds) {
        return roleIds != null && roleIds.contains(DIRECT_ROLE);
    }

    private static boolean containsPostRole(Collection<Long> roleIds) {
        return roleIds != null && roleIds.contains(POST_ROLE);
    }
}
