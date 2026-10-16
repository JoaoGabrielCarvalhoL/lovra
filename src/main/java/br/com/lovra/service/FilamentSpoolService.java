package br.com.lovra.service;

import java.util.UUID;

import br.com.lovra.dto.common.PageResponse;
import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;

public interface FilamentSpoolService {

	FilamentSpoolResponse create(final UUID filamentId, final CreateFilamentSpoolRequest request);

	FilamentSpoolResponse findById(final UUID id);

	PageResponse<FilamentSpoolResponse> findAllByFilamentId(final UUID filamentId, final int page, final int size);

	void update(final UUID id, final UpdateFilamentSpoolRequest request);

	void delete(final UUID id);
}
