package br.com.lovra.repository;

import java.util.UUID;

import br.com.lovra.entity.Filament;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class FilamentRepository implements PanacheRepositoryBase<Filament, UUID> {
	

}
