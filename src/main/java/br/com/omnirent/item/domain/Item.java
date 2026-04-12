package br.com.omnirent.item.domain;

import br.com.omnirent.address.Address;
import br.com.omnirent.category.SubCategory;
import br.com.omnirent.common.NamedEntity;
import br.com.omnirent.common.enums.ItemStatus;
import br.com.omnirent.user.domain.User;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "items")
public class Item extends NamedEntity {
	private static final long serialVersionUID = 1L;
	
	@Embedded
	private ItemData itemData;
	
	@Enumerated(EnumType.STRING)
	public ItemStatus itemStatus;
	
	@Column(name = "owner_id")
	private String ownerId;
	
	@Column(name = "sub_category_id")
	private String subCategoryId;
	
	@Column(name = "pickup_address_id")
	private String pickupAddressId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sub_category_id", insertable = false, updatable = false)
	private SubCategory subCategory;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pickup_address_id", insertable = false, updatable = false)
	private Address pickupAddress;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", insertable = false, updatable = false)
	private User owner;
	
	public void updateItemStatus(String status) {
	    setItemStatus(ItemStatus.fromString(status));
	}
	
	public void assignOwner(User owner) {
		this.owner = owner;
		this.ownerId = owner.getId();

	}
	
	public void assignSubCategory(SubCategory subCategory) {
		this.subCategory = subCategory;
		this.subCategoryId = subCategory.getId();
	}
	
	public void assignAddress(Address address) {
		this.pickupAddress = address;
		this.pickupAddressId = pickupAddress.getId();
	}
}
