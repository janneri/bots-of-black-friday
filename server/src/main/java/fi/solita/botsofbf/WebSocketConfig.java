package fi.solita.botsofbf;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableSimpleBroker("/topic");
		config.setApplicationDestinationPrefixes("/app");
	}

	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/hello")
				// Azure will respond with 403, because of CORS without this.
				.setAllowedOriginPatterns(
						"http://localhost",
						"http://localhost:5173",
						"http://127.0.0.1:5173",
						"https://bots-of-black-friday.azurewebsites.net",
						"https://bots-of-black-friday-helsinki.azurewebsites.net",
						"https://bots-of-black-friday-oulu.azurewebsites.net",
						"https://bots-of-black-friday-turku.azurewebsites.net",
						"https://bots-of-black-friday-tampere.azurewebsites.net"
				);
	}

}
