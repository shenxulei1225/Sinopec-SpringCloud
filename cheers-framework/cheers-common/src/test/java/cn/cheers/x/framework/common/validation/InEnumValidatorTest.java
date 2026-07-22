package cn.cheers.x.framework.common.validation;

import cn.cheers.x.framework.common.core.ArrayValuable;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 覆盖 HV000084：{@code @InEnum} 注解属性 / 默认 message 与 Hibernate Validator 8 的兼容性。
 */
class InEnumValidatorTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void validate_buildsConstraintMetadata_withoutHv000084() {
        SampleReq req = new SampleReq();
        req.setType(10);
        assertDoesNotThrow(() -> validator.validate(req));
    }

    @Test
    void validate_acceptsValueInEnum() {
        SampleReq req = new SampleReq();
        req.setType(10);
        Set<ConstraintViolation<SampleReq>> violations = validator.validate(req);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validate_rejectsValueOutOfEnum() {
        SampleReq req = new SampleReq();
        req.setType(99);
        Set<ConstraintViolation<SampleReq>> violations = validator.validate(req);
        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertTrue(violations.iterator().next().getMessage().contains("必须在指定范围"));
    }

    @Data
    static class SampleReq {
        @InEnum(SampleTypeEnum.class)
        private Integer type;
    }

    enum SampleTypeEnum implements ArrayValuable<Integer> {
        A(10),
        B(20);

        private final Integer value;

        SampleTypeEnum(Integer value) {
            this.value = value;
        }

        @Override
        public Integer[] array() {
            return Arrays.stream(values()).map(e -> e.value).toArray(Integer[]::new);
        }
    }
}
