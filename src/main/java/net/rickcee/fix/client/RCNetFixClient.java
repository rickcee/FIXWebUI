package net.rickcee.fix.client;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import lombok.extern.slf4j.Slf4j;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;
import quickfix.examples.executor.Executor;
import quickfix.field.AllocStatus;
import quickfix.field.TransactTime;
import quickfix.fix44.AllocationInstruction;
import quickfix.fix44.AllocationInstructionAck;

/**
 * @author rickcee
 *
 */
@Slf4j
public class RCNetFixClient extends quickfix.MessageCracker implements quickfix.Application {
    
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
		log.info("--------- fromApp ---------");
		try {
			crack(message, sessionId);
		} catch (Exception e) {
			log.error(" Error processing Msg: [" + message + "]: " + e.getMessage(), e);
		}
		
	}
	
	public void onMessage(AllocationInstruction ai, SessionID sessionId) throws FieldNotFound {
		log.info("onMessage(" + ai + ", " + sessionId + ")");
		AllocationInstructionAck aiAck = new AllocationInstructionAck(ai.getAllocID(), new TransactTime(), new AllocStatus(AllocStatus.ACCEPTED));
		try {
			Session.lookupSession(sessionId).send(aiAck);
		} catch (Exception e) {
			log.error(" Error sending Msg: [" + aiAck + "]: " + e.getMessage(), e);
		}
	}

    public static void main(String[] args) throws Exception {
        try {
            InputStream inputStream = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();
            
            System.out.println(settings);

            MessageFactory messageFactory = new DefaultMessageFactory();
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new ScreenLogFactory();

            RCNetFixClient application = new RCNetFixClient();
            SocketInitiator initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
            initiator.start();

            SessionID sessionId = initiator.getSessions().get(0);
            Session.lookupSession(sessionId).logon();
            
            System.out.println("press <enter> to quit");
            System.in.read();

            //application.stop();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static InputStream getSettingsInputStream(String[] args) throws FileNotFoundException {
        InputStream inputStream = null;
        if (args.length == 0) {
            inputStream = RCNetFixClient.class.getResourceAsStream("net/rickcee/fix/client/fix-rcnet-client1.cfg");
        } else if (args.length == 1) {
        	inputStream = RCNetFixClient.class.getResourceAsStream(args[0]);
        	if(inputStream == null) {
                inputStream = new FileInputStream(args[0]);
        	}
        }
        if (inputStream == null) {
            System.out.println("usage: " + Executor.class.getName() + " [configFile].");
            System.exit(1);
        }
        return inputStream;
    }

}
