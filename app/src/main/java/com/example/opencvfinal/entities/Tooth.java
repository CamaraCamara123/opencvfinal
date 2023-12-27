package com.example.opencvfinal.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Tooth implements Serializable {
    private Long id;
    private String name;
    private Set<PW> pws = new HashSet<>();

    public Tooth() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<PW> getPws() {
        return pws;
    }

    public void setPws(Set<PW> pws) {
        this.pws = pws;
    }
}
