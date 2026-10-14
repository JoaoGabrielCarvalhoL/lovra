package br.com.lovra.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_stored_file")
public class StoredFile {
	
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;
	
	@Column(nullable = false)
	private String originalFilename;
	
	@Column(nullable = false)
	private String contentType; 
	
	@Column(nullable = false)
	private Long sizeInBytes; 
	
	@Column(nullable = false, length = 64, unique = true)
	private String sha256; 
	
	@Column(nullable = false)
	private String storageReference;
	
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@Column(nullable = false)
	private Instant updatedAt;
	
	public StoredFile() {}

	public StoredFile(String originalFilename, String contentType, Long sizeInBytes,
			String sha256, String storageReference) {
		this.originalFilename = originalFilename;
		this.contentType = contentType;
		this.sizeInBytes = sizeInBytes;
		this.sha256 = sha256;
		this.storageReference = storageReference;
	}

	public UUID getId() {
		return id;
	}

	public String getOriginalFilename() {
		return originalFilename;
	}

	public String getContentType() {
		return contentType;
	}

	public Long getSizeInBytes() {
		return sizeInBytes;
	}

	public String getSha256() {
		return sha256;
	}

	public String getStorageReference() {
		return storageReference;
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
