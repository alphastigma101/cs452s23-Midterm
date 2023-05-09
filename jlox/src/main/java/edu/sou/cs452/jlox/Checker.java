package edu.sou.cs452.jlox;
import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE, ElementType.METHOD})
public @interface Checker {
    Class<? extends StaticBoundProcessor> value() default StaticBoundProcessor.ListBoundsChecker.class;
}
