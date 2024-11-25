package ru.kopytov.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import ru.kopytov.dto.TaskDto;

import java.util.Arrays;
import java.util.List;

@Component
@Aspect
@Slf4j
public class MainAspect {

    @Before("target(ru.kopytov.repository.TaskRepository))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Start method of repository: " + joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info("with args: " + Arrays.toString(args));
        }
    }

    @AfterThrowing("target(ru.kopytov.service.BaseTaskService))")
    public void logAfterThrowing(JoinPoint joinPoint) {
        log.warn("Throwing exception in method: " + joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "@annotation(HandlingResult)",
            returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, List<TaskDto> result) {
        log.info("Starting method: " + joinPoint.getSignature().getName());
        log.info("Returning result : ");
        if (!CollectionUtils.isEmpty(result)) {
            result.forEach(System.out::println);
        }

    }

    @Around("@annotation(LogController)")
    public Object logControllerAround(ProceedingJoinPoint joinPoint) {
        log.info("Start controller method: " + joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs();
        if (args != null && args.length > 0) {
            log.info("with args: " + Arrays.toString(args));
        }
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        log.info("End controller method: " + joinPoint.getSignature().getName());
        return result;
    }
}
