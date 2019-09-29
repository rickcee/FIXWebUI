/**
 * 
 */
package net.rickcee.fix.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.LogFactory;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.SessionSettings;
import quickfix.ThreadedSocketAcceptor;

@Configuration
@Slf4j
public class FixConfig {

	private final String fileName = "fix-session.cfg";
	@Autowired
	private RCNetFixServer application;

	@Bean
	public ThreadedSocketAcceptor threadedSocketAcceptor() {
		ThreadedSocketAcceptor threadedSocketAcceptor = null;

		try {
			SessionSettings settings = new SessionSettings(FixConfig.class.getClassLoader().getResourceAsStream(fileName));
			MessageStoreFactory storeFactory = new FileStoreFactory(settings);
			LogFactory logFactory = new FileLogFactory(settings);
			MessageFactory messageFactory = new DefaultMessageFactory();
			threadedSocketAcceptor = new ThreadedSocketAcceptor(application, storeFactory, settings, logFactory,
					messageFactory);
			
			//threadedSocketAcceptor.start();
			
		} catch (ConfigError configError) {
			log.error(configError.getMessage(), configError);
		}

		return threadedSocketAcceptor;
	}
}
