package br.com.lovra.entity;

import java.time.Instant;
import java.util.UUID;

import br.com.lovra.entity.enumerations.FilamentType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_filament")
public class Filament {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, length = 150)
	private String name;
	
	@Column(nullable = false, length = 100)
	private String brand; 
	
	@Column(nullable = false, length = 100)
	private String color;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private FilamentType filamentType;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant updatedAt;
	
	public Filament() {}
	
	public Filament(String name, String brand, String color, FilamentType filamentType) {
		this.name = name;
		this.brand = brand;
		this.color = color;
		this.filamentType = filamentType;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public FilamentType getFilamentType() {
		return filamentType;
	}

	public void setFilamentType(FilamentType filamentType) {
		this.filamentType = filamentType;
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
