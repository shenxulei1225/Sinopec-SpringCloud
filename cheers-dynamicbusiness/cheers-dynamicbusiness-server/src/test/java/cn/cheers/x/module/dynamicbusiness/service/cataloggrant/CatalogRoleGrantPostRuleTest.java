package cn.cheers.x.module.dynamicbusiness.service.cataloggrant;

import cn.cheers.x.framework.common.pojo.CommonResult;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.entitytype.EntityTypeDO;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.cataloggrant.CatalogRoleGrantMapper;
import cn.cheers.x.module.dynamicbusiness.dal.mysql.entitytype.EntityTypeMapper;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.CatalogOwnershipQueryService;
import cn.cheers.x.module.dynamicbusiness.service.datapermission.ManagementUnitCatalogQueryService;
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CatalogRoleGrantPostRuleTest {

    private static final long USER = 7L;
    private static final long DIRECT_ROLE = 1L;
    private static final long POST_ROLE = 209L;
    private static final long ZONE_MODEL = 2973L;
    private static final long COMPANY_MODEL = 2972L;

    @Mock
    private CatalogRoleGrantMapper catalogRoleGrantMapper;
    @Mock
    private EntityTypeMapper entityTypeMapper;
    @Mock
    private CatalogOwnershipQueryService catalogOwnershipQueryService;
    @Mock
    private ManagementUnitCatalogQueryService managementUnitCatalogQueryService;
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
        when(catalogRoleGrantMapper.existsGrant(any(), anyString(), anyString(), anyString())).thenReturn(false);
        when(catalogRoleGrantMapper.existsGrant(eq(List.of(DIRECT_ROLE)), anyString(), eq("entity"), eq("query")))
                .thenReturn(true);
        when(catalogRoleGrantMapper.existsGrant(eq(Set.of(POST_ROLE)), anyString(), eq("entity"), eq("query")))
                .thenReturn(true);
        when(managementUnitCatalogQueryService.loadCatalogsByModelId())
                .thenReturn(Map.of(ZONE_MODEL, Set.of("equipment"), COMPANY_MODEL, Set.of()));
    }

    @Test
    void openCatalogAcceptsDirectRole() {
        when(catalogOwnershipQueryService.isRestricted("sop")).thenReturn(false);
        when(postAssignmentQueryService.listActivePosts(USER)).thenReturn(List.of());

        assertThat(service.allow(USER, "sop", "entity", "query")).isTrue();
    }

    @Test
    void restrictedCatalogIgnoresDirectRole() {
        when(catalogOwnershipQueryService.isRestricted("equipment")).thenReturn(true);
        when(postAssignmentQueryService.listActivePosts(USER)).thenReturn(List.of());

        assertThat(service.allow(USER, "equipment", "entity", "query")).isFalse();
    }

    @Test
    void restrictedCatalogAcceptsPostWhoseUnitTypeRegisteredIt() {
        when(catalogOwnershipQueryService.isRestricted("equipment")).thenReturn(true);
        when(postAssignmentQueryService.listActivePosts(USER)).thenReturn(List.of(
                new PostAssignmentQueryService.ActivePost(1L, 86L, ZONE_MODEL, Set.of(POST_ROLE))));

        assertThat(service.allow(USER, "equipment", "entity", "query")).isTrue();
    }

    @Test
    void postRoleNeedsUnitTypeRegistration() {
        when(catalogOwnershipQueryService.isRestricted("equipment")).thenReturn(true);
        when(postAssignmentQueryService.listActivePosts(USER)).thenReturn(List.of(
                new PostAssignmentQueryService.ActivePost(1L, 30L, COMPANY_MODEL, Set.of(POST_ROLE))));

        assertThat(service.allow(USER, "equipment", "entity", "query")).isFalse();
    }
}
