package com.example.docconnetingalarm.common.interceptor;

import com.example.docconnetingalarm.common.config.JwtUtil;
import com.example.docconnetingalarm.common.exception.constant.ErrorCode;
import com.example.docconnetingalarm.common.exception.object.ClientException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;



    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand())) {

            String bearerToken = stompHeaderAccessor.getFirstNativeHeader("Authorization");

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                throw new ClientException(ErrorCode.JWT_TOKEN_REQUIRED);
            }

            String jwt = jwtUtil.substringToken(bearerToken);
            try {
                Claims claims = jwtUtil.extractClaims(jwt);
                if (claims == null) {
                    throw new ClientException(ErrorCode.INVALID_JWT_FORMAT);
                }

                Long userId = Long.parseLong(claims.getSubject());
                String role = claims.get("role", String.class);

                // 인증된 사용자 정보 세션에 등록
                // 웹소켓 연결에 대응되는 내부 저장소 Map을 의미한다
                stompHeaderAccessor.getSessionAttributes().put("userId", userId);

            } catch (SecurityException | MalformedJwtException e) {
                throw new ClientException(ErrorCode.INVALID_JWT_SIGNATURE);
            } catch (ExpiredJwtException e) {
                throw new ClientException(ErrorCode.EXPIRED_JWT_TOKEN);
            } catch (UnsupportedJwtException e) {
                throw new ClientException(ErrorCode.UNSUPPORTED_JWT_TOKEN);
            } catch (Exception e) {
                throw new ClientException(ErrorCode.INVALID_JWT_TOKEN);
            }
        }

        return message;
    }
}
