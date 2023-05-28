package com.concurrency.seckill.validator;

import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {

    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.required();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (!required && StringUtils.isEmpty(s)) return true;
        return isMobile(s);
    }

    private static final Pattern mobile_pattern = Pattern.compile("[1]([3-9])[0-9]{9}$"); // 正则表达式校验

    private static boolean isMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) return false;
        return mobile_pattern.matcher(mobile).matches();
    }
}
