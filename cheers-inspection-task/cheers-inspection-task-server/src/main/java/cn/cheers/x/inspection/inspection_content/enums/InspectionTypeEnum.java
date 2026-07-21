package cn.cheers.x.inspection.inspection_content.enums;

import cn.cheers.x.framework.common.exception.util.ServiceExceptionUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.cheers.x.framework.common.exception.enums.GlobalErrorCodeConstants.BAD_REQUEST;

/**
 * 巡检类型枚举。
 */
@Getter
@AllArgsConstructor
public enum InspectionTypeEnum {

    HUMAN("HUMAN"),
    GROUND_ROBOT("GROUND_ROBOT"),
    UAV("UAV");

    private static final Set<String> VALUES = Arrays.stream(values())
            .map(InspectionTypeEnum::getCode)
            .collect(Collectors.toSet());

    private final String code;

    public static void validate(String inspectionType) {
        if (inspectionType == null || inspectionType.isBlank() || !VALUES.contains(inspectionType)) {
            throw ServiceExceptionUtil.exception(BAD_REQUEST,
                    "巡检类型无效，必须是 HUMAN、GROUND_ROBOT 或 UAV");
        }
    }

    public static String normalize(String inspectionType) {
        validate(inspectionType);
        return inspectionType;
    }
}
