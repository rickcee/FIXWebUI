/**
 * 
 */
package net.rickcee.fix.generator.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rickcee
 *
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true, value = "settlementCurrency, settlementLocation")
public class FixAllocModel {
	private String id;
	private String account;
	private Long quantity;
	private Double netMoney;
	private Double accruedInterest;
	private String settlementCurrency;
	private String settlementLocation;
}
