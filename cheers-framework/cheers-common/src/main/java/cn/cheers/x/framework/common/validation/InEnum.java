package cn.cheers.x.framework.common.validation;

import cn.cheers.x.framework.common.core.ArrayValuable;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({
        ElementType.METHOD,
        ElementType.FIELD,
        ElementType.ANNOTATION_TYPE,
        ElementType.CONSTRUCTOR,
        ElementType.PARAMETER,
        ElementType.TYPE_USE
})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(
        validatedBy = {InEnumValidator.class, InEnumCollectionValidator.class}
)
public @interface InEnum {

    /**
     * 实现 {@link ArrayValuable} 的枚举类。
     * <p>
     * 注意：属性类型勿写成 {@code Class<? extends ArrayValuable<?>>}（嵌套通配符）。
     * Hibernate Validator 8 读取注解属性时会触发 HV000084
     * （Unable to get attribute 'value' from annotation）。
     * <p>
     * 默认 message 勿使用 {@code {value}}：会与本属性名冲突，插值时再次踩 HV000084。
     */
    @SuppressWarnings("rawtypes")
    Class<? extends ArrayValuable> value();

    String message() default "必须在指定范围 {values}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
