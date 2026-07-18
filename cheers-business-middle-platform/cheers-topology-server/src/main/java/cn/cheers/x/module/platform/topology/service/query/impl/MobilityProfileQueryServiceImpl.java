package cn.cheers.x.module.platform.topology.service.query.impl;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.topology.dal.dataobject.MobilityProfileDO;
import cn.cheers.x.module.platform.topology.dal.mysql.MobilityProfileMapper;
import cn.cheers.x.module.platform.topology.service.convert.MobilityProfileConvert;
import cn.cheers.x.module.platform.topology.service.query.MobilityProfileQueryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MobilityProfileQueryServiceImpl implements MobilityProfileQueryService {

    @Resource
    private MobilityProfileMapper mobilityProfileMapper;

    @Override
    public List<MobilityProfileDTO> listProfiles() {
        return mobilityProfileMapper.selectAllActive().stream()
                .map(MobilityProfileConvert::toDto)
                .toList();
    }

    @Override
    public MobilityProfileDTO getProfile(String profileId) {
        MobilityProfileDO profile = mobilityProfileMapper.selectById(profileId);
        return profile == null ? null : MobilityProfileConvert.toDto(profile);
    }
}
