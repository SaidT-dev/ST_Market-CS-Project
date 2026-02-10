package com.GUI;

import com.formdev.flatlaf.FlatClientProperties;
import com.model.Product;
import com.model.Stock;
import com.service.ProductService;

import javax.swing.*;
import java.awt.*;

public class ProductFormDialog extends JDialog {
    private final InventoryPanel parentPanel;
    private final ProductService productService;

    // Champs du formulaire
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtPrice;
    private JSpinner spinQty;
    private JSpinner spinMinQty;

    public  ProductFormDialog(Frame owner, InventoryPanel parentPanel) {
        super(owner, "Nouveau Produit", true);
        this.parentPanel = parentPanel;
        this.productService = new ProductService();
        initUI();
    }

    private void initUI() {
        setSize(400, 500);
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(StyleUtils.COLOR_WHITE);

        JLabel lblTitle = new JLabel("Ajouter un Produit", SwingConstants.CENTER);
        lblTitle.setFont(StyleUtils.FONT_TITLE);
        lblTitle.setForeground(StyleUtils.COLOR_TEXT_DARK);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(lblTitle, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.white);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 0, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 5, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        addLabel(formPanel, "Référence (ID / Code-barres)", gbc, 0);
        txtId = createTextField();
        addInput(formPanel, txtId, gbc, 1);

        // Nom
        addLabel(formPanel, "Nom du Produit", gbc, 2);
        txtName = createTextField();
        addInput(formPanel, txtName, gbc, 3);

        // Prix
        addLabel(formPanel, "Prix Unitaire (DA)", gbc, 4);
        txtPrice = createTextField();
        addInput(formPanel, txtPrice, gbc, 5);

        // Quantité (Stock initial)
        addLabel(formPanel, "Quantité Initiale", gbc, 6);
        spinQty = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
        addInput(formPanel, spinQty, gbc, 7);

        // Quantité Minimale (Alerte)
        addLabel(formPanel, "Seuil d'alerte (Stock min)", gbc, 8);
        spinMinQty = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1));
        addInput(formPanel, spinMinQty, gbc, 9);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        JButton btnCancel = new JButton("Annuler");
        StyleUtils.applySecondaryButtonStyle(btnCancel);
        btnCancel.addActionListener(e -> dispose()); // Fermer

        JButton btnSave = new JButton("Enregistrer");
        StyleUtils.applyPrimaryButtonStyle(btnSave);
        btnSave.addActionListener(e -> saveProduct());

        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSave);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void addLabel(JPanel p, String text, GridBagConstraints gbc, int gridy) {
        JLabel l = new JLabel(text);
        l.setFont(StyleUtils.FONT_BOLD);
        l.setForeground(StyleUtils.COLOR_TEXT_GRAY);
        gbc.gridy = gridy;
        p.add(l, gbc);
    }

    private void addInput(JPanel p, JComponent c, GridBagConstraints gbc, int gridy) {
        gbc.gridy = gridy;
        p.add(c, gbc);
    }

    private JTextField createTextField() {
        JTextField tf = new JTextField();
        tf.setPreferredSize(new Dimension(0, 35));
        tf.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 0,10,0,10");
        return tf;
    }

    private void saveProduct() {
        try {
            // 1. Validation basique
            if (txtId.getText().isEmpty() || txtName.getText().isEmpty() || txtPrice.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir tous les champs obligatoires.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // 2. Conversion des données
            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            double price = Double.parseDouble(txtPrice.getText().trim());
            int quantity = (int) spinQty.getValue();
            int minQty = (int) spinMinQty.getValue();

            // 3. Création des objets
            Stock stock = new Stock(id, quantity, minQty);
            Product product = new Product(id, name, price, stock);
            // Liaison Stock <-> Product
            product.getStock().setProductId(id);

            // 4. Appel au Service
            productService.createProduct(product);

            // 5. Succès
            JOptionPane.showMessageDialog(this, "Produit ajouté avec succès !");
            parentPanel.loadData(); // RECHARGER LE TABLEAU DERRIÈRE
            dispose(); // Fermer la fenêtre

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "L'ID et le Prix doivent être des nombres valides.", "Erreur Format", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}
