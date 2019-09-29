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
 * 
 */

/**
 * @author rickcee
 *
 */
@Slf4j
public class Initiator extends quickfix.MessageCracker implements quickfix.Application {
	//public static String fix="8=FIX.4.49=22035=J22=134=148=9999949=RCNET52=20190928-00:01:33.49753=100054=156=DUMMY115=uuuuu857=1892=278=279=ACCT-180=500154=500050467=1736=USD742=50782=DTC784=1079=ACCT-280=500154=500050467=2736=CAD742=5010=254";
    
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void toAdmin(Message message, SessionID sessionId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fromAdmin(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		// TODO Auto-generated method stub
		
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
			e.printStackTrace();
		}
		
	}
	
 //   public void onMessage(quickfix.fix40.NewOrderSingle order, SessionID sessionID) throws FieldNotFound,
	public void onMessage(AllocationInstruction ai, SessionID sessionId) throws FieldNotFound {
		log.info("onMessage(" + ai + ", " + sessionId + ")");
		AllocationInstructionAck aiAck = new AllocationInstructionAck(ai.getAllocID(), new TransactTime(), new AllocStatus(AllocStatus.ACCEPTED));
		try {
			Session.lookupSession(sessionId).send(aiAck);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

            Initiator application = new Initiator();
            SocketInitiator initiator = new SocketInitiator(application, messageStoreFactory, settings, logFactory, messageFactory);
            initiator.start();

            SessionID sessionId = initiator.getSessions().get(0);
            Session.lookupSession(sessionId).logon();
            
            //AllocationInstruction ai = new AllocationInstruction();
            //ai.fromString(fix, new DefaultDataDictionaryProvider().getSessionDataDictionary("FIX44"), false);
            //Session.lookupSession(sessionId).send(ai);

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
            inputStream = Acceptor.class.getResourceAsStream("initiator.cfg");
        } else if (args.length == 1) {
        	inputStream = Acceptor.class.getResourceAsStream(args[0]);
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
