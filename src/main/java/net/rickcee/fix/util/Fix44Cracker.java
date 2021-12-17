/**
 * 
 */
package net.rickcee.fix.util;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import quickfix.FieldNotFound;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.field.AffirmStatus;
import quickfix.fix44.Confirmation;
import quickfix.fix44.ConfirmationAck;
import quickfix.fix44.MessageCracker;

/**
 * @author rickcee
 *
 */
@Slf4j
@Component
public class Fix44Cracker extends MessageCracker {

	@Override
	public void onMessage(quickfix.fix44.AllocationInstructionAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationInstructionAck: " + sessionId + " // " + ai.getAllocID());
	}

	@Override
	public void onMessage(quickfix.fix44.AllocationReportAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationReportAck: " + sessionId + " // " + ai.getAllocID());
	}
	
	@Override
	public void onMessage(Confirmation cf, SessionID sessionId) throws FieldNotFound {
		log.info("onMessage(" + cf + ", " + sessionId + ")");
		//AllocationInstructionAck aiAck = new AllocationInstructionAck(ai.getAllocID(), new TransactTime(), new AllocStatus(AllocStatus.ACCEPTED));
		ConfirmationAck ack = new ConfirmationAck(cf.getConfirmID(), cf.getTradeDate(), cf.getTransactTime(),
				new AffirmStatus(AffirmStatus.RECEIVED));
		try {
			Session.lookupSession(sessionId).send(ack);
		} catch (Exception e) {
			log.error(" Error sending Msg: [" + ack + "]: " + e.getMessage(), e);
		}

		ack = new ConfirmationAck(cf.getConfirmID(), cf.getTradeDate(), cf.getTransactTime(),
				new AffirmStatus(AffirmStatus.AFFIRMED));
		try {
			Session.lookupSession(sessionId).send(ack);
		} catch (Exception e) {
			log.error(" Error sending Msg: [" + ack + "]: " + e.getMessage(), e);
		}

	}
}
