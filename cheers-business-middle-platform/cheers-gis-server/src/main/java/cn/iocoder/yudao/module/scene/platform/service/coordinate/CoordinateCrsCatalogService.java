package cn.iocoder.yudao.module.scene.platform.service.coordinate;

import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateCrsCatalogRespVO;

import java.util.List;

public interface CoordinateCrsCatalogService {

    List<CoordinateCrsCatalogRespVO> getEnabledCatalogList(String crsType);

    CoordinateCrsCatalogRespVO getByCrsCode(String crsCode);
}
