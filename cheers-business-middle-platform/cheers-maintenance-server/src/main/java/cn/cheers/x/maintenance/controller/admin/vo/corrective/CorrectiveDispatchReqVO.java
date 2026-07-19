package cn.cheers.x.maintenance.controller.admin.vo.corrective;
import lombok.Data;
@Data
public class CorrectiveDispatchReqVO {
    /** 可选；缺省走绑定解析（需 assetTypeCode） */
    private Long fieldStandardId;
}
