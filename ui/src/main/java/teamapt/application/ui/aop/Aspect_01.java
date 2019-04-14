package teamapt.application.ui.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class Aspect_01 {


    // Any method call that re
    @Before("execution(teamapt.application.ui.aop.* String(String))")
    public void beforeEveryMethodCall(){
        System.out.println("Before any method call");
    }
}
