package br.com.lovra.repository;

import java.util.List;
import java.util.UUID;

import br.com.lovra.entity.FilamentSpool;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import io.quarkus.panache.common.Sort;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FilamentSpoolRepository implements PanacheRepositoryBase<FilamentSpool, UUID>{

	public List<FilamentSpool> findByFilamentId(UUID filamentId) {
        return find("filament.id", filamentId).list();
    }
	
	public PanacheQuery<FilamentSpool> findQueryByFilamentId(UUID filamentId) {
	    return find(
	        "filament.id = ?1",
	        Sort.ascending("purchasedAt", "id"),
	        filamentId
	    );
	}
}
