/**
 * 
 */
package net.rickcee.fix.generator.model;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rickcee
 *
 */
@Getter
@Setter
public class FixAllocationMsgModel extends AllocationTestCase {
	private String sessionId;
	private Boolean sendToClient = Boolean.TRUE;
}
