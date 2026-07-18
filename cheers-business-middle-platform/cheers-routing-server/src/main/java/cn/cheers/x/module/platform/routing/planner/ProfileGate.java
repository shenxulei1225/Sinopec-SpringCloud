package cn.cheers.x.module.platform.routing.planner;

import cn.cheers.x.module.platform.contract.dto.network.MobilityProfileDTO;
import cn.cheers.x.module.platform.contract.dto.network.PathNetworkDTO;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_PROFILE_NETWORK_FORBIDDEN;
import static cn.cheers.x.module.platform.routing.enums.ErrorCodeConstants.ROUTE_PROFILE_PORTAL_FORBIDDEN;
import static cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil.exception;

@Component
public class ProfileGate {

    public void assertAllowed(MobilityProfileDTO profile, PathNetworkDTO network) {
        if (profile == null || network == null || network.getNetworkKind() == null) {
            throw exception(ROUTE_PROFILE_NETWORK_FORBIDDEN);
        }
        if (CollectionUtils.isEmpty(profile.getAllowedNetworkKinds())
                || !profile.getAllowedNetworkKinds().contains(network.getNetworkKind())) {
            throw exception(ROUTE_PROFILE_NETWORK_FORBIDDEN);
        }
    }

    public void assertPortalAllowed(MobilityProfileDTO profile, boolean usesPortal) {
        if (profile == null || !usesPortal) {
            return;
        }
        if ("ground_robot".equals(profile.getProfileId())) {
            throw exception(ROUTE_PROFILE_PORTAL_FORBIDDEN);
        }
    }
}
