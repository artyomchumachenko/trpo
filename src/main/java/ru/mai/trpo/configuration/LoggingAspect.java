package ru.mai.trpo.configuration;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Логирование входящих запросов и ответов контроллеров
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logRestController(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] arguments = joinPoint.getArgs();

        log.info("Incoming request: {} {} to {}.{} with arguments: {}",
                request.getMethod(), request.getRequestURI(), className, methodName, Arrays.toString(arguments));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }

        log.info("Response from {}.{}: {}", className, methodName, result);

        return result;
    }

    // Логирование входов в методы сервисов
    @Around("within(@org.springframework.stereotype.Service *)")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] arguments = joinPoint.getArgs();

        log.info("Entering into service method {}.{} with arguments: {}", className, methodName, Arrays.toString(arguments));

        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Exception in {}.{}: {}", className, methodName, e.getMessage());
            throw e;
        }

        log.info("Method {}.{} returned: {}", className, methodName, result);

        return result;
    }
}
