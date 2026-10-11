package fr.santelog.suivitransports.service;

import fr.santelog.suivitransports.entity.Destinataire;
import fr.santelog.suivitransports.repository.DestinataireRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DestinataireServiceTest {

    @Mock
    private DestinataireRepository destinataireRepository;

    @InjectMocks
    private DestinataireService destinataireService;

    @Test
    void create_returnsOneWhenNomPresent() {
        Destinataire destinataire = new Destinataire();
        destinataire.setNom("CHU de Lille");
        destinataire.setVille("Lille");

        int code = destinataireService.create(destinataire);

        assertEquals(1, code);
        verify(destinataireRepository).save(destinataire);
    }
}
