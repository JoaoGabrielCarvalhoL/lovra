package br.com.lovra.service;

import java.util.UUID;

import br.com.lovra.dto.common.PageResponse;
import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.FilamentResponse;
import br.com.lovra.dto.filament.UpdateFilamentRequest;

public interface FilamentService {
	
	FilamentResponse create(final CreateFilamentRequest request);

	FilamentResponse findById(final UUID id);

	PageResponse<FilamentResponse> findAll(int page, int size);

	FilamentResponse update(final UUID id, final UpdateFilamentRequest request);

	void delete(final UUID id);
}
