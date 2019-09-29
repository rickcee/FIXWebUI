/**
 * 
 */
package net.rickcee.fix.server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import quickfix.ConfigError;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.ThreadedSocketAcceptor;

/**
 * @author rickcee
 *
 */
@Component
@Slf4j
public class AppLogonListener implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private ThreadedSocketAcceptor threadedSocketAcceptor;

	@Autowired
	private RCNetFixServer application;

	private boolean initiatorStarted = false;

	@Override
	public void onApplicationEvent(ContextRefreshedEvent refreshedEvent) {
		startFixInitiator();
	}

	private void startFixInitiator() {
		if (!initiatorStarted) {
			try {
				threadedSocketAcceptor.start();
				log.info("--------- ThreadedSocketInitiator started ---------");
				initiatorStarted = true;
			} catch (ConfigError configError) {
				configError.printStackTrace();
				log.error("--------- ThreadedSocketInitiator ran into an error ---------");
			}
		} else {
			logon();
		}
	}

	private void logon() {
		if (threadedSocketAcceptor.getSessions() != null && threadedSocketAcceptor.getSessions().size() > 0) {
			for (SessionID sessionID : threadedSocketAcceptor.getSessions()) {
				log.info("SessionID: " + sessionID.toString() + " - Calling logon()...");
				Session.lookupSession(sessionID).logon();
			}
		}
	}
	
	@PreDestroy
	private void destroy() {
		threadedSocketAcceptor.stop();
	}

	@Scheduled(fixedRate = 5000)
	public void clientStatus() {
		log.info("Client Status | Logged on: {}. Current Time: {}", threadedSocketAcceptor.isLoggedOn(),
				LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
	}
}
