package io.springstudent.meeting.common.signal;

import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author zhouning
 * @date 2023/04/19 8:56
 */
@Component
public class SignalServerStarter implements CommandLineRunner {

    @Resource
    private SocketIOServer socketIOServer;

    @Override
    public void run(String... args) throws Exception {
        socketIOServer.start();
    }
}
