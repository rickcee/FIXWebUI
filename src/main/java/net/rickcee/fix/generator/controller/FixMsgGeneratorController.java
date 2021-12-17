package net.rickcee.fix.generator.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.rickcee.fix.client.RCNetFixClient;
import net.rickcee.fix.server.RCNetFixServer;
import net.rickcee.fix.util.FIX44;
import net.rickcee.fix.util.FIX50;
import net.rickcee.fix.util.Fix44Cracker;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.ThreadedSocketAcceptor;
import quickfix.field.AllocAccount;
import quickfix.field.AllocQty;
import quickfix.field.AvgPx;
import quickfix.field.ConfirmID;
import quickfix.field.ConfirmStatus;
import quickfix.field.ConfirmTransType;
import quickfix.field.GrossTradeAmt;
import quickfix.field.NetMoney;
import quickfix.field.NoCapacities;
import quickfix.field.NoLegs;
import quickfix.field.NoUnderlyings;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.Side;
import quickfix.field.TargetCompID;
import quickfix.field.TradeDate;
import quickfix.field.TransactTime;
import quickfix.fix44.Confirmation;

@RestController
//@RequestMapping("/secured/")
@Slf4j
public class FixMsgGeneratorController {
	@Autowired
	private FIX44 fix44;
	@Autowired
	private Fix44Cracker f44cracker;
	@Autowired
	private FIX50 fix50;
	@Autowired
	private Environment env;

	@Autowired
	private ThreadedSocketAcceptor threadedSocketAcceptor;
	@Autowired
	private RCNetFixServer fixServer;
	@Autowired
	private RCNetFixClient fixClient;
	
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
	
	@RequestMapping(method = RequestMethod.GET, path = "/support/fix/confirm/send", produces = { MediaType.APPLICATION_JSON })
	public Object sendConfirm() {
		HashMap<String, String> result = new HashMap<>();
		try {
			fixClient.sendConfirmation();
			result.put("result", "OK");
		} catch (Exception e) {
			result.put("result", "ERROR");
			result.put("error", e.getMessage());
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	@RequestMapping(method = RequestMethod.GET, path = "/support/fix/confirm", produces = { MediaType.TEXT_PLAIN })
	public String createConfirm() {
		Confirmation conf = new Confirmation();
		conf.getHeader().setField(new SenderCompID("RCBROKER"));
		conf.getHeader().setField(new TargetCompID("RC-E-TRADING"));
		conf.getHeader().setField(new SendingTime(LocalDateTime.now()));
		conf.set(new ConfirmID("CID-000001"));
		conf.set(new ConfirmTransType(ConfirmTransType.NEW));
		conf.set(new ConfirmStatus(ConfirmStatus.RECEIVED));
		conf.set(new TransactTime(LocalDateTime.now()));
		conf.set(new TradeDate("20200723"));
		conf.set(new NoUnderlyings(0));
		conf.set(new NoLegs(0));
		conf.set(new AllocQty(150000));
		conf.set(new Side(Side.BUY));
		conf.set(new NoCapacities(0));
		conf.set(new AvgPx(99.863));
		conf.set(new AllocAccount("ALLOC-ACCT-1"));
		conf.set(new GrossTradeAmt(149794.5));
		conf.set(new NetMoney(149794.5));
		log.info(conf.toString());
		return conf.toString();
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/support/fix/process", produces = { "application/json" })
	public Object processCustomFix(@RequestBody String fixMsg) {
		HashMap<String, String> result = new HashMap<>();
		try {
			quickfix.fix44.Message msg = new quickfix.fix44.Message();
			msg.fromString(fixMsg, null, false);
			//SwingUtilities.invokeLater(appCtx.getBean(FixMessageProcessor.class, msg, true));
			SessionID s = null;
			for(SessionID sess : threadedSocketAcceptor.getSessions()) {
				if(sess.toString().startsWith("FIX.4.4")) {
					s = sess;
					break;
				}
			}
			f44cracker.crack(msg, s);
			result.put("result", "OK");
		} catch(Exception e) {
			result.put("result", "ERROR");
			result.put("error", e.getMessage());
			log.error(e.getMessage(), e);
		}
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
	
}