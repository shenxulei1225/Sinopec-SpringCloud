package cn.cheers.x.module.dynamicbusiness.service.reference.provider;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReferenceBatchGetReq {

    private List<String> ids;
}
