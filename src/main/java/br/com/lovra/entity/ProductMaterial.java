package br.com.lovra.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "tb_product_material", uniqueConstraints = {
		@UniqueConstraint(
				name = "uk_product_material_product_filament",
	            columnNames = { "product_id", "filament_id" })
})
public class ProductMaterial {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "filament_id", nullable = false)
	private Filament filament; 
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal weightInGrams;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant updatedAt;
	
	public ProductMaterial() {}
	
	public ProductMaterial(Product product, Filament filament, BigDecimal weightInGrams) {
		super();
		this.product = product;
		this.filament = filament;
		this.weightInGrams = weightInGrams;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Filament getFilament() {
		return filament;
	}

	public void setFilament(Filament filament) {
		this.filament = filament;
	}

	public BigDecimal getWeightInGrams() {
		return weightInGrams;
	}

	public void setWeightInGrams(BigDecimal weightInGrams) {
		this.weightInGrams = weightInGrams;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
	
	private void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Instant getUpdatedAt() {
		return updatedAt;
	}

	private void setUpdatedAt(Instant updatedAt) {
		this.updatedAt = updatedAt;
	}
	
	@PrePersist
	public void setupCreatedAt() {
		Instant now = Instant.now();
		setCreatedAt(now);
		setUpdatedAt(now);
	}
	
	@PreUpdate
	public void setupUpdatedAt() {
		setUpdatedAt(Instant.now());
	}
	

}
