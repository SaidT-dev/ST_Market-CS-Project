package com.GUI;

import com.model.Employe;
import com.service.EmployeService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EmployesPanel extends JPanel {

    private EmployeService employeService;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Employe> employes;
    private JButton btnDelete;

    public EmployesPanel() {
        this.employeService = new EmployeService();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout(0, 20));
        setBackground(StyleUtils.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // --- HEADER ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(StyleUtils.COLOR_BACKGROUND);

        JLabel title = new JLabel("Gestion des Employés");
        title.setFont(StyleUtils.FONT_TITLE);
        title.setForeground(StyleUtils.COLOR_TEXT_DARK);
        header.add(title, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        btnDelete = new JButton("Supprimer");
        StyleUtils.applyDangerButtonStyle(btnDelete);
        btnDelete.setVisible(false);
        btnDelete.addActionListener(e -> handleDelete());

        JButton btnAdd = new JButton("Nouvel Employé");
        StyleUtils.applyPrimaryButtonStyle(btnAdd);
        btnAdd.addActionListener(e -> {
            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
            new EmployeFormDiag(parent, this).setVisible(true);
        });

        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnAdd);

        header.add(buttonsPanel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // --- TABLEAU ---
        String[] cols = {"ID", "Nom Complet", "Nom d'utilisateur", "Rôle", "Téléphone", "Adresse"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(40);
        table.setFont(StyleUtils.FONT_REGULAR);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(StyleUtils.FONT_BOLD);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isRowSelected = table.getSelectedRow() != -1;
                btnDelete.setVisible(isRowSelected);
            }
        });

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = table.getSelectedRow();
                    if (selectedRow >= 0) {
                        int modelRow = table.convertRowIndexToModel(selectedRow);
                        Employe selectedEmploye = employes.get(modelRow);
                        Frame parent = (Frame) SwingUtilities.getWindowAncestor(EmployesPanel.this);
                        new EmployeFormDiag(parent, EmployesPanel.this, selectedEmploye).setVisible(true);
                    }
                }
            }
        });

        // Centrer le texte
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        add(scroll, BorderLayout.CENTER);
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        Employe selectedEmploye = employes.get(table.convertRowIndexToModel(selectedRow));

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer l'employé '" + selectedEmploye.getFirstName() + " " + selectedEmploye.getFamilyName() + "' ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                employeService.deleteEmploye(selectedEmploye.getEmployeId());
                loadData();
                JOptionPane.showMessageDialog(this, "Employé supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData() {
        tableModel.setRowCount(0);
        employes = employeService.getAllEmployes();

        for (Employe e : employes) {
            Object[] row = {
                    e.getEmployeId(),
                    e.getFirstName() + " " + e.getFamilyName().toUpperCase(),
                    e.getUsername(),
                    e.getRole().getRoleName(),
                    e.getPhoneNumber(),
                    e.getAddress()
            };
            tableModel.addRow(row);
        }
    }
}
