package br.com.lovra.service.impl;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.com.lovra.dto.common.PageResponse;
import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.FilamentResponse;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import br.com.lovra.entity.Filament;
import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.mapper.FilamentMapper;
import br.com.lovra.repository.FilamentRepository;
import br.com.lovra.service.FilamentService;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class FilamentServiceImpl implements FilamentService {

	private final FilamentRepository filamentRepository; 
	private final FilamentMapper filamentMapper;
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public FilamentServiceImpl(FilamentRepository filamentRepository, 
			FilamentMapper filamentMapper) {
		this.filamentRepository = filamentRepository;
		this.filamentMapper = filamentMapper;
	}

	@Override
	@Transactional
	public FilamentResponse create(CreateFilamentRequest request) {
		logger.info("Saving filament: {} into database.", request);
		Filament filament = this.filamentMapper.toFilament(request);
		this.filamentRepository.persist(filament);
		return filamentMapper.toFilamentResponse(filament);
	}

	@Override
	public FilamentResponse findById(UUID id) {
		logger.info("Getting filament by id: {}", id);
		return this.filamentRepository.findByIdOptional(id)
			.map(filamentMapper::toFilamentResponse)
			.orElseThrow(() -> new FilamentNotFoundException("Filament not found with id: " + id));
	}

	@Override
	public PageResponse<FilamentResponse> findAll(int page, int size) {
		logger.debug("Getting filaments. Page: {}, size: {}", page, size);
		
	    PanacheQuery<Filament> query = filamentRepository.findAll(Sort.ascending("name", "id"));
	    query.page(Page.of(page, size));

	    List<FilamentResponse> content = query.list()
	        .stream()
	        .map(filamentMapper::toFilamentResponse)
	        .toList();

	    long totalElements = query.count();
	    int totalPages = query.pageCount();
	    return new PageResponse<>(content, page, size, totalElements, totalPages);
	}

	@Override
	@Transactional
	public FilamentResponse update(UUID id, UpdateFilamentRequest request) {
		Filament filament = this.filamentRepository.findByIdOptional(id)
		.orElseThrow(() -> new FilamentNotFoundException("Filament not found with id: " + id));
		this.filamentMapper.updateFilament(request, filament);
		return filamentMapper.toFilamentResponse(filament);
	}

}
