package cn.cheers.x.module.dynamicbusiness.service.entity.scene;

import cn.cheers.x.framework.common.exception.ServiceException;
import cn.cheers.x.module.dynamicbusiness.controller.admin.entity.vo.FieldFilterReqVO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FacilityOwningListQueryGateTest {

    @Test
    void 全网类型不拦() {
        assertDoesNotThrow(() ->
                FacilityOwningListQueryGate.assertListHasOwningOrIdPin(false, null, List.of()));
    }

    @Test
    void 站场级列表没有所属场站就报错() {
        ServiceException ex = assertThrows(ServiceException.class, () ->
                FacilityOwningListQueryGate.assertListHasOwningOrIdPin(true, null, List.of()));
        assertEquals(FacilityOwningListQueryGate.MISSING_FACILITY_MESSAGE, ex.getMessage());
    }

    @Test
    void 带了所属场站可以查() {
        FieldFilterReqVO filter = new FieldFilterReqVO();
        filter.setFieldCode("facility_id");
        filter.setOp("EQ");
        filter.setValue(44);
        assertDoesNotThrow(() ->
                FacilityOwningListQueryGate.assertListHasOwningOrIdPin(true, null, List.of(filter)));
    }

    @Test
    void 按实体id点名可以查() {
        FieldFilterReqVO filter = new FieldFilterReqVO();
        filter.setFieldCode("id");
        filter.setOp("IN");
        filter.setValue(List.of(43, 71));
        assertDoesNotThrow(() ->
                FacilityOwningListQueryGate.assertListHasOwningOrIdPin(true, null, List.of(filter)));
    }
}
