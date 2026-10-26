package br.com.lovra.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.lovra.dto.common.PageResponse;
import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import br.com.lovra.entity.Filament;
import br.com.lovra.entity.FilamentSpool;
import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.exception.FilamentSpoolNotFoundException;
import br.com.lovra.mapper.FilamentSpoolMapper;
import br.com.lovra.repository.FilamentRepository;
import br.com.lovra.repository.FilamentSpoolRepository;
import br.com.lovra.service.FilamentSpoolService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FilamentSpoolServiceImpl implements FilamentSpoolService {
	
	private final FilamentRepository filamentRepository; 
	private final FilamentSpoolRepository filamentSpoolRepository;
	private final FilamentSpoolMapper filamentSpoolMapper;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public FilamentSpoolServiceImpl(FilamentRepository filamentRepository,
			FilamentSpoolRepository filamentSpoolRepository, FilamentSpoolMapper filamentSpoolMapper) {
		this.filamentRepository = filamentRepository;
		this.filamentSpoolRepository = filamentSpoolRepository;
		this.filamentSpoolMapper = filamentSpoolMapper;
	}

	@Transactional
	@Override
	public FilamentSpoolResponse create(UUID filamentId, CreateFilamentSpoolRequest request) {
		logger.info("Getting filament by id: {}", filamentId);
		Filament filament = this.filamentRepository.findByIdOptional(filamentId)
			.orElseThrow(() -> new FilamentNotFoundException("Filament not found with id: " + filamentId));
		
		logger.info("Saving filamentSpool into database.");
		FilamentSpool filamentSpool = this.filamentSpoolMapper.toFilamentSpool(request);
		filamentSpool.setFilament(filament);
		filamentSpool.setRemainingWeightInGrams(request.initialWeightInGrams());
		
		filamentSpoolRepository.persist(filamentSpool);
		return filamentSpoolMapper.toFilamentSpoolResponse(filamentSpool);
	}

	@Override
	public FilamentSpoolResponse findById(UUID id) {
		logger.info("Getting filamentSpool by id: {}", id);
		return this.filamentSpoolRepository.findByIdOptional(id)
				.map(this.filamentSpoolMapper::toFilamentSpoolResponse)
				.orElseThrow(() -> new FilamentSpoolNotFoundException("Filament Spool not found with id: " + id));
	}

	@Override
	public PageResponse<FilamentSpoolResponse> findAllByFilamentId(UUID filamentId, int page, int size) {
		logger.debug("Getting filament spools by filament ID: {}. Page: {}, size: {}", filamentId, page, size);
		System.out.println("FilamentSpoolResourceImpl reached: " + filamentId);
		this.filamentRepository.findByIdOptional(filamentId)
			.orElseThrow(() -> new FilamentNotFoundException("Filament not found with id: " + filamentId));
		
		PanacheQuery<FilamentSpool> query = filamentSpoolRepository.findQueryByFilamentId(filamentId);
		query.page(Page.of(page, size));
		
		List<FilamentSpoolResponse> content = query.list().stream()
				.map(this.filamentSpoolMapper::toFilamentSpoolResponse)
				.toList();
		
		return new PageResponse<FilamentSpoolResponse>(content, page, size, query.count(), query.pageCount());
	}

	@Transactional
	@Override
	public void update(UUID id, UpdateFilamentSpoolRequest request) {
		if (request.remainingWeightInGrams().compareTo(request.initialWeightInGrams()) > 0) {
		    throw new IllegalArgumentException("Remaining weight cannot exceed initial weight.");
		}
		FilamentSpool filamentSpool = this.filamentSpoolRepository.findByIdOptional(id)
			.orElseThrow(() -> new  FilamentSpoolNotFoundException("Filament Spool not found with id: " + id));
		filamentSpoolMapper.updateFilamentSpool(request, filamentSpool);
	}

	@Transactional
	@Override
	public void delete(UUID id) {
		FilamentSpool filamentSpool = this.filamentSpoolRepository.findByIdOptional(id)
				.orElseThrow(() -> new  FilamentSpoolNotFoundException("Filament Spool not found with id: " + id));
		this.filamentSpoolRepository.delete(filamentSpool);
	}

	
}
