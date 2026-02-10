package com.GUI;


import com.formdev.flatlaf.FlatClientProperties;
import com.model.Employe;
import com.service.EmployeService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginView extends JFrame {

    private EmployeService employeService;
    private JTextField fldUsername;
    private JPasswordField fldPassword;

    public LoginView() {
        employeService = new EmployeService();
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ressources/img/logo500.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Icône introuvable");
        }
        initUI();
    }

    private void initUI() {
        setTitle("ST Market - Connexion");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(StyleUtils.COLOR_BACKGROUND);

        JPanel loginCard = new JPanel(new GridBagLayout());
        loginCard.setBackground(StyleUtils.COLOR_WHITE);
        loginCard.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        loginCard.setPreferredSize(new Dimension(400, 550));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/ressources/img/logo.png"));
            Image image = logoIcon.getImage();
            Image newimg = image.getScaledInstance(80, 80, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(newimg);
            JLabel logoLabel = new JLabel(logoIcon);
            gbc.gridy = 0;
            gbc.insets = new Insets(30, 40, 10, 40);
            loginCard.add(logoLabel, gbc);
        } catch (Exception e) {
            System.err.println("Impossible de charger le logo: " + e.getMessage());
        }

        JLabel lblTitle = new JLabel("ST Market", SwingConstants.CENTER);
        lblTitle.setFont(StyleUtils.FONT_TITLE);
        lblTitle.setForeground(StyleUtils.COLOR_ACCENT);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 40, 10, 40);
        loginCard.add(lblTitle, gbc);

        JLabel lblSubtitle = new JLabel("Bienvenue ! Connectez-vous",
                SwingConstants.CENTER);
        lblSubtitle.setFont(StyleUtils.FONT_SUBTITLE);
        lblSubtitle.setForeground(StyleUtils.COLOR_TEXT_GRAY);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 40, 30, 40);
        loginCard.add(lblSubtitle, gbc);

        JLabel lblUsername = new JLabel("Nom d'utilisateur");
        lblUsername.setFont(StyleUtils.FONT_BOLD);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 40, 5, 40);
        loginCard.add(lblUsername, gbc);

        fldUsername = new JTextField();
        fldUsername.setPreferredSize(new Dimension(0, 40));
        fldUsername.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Entrez votre identifiant");
        fldUsername.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; margin: 0, 10, 0 ,10");
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 40, 15, 40);
        loginCard.add(fldUsername, gbc);

        JLabel lblPassword = new JLabel("Mot de passe");
        lblPassword.setFont(StyleUtils.FONT_BOLD);
        gbc.gridy = 5;
        gbc.insets = new Insets(0, 40, 5, 40);
        loginCard.add(lblPassword, gbc);

        fldPassword = new JPasswordField();
        fldPassword.setPreferredSize(new Dimension(0, 40));
        fldPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT,
                "Entrez votre mot de passe");
        fldPassword.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; margin: 0, 10, 0 ,10; showRevealButton: true");
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 40, 30, 40);
        loginCard.add(fldPassword, gbc);

        JButton btnLogin = new JButton("Se connecter");
        StyleUtils.applyPrimaryButtonStyle(btnLogin);
        btnLogin.setPreferredSize(new Dimension(0, 45));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 40, 40, 40);
        loginCard.add(btnLogin, gbc);
        btnLogin.addActionListener(e -> performLogin());

        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e){
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    performLogin();
                }
            }
        };
        fldUsername.addKeyListener(enterKeyAdapter);
        fldPassword.addKeyListener(enterKeyAdapter);

        mainPanel.add(loginCard);
        setContentPane(mainPanel);
    }

    private void performLogin(){
        String user = fldUsername.getText().trim();
        String pass = new String(fldPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs.", "Attention", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Employe employe = employeService.login(user, pass);

        if (employe != null) {
            new DashboardView(employe).setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Identifiants incorrects.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
