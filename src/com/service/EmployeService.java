package com.service;

import com.auth.PasswordUtil;
import com.dao.EmployeDAO;
import com.model.Employe;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class EmployeService {
    private EmployeDAO employeDAO;

    public EmployeService() {
        this.employeDAO = new EmployeDAO();
    }

    public Employe RegisterEmploye(Employe employe, String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException {

        String baseUsername = generateBaseUsername(employe.getFirstName(), employe.getFamilyName());
        String finalUsername = baseUsername;
        int suffix = 1;

        while(employeDAO.getEmployeByUsername(finalUsername) != null) {
            finalUsername = baseUsername + suffix;
            suffix++;
        }

        employe.setUsername(finalUsername);
        String hashedPassword = PasswordUtil.hashPassword(password);
        employe.setPasswordHash(hashedPassword);

        return employeDAO.insertEmploye(employe);
    }

    private String generateBaseUsername(String firstName, String familyName) {
        if (firstName == null || firstName.isEmpty() || familyName == null || familyName.isEmpty()) {
            return "user";
        }

        return (firstName.substring(0, 1) + "." + familyName)
                .toLowerCase()
                .replaceAll("\\s+", "");
    }

    public Employe login(String username, String password){
        Employe employe = employeDAO.getEmployeByUsername(username);
        if(employe == null){
            return null;
        }

        try{
            if(PasswordUtil.checkPassword(password, employe.getPasswordHash())){
                return employe;
            } else {
                return null;
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e){
            System.err.println("Erreur de sécurité lors de la vérification du mot de passe: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public List<Employe> getAllEmployes() {
        return employeDAO.getAllEmployes();
    }

    public boolean updateEmploye(Employe employe, String newPassword) {
        if (newPassword != null && !newPassword.isEmpty()) {
            try {
                String hashedPassword = PasswordUtil.hashPassword(newPassword);
                employe.setPasswordHash(hashedPassword);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                System.err.println("Erreur de sécurité lors du hachage du nouveau mot de passe: " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            Employe existingEmploye = employeDAO.getEmployeById(employe.getEmployeId());
            if (existingEmploye == null) {
                return false;
            }
            employe.setPasswordHash(existingEmploye.getPasswordHash());
        }
        return employeDAO.updateEmploye(employe);
    }

    public void deleteEmploye(int employeId) {
        employeDAO.deleteEmploye(employeId);
    }
}
