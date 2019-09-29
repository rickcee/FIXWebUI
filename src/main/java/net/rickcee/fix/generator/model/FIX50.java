/**
 * 
 */
package net.rickcee.fix.generator.model;

import java.util.UUID;

import org.springframework.stereotype.Component;

import quickfix.field.AllocAccount;
import quickfix.field.AllocAccruedInterestAmt;
import quickfix.field.AllocID;
import quickfix.field.AllocNetMoney;
import quickfix.field.AllocNoOrdersType;
import quickfix.field.AllocQty;
import quickfix.field.AllocSettlCurrency;
import quickfix.field.AllocTransType;
import quickfix.field.AllocType;
import quickfix.field.AvgPx;
import quickfix.field.DlvyInstType;
import quickfix.field.IndividualAllocID;
import quickfix.field.NestedPartyID;
import quickfix.field.NestedPartyIDSource;
import quickfix.field.OnBehalfOfCompID;
import quickfix.field.PartyIDSource;
import quickfix.field.PartyRole;
import quickfix.field.Quantity;
import quickfix.field.SecurityID;
import quickfix.field.SecurityIDSource;
import quickfix.field.SettlDeliveryType;
import quickfix.field.SettlInstSource;
import quickfix.field.SettlPartyID;
import quickfix.field.SettlPartyIDSource;
import quickfix.field.SettlPartyRole;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.TradeDate;
import quickfix.fix50.AllocationReport;
import quickfix.fix50.component.NestedParties;
import quickfix.fix50.component.SettlInstructionsData;
import quickfix.fix50.component.SettlParties;

/**
 * @author rickcee
 *
 */
@Component
public class FIX50 {

	public synchronized AllocationReport generateAllocationInstruction(FixAllocationMsgModel model) {
		AllocationReport ai = new AllocationReport();
		
		ai.getHeader().setField(new OnBehalfOfCompID(model.getOnBehalfOfCompId()));

		/* Mandatory Fields */
		// Tag 70
		ai.setField(new AllocID(UUID.randomUUID().toString()));
		// Tag 71
		ai.setField(new AllocTransType(AllocTransType.NEW));
		// Tag 626
		ai.setField(new AllocType(AllocType.CALCULATED));
		// Tag 6
		ai.setField(new AvgPx(model.getAvgPrice() != null ? model.getAvgPrice() : 99.98));
		// Tag 54
		ai.setField(new Side(model.getBuySell().charAt(0)));
		// Tag 52
		long qty = model.getAllocs().stream().mapToLong(q -> q.getQuantity()).sum();
		ai.setField(new Quantity(qty));
		// Tag 857
		ai.setField(new AllocNoOrdersType(AllocNoOrdersType.EXPLICIT_LIST_PROVIDED));
		// Tag 75
		ai.setField(new TradeDate(model.getTradeDate()));
		// Tag 55
		ai.setField(new Symbol("[N/A]"));
		
		ai.setField(new SecurityIDSource(model.getSecuritySource()));
		ai.setField(new SecurityID(model.getSecurityId()));
		
		model.getAllocs().forEach(alloc -> {
			AllocationReport.NoAllocs g = new AllocationReport.NoAllocs();
			g.setField(new AllocAccount(alloc.getAccount()));
			g.setField(new AllocQty(alloc.getQuantity()));
			g.setField(new IndividualAllocID(alloc.getId()));
			
			g.setField(new AllocSettlCurrency(alloc.getSettlementCurrency()));
			g.setField(new AllocAccruedInterestAmt(alloc.getAccruedInterest()));
			g.setField(new AllocNetMoney(alloc.getNetMoney()));
			
			// For Account aliases
			NestedParties np = new NestedParties();
			NestedParties.NoNestedPartyIDs party = new NestedParties.NoNestedPartyIDs();
			party.set(new NestedPartyIDSource(PartyIDSource.PROPRIETARY_CUSTOM_CODE));
			party.set(new NestedPartyID("RCNET-CLIENT-14"));
			// Add Party
			np.addGroup(party);
			// Add NestedParties
			g.set(np);
			
			if(alloc.getSettlementLocation() != null) {
				/* Settlement Instruction */
				SettlInstructionsData sid = new SettlInstructionsData();
				sid.set(new SettlDeliveryType(SettlDeliveryType.VERSUS_PAYMENT_DELIVER_PAYMENT));
				
				SettlInstructionsData.NoDlvyInst dlvrGroup = new SettlInstructionsData.NoDlvyInst();
				dlvrGroup.set(new SettlInstSource(SettlInstSource.INSTITUTIONS_INSTRUCTIONS));
				dlvrGroup.set(new DlvyInstType(DlvyInstType.SECURITIES));
				SettlParties sttlParties = new SettlParties();
				
				SettlParties.NoSettlPartyIDs sttlparty = new SettlParties.NoSettlPartyIDs();
				sttlparty.set(new SettlPartyID(alloc.getSettlementLocation()));
				sttlparty.set(new SettlPartyIDSource(PartyIDSource.GENERALLY_ACCEPTED_MARKET_PARTICIPANT_IDENTIFIER));
				sttlparty.set(new SettlPartyRole(PartyRole.SETTLEMENT_LOCATION));
				sttlParties.addGroup(sttlparty);
				
				// Add Settlement Party
				dlvrGroup.addGroup(sttlparty);
				
				// Add Delivery Instruction
				sid.addGroup(dlvrGroup);
				
				// Add Settlement Data
				g.set(sid);
			}

			// Add Allocation
			ai.addGroup(g);
		});

		return ai;
	}
}
