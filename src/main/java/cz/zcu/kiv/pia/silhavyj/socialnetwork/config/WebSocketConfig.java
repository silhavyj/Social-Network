package cz.zcu.kiv.pia.silhavyj.socialnetwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/***
 * Configuration of web socket message broker. Withing this project, web sockets are
 * used when sending chat messages between two friends (users) as well as notifying
 * users when their friends get online/offline.
 *
 * @author Jakub Silhavy (A21N0072P)
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /***
     * Configures the message broker (destination prefixes, ...)
     * @param registry an instance of MessageBrokerRegistry through which we set up the message broker
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry
            .setApplicationDestinationPrefixes("/app")
            .enableSimpleBroker("/topic");
    }

    /***
     * Configures stomp end points which clients will use to communicate with the server.
     * @param registry an instance of StompEndpointRegistry through which we set up the stomp end points
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws")
            .setAllowedOriginPatterns("*")
            // .setAllowedOrigins("http://10.10.2.103:8085")
            .withSockJS();
    }
}
