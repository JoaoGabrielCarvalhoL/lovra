package br.com.lovra.repository;

import java.util.UUID;

import br.com.lovra.entity.ProductMaterial;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductMaterialRepository implements PanacheRepositoryBase<ProductMaterial, UUID>{

}
