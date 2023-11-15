package io.springstudent.meeting.common.signal;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import com.corundumstudio.socketio.annotation.SpringAnnotationScanner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouning
 * @date 2023/04/18 13:37
 */
@Configuration
public class SignalServerConfig {
    @Value("${socketio.port}")
    private Integer socketioPort;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setPort(socketioPort);
        config.setMaxFramePayloadLength(1024 * 1024);
        config.setMaxHttpContentLength(1024 * 1024);
        config.setAllowCustomRequests(true);
        config.setWorkerThreads(100);
        config.setTransports(Transport.POLLING, Transport.WEBSOCKET);
        config.setAuthorizationListener(data -> {
            data.getHttpHeaders().get("authroization");
            return true;
        });
        SocketIOServer server = new SocketIOServer(config);
        return server;
    }

    @Bean
    public SpringAnnotationScanner springAnnotationScanner() {
        return new SpringAnnotationScanner(socketIOServer());
    }
}
