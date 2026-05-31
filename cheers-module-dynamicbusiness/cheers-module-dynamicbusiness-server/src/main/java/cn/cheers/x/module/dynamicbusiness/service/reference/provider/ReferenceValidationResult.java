package cn.cheers.x.module.dynamicbusiness.service.reference.provider;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferenceValidationResult {

    private Boolean valid;
    private String reason;

    public Boolean isValid() {
        return valid;
    }
}
