package br.com.lovra.entity;

import java.time.Instant;
import java.util.UUID;

import br.com.lovra.entity.enumerations.PrintModelStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_print_model")
public class PrintModel {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false, length = 150)
	private String name; 
	
	@Column(columnDefinition = "TEXT")
	private String description; //Implementar fulltextsearch 
	
	@Column(nullable = false)
	private String sourceUrl;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private PrintModelStatus status = PrintModelStatus.PENDING_VALIDATION;
	
	@OneToOne(fetch = FetchType.LAZY, optional = false, cascade = { CascadeType.PERSIST })
	@JoinColumn(nullable = false, unique = true, name = "stored_file_id")
	private StoredFile storedFile;
		
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant updatedAt;
	
	public PrintModel() {}
	
	public PrintModel(String name, String description, String sourceUrl, StoredFile storedFile) {
		this.name = name;
		this.description = description;
		this.sourceUrl = sourceUrl;
		this.storedFile = storedFile;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSourceUrl() {
		return sourceUrl;
	}

	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}

	public PrintModelStatus getStatus() {
		return status;
	}

	public void setStatus(PrintModelStatus status) {
		this.status = status;
	}

	public StoredFile getStoredFile() {
		return storedFile;
	}

	public void setStoredFile(StoredFile storedFile) {
		this.storedFile = storedFile;
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
