/**
 * 
 */
package net.rickcee.fix.server;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import quickfix.Application;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
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
public class RCNetFixServer extends quickfix.fix44.MessageCracker implements Application {
	
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
		try {
			// Customize session msgs...
			crack(message, sessionId);
		} catch (Exception e) {
			log.error(" Error processing Msg: [" + message + "]: " + e.getMessage(), e);
		}		
	}

	@Override
	public void toApp(Message message, SessionID sessionId) throws DoNotSend {
		log.info("--------- toApp ---------");
	}

	@Override
	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		log.info("--------- fromApp ---------");
		try {
			crack(message, sessionId);
		} catch (Exception e) {
			log.error(" Error processing Msg: [" + message + "]: " + e.getMessage(), e);
		}		
	}

	public void onMessage(quickfix.fix44.AllocationInstructionAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationInstructionAck: " + sessionId + " // " + ai.getAllocID());
	}

	public void onMessage(quickfix.fix50.AllocationInstructionAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationInstructionAck: " + sessionId + " // " + ai.getAllocID());
	}

	public void onMessage(quickfix.fix44.AllocationReportAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationReportAck: " + sessionId + " // " + ai.getAllocID());
	}

	public void onMessage(quickfix.fix50.AllocationReportAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationReportAck: " + sessionId + " // " + ai.getAllocID());
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
