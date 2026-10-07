package com.main;
import com.GUI.LoginView;

import com.dao.RoleDAO;
import com.formdev.flatlaf.FlatLightLaf;
import com.model.Employe;
import com.model.Role;
import com.service.EmployeService;

import javax.swing.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        try {
            RoleDAO roleDAO = new RoleDAO();
            if (roleDAO.getAllRole().isEmpty()) {
                System.out.println("Initialisation des rôles...");
                roleDAO.insertRole(new Role(1, "Manager"));
                roleDAO.insertRole(new Role(2, "Caissier"));
                roleDAO.insertRole(new Role(3, "Magasinier"));
            }

            EmployeService employeService = new EmployeService();
            List<Employe> employes = employeService.getAllEmployes();

            if (employes.isEmpty()) {
                System.out.println("Base de données vide. Création de l'administrateur par défaut...");

                Employe admin = new Employe();
                admin.setFirstName("Admin");
                admin.setFamilyName("System");
                admin.setRole(new Role(1, "Manager"));
                admin.setAddress("Local");
                admin.setPhoneNumber("0000000000");
                admin.setBirthDate(java.time.LocalDate.now());

                Employe registeredAdmin = employeService.RegisterEmploye(admin, "admin");

                if (registeredAdmin != null) {
                    JOptionPane.showMessageDialog(null,
                            "Premier lancement détecté !\n\nUn compte Administrateur a été créé :\nUser: " + registeredAdmin.getUsername() + "\nPass: admin",
                            "Initialisation", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Premier lancement détecté !\n\nImpossible de créer le compte Administrateur.",
                            "Initialisation", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new LoginView().setVisible(true));

    }
}
