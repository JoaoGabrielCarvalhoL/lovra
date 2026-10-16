package br.com.lovra.entity;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
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

@Entity
@Table(name = "tb_filament_spool")
public class FilamentSpool {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "filament_id", nullable = false)
	private Filament filament;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal purchasePrice;
	
	@Column(nullable = false, precision = 12, scale = 2)
	private BigDecimal initialWeightInGrams; 
	
	@Column(nullable = false)
	private LocalDate purchasedAt;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant updatedAt;
	
	public FilamentSpool() {}
	
	public FilamentSpool(Filament filament, BigDecimal purchasePrice,
			BigDecimal initialWeightInGrams, LocalDate purchasedAt) {
		this.filament = filament;
		this.purchasePrice = purchasePrice;
		this.initialWeightInGrams = initialWeightInGrams;
		this.purchasedAt = purchasedAt;
	}
	
	public UUID getId() {
		return id;
	}

	public Filament getFilament() {
		return filament;
	}

	public void setFilament(Filament filament) {
		this.filament = filament;
	}

	public BigDecimal getPurchasePrice() {
		return purchasePrice;
	}

	public void setPurchasePrice(BigDecimal purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

	public BigDecimal getInitialWeightInGrams() {
		return initialWeightInGrams;
	}

	public void setInitialWeightInGrams(BigDecimal initialWeightInGrams) {
		this.initialWeightInGrams = initialWeightInGrams;
	}

	public LocalDate getPurchasedAt() {
		return purchasedAt;
	}

	public void setPurchasedAt(LocalDate purchasedAt) {
		this.purchasedAt = purchasedAt;
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
