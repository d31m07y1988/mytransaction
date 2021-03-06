package itpark.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
public class LoggingAspect {
    private Logger logger = Logger.getLogger(getClass().getName());

    @Before("execution(void itpark..transfer(*,*,*))")
    public void logCustomeTransfer(JoinPoint joinPoint) {
        logger.info("transfer called");
        logger.info(joinPoint.getTarget().getClass().getName());
    }

    @Before("execution(* itpark.service.*Product*.find*(*))")
    public void logAllProducts(JoinPoint joinPoint) {
        logger.info("product find products called");
        logger.info(joinPoint.getTarget().getClass().getName());
    }

    @Before("execution(* itpark.service.*Product*.get*(*))")
    public void logGetProducts(JoinPoint joinPoint) {
        logger.info("product get product called");
        logger.info(joinPoint.getTarget().getClass().getName());
    }

    @Before("execution(void itpark.service.*Product*.buy(*,*,*))")
    public void logBuyProducts(JoinPoint joinPoint) {
        logger.info("product buy called");
        logger.info(joinPoint.getTarget().getClass().getName());
    }

}
