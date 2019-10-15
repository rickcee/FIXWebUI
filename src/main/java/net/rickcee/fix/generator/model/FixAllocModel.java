/**
 * 
 */
package net.rickcee.fix.generator.model;

import java.util.UUID;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author rickcee
 *
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true, value = "settlementCurrency, settlementLocation")
@Entity
@Table(name = "allocation_detail")
public class FixAllocModel {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private UUID uui;
	private String id;
	private String account;
	private Long quantity;
	private Double netMoney;
	private Double accruedInterest;
	private String settlementCurrency;
	private String settlementLocation;
}
