package cn.cheers.x.module.dynamicbusiness.service.directory;

import cn.cheers.x.module.dynamicbusiness.api.directory.dto.DepartmentDTO;
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

class DepartmentDirectoryQueryServiceTest {

    private EntityCoreService entityCoreService;
    private EntityRepository entityRepository;
    private EntityDedicatedColumnService dedicatedColumnService;
    private DepartmentDirectoryQueryService service;

    @BeforeEach
    void setUp() {
        entityCoreService = mock(EntityCoreService.class);
        entityRepository = mock(EntityRepository.class);
        dedicatedColumnService = mock(EntityDedicatedColumnService.class);
        service = new DepartmentDirectoryQueryService(
                entityCoreService, entityRepository, dedicatedColumnService);
    }

    @Test
    void listSubtree_returnsRootAndAllDescendantsInOneBatch() {
        EntityDO root = department(1L, null, "/1/", null);
        EntityDO child = department(2L, 1L, "/1/2/", null);
        EntityDO grandchild = department(3L, 2L, "/1/2/3/", null);
        EntityDO outside = department(9L, null, "/9/", null);
        when(entityCoreService.listTreeEntities("department", null))
                .thenReturn(List.of(root, child, grandchild, outside));
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(1L, 2L, 3L), "department", false))
                .thenReturn(List.of(root, child, grandchild));

        List<DepartmentDTO> result = service.listSubtree(1L);

        assertEquals(List.of(1L, 2L, 3L), result.stream().map(DepartmentDTO::getId).toList());
    }

    @Test
    void getLeaderUserAccountId_readsDedicatedDepartmentColumn() {
        EntityDO row = department(7L, null, "/7/", 88L);
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(7L), "department", false)).thenReturn(List.of(row));

        assertEquals(88L, service.getLeaderUserAccountId(7L));
    }

    @Test
    void listDepartmentsByIds_loadsRequestedDepartmentsInBatch() {
        EntityDO first = department(4L, null, "/4/", null);
        EntityDO second = department(5L, null, "/5/", 99L);
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(4L, 5L), "department", false)).thenReturn(List.of(first, second));

        List<DepartmentDTO> result = service.listDepartmentsByIds(List.of(4L, 5L));

        assertEquals(List.of(4L, 5L), result.stream().map(DepartmentDTO::getId).toList());
        assertEquals(99L, result.get(1).getLeaderUserId());
    }

    @Test
    void listUserAccountIdsByDepartmentIds_filtersAndGroupsInBatches() {
        EntityDO department10 = department(10L, null, "/10/", null);
        EntityDO department20 = department(20L, null, "/20/", null);
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(10L, 20L), "department", false))
                .thenReturn(List.of(department10, department20));
        when(dedicatedColumnService.resolvePhysicalColumn("user_account", "department_id"))
                .thenReturn("department_id");
        when(entityCoreService.listEntities("user_account", null, null))
                .thenReturn(List.of(user(101L, null), user(102L, null), user(103L, null)));
        when(entityRepository.retainOrderedIdsByPhysicalFilters(
                eq(List.of(101L, 102L, 103L)), eq("user_account"), anyList()))
                .thenReturn(List.of(101L, 102L, 103L));
        when(entityCoreService.listByIdsWithDedicatedBaseFields(
                List.of(101L, 102L, 103L), "user_account", false))
                .thenReturn(List.of(user(101L, 10L), user(102L, 20L), user(103L, 10L)));

        Map<Long, List<Long>> result = service.listUserAccountIdsByDepartmentIds(List.of(10L, 20L));

        assertEquals(List.of(101L, 103L), result.get(10L));
        assertEquals(List.of(102L), result.get(20L));
    }

    private static EntityDO department(Long id, Long parentId, String treePath, Long leaderUserId) {
        EntityDO row = EntityDO.builder()
                .id(id)
                .entityTypeCode("department")
                .code("D" + id)
                .name("部门" + id)
                .parentId(parentId)
                .treePath(treePath)
                .status(1)
                .build();
        if (leaderUserId != null) {
            row.setDedicatedBaseFieldValues(Map.of("leader_user_id", leaderUserId));
        }
        return row;
    }

    private static EntityDO user(Long id, Long departmentId) {
        EntityDO row = EntityDO.builder()
                .id(id)
                .entityTypeCode("user_account")
                .code("U" + id)
                .name("用户" + id)
                .status(1)
                .build();
        if (departmentId != null) {
            row.setDedicatedBaseFieldValues(Map.of("department_id", departmentId));
        }
        return row;
    }
}
