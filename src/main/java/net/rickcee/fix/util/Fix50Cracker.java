/**
 * 
 */
package net.rickcee.fix.util;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import quickfix.FieldNotFound;
import quickfix.SessionID;
import quickfix.fix50.MessageCracker;

/**
 * @author rickcee
 *
 */
@Slf4j
@Component
public class Fix50Cracker extends MessageCracker {

	@Override
	public void onMessage(quickfix.fix50.AllocationInstructionAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationInstructionAck: " + sessionId + " // " + ai.getAllocID());
	}

	@Override
	public void onMessage(quickfix.fix50.AllocationReportAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationReportAck: " + sessionId + " // " + ai.getAllocID());
	}

}
