import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import lombok.extern.slf4j.Slf4j;
import quickfix.DefaultDataDictionaryProvider;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.InvalidMessage;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.ScreenLogFactory;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
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
public class Acceptor extends quickfix.MessageCracker implements quickfix.Application {
	String fix="8=FIX.4.49=29335=J6=105.1210937522=134=148=9999952=20190928-19:05:27.55853=100054=155=[N/A]70=3ab669c9-2327-4a21-8222-a08c669ca75771=075=20190927115=912828CR5626=1857=1892=278=279=ACCT-180=500154=500050467=1736=USD742=50782=DTC784=1079=ACCT-280=500154=500050467=2736=CAD742=5010=243";
    
	@Override
	public void onCreate(SessionID sessionId) {
		log.info("--------- onCreate ---------");
	}

	@Override
	public void onLogon(SessionID sessionId) {
		log.info("--------- onLogon ---------");
 
        try {
			AllocationInstruction ai = new AllocationInstruction();
			ai.fromString(fix, new DefaultDataDictionaryProvider().getSessionDataDictionary("FIX44"), false);
			Session.lookupSession(sessionId).send(ai);
		} catch (InvalidMessage e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		//Session.lookupSession(sessionId).send(message);
		
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
	
	public void onMessage(AllocationInstructionAck ai, SessionID sessionId) throws FieldNotFound {
		log.info("AllocationInstructionAck: " + sessionId + " // " + ai.getAllocID());
	}
	
//    private void stop() {
//        try {
//            jmxExporter.getMBeanServer().unregisterMBean(connectorObjectName);
//        } catch (Exception e) {
//            log.error("Failed to unregister acceptor from JMX", e);
//        }
//        acceptor.stop();
//    }

    public static void main(String[] args) throws Exception {
        try {
            InputStream inputStream = getSettingsInputStream(args);
            SessionSettings settings = new SessionSettings(inputStream);
            inputStream.close();
            
            System.out.println(settings);

            MessageFactory messageFactory = new DefaultMessageFactory();
            MessageStoreFactory messageStoreFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new ScreenLogFactory();

            Acceptor application = new Acceptor();
			SocketAcceptor initiator = new SocketAcceptor(application, messageStoreFactory, settings, logFactory, messageFactory);
            initiator.start();

            CountDownLatch latch = new CountDownLatch(1);
            latch.await();

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
            inputStream = Acceptor.class.getResourceAsStream("acceptor.cfg");
        } else if (args.length == 1) {
            inputStream = new FileInputStream(args[0]);
        }
        if (inputStream == null) {
            System.out.println("usage: " + Executor.class.getName() + " [configFile].");
            System.exit(1);
        }
        return inputStream;
    }
}
