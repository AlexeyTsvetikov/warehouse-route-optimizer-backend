package ru.tsvetikov.warehouse.router.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect {

    @Around("@annotation(org.springframework.transaction.annotation.Transactional)")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        long startTime = System.currentTimeMillis();
        SqlQueryCounter.start();

        try {
            Object result = joinPoint.proceed();

            long duration = System.currentTimeMillis() - startTime;
            int totalQueries = SqlQueryCounter.getTotalCount();
            int maxDuplicates = SqlQueryCounter.getMaxDuplicateCount();

            if (maxDuplicates > 1) {
                log.warn("[N+1 DETECTED] [{}] Method {} completed in {} ms | SQL total: {} (identical query repeated {} times!)",
                        className, methodName, duration, totalQueries, maxDuplicates);
            } else {
                log.info("[{}] Method {} completed in {} ms | SQL: {}",
                        className, methodName, duration, totalQueries);
            }

            return result;

        } catch (Exception e) {
            log.error("[{}] Method {} failed: {}", className, methodName, e.getMessage());
            throw e;
        } finally {
            SqlQueryCounter.clear();
        }
    }
}
