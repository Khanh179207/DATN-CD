package poly.edu.config;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ExecutorChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;

// Import Trưởng phòng An ninh của sếp
import poly.edu.util.JwtUtils;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/user");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
    }

    // 🚀 TRẠM KIỂM SOÁT VÉ XUYÊN LUỒNG
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 🔥 NÂNG CẤP LÊN ExecutorChannelInterceptor
        registration.interceptors(new ExecutorChannelInterceptor() {

            // Bước 1: Chạy ở Luồng Số 1 (Đón tin nhắn) - Lấy Token ra kiểm tra
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())
                        || StompCommand.SEND.equals(accessor.getCommand())) {
                    List<String> authorization = accessor.getNativeHeader("Authorization");
                    if ((authorization == null || authorization.isEmpty())) {
                        // Some clients may send lowercase header names
                        authorization = accessor.getNativeHeader("authorization");
                    }

                    if (authorization != null && !authorization.isEmpty()
                            && authorization.get(0).startsWith("Bearer ")) {
                        String jwt = authorization.get(0).substring(7);

                        try {
                            if (jwtUtils.validateJwtToken(jwt)) {
                                String email = jwtUtils.getEmailFromJwtToken(jwt);

                                if (email != null) {
                                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                            userDetails, null, userDetails.getAuthorities());

                                    // Cấp thẻ cho cái tin nhắn này (STOMP Header)
                                    accessor.setUser(auth);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Lỗi Token: " + e.getMessage());
                        }
                    }
                }
                return message;
            }

            // 🔥 Bước 2: Chạy ở Luồng Số 2 (Luồng thực thi Controller) - Nhét vé vào túi áo
            @Override
            public Message<?> beforeHandle(Message<?> message, MessageChannel channel, MessageHandler handler) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                // Bây giờ nó đã nằm trên cùng luồng với @PreAuthorize, nhét vào đây là ông bảo
                // vệ thấy ngay!
                if (accessor != null && accessor.getUser() != null) {
                    SecurityContextHolder.getContext().setAuthentication((Authentication) accessor.getUser());
                }
                return message;
            }

            // Bước 3: Dọn dẹp túi áo khi Controller chạy xong (Bảo mật)
            @Override
            public void afterMessageHandled(Message<?> message, MessageChannel channel, MessageHandler handler,
                    Exception ex) {
                SecurityContextHolder.clearContext();
            }
        });
    }
}