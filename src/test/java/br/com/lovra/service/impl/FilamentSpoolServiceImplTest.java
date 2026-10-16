package br.com.lovra.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.lovra.dto.common.PageResponse;
import br.com.lovra.dto.filamentspool.CreateFilamentSpoolRequest;
import br.com.lovra.dto.filamentspool.FilamentSpoolResponse;
import br.com.lovra.dto.filamentspool.UpdateFilamentSpoolRequest;
import br.com.lovra.entity.Filament;
import br.com.lovra.entity.FilamentSpool;
import br.com.lovra.entity.enumerations.FilamentType;
import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.exception.FilamentSpoolNotFoundException;
import br.com.lovra.mapper.FilamentSpoolMapper;
import br.com.lovra.repository.FilamentRepository;
import br.com.lovra.repository.FilamentSpoolRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;

@ExtendWith(MockitoExtension.class)
class FilamentSpoolServiceImplTest {

	@Mock
    private FilamentRepository filamentRepository;

    @Mock
    private FilamentSpoolRepository filamentSpoolRepository;

    @Mock
    private FilamentSpoolMapper filamentSpoolMapper;

    @InjectMocks
    private FilamentSpoolServiceImpl filamentSpoolService;
    
    @Mock
	private PanacheQuery<FilamentSpool> panacheQuery;

    @Test
    void shouldCreateFilamentSpoolSuccessfully() {
    	UUID filamentSpoolId = UUID.randomUUID();
    	UUID filamentId = UUID.randomUUID();
    	
    	Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
    	FilamentSpool filamentSpool = new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	FilamentSpoolResponse filamentSpoolResponse = new FilamentSpoolResponse(filamentSpoolId, filamentId, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	CreateFilamentSpoolRequest createFilamentSpoolRequest = new CreateFilamentSpoolRequest(new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	
    	Mockito.when(this.filamentRepository.findByIdOptional(filamentId)).thenReturn(Optional.of(filament));
    	Mockito.when(this.filamentSpoolMapper.toFilamentSpool(createFilamentSpoolRequest)).thenReturn(filamentSpool);
    	Mockito.when(this.filamentSpoolMapper.toFilamentSpoolResponse(filamentSpool)).thenReturn(filamentSpoolResponse);
    	
    	FilamentSpoolResponse actual = this.filamentSpoolService.create(filamentId, createFilamentSpoolRequest);
    	
    	Assertions.assertEquals(filamentSpoolResponse, actual);
    	Mockito.verify(this.filamentSpoolMapper).toFilamentSpoolResponse(filamentSpool);
		Mockito.verify(this.filamentRepository).findByIdOptional(filamentId);
		Mockito.verify(this.filamentSpoolRepository).persist(filamentSpool);
    }

    @Test
    void shouldThrowExceptionWhenCreatingSpoolWithNonexistentFilament() {
    	UUID filamentId = UUID.randomUUID();
    	CreateFilamentSpoolRequest createFilamentSpoolRequest = new CreateFilamentSpoolRequest(new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	
		Mockito.when(filamentRepository.findByIdOptional(filamentId)).thenReturn(Optional.empty());
		Assertions.assertThrows(FilamentNotFoundException.class, () -> {
			this.filamentSpoolService.create(filamentId, createFilamentSpoolRequest);
		});
		Mockito.verify(filamentRepository).findByIdOptional(filamentId);
    }

    @Test
    void shouldFindFilamentSpoolByIdSuccessfully() {
    	UUID filamentSpoolId = UUID.randomUUID();
    	UUID filamentId = UUID.randomUUID();
    	Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
    	FilamentSpool filamentSpool = new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	FilamentSpoolResponse filamentSpoolResponse = new FilamentSpoolResponse(filamentSpoolId, filamentId, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	Mockito.when(filamentSpoolRepository.findByIdOptional(filamentSpoolId)).thenReturn(Optional.of(filamentSpool));
    	Mockito.when(this.filamentSpoolMapper.toFilamentSpoolResponse(filamentSpool)).thenReturn(filamentSpoolResponse);
    	
    	FilamentSpoolResponse actual = this.filamentSpoolService.findById(filamentSpoolId);
    	Assertions.assertEquals(filamentSpoolResponse, actual);
    	Mockito.verify(filamentSpoolRepository).findByIdOptional(filamentSpoolId);
        Mockito.verify(filamentSpoolMapper).toFilamentSpoolResponse(filamentSpool);
    }

    @Test
    void shouldThrowExceptionWhenFilamentSpoolIsNotFoundById() {
    	UUID filamentSpoolId = UUID.randomUUID();
    	Mockito.when(filamentSpoolRepository.findByIdOptional(filamentSpoolId)).thenReturn(Optional.empty());
    	
    	Assertions.assertThrows(FilamentSpoolNotFoundException.class, () -> {
    		this.filamentSpoolService.findById(filamentSpoolId);
		});
    	
    	Mockito.verify(filamentSpoolRepository).findByIdOptional(filamentSpoolId);
    }

    @Test
    void shouldFindAllFilamentSpoolsByFilamentIdSuccessfully() {
    	UUID filamentId = UUID.randomUUID();
        UUID filamentSpoolId = UUID.randomUUID();

        int page = 0;
        int size = 20;

        Filament filament = new Filament(
            "PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA
        );

        FilamentSpool filamentSpool = new FilamentSpool(
            filament,
            new BigDecimal("100.00"),
            new BigDecimal("1000.00"),
            LocalDate.of(2026, 9, 1)
        );

        FilamentSpoolResponse filamentSpoolResponse = new FilamentSpoolResponse(
            filamentSpoolId,
            filamentId,
            new BigDecimal("100.00"),
            new BigDecimal("1000.00"),
            LocalDate.of(2026, 9, 1));

        @SuppressWarnings("unchecked")
        PanacheQuery<FilamentSpool> query = Mockito.mock(PanacheQuery.class);

        Mockito.when(filamentRepository.findByIdOptional(filamentId))
            .thenReturn(Optional.of(filament));

        Mockito.when(filamentSpoolRepository.findQueryByFilamentId(filamentId))
            .thenReturn(query);

        Mockito.when(query.list())
            .thenReturn(List.of(filamentSpool));

        Mockito.when(query.count())
            .thenReturn(1L);

        Mockito.when(query.pageCount())
            .thenReturn(1);

        Mockito.when(filamentSpoolMapper.toFilamentSpoolResponse(filamentSpool))
            .thenReturn(filamentSpoolResponse);

        PageResponse<FilamentSpoolResponse> actual =
            filamentSpoolService.findAllByFilamentId(filamentId, page, size);

        Assertions.assertNotNull(actual);
        Assertions.assertEquals(1, actual.content().size());
        Assertions.assertEquals(filamentSpoolResponse, actual.content().get(0));
        Assertions.assertEquals(page, actual.page());
        Assertions.assertEquals(size, actual.size());
        Assertions.assertEquals(1L, actual.totalElements());
        Assertions.assertEquals(1, actual.totalPages());

        Mockito.verify(filamentRepository).findByIdOptional(filamentId);
        Mockito.verify(filamentSpoolRepository).findQueryByFilamentId(filamentId);
        Mockito.verify(query).page(Mockito.argThat(
            p -> p.index == page && p.size == size
        ));
        Mockito.verify(query).list();
        Mockito.verify(query).count();
        Mockito.verify(query).pageCount();
        Mockito.verify(filamentSpoolMapper).toFilamentSpoolResponse(filamentSpool);
    }

    @Test
    void shouldReturnEmptyPageWhenFilamentHasNoSpools() {
    	int page = 0;
        int size = 2;
        
        UUID filamentId = UUID.randomUUID();
        Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);   
        
        Mockito.when(filamentSpoolRepository.findQueryByFilamentId(Mockito.any(UUID.class))).thenReturn(panacheQuery);
        Mockito.when(this.filamentRepository.findByIdOptional(filamentId)).thenReturn(Optional.of(filament));
        Mockito.when(panacheQuery.list()).thenReturn(List.of());
        Mockito.when(panacheQuery.count()).thenReturn(0L);
        Mockito.when(panacheQuery.pageCount()).thenReturn(0);

        PageResponse<FilamentSpoolResponse> actual = filamentSpoolService.findAllByFilamentId(filamentId, page, size);

        Assertions.assertTrue(actual.content().isEmpty());
        Assertions.assertEquals(0L, actual.totalElements());
        Assertions.assertEquals(0, actual.totalPages());

        Mockito.verifyNoInteractions(filamentSpoolMapper);
    }

    @Test
    void shouldThrowExceptionWhenListingSpoolsForNonexistentFilament() {
    	int page = 0;
        int size = 2;
        
        UUID filamentId = UUID.randomUUID();

        Mockito.when(filamentRepository.findByIdOptional(filamentId)).thenReturn(Optional.empty());
        Assertions.assertThrows(FilamentNotFoundException.class, () -> {
        	filamentSpoolService.findAllByFilamentId(filamentId, page, size);
		});
    	
    	Mockito.verify(filamentRepository).findByIdOptional(filamentId);
    }

    @Test
    void shouldUpdateFilamentSpoolSuccessfully() {
    	UUID filamentSpoolId = UUID.randomUUID();
    	
    	Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
    	FilamentSpool filamentSpool = new FilamentSpool(filament, new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	UpdateFilamentSpoolRequest updateFilamentSpoolRequest = new UpdateFilamentSpoolRequest(new BigDecimal("100.00"), new BigDecimal("100.00"), LocalDate.of(2026, 9,1));
    	
    	Mockito.when(this.filamentSpoolRepository.findByIdOptional(filamentSpoolId)).thenReturn(Optional.of(filamentSpool));
		Mockito.doNothing().when(this.filamentSpoolMapper).updateFilamentSpool(updateFilamentSpoolRequest, filamentSpool);

		this.filamentSpoolService.update(filamentSpoolId, updateFilamentSpoolRequest);
	
		Mockito.verify(this.filamentSpoolRepository).findByIdOptional(filamentSpoolId);
		Mockito.verify(this.filamentSpoolMapper).updateFilamentSpool(updateFilamentSpoolRequest, filamentSpool);
    }
    
    @Test
    void shouldDeleteFilamentSpoolSuccessfully() {
        UUID filamentSpoolId = UUID.randomUUID();

        FilamentSpool filamentSpool = Mockito.mock(FilamentSpool.class);

        Mockito.when(filamentSpoolRepository.findByIdOptional(filamentSpoolId)).thenReturn(Optional.of(filamentSpool));
        filamentSpoolService.delete(filamentSpoolId);

        Mockito.verify(filamentSpoolRepository).findByIdOptional(filamentSpoolId);
        Mockito.verify(filamentSpoolRepository).delete(filamentSpool);
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonexistentFilamentSpool() {
        UUID filamentSpoolId = UUID.randomUUID();

        Mockito.when(filamentSpoolRepository.findByIdOptional(filamentSpoolId)).thenReturn(Optional.empty());

        Assertions.assertThrows(
                FilamentSpoolNotFoundException.class,
                () -> filamentSpoolService.delete(filamentSpoolId));

        Mockito.verify(filamentSpoolRepository).findByIdOptional(filamentSpoolId);

        Mockito.verify(filamentSpoolRepository, Mockito.never()).delete(Mockito.any(FilamentSpool.class));
    }
	

}
