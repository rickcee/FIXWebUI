/**
 * 
 */
package net.rickcee.fix.generator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rickcee
 *
 */
@Getter
@Setter
public class FixAllocationMsgModel {
	private String sessionId;
	private String onBehalfOfCompId;
	private String buySell;
	private String securitySource;
	private String securityId;
	private String tradeDate;
	private String settleDate;
	private Long quantity;
	private Double avgPrice;
	private Boolean sendToClient = Boolean.TRUE;
	private List<FixAllocModel> allocs;
	private List<Map<String, String>> customTags = new ArrayList<>();
}
