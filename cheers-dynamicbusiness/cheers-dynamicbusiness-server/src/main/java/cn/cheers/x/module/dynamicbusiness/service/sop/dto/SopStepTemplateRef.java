package cn.cheers.x.module.dynamicbusiness.service.sop.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * SOP 步骤模板引用（有序步骤中的一步）。
 */
@Data
public class SopStepTemplateRef {

    private String stepTemplateId;
    private Integer order;
    private List<String> paramSlots = new ArrayList<>();
}
