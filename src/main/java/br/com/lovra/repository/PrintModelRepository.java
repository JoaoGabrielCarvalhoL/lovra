package br.com.lovra.repository;

import java.util.UUID;

import br.com.lovra.entity.PrintModel;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PrintModelRepository implements PanacheRepositoryBase<PrintModel, UUID> {

}
