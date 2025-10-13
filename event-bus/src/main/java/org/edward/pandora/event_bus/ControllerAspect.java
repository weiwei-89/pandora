package org.edward.pandora.event_bus;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.edward.pandora.common.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ControllerAspect {
    private static final Logger logger = LoggerFactory.getLogger(ControllerAspect.class);

    @Pointcut("execution(* org.edward.pandora.event_bus..*Controller.*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        logger.info("--> [http]{}:{}", method.getDeclaringClass().getName(), method.getName());
        try {
            Object result = joinPoint.proceed();
            logger.info("--> done");
            if(result instanceof Response) {
                return result;
            }
            return Response.ok().setData(result);
        } catch(Throwable e) {
            return Response.error(e.getMessage());
        }
    }
}