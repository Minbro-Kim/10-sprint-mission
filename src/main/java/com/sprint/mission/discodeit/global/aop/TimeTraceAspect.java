package com.sprint.mission.discodeit.global.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TimeTraceAspect {

  @Around("execution(* com.sprint.mission.discodeit.controller..*(..))")
  //서비스 레벨로 찍으려면 아래 문장 추가
  //|| execution(* com.sprint.mission.discodeit.service..*(..))
  public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {

    long start = System.currentTimeMillis();
    boolean success = true;
    String taskName = joinPoint.getSignature().toShortString();

    try {
      return joinPoint.proceed();
    } catch (Throwable throwable) {
      success = false;
      throw throwable;//다시 예외 던져주기
    } finally {
      long end = System.currentTimeMillis();
      long time = end - start;
      if (success) {
        if (time > 200) {
          log.info("[SLOW] {} |  {} ms", taskName, time);
        } else {
          log.debug("[OK] {} | {}ms", taskName, time);
        }
      }
//      else {/예외 로그가 있으므로 제외
//        log.warn("[FAIL] {} | {}ms", taskName, time);
//      }

    }
  }
}