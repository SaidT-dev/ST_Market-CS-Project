package com.GUI;

import com.model.Product;
import com.service.ProductService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private ProductService productService;
    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnRestock;
    private JButton btnDelete;

    public InventoryPanel(){
        this.productService = new ProductService();
        initUI();
        loadData();
    }

    private void initUI(){
        setLayout(new BorderLayout(0, 20));
        setBackground(StyleUtils.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(StyleUtils.COLOR_BACKGROUND);

        JLabel lblTitle = new JLabel("Inventaire des Produits");
        lblTitle.setFont(StyleUtils.FONT_TITLE);
        lblTitle.setForeground(StyleUtils.COLOR_TEXT_DARK);
        headerPanel.add(lblTitle, BorderLayout.WEST);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setOpaque(false);

        btnRestock = new JButton("Réapprovisionner");
        StyleUtils.applySecondaryButtonStyle(btnRestock);
        btnRestock.setVisible(false);
        btnRestock.addActionListener(e -> handleRestock());

        btnDelete = new JButton("Supprimer");
        StyleUtils.applyDangerButtonStyle(btnDelete);
        btnDelete.setVisible(false);
        btnDelete.addActionListener(e -> handleDelete());

        JButton btnAdd = new JButton("Nouveau Produit");
        StyleUtils.applyPrimaryButtonStyle(btnAdd);
        btnAdd.addActionListener(e -> {
            Frame parentWindow = (Frame) SwingUtilities.getWindowAncestor(this);
            ProductFormDialog dialog = new ProductFormDialog(parentWindow, this);
            dialog.setVisible(true);
        });

        buttonsPanel.add(btnRestock);
        buttonsPanel.add(btnDelete);
        buttonsPanel.add(btnAdd);

        headerPanel.add(buttonsPanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        String[] columns = {"ID", "Nom du Produit", "Prix Unitaire", "Quantité", "État du Stock"};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Rendre les cellules non éditables
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        table.setRowHeight(40);
        table.setFont(StyleUtils.FONT_REGULAR);
        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean isRowSelected = table.getSelectedRow() != -1;
                btnRestock.setVisible(isRowSelected);
                btnDelete.setVisible(isRowSelected);
            }
        });

        StyleUtils.applyTableHeaderStyle(table);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        this.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                loadData();
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {
            }
        });

        add(scrollPane, BorderLayout.CENTER);
    }

    private void handleRestock() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        long productId = (long) table.getValueAt(selectedRow, 0);
        showRestockDialog(productId);
    }

    private void handleDelete() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) return;

        long productId = (long) table.getValueAt(selectedRow, 0);
        String productName = (String) table.getValueAt(selectedRow, 1);

        int confirmation = JOptionPane.showConfirmDialog(
                this,
                "Êtes-vous sûr de vouloir supprimer le produit '" + productName + "' ?\nCette action est irréversible.",
                "Confirmation de suppression",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmation == JOptionPane.YES_OPTION) {
            try {
                productService.deleteProduct(productId);
                loadData();
                JOptionPane.showMessageDialog(this, "Produit supprimé avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la suppression : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showRestockDialog(long productId) {
        String quantityStr = JOptionPane.showInputDialog(
                this,
                "Entrez la quantité à ajouter au stock :",
                "Réapprovisionner le produit",
                JOptionPane.PLAIN_MESSAGE
        );

        if (quantityStr != null && !quantityStr.trim().isEmpty()) {
            try {
                int quantityToAdd = Integer.parseInt(quantityStr);
                if (quantityToAdd <= 0) {
                    JOptionPane.showMessageDialog(this, "La quantité doit être un nombre positif.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                productService.restockProduct(productId, quantityToAdd);
                loadData(); // Refresh the table
                JOptionPane.showMessageDialog(this, "Stock mis à jour avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide.", "Erreur de format", JOptionPane.ERROR_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la mise à jour du stock : " + e.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void loadData(){
        // Force re-index
        tableModel.setRowCount(0);
        List<Product> products = productService.findAllProducts();

        for(Product p : products){
            int qty = p.getStock().getCurrentQuantity();
            int min = p.getStock().getMinimalQuantity();
            String status = (qty <= min) ? "Faible" : "En Stock";
            Object[] row = {
                    p.getProductId(),
                    p.getProductName(),
                    String.format("%.2f DA", p.getUnitPrice()),
                    qty,
                    status
            };
            tableModel.addRow(row);
        }
    }
}
