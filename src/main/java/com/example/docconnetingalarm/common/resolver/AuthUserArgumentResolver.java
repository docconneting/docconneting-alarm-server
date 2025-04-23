package com.example.docconnetingalarm.common.resolver;

import com.example.docconnetingalarm.common.exception.constant.ErrorCode;
import com.example.docconnetingalarm.common.exception.object.ServerException;
import com.example.docconnetingalarm.domain.auth.annotation.Auth;
import com.example.docconnetingalarm.domain.auth.entity.AuthUser;
import com.example.docconnetingalarm.domain.user.enums.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Slf4j
public class AuthUserArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAuthAnnotation = parameter.getParameterAnnotation(Auth.class) != null;
        boolean isAuthUserType = parameter.getParameterType().equals(AuthUser.class);

        // @Auth 어노테이션과 AuthUser 타입이 함께 사용되지 않은 경우 예외 발생
        if (hasAuthAnnotation != isAuthUserType) {
            throw new ServerException(ErrorCode.AUTH_WITHOUT_AUTHUSER);
        }

        return hasAuthAnnotation;
    }

    @Override
    public Object resolveArgument(
            @Nullable MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory
    ) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // JwtFilter 에서 set 한 userId와 userRole 값을 가져옴
        Long userId = (Long) request.getAttribute("userId");
        UserRole userRole = (UserRole) request.getAttribute("userRole");
        return AuthUser.of(userId,userRole);
    }
}
