package net.rickcee.fix.generator.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.rickcee.fix.generator.model.FIX44;
import net.rickcee.fix.generator.model.FIX50;
import net.rickcee.fix.generator.model.FixAllocationMsgModel;
import net.rickcee.fix.server.RCNetFixServer;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.ThreadedSocketAcceptor;

@RestController
//@RequestMapping("/secured/")
@Slf4j
public class FixMsgGeneratorController {
	@Autowired
	private FIX44 fix44;
	@Autowired
	private FIX50 fix50;
	@Autowired
	private Environment env;

	@Autowired
	private ThreadedSocketAcceptor threadedSocketAcceptor;
	@Autowired
	private RCNetFixServer fixServer;
	
	@RequestMapping(method = RequestMethod.GET, path = "/HealthCheck", produces = { "application/json" })
	public Object healthCheck() {
		HashMap<String, String> result = new HashMap<>();
		result.put("result", "OK");
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/secured/service", produces = { "application/json" })
	public Object service1() {
		HashMap<String, String> result = new HashMap<>();
		result.put("secured", "true");
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/public/defaults", produces = { "application/json" })
	public Object servicePublic1() {
		HashMap<String, String> result = new HashMap<>();
		//result.put("senderCompId", env.getProperty("fix.defaultSenderCompId"));
		//result.put("targetCompId", env.getProperty("fix.defaultTargetCompId"));
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/public/fix/sessions", produces = { "application/json" })
	public Object activeFixSessions() {
		List<HashMap<String, Object>> sessions = new ArrayList<>();

		threadedSocketAcceptor.getSessions().forEach(sessionObj -> {
			HashMap<String, Object> session = new HashMap<>();
			session.put("name", sessionObj.toString());
			session.put("value", sessionObj);
			sessions.add(session);
		});

		return sessions;
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/public/fix/allocation/send", produces = { "application/json" })
	public Object generateFixMessage(@RequestBody FixAllocationMsgModel model) {
		HashMap<String, String> result = new HashMap<>();
		
		String session = model.getSessionId();
		log.info("FIX Session ID: " + session);
		SessionID sessionId = null;
		for(SessionID s : threadedSocketAcceptor.getSessions()) {
			if(session.equals(s.toString())) {
				sessionId = s;
			}
		}
		
		Message msg = null;
		if(session.startsWith("FIX.4.4")) {
			msg = fix44.generateAllocationInstruction(model);
		} else if(session.startsWith("FIXT.1.1")) {
			msg = fix50.generateAllocationInstruction(model);
		} else {
			// ??
		}
		
		if(model.getSendToClient()) {
			fixServer.sendMessage(msg, sessionId);
		}
		
		// This MUST be after sendMessage so we ensure common fields are added by quickfixj.
		result.put("result", msg.toString());
		
		return result;
	}
	
//	@RequestMapping(method = RequestMethod.POST, path = "/public/service/generate", produces = { "application/json" })
//	public Object generateFixMessage(@RequestBody FixMsgModel model) {
//		HashMap<String, String> result = new HashMap<>();
//		
//		String fixVersion = model.getFixVersion();
//		log.info("FIX Version: " + fixVersion);
//		SessionID session = null;
//		for(SessionID s : threadedSocketInitiator.getSessions()) {
//			if("FIX44".equals(fixVersion) && s.getBeginString().equals("FIX.4.4")) {
//				session = s;
//			} else if("FIXT1.1".equals(fixVersion) && s.getBeginString().equals("FIXT.1.1")) {
//				session = s;
//			}
//		}
//		
//		if("FIX44".equals(fixVersion)) {
//			result.put("result", fix44.generateAllocationInstruction(model).toString());
//			try {
//				fixClient.toApp(fix44.generateAllocationInstruction(model), session);
//			} catch (DoNotSend e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} else if("FIXT1.1".equals(fixVersion)) {
//			result.put("result", fix50.generateAllocationInstruction(model).toString());
//			
//			//threadedSocketInitiator.getSessions().get(0).
//			// FIX.4.4
//			// FIXT.1.1
//			try {
//				fixClient.toApp(fix50.generateAllocationInstruction(model), session);
//			} catch (DoNotSend e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return result;
//	}
	
//	@RequestMapping(method = RequestMethod.GET, path = "/public/alloc", produces = { "text/plain" })
//	public Object servicePublic2() {
//		quickfix.fix44.AllocationInstruction ai = new AllocationInstruction();
//		ai.set(new Quantity(1000));
//		ai.set(new Side(Side.BUY));
//		ai.setField(new SenderCompID("BLP"));
//		ai.setField(new TargetCompID("RBSG"));
//		ai.setField(new MsgSeqNum(1));
//		ai.setField(new SendingTime(LocalDateTime.now()));
//		ai.setField(new AvgPx(105.12109375));
//		ai.set(new Currency("USD"));
//		ai.setField(new SecurityIDSource(SecurityIDSource.CUSIP));
//		ai.setField(new SecurityID("9128285P1"));
//		ai.setField(new AllocNoOrdersType(AllocNoOrdersType.EXPLICIT_LIST_PROVIDED));
//		ai.setField(new TotNoAllocs(2));
//		
//		Group g = new Group(NoAllocs.FIELD, AllocAccount.FIELD);
//		g.setField(new AllocAccount("EXT-ACCT-1"));
//		g.setField(new AllocQty(500));
//		g.setField(new AllocNetMoney(550));
//		g.setField(new AllocAccruedInterestAmt(50));
//		g.setField(new AllocSettlCurrency("USD"));
//		ai.addGroup(g);
//		
//		g.setField(new AllocAccount("EXT-ACCT-2"));
//		g.setField(new AllocQty(500));
//		g.setField(new AllocNetMoney(550));
//		g.setField(new AllocAccruedInterestAmt(50));
//		g.setField(new AllocSettlCurrency("CAD"));
//		ai.addGroup(g);
//				
//		return ai.toString();
//	}
	
}