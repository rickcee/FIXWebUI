/**
 * 
 */
package net.rickcee.fix.generator.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Data;
import net.rickcee.fix.util.HashMapConverter;

/**
 * @author rickcee
 *
 */
@Data
@Entity
@Table(name = "allocation_test_case")
//@Builder
public class AllocationTestCase implements Serializable {
	private static final long serialVersionUID = -3363044234317417889L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
	private String onBehalfOfCompId;
	private String buySell;
	private String securitySource;
	private String securityId;
	private String tradeDate;
	private String settleDate;
	private Long quantity;
	private Double avgPrice;
	@OneToMany(cascade = CascadeType.ALL)
	private List<FixAllocModel> allocs = new ArrayList<>();
	@Lob
	@Column(columnDefinition = "clob")
	@Convert(converter = HashMapConverter.class)
	private Map<String, String> customTags = new HashMap<>();

}
