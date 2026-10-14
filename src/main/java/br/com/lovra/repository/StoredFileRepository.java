package br.com.lovra.repository;

import java.util.UUID;

import br.com.lovra.entity.StoredFile;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoredFileRepository implements PanacheRepositoryBase<StoredFile, UUID>{

}
