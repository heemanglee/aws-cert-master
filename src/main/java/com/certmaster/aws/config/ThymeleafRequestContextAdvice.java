package com.certmaster.aws.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ThymeleafRequestContextAdvice {

    @ModelAttribute("requestUri")
    public String requestUri() {
        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attr == null) {
            return null;
        }
        
        HttpServletRequest request = attr.getRequest();
        return request.getRequestURI();
    }
} 