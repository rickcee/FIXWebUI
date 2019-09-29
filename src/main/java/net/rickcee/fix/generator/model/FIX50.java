/**
 * 
 */
package net.rickcee.fix.generator.model;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import quickfix.Group;
import quickfix.field.AllocAccount;
import quickfix.field.AllocAccruedInterestAmt;
import quickfix.field.AllocNetMoney;
import quickfix.field.AllocNoOrdersType;
import quickfix.field.AllocQty;
import quickfix.field.AllocSettlCurrency;
import quickfix.field.AvgPx;
import quickfix.field.Currency;
import quickfix.field.IndividualAllocID;
import quickfix.field.MsgSeqNum;
import quickfix.field.NoAllocs;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.Quantity;
import quickfix.field.SecurityID;
import quickfix.field.SecurityIDSource;
import quickfix.field.SenderCompID;
import quickfix.field.SendingTime;
import quickfix.field.SettlPartyID;
import quickfix.field.SettlPartyRole;
import quickfix.field.Side;
import quickfix.field.TargetCompID;
import quickfix.field.TotNoAllocs;
import quickfix.fix50.AllocationReport;

/**
 * @author rickcee
 *
 */
@Component
public class FIX50 {

	public synchronized AllocationReport generateAllocationInstruction(FixAllocationInstructionMsgModel model) {
		AllocationReport ai = new AllocationReport();
		long qty = model.getAllocs().stream().mapToLong(q -> q.getQuantity()).sum();
		
		ai.set(new Quantity(qty));
		ai.set(new Side(model.getBuySell().charAt(0)));
		//ai.setField(new SenderCompID(model.getSenderCompId()));
		//ai.setField(new TargetCompID(model.getTargetCompId()));
		ai.setField(new OnBehalfOfCompID(model.getOnBehalfOfCompId()));
		ai.setField(new MsgSeqNum(1));
		ai.setField(new SendingTime(LocalDateTime.now()));
		//ai.setField(new AvgPx(105.12109375));
		//ai.set(new Currency("USD"));
		ai.setField(new SecurityIDSource(model.getSecuritySource()));
		ai.setField(new SecurityID(model.getSecurityId()));
		ai.setField(new AllocNoOrdersType(AllocNoOrdersType.EXPLICIT_LIST_PROVIDED));
		ai.setField(new TotNoAllocs(model.getAllocs().size()));

		model.getAllocs().forEach(alloc -> {
			Group g = new Group(NoAllocs.FIELD, AllocAccount.FIELD);
			g.setField(new IndividualAllocID(alloc.getId()));
			g.setField(new AllocAccount(alloc.getAccount()));
			g.setField(new AllocQty(alloc.getQuantity()));
			g.setField(new AllocNetMoney(alloc.getNetMoney()));
			g.setField(new AllocAccruedInterestAmt(alloc.getAccruedInterest()));
			g.setField(new AllocSettlCurrency(alloc.getSettlementCurrency()));
			if (alloc.getSettlementLocation() != null) {
				g.setField(new SettlPartyRole(10));
				g.setField(new SettlPartyID(alloc.getSettlementLocation()));
			}
			ai.addGroup(g);
		});

		return ai;
	}
}
