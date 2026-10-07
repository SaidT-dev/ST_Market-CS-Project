package com.GUI;

import com.model.Employe;

import javax.swing.*;
import java.awt.*;

public class DashboardView extends JFrame {
    private Employe currentUser;
    private CardLayout cardLayout;
    private JPanel contentArea;

    public DashboardView(Employe currentUser) {
        this.currentUser = currentUser;
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ressources/img/logo500.png"));
            setIconImage(icon.getImage());
        } catch (Exception e) {
            System.err.println("Icône introuvable");
        }
        initUI();
    }

    private void initUI() {
        setTitle("ST Market - Tableau de Bord");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel();
        sidebar.setBackground(StyleUtils.COLOR_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setLayout(new BorderLayout());

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/ressources/img/logo.png"));
            Image image = logoIcon.getImage();
            Image newimg = image.getScaledInstance(120, 120,  java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(newimg);
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 10, 0));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

            JLabel lblTitle = new JLabel("ST Market", SwingConstants.CENTER);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(StyleUtils.FONT_TITLE);
            lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.setOpaque(false);
            topPanel.add(logoLabel, BorderLayout.CENTER);
            topPanel.add(lblTitle, BorderLayout.SOUTH);

            sidebar.add(topPanel, BorderLayout.NORTH);
        } catch (Exception e) {
            JLabel lblTitle = new JLabel("ST Market", SwingConstants.CENTER);
            lblTitle.setForeground(Color.WHITE);
            lblTitle.setFont(StyleUtils.FONT_TITLE);
            lblTitle.setBorder(BorderFactory.createEmptyBorder(30, 0, 30, 0));
            sidebar.add(lblTitle, BorderLayout.NORTH);
            System.err.println("Impossible de charger le logo: " + e.getMessage());
        }

        JPanel menuContainer = new JPanel(new GridBagLayout());
        menuContainer.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 10, 5, 10);
        JButton btnInventory = createMenuButton("Inventaire", "/ressources/icons/box.png");
        btnInventory.addActionListener(e -> cardLayout.show(contentArea, "INVENTORY"));

        JButton btnSales = createMenuButton("Ventes", "/ressources/icons/shopping-cart.png");
        btnSales.addActionListener(e -> cardLayout.show(contentArea, "POS"));

        JButton btnEmployees = createMenuButton("Employés", "/ressources/icons/id-card.png");
        btnEmployees.addActionListener(e -> cardLayout.show(contentArea, "EMPLOYEES"));

        gbc.gridy = 0;
        menuContainer.add(btnInventory, gbc);
        gbc.gridy = 1;
        menuContainer.add(btnSales, gbc);
        gbc.gridy = 2;
        menuContainer.add(btnEmployees, gbc);

        sidebar.add(menuContainer, BorderLayout.CENTER);

        JButton btnLogout = createMenuButton("Déconnexion", "/ressources/icons/power-button.png");
        StyleUtils.applySecondaryButtonStyle(btnLogout);
        btnLogout.setBackground(StyleUtils.COLOR_DANGER);
        btnLogout.setForeground(Color.WHITE);
        btnLogout.addActionListener(e -> logout());

        JPanel logoutContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        logoutContainer.setOpaque(false);
        logoutContainer.add(btnLogout);
        sidebar.add(logoutContainer, BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(StyleUtils.COLOR_BACKGROUND);

        contentArea.add(new InventoryPanel(), "INVENTORY");
        contentArea.add(new com.GUI.SalesPanel(currentUser), "POS");
        contentArea.add(new EmployesPanel(), "EMPLOYEES");

        int userRoleId = currentUser.getRole().getRoleId();
        switch (userRoleId) {
            case 2: // Caissier
                btnInventory.setEnabled(false);
                btnEmployees.setEnabled(false);
                cardLayout.show(contentArea, "POS");
                break;
            case 3: // Magasinier
                btnSales.setEnabled(false);
                btnEmployees.setEnabled(false);
                cardLayout.show(contentArea, "INVENTORY");
                break;
            case 1: // Manager
                cardLayout.show(contentArea, "INVENTORY");
                break;
            default:
                btnInventory.setEnabled(false);
                btnSales.setEnabled(false);
                btnEmployees.setEnabled(false);
                break;
        }

        add(sidebar, BorderLayout.WEST);
        add(contentArea, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton btn = new JButton(text);
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource(iconPath));
            Image scaledImg = icon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Icône introuvable: " + iconPath);
        }
        btn.setIconTextGap(15);
        btn.setForeground(Color.WHITE);
        btn.setBackground(StyleUtils.COLOR_SIDEBAR_BG);
        btn.setFont(StyleUtils.FONT_SUBTITLE);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setMargin(new Insets(0, 20, 0, 0));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(StyleUtils.COLOR_ACCENT);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(StyleUtils.COLOR_SIDEBAR_BG);
            }
        });

        return btn;
    }


    private void logout() {
        this.dispose();
        new LoginView().setVisible(true);
    }
}
