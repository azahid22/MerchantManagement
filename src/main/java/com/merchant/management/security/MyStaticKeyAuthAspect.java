package com.merchant.management.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class MyStaticKeyAuthAspect {
    private static final Logger LOG = LoggerFactory.getLogger(MyStaticKeyAuthAspect.class);

    @Value("${merchant.management.static.key}")
    private String staticKey;
    @Autowired
    private HttpServletRequest request;

    @Pointcut("execution(* com.merchant.management.controller.MerchantController.*(..))")
    public void controllerMethods() {
    }


    @Around("controllerMethods()")
    public Object authenticateAndAuthorize(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = request.getHeader("Authorization");
        if (key == null || !key.equals(staticKey)) {
            LOG.warn("Invalid Static Key provided.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Static Key Provided");
        }

        return joinPoint.proceed();
    }
}
