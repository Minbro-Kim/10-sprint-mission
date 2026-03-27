package com.sprint.mission.discodeit.global.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NotSpaceValidator implements ConstraintValidator<NotSpace, String> {

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    return s == null || StringUtils.hasText(s);
  }
}
