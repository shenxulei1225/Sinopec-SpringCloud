package cn.cheers.x.module.dynamicbusiness.service.reference.provider;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferenceValidateReq {

    private String id;
    private String context;
}
