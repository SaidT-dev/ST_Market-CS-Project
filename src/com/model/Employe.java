package com.model;

import java.time.LocalDate;
import java.util.Objects;

public class Employe {
    private int employeId;
    private String firstName;
    private String familyName;
    private LocalDate birthDate;
    private String address;
    private String phoneNumber;
    private String passwordHash;
    private Role role;
    private String username;

    public Employe(){

    }
    public Employe(int employeId, String firstName, String familyName, LocalDate birthDate, String address, String phoneNumber, String passwordHash, Role role, String username) {
        this.employeId = employeId;
        this.firstName = firstName;
        this.familyName = familyName;
        this.birthDate = birthDate;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.passwordHash = passwordHash;
        this.role = role;
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employe employe = (Employe) o;
        return this.employeId == employe.employeId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeId);
    }

    @Override
    public String toString() {
        return "Employe{" +
                "employeId=" + employeId +
                ", firstName='" + firstName + '\'' +
                ", familyName='" + familyName + '\'' +
                ", birthDate=" + birthDate +
                ", address='" + address + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", role=" + role +
                ", username='" + username + '\'' +
                '}';
    }


    public int getEmployeId() {
        return employeId;
    }

    public  void setEmployeId(int employeId) {
        this.employeId = employeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public  void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
