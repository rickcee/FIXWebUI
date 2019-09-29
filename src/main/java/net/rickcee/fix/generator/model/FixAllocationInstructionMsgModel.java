/**
 * 
 */
package net.rickcee.fix.generator.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rickcee
 *
 */
@Getter
@Setter
public class FixAllocationInstructionMsgModel {
	private String sessionId;
	private String onBehalfOfCompId;
	private String buySell;
	private String securitySource;
	private String securityId;
	private String tradeDate;
	private String settleDate;
	private Long quantity;
	private Boolean sendToClient = Boolean.TRUE;
	private List<FixAllocModel> allocs;
}
