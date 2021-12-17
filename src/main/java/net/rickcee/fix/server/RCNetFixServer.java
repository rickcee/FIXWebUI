/**
 * 
 */
package net.rickcee.fix.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import net.rickcee.fix.util.Fix44Cracker;
import net.rickcee.fix.util.Fix50Cracker;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.fix44.Heartbeat;
import quickfix.fix44.Logon;
import quickfix.fix44.Reject;

/**
 * @author rickcee
 *
 */
@Slf4j
@Component
public class RCNetFixServer implements Application {
	@Autowired
	private Fix44Cracker msgCracker44;
	@Autowired
	private Fix50Cracker msgCracker50;
	
	@Override
	public void onCreate(SessionID sessionId) {
		log.info("--------- onCreate ---------");
	}

	@Override
	public void onLogon(SessionID sessionId) {
		log.info("--------- onLogon ---------");
	}

	@Override
	public void onLogout(SessionID sessionId) {
		log.info("--------- onLogout ---------");
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		log.info("--------- toAdmin ---------");
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		log.info("--------- fromAdmin ---------");
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		log.info("--------- toApp ---------");
	}

	@Override
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		log.info(this.getClass().getSimpleName() + " - fromApp: [" + message.toRawString() + "]");
		try {
			if (sessionId.getBeginString().startsWith("FIX.4.4")) {
				msgCracker44.crack(message, sessionId);
			} else if (sessionId.getBeginString().startsWith("FIX.5.0")) {
				msgCracker50.crack(message, sessionId);
			}
		} catch (Exception e) {
			log.error(" Error processing Msg: [" + message + "]: " + e.getMessage(), e);
		}		
	}
	
	public void onMessage(Reject ai, SessionID sessionId) throws FieldNotFound {
		log.info("Reject: " + sessionId + " // " + ai);
	}

	public void onMessage(Logon ai, SessionID sessionId) throws FieldNotFound {
		log.info("Logon: " + sessionId + " // " + ai);
	}

	public void onMessage(Heartbeat ai, SessionID sessionId) throws FieldNotFound {
		log.info("Heartbeat: " + sessionId + " // " + ai);
	}
	
	public void sendMessage(Message msg, SessionID sessionId) {
		Session.lookupSession(sessionId).send(msg);
	}
}
