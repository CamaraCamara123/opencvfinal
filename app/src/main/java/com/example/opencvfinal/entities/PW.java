package com.example.opencvfinal.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class PW implements Serializable {
    private Long id;
    private String title;
    private String objectif;
    private byte[] docs;
    private String docsContentType;
    private Float mesureLeft;
    private Float mesureRight;
    private Float mesureCenter;
    private Tooth tooth;
    private Set<Groupe> groupes = new HashSet<>();

    public PW() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public byte[] getDocs() {
        return docs;
    }

    public void setDocs(byte[] docs) {
        this.docs = docs;
    }

    public String getDocsContentType() {
        return docsContentType;
    }

    public void setDocsContentType(String docsContentType) {
        this.docsContentType = docsContentType;
    }

    public Float getMesureLeft() {
        return mesureLeft;
    }

    public void setMesureLeft(Float mesureLeft) {
        this.mesureLeft = mesureLeft;
    }

    public Float getMesureRight() {
        return mesureRight;
    }

    public void setMesureRight(Float mesureRight) {
        this.mesureRight = mesureRight;
    }

    public Float getMesureCenter() {
        return mesureCenter;
    }

    public void setMesureCenter(Float mesureCenter) {
        this.mesureCenter = mesureCenter;
    }

    public Tooth getTooth() {
        return tooth;
    }

    public void setTooth(Tooth tooth) {
        this.tooth = tooth;
    }

    public Set<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(Set<Groupe> groupes) {
        this.groupes = groupes;
    }
}
