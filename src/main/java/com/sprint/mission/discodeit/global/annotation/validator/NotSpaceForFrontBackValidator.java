package com.sprint.mission.discodeit.global.annotation.validator;

import com.sprint.mission.discodeit.global.annotation.annotation.NotSpaceForFrontBack;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotSpaceForFrontBackValidator implements
    ConstraintValidator<NotSpaceForFrontBack, String> {

  @Override
  public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
    return s == null || s.trim().equals(s);
  }
}
