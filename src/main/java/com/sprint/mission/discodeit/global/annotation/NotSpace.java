package com.sprint.mission.discodeit.global.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})//컨트롤러에서 사용할수도 있으니 파라미터도 허용
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = NotSpaceValidator.class)
public @interface NotSpace {

  String message() default "공백이 아니어야 합니다";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
