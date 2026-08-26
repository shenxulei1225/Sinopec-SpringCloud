package cn.cheers.x.module.dynamicbusiness.service.model.governance;

import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import cn.cheers.x.framework.common.pojo.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelGovernanceQueryServiceImplTest {

    private final ModelGovernanceQueryService service = new ModelGovernanceQueryServiceImpl();

    @Test
    void localModelsAreVisibleOnlyAtTheirOriginFacility() {
        ModelDO facilityA = model(1L, "LOCAL", 10L, 1);
        ModelDO facilityB = model(2L, "LOCAL", 20L, 1);

        List<ModelDO> visible = service.filterVisible(List.of(facilityA, facilityB), 10L, false);

        assertEquals(List.of(1L), visible.stream().map(ModelDO::getId).toList());
    }

    @Test
    void enabledCompanyModelsAreVisibleAtEveryFacility() {
        ModelDO company = model(3L, "COMPANY", null, 1);

        assertEquals(List.of(company), service.filterVisible(List.of(company), 10L, false));
        assertEquals(List.of(company), service.filterVisible(List.of(company), 20L, false));
    }

    @Test
    void disabledCompanyModelsAreExcludedFromDefaultLists() {
        ModelDO company = model(4L, "COMPANY", null, 0);

        assertEquals(List.of(), service.filterVisible(List.of(company), 10L, false));
    }

    @Test
    void networkDataAdminCanSeeLocalModelsAcrossFacilities() {
        ModelDO facilityB = model(5L, "LOCAL", 20L, 1);

        assertEquals(List.of(facilityB), service.filterVisible(List.of(facilityB), 10L, true));
    }

    @Test
    void pageTotalIsCalculatedAfterVisibilityFiltering() {
        ModelDO visibleCompany = model(6L, "COMPANY", null, 1);
        ModelDO hiddenLocal = model(7L, "LOCAL", 20L, 1);

        PageResult<ModelDO> page = service.filterVisiblePage(
                List.of(visibleCompany, hiddenLocal), 10L, false, 1, 10);

        assertEquals(1L, page.getTotal());
        assertEquals(List.of(visibleCompany), page.getList());
    }

    private static ModelDO model(Long id, String governanceStatus, Long originFacilityId, Integer status) {
        ModelDO model = new ModelDO();
        model.setId(id);
        model.setGovernanceStatus(governanceStatus);
        model.setOriginFacilityId(originFacilityId);
        model.setStatus(status);
        return model;
    }
}
