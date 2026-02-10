package com.GUI;

import com.dao.RoleDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.model.Employe;
import com.model.Role;
import com.service.EmployeService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.*;
import java.awt.*;

public class EmployeFormDiag extends JDialog{
    private final EmployesPanel parentPanel;
    private final EmployeService employeService;
    private final RoleDAO roleDAO;

    // Champs
    private JTextField txtFirstName;
    private JTextField txtFamilyName;
    private JTextField txtPhone;
    private JTextField txtAddress;
    private JTextField txtBirthDate; // Format YYYY-MM-DD
    private JPasswordField txtPassword;
    private JComboBox<Role> comboRole;
    private Employe employeToUpdate;

    public EmployeFormDiag(Frame owner, EmployesPanel parentPanel) {
        super(owner, "Nouvel Employé", true);
        this.parentPanel = parentPanel;
        this.employeService = new EmployeService();
        this.roleDAO = new RoleDAO();
        initUI();
    }

    public EmployeFormDiag(Frame owner, EmployesPanel parentPanel, Employe employeToUpdate) {
        super(owner, "Modifier l'Employé", true);
        this.parentPanel = parentPanel;
        this.employeService = new EmployeService();
        this.roleDAO = new RoleDAO();
        this.employeToUpdate = employeToUpdate;
        initUI();
        populateForm();
    }

    private void initUI() {
        setSize(450, 600);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // 1. TITRE
        JLabel lblTitle = new JLabel("Formulaire Employé", SwingConstants.CENTER);
        lblTitle.setFont(StyleUtils.FONT_TITLE);
        lblTitle.setForeground(StyleUtils.COLOR_TEXT_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));
        add(lblTitle, BorderLayout.NORTH);

        // 2. FORMULAIRE
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int y = 0;

        // Prénom
        addLabel(formPanel, "Prénom", gbc, y++);
        txtFirstName = createTextField();
        addInput(formPanel, txtFirstName, gbc, y++);

        // Nom
        addLabel(formPanel, "Nom de famille", gbc, y++);
        txtFamilyName = createTextField();
        addInput(formPanel, txtFamilyName, gbc, y++);

        // Rôle (Combobox)
        addLabel(formPanel, "Rôle / Poste", gbc, y++);
        comboRole = new JComboBox<>();
        loadRoles(); // Remplir la liste
        addInput(formPanel, comboRole, gbc, y++);

        // Date Naissance
        addLabel(formPanel, "Date de naissance (AAAA-MM-JJ)", gbc, y++);
        txtBirthDate = createTextField();
        txtBirthDate.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "ex: 1995-05-20");
        addInput(formPanel, txtBirthDate, gbc, y++);

        // Téléphone
        addLabel(formPanel, "Téléphone", gbc, y++);
        txtPhone = createTextField();
        addInput(formPanel, txtPhone, gbc, y++);

        // Adresse
        addLabel(formPanel, "Adresse", gbc, y++);
        txtAddress = createTextField();
        addInput(formPanel, txtAddress, gbc, y++);

        // Mot de passe
        addLabel(formPanel, "Mot de passe initial", gbc, y++);
        txtPassword = new JPasswordField();
        txtPassword.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");
        txtPassword.setPreferredSize(new Dimension(0, 35));
        addInput(formPanel, txtPassword, gbc, y++);

        add(formPanel, BorderLayout.CENTER);

        // 3. BOUTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Annuler");
        StyleUtils.applySecondaryButtonStyle(btnCancel);
        btnCancel.addActionListener(e -> dispose());

        JButton btnSave = new JButton("Créer l'employé");
        StyleUtils.applyPrimaryButtonStyle(btnSave);
        btnSave.addActionListener(e -> saveEmploye());

        if (employeToUpdate != null) {
            btnSave.setText("Mettre à jour");
            txtPassword.setToolTipText("Laissez vide pour ne pas changer le mot de passe");
        }

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRoles() {
        List<Role> roles = roleDAO.getAllRole();
        for (Role r : roles) {
            comboRole.addItem(r);
        }
        // Petit renderer pour afficher juste le nom du rôle
        comboRole.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Role) {
                    setText(((Role) value).getRoleName());
                }
                return this;
            }
        });
    }

    private void populateForm() {
        if (employeToUpdate != null) {
            txtFirstName.setText(employeToUpdate.getFirstName());
            txtFamilyName.setText(employeToUpdate.getFamilyName());
            txtPhone.setText(employeToUpdate.getPhoneNumber());
            txtAddress.setText(employeToUpdate.getAddress());
            txtBirthDate.setText(employeToUpdate.getBirthDate().toString());

            for (int i = 0; i < comboRole.getItemCount(); i++) {
                if (comboRole.getItemAt(i).getRoleId() == employeToUpdate.getRole().getRoleId()) {
                    comboRole.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void saveEmploye() {
        try {
            // Validation simple
            if (txtFirstName.getText().isEmpty() || txtFamilyName.getText().isEmpty() || txtPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Nom, Prénom et Mot de passe sont obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Création objet
            Employe emp = new Employe();
            emp.setFirstName(txtFirstName.getText().trim());
            emp.setFamilyName(txtFamilyName.getText().trim());
            emp.setPhoneNumber(txtPhone.getText().trim());
            emp.setAddress(txtAddress.getText().trim());
            emp.setRole((Role) comboRole.getSelectedItem());

            // Parsing Date
            try {
                emp.setBirthDate(LocalDate.parse(txtBirthDate.getText().trim()));
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, "Format de date invalide. Utilisez AAAA-MM-JJ (ex: 1990-01-31)", "Erreur Date", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String pass = new String(txtPassword.getPassword());

            if (employeToUpdate == null) {
                // Appel Service (Génère username + hash password)
                Employe created = employeService.RegisterEmploye(emp, pass);

                if (created != null) {
                    String msg = "Employé créé avec succès !\n\n" +
                            "👤 Username généré : " + created.getUsername() + "\n" +
                            "🔑 Mot de passe : " + pass;
                    JOptionPane.showMessageDialog(this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);
                    parentPanel.loadData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la création.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } else { // Mode mise à jour
                emp.setEmployeId(employeToUpdate.getEmployeId());
                emp.setUsername(employeToUpdate.getUsername()); // Conserver l'username
                boolean success = employeService.updateEmploye(emp, pass.isEmpty() ? null : pass);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Employé mis à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    parentPanel.loadData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur technique : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helpers UI
    private void addLabel(JPanel p, String text, GridBagConstraints gbc, int y) {
        JLabel l = new JLabel(text);
        l.setFont(StyleUtils.FONT_BOLD);
        l.setForeground(StyleUtils.COLOR_TEXT_GRAY);
        gbc.gridy = y;
        p.add(l, gbc);
    }

    private void addInput(JPanel p, JComponent c, GridBagConstraints gbc, int y) {
        gbc.gridy = y;
        p.add(c, gbc);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 35));
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        return tf;
    }
}
