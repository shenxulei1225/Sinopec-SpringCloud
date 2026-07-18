package cn.cheers.x.scene.platform.service.coordinate;

import cn.cheers.x.scene.platform.controller.admin.coordinate.vo.CoordinateCrsCatalogRespVO;

import java.util.List;

public interface CoordinateCrsCatalogService {

    List<CoordinateCrsCatalogRespVO> getEnabledCatalogList(String crsType);

    CoordinateCrsCatalogRespVO getByCrsCode(String crsCode);
}
