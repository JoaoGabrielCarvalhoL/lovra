package br.com.lovra.service.impl;

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
import br.com.lovra.dto.filament.CreateFilamentRequest;
import br.com.lovra.dto.filament.FilamentResponse;
import br.com.lovra.dto.filament.UpdateFilamentRequest;
import br.com.lovra.entity.Filament;
import br.com.lovra.entity.enumerations.FilamentType;
import br.com.lovra.exception.FilamentNotFoundException;
import br.com.lovra.mapper.FilamentMapper;
import br.com.lovra.repository.FilamentRepository;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;

@ExtendWith(MockitoExtension.class)
class FilamentServiceImplTest {
	
	@InjectMocks
	private FilamentServiceImpl filamentService;

	@Mock
	private FilamentRepository filamentRepository;
	
	@Mock
	private FilamentMapper filamentMapper;
	
	@Mock
	private PanacheQuery<Filament> panacheQuery;
	
	@Test
    void shouldCreateFilamentSuccessfully() {
		
		UUID id = UUID.randomUUID();
		
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		CreateFilamentRequest createFilamentRequest = new CreateFilamentRequest("PLA Black Silk",  "Voolt3D", "Black", FilamentType.PLA);
		FilamentResponse expected = new FilamentResponse(id, "PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		
		Mockito.when(this.filamentMapper.toFilament(createFilamentRequest)).thenReturn(filament);
		Mockito.when(this.filamentMapper.toFilamentResponse(filament)).thenReturn(expected);
		Mockito.doNothing().when(this.filamentRepository).persist(filament);
		
		FilamentResponse actual = filamentService.create(createFilamentRequest);
		Assertions.assertEquals(expected, actual);
		Assertions.assertEquals(id, actual.id());
		
		Mockito.verify(this.filamentMapper).toFilament(createFilamentRequest);
		Mockito.verify(this.filamentRepository).persist(filament);
		Mockito.verify(this.filamentMapper).toFilamentResponse(filament);
    }

	@Test
    void shouldFindFilamentByIdSuccessfully() {
		UUID id = UUID.randomUUID();
		
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		FilamentResponse expected = new FilamentResponse(id, "PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		
		Mockito.when(this.filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		Mockito.when(this.filamentMapper.toFilamentResponse(filament)).thenReturn(expected);

		FilamentResponse actual = this.filamentService.findById(id);
	
		Assertions.assertEquals(expected, actual);
		Assertions.assertEquals(id, actual.id());
		
		Mockito.verify(this.filamentMapper).toFilamentResponse(filament);
		Mockito.verify(this.filamentRepository).findByIdOptional(id);
    }

    @Test
    void shouldThrowFilamentNotFoundExceptionWhenFindingNonexistentFilament() {
    	UUID id = UUID.randomUUID();
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.empty());
		Assertions.assertThrows(FilamentNotFoundException.class, () -> {
			filamentService.findById(id);
		});
		Mockito.verify(filamentRepository).findByIdOptional(id);
    }

    @Test
    void shouldReturnPaginatedFilamentsSuccessfully() {
        int page = 0;
        int size = 2;

        Filament firstFilament = new Filament("PLA Basic", "Bambu Lab", "Black", FilamentType.PLA);
        Filament secondFilament = new Filament("PLA Silk", "Voolt3D", "Blue", FilamentType.PLA);

        FilamentResponse firstResponse = new FilamentResponse(UUID.randomUUID(), "PLA Basic", "Bambu Lab", "Black", FilamentType.PLA);
        FilamentResponse secondResponse = new FilamentResponse(UUID.randomUUID(), "PLA Silk", "Voolt3D", "Blue", FilamentType.PLA);

        Mockito.when(filamentRepository.findAll(Mockito.any(Sort.class))).thenReturn(panacheQuery);
        Mockito.when(panacheQuery.list()).thenReturn(List.of(firstFilament, secondFilament));
        Mockito.when(panacheQuery.count()).thenReturn(10L);
        Mockito.when(panacheQuery.pageCount()).thenReturn(5);
        Mockito.when(filamentMapper.toFilamentResponse(firstFilament)).thenReturn(firstResponse);
        Mockito.when(filamentMapper.toFilamentResponse(secondFilament)).thenReturn(secondResponse);

        PageResponse<FilamentResponse> actual =filamentService.findAll(page, size);

        Assertions.assertEquals(List.of(firstResponse, secondResponse),actual.content());

        Assertions.assertEquals(page, actual.page());
        Assertions.assertEquals(size, actual.size());
        Assertions.assertEquals(10L, actual.totalElements());
        Assertions.assertEquals(5, actual.totalPages());

        Mockito.verify(panacheQuery).page(
        	    Mockito.argThat(p ->
        	        p.index == page && p.size == size
        	    )
        	);
    }

    @Test
    void shouldReturnEmptyPageWhenNoFilamentsExist() {
    	 int page = 0;
         int size = 2;

         Mockito.when(filamentRepository.findAll(Mockito.any(Sort.class))).thenReturn(panacheQuery);
         Mockito.when(panacheQuery.list()).thenReturn(List.of());
         Mockito.when(panacheQuery.count()).thenReturn(0L);
         Mockito.when(panacheQuery.pageCount()).thenReturn(0);

         PageResponse<FilamentResponse> actual = filamentService.findAll(page, size);

         Assertions.assertTrue(actual.content().isEmpty());
         Assertions.assertEquals(0L, actual.totalElements());
         Assertions.assertEquals(0, actual.totalPages());

         Mockito.verifyNoInteractions(filamentMapper);
    }

    @Test
    void shouldUpdateFilamentSuccessfully() {
    	UUID id = UUID.randomUUID();
		
		Filament filament = new Filament("PLA Black Silk", "Voolt3D", "Black", FilamentType.PLA);
		UpdateFilamentRequest updateFilamentRequest = new UpdateFilamentRequest("PLA Black Silk",  "Voolt3D", "Black", FilamentType.PLA);
		FilamentResponse expected = new FilamentResponse(id, "PLA Brown Silk", "Voolt3D", "Brown", FilamentType.PLA);
		
		Mockito.when(this.filamentRepository.findByIdOptional(id)).thenReturn(Optional.of(filament));
		Mockito.when(this.filamentMapper.toFilamentResponse(filament)).thenReturn(expected);
		Mockito.doNothing().when(this.filamentMapper).updateFilament(updateFilamentRequest, filament);

		FilamentResponse actual = this.filamentService.update(id, updateFilamentRequest);
	
		Assertions.assertEquals(expected, actual);
		Assertions.assertEquals(id, actual.id());
		
		Mockito.verify(this.filamentMapper).toFilamentResponse(filament);
		Mockito.verify(this.filamentRepository).findByIdOptional(id);
    }

    @Test
    void shouldThrowFilamentNotFoundExceptionWhenUpdatingNonexistentFilament() {
    	UUID id = UUID.randomUUID();
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.empty());
		Assertions.assertThrows(FilamentNotFoundException.class, () -> {
			filamentService.findById(id);
		});
		Mockito.verify(filamentRepository).findByIdOptional(id);

    }

    @Test
    void shouldNotInvokeMapperWhenFilamentDoesNotExist() {
    	UUID id = UUID.randomUUID();
		Mockito.when(filamentRepository.findByIdOptional(id)).thenReturn(Optional.empty());
		Assertions.assertThrows(FilamentNotFoundException.class, () -> {
			filamentService.findById(id);
		});
		Mockito.verify(filamentRepository).findByIdOptional(id);
		Mockito.verifyNoInteractions(filamentMapper);
    }

}
