package cn.iocoder.yudao.module.scene.platform.service.coordinate.impl;

import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.module.scene.platform.controller.admin.coordinate.vo.CoordinateCrsCatalogRespVO;
import cn.iocoder.yudao.module.scene.platform.dal.dataobject.coordinate.CoordinateCrsCatalogDO;
import cn.iocoder.yudao.module.scene.platform.dal.mysql.coordinate.CoordinateCrsCatalogMapper;
import cn.iocoder.yudao.module.scene.platform.service.coordinate.CoordinateCrsCatalogService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class CoordinateCrsCatalogServiceImpl implements CoordinateCrsCatalogService {

    @Resource
    private CoordinateCrsCatalogMapper coordinateCrsCatalogMapper;

    @Override
    public List<CoordinateCrsCatalogRespVO> getEnabledCatalogList(String crsType) {
        List<CoordinateCrsCatalogRespVO> list = BeanUtils.toBean(coordinateCrsCatalogMapper.selectListEnabled(), CoordinateCrsCatalogRespVO.class);
        if (crsType == null || crsType.isBlank()) {
            return list;
        }
        return list.stream()
                .filter(item -> Objects.equals(item.getCrsType(), crsType.trim().toUpperCase()))
                .toList();
    }

    @Override
    public CoordinateCrsCatalogRespVO getByCrsCode(String crsCode) {
        if (crsCode == null || crsCode.isBlank()) {
            return null;
        }
        CoordinateCrsCatalogDO data = coordinateCrsCatalogMapper.selectByCrsCode(crsCode);
        return BeanUtils.toBean(data, CoordinateCrsCatalogRespVO.class);
    }
}
