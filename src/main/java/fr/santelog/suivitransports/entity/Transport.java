package fr.santelog.suivitransports.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transport")
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    private TypeProduit typeProduit;

    @Enumerated(EnumType.STRING)
    private StatutTransport statut;

    private LocalDateTime dateDepart;

    private LocalDateTime dateArriveePrevue;

    private Double temperatureMin;

    private Double temperatureMax;

    private String chauffeur;

    @Column(length = 2000)
    private String commentaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinataire_id")
    private Destinataire destinataire;

    @OneToMany(mappedBy = "transport", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ReleveTemperature> releves = new ArrayList<>();

    public Transport() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public TypeProduit getTypeProduit() {
        return typeProduit;
    }

    public void setTypeProduit(TypeProduit typeProduit) {
        this.typeProduit = typeProduit;
    }

    public StatutTransport getStatut() {
        return statut;
    }

    public void setStatut(StatutTransport statut) {
        this.statut = statut;
    }

    public LocalDateTime getDateDepart() {
        return dateDepart;
    }

    public void setDateDepart(LocalDateTime dateDepart) {
        this.dateDepart = dateDepart;
    }

    public LocalDateTime getDateArriveePrevue() {
        return dateArriveePrevue;
    }

    public void setDateArriveePrevue(LocalDateTime dateArriveePrevue) {
        this.dateArriveePrevue = dateArriveePrevue;
    }

    public Double getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(Double temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public Double getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(Double temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public String getChauffeur() {
        return chauffeur;
    }

    public void setChauffeur(String chauffeur) {
        this.chauffeur = chauffeur;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public Destinataire getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Destinataire destinataire) {
        this.destinataire = destinataire;
    }

    public List<ReleveTemperature> getReleves() {
        return releves;
    }

    public void setReleves(List<ReleveTemperature> releves) {
        this.releves = releves;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ", reference='" + reference + '\'' +
                ", typeProduit=" + typeProduit +
                ", statut=" + statut +
                ", dateDepart=" + dateDepart +
                ", dateArriveePrevue=" + dateArriveePrevue +
                ", temperatureMin=" + temperatureMin +
                ", temperatureMax=" + temperatureMax +
                ", chauffeur='" + chauffeur + '\'' +
                ", commentaire='" + commentaire + '\'' +
                ", destinataire=" + destinataire +
                '}';
    }
}
