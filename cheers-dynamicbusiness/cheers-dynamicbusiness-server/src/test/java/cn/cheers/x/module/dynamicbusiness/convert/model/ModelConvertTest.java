package cn.cheers.x.module.dynamicbusiness.convert.model;

import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelCreateReqVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelRespVO;
import cn.cheers.x.module.dynamicbusiness.controller.admin.model.vo.ModelUpdateReqVO;
import cn.cheers.x.module.dynamicbusiness.dal.dataobject.model.ModelDO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ModelConvertTest {

    @Test
    void shouldCarryGovernanceFieldsAcrossApiBoundary() {
        ModelCreateReqVO createReqVO = new ModelCreateReqVO();
        createReqVO.setGovernanceStatus("LOCAL");
        assertEquals("LOCAL", ModelConvert.INSTANCE.convert(createReqVO).getGovernanceStatus());

        ModelUpdateReqVO updateReqVO = new ModelUpdateReqVO();
        updateReqVO.setGovernanceStatus("COMPANY");
        assertEquals("COMPANY", ModelConvert.INSTANCE.convert(updateReqVO).getGovernanceStatus());

        ModelDO model = new ModelDO();
        model.setGovernanceStatus("LOCAL");
        model.setOriginFacilityId(23L);
        model.setCreatorUserId(42L);

        ModelRespVO response = ModelConvert.INSTANCE.convert(model);
        assertEquals("LOCAL", response.getGovernanceStatus());
        assertEquals(23L, response.getOriginFacilityId());
        assertEquals(42L, response.getCreatorUserId());
    }
}
