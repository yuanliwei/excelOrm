package com.ylw.excelorm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Sheet {
	// int value();
	int order() default 0;

	int wrongIgnoreNum() default 0;

	int firstRow() default 1;
}
