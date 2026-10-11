package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.Transport;
import fr.santelog.suivitransports.repository.DestinataireRepository;
import fr.santelog.suivitransports.repository.TransportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransportServiceTest {

    @Mock
    private TransportRepository transportRepository;

    @Mock
    private DestinataireRepository destinataireRepository;

    @InjectMocks
    private TransportService transportService;

    @Test
    void listerTous_retourneLesTransportsDuRepository() {
        Transport t1 = new Transport();
        t1.setReference("TRP-2024-0001");
        Transport t2 = new Transport();
        t2.setReference("TRP-2024-0002");
        when(transportRepository.findAll()).thenReturn(List.of(t1, t2));

        List<Transport> resultat = transportService.listerTous();

        assertEquals(2, resultat.size());
        assertEquals("TRP-2024-0001", resultat.get(0).getReference());
    }

    @Test
    void trouverParId_retourneNullSiAbsent() {
        when(transportRepository.findById(99L)).thenReturn(Optional.empty());

        Transport resultat = transportService.trouverParId(99L);

        assertNull(resultat);
    }
}
