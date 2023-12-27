package com.example.opencvfinal.entities;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class Professor implements Serializable {
    private Long id;
    private String grade;
    private AdminUserDTO adminUserDTO;
    private Set<Groupe> groupes = new HashSet<>();

    public Professor() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public AdminUserDTO getAdminUserDTO() {
        return adminUserDTO;
    }

    public void setAdminUserDTO(AdminUserDTO adminUserDTO) {
        this.adminUserDTO = adminUserDTO;
    }

    public Set<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(Set<Groupe> groupes) {
        this.groupes = groupes;
    }
}
