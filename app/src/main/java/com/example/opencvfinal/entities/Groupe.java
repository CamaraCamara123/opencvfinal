package com.example.opencvfinal.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Groupe implements Serializable {
    private Long id;
    private String code;
    private String year;
    private Professor professor;
    private Set<Student> students = new HashSet<>();
    private Set<PW> pws = new HashSet<>();

    public Groupe() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Set<Student> getStudents() {
        return students;
    }

    public void setStudents(Set<Student> students) {
        this.students = students;
    }

    public Set<PW> getPws() {
        return pws;
    }

    public void setPws(Set<PW> pws) {
        this.pws = pws;
    }
}
