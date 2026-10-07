package com.GUI;

import com.formdev.flatlaf.FlatClientProperties;
import com.model.Employe;
import com.model.Product;
import com.model.Sale;
import com.model.SaleDetail;
import com.service.ProductService;
import com.service.SaleService;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SalesPanel extends JPanel {

    private final Employe currentUser;
    private final ProductService productService;
    private final SaleService saleService;


    private JTable productsTable;
    private DefaultTableModel productsModel;
    private JTextField txtSearch;
    private List<Product> allProducts;


    private JTable cartTable;
    private DefaultTableModel cartModel;
    private JLabel lblTotal;
    private JTextField txtCashGiven;


    private List<SaleDetail> currentCartDetails;

    public SalesPanel(Employe currentUser) {
        this.currentUser = currentUser;
        this.productService = new ProductService();
        this.saleService = new SaleService();
        this.currentCartDetails = new ArrayList<>();

        initUI();
        loadProducts();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtils.COLOR_BACKGROUND);
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(StyleUtils.FONT_BOLD);

        JPanel posPanel = new JPanel(new BorderLayout(15, 0));
        posPanel.setOpaque(false);
        posPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel leftPanel = createLeftPanel();
        JPanel rightPanel = createRightPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);

        posPanel.add(splitPane, BorderLayout.CENTER);

        SalesHistoryPanel historyPanel = new SalesHistoryPanel();

        tabbedPane.addTab("Nouvelle Vente", posPanel);
        tabbedPane.addTab("Historique des Ventes", historyPanel);

        try {
            ImageIcon saleIcon = new ImageIcon(getClass().getResource("/ressources/icons/shopping-cart.png"));
            Image scaledSaleIcon = saleIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            tabbedPane.setIconAt(0, new ImageIcon(scaledSaleIcon));
        } catch (Exception e) {
            System.err.println("Icône de vente introuvable.");
        }

        try {
            ImageIcon historyIcon = new ImageIcon(getClass().getResource("/ressources/icons/chart.png"));
            Image scaledHistoryIcon = historyIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            tabbedPane.setIconAt(1, new ImageIcon(scaledHistoryIcon));
        } catch (Exception e) {
            System.err.println("Icône d'historique introuvable.");
        }

        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedIndex() == 1) {
                historyPanel.loadData();
            }
        });

        this.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                loadProducts();
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {}
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {}
        });

        add(tabbedPane, BorderLayout.CENTER);
    }




    private JPanel createLeftPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(StyleUtils.COLOR_BACKGROUND);


        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);

        try {
            ImageIcon searchIcon = new ImageIcon(getClass().getResource("/ressources/icons/loupe.png"));
            Image scaledIcon = searchIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            JLabel lblIcon = new JLabel(new ImageIcon(scaledIcon));
            searchPanel.add(lblIcon, BorderLayout.WEST);
        } catch (Exception e) {
            JLabel lblIcon = new JLabel("🔍");
            lblIcon.setFont(new Font("Segoe UI", Font.PLAIN, 20));
            searchPanel.add(lblIcon, BorderLayout.WEST);
            System.err.println("Icône de recherche introuvable.");
        }

        txtSearch = new JTextField();
        txtSearch.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Rechercher par Nom ou ID...");
        txtSearch.putClientProperty(FlatClientProperties.STYLE, "arc: 10; margin: 5,10,5,10");
        txtSearch.setFont(StyleUtils.FONT_REGULAR);

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterProducts(txtSearch.getText());
            }
        });

        searchPanel.add(txtSearch, BorderLayout.CENTER);


        String[] cols = {"ID", "Produit", "Prix", "Stock"};
        productsModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        productsTable = new JTable(productsModel);
        productsTable.setRowHeight(35);
        productsTable.setFont(StyleUtils.FONT_REGULAR);
        productsTable.getTableHeader().setFont(StyleUtils.FONT_BOLD);
        productsTable.setShowVerticalLines(false);


        productsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    addToCart();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(productsTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createEmptyBorder());



        JButton btnAdd = new JButton("Ajouter au Panier");
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/ressources/icons/shopping-cart.png"));
            Image scaledImg = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
            btnAdd.setIcon(new ImageIcon(scaledImg));
        } catch (Exception e) {
            System.err.println("Icône introuvable: /ressources/icons/shopping-cart.png");
        }
        btnAdd.setIconTextGap(10);
        btnAdd.setBackground(Color.WHITE);
        btnAdd.setFont(StyleUtils.FONT_BOLD);
        btnAdd.setForeground(StyleUtils.COLOR_ACCENT);
        btnAdd.addActionListener(e -> addToCart());

        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnAdd, BorderLayout.SOUTH);

        return panel;
    }




    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.putClientProperty(FlatClientProperties.STYLE, "arc: 15");


        JLabel title = new JLabel("Ticket de Vente", SwingConstants.CENTER);
        title.setFont(StyleUtils.FONT_TITLE);
        title.setForeground(StyleUtils.COLOR_TEXT_DARK);


        String[] cols = {"Produit", "Prix Unitaire", "Quantité", "Sous-Total"};
        cartModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 2;
            }
        };

        cartModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int column = e.getColumn();

                if (column == 2) {
                    updateCartItemQuantity(row);
                }
            }
        });

        cartTable = new JTable(cartModel);
        cartTable.setRowHeight(30);
        cartTable.setFont(StyleUtils.FONT_REGULAR);
        cartTable.setShowVerticalLines(false);
        cartTable.setGridColor(new Color(240, 240, 240));

        JScrollPane scroll = new JScrollPane(cartTable);
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(230, 230, 230)));


        JPanel paymentPanel = new JPanel(new GridBagLayout());
        paymentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(5, 0, 5, 0); gbc.gridx = 0;


        lblTotal = new JLabel("TOTAL : 0.00 DA");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(StyleUtils.COLOR_ACCENT);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);


        JLabel lblCash = new JLabel("Espèces Reçues (DA) :");
        lblCash.setFont(StyleUtils.FONT_BOLD);

        txtCashGiven = new JTextField("0");
        txtCashGiven.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtCashGiven.setHorizontalAlignment(SwingConstants.RIGHT);
        txtCashGiven.putClientProperty(FlatClientProperties.STYLE, "arc: 10");


        JButton btnValidate = new JButton("VALIDER LA VENTE");
        btnValidate.setBackground(new Color(16, 185, 129));
        btnValidate.setForeground(Color.WHITE);
        btnValidate.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnValidate.setPreferredSize(new Dimension(0, 50));
        btnValidate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnValidate.putClientProperty(FlatClientProperties.STYLE, "arc: 10; borderWidth: 0");

        btnValidate.addActionListener(e -> {
            if (currentCartDetails.isEmpty()) return;

            try {
                BigDecimal given = new BigDecimal(txtCashGiven.getText().replace(",", "."));
                BigDecimal total = calculateTotal();

                if (given.compareTo(total) < 0) {
                    JOptionPane.showMessageDialog(this,
                            "Montant insuffisant. Total à payer : " + total + " DA",
                            "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                btnValidate.setEnabled(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                new SwingWorker<Sale, Void>() {
                    @Override
                    protected Sale doInBackground() throws Exception {
                        Sale saleToProcess = new Sale();
                        saleToProcess.setCashier(currentUser);
                        saleToProcess.setSaleDetails(currentCartDetails);
                        saleToProcess.setSaleDate(LocalDateTime.now());
                        saleToProcess.setTotalPrice(total);
                        saleToProcess.setGivenByClient(given);
                        saleToProcess.setChangeToReturn(given.subtract(total));

                        return saleService.createSale(saleToProcess);
                    }

                    @Override
                    protected void done() {
                        try {
                            Sale completedSale = get();

                            String msg = String.format("Vente Validée !\n\nTotal: %s DA\nReçu: %s DA\n\nMONNAIE À RENDRE: %s DA",
                                    completedSale.getTotalPrice(), completedSale.getGivenByClient(), completedSale.getChangeToReturn());

                            JOptionPane.showMessageDialog(SalesPanel.this, msg, "Succès", JOptionPane.INFORMATION_MESSAGE);

                            clearCart();
                            loadProducts();

                        } catch (Exception ex) {
                            Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                            String message = cause.getMessage() != null ? cause.getMessage() : ex.toString();
                            JOptionPane.showMessageDialog(SalesPanel.this, "Erreur : " + message, "Erreur", JOptionPane.ERROR_MESSAGE);
                            ex.printStackTrace();
                        } finally {
                            btnValidate.setEnabled(true);
                            setCursor(Cursor.getDefaultCursor());
                        }
                    }
                }.execute();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Montant invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton btnClear = new JButton("Annuler le Ticket");
        btnClear.setBackground(new Color(239, 68, 68));
        btnClear.setForeground(Color.WHITE);
        btnClear.addActionListener(e -> clearCart());


        gbc.gridy = 0; paymentPanel.add(lblTotal, gbc);
        gbc.gridy = 1; paymentPanel.add(new JSeparator(), gbc);
        gbc.gridy = 2; paymentPanel.add(lblCash, gbc);
        gbc.gridy = 3; paymentPanel.add(txtCashGiven, gbc);
        gbc.gridy = 4; paymentPanel.add(Box.createVerticalStrut(10), gbc);
        gbc.gridy = 5; paymentPanel.add(btnValidate, gbc);
        gbc.gridy = 6; paymentPanel.add(btnClear, gbc);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(paymentPanel, BorderLayout.SOUTH);

        return panel;
    }



    private void loadProducts() {
        allProducts = productService.findAllProducts();
        filterProducts("");
    }

    private void filterProducts(String query) {
        productsModel.setRowCount(0);
        String lowerQuery = query.toLowerCase();

        if (allProducts != null) {
            for (Product p : allProducts) {

                if (p.getProductName().toLowerCase().contains(lowerQuery) ||
                        String.valueOf(p.getProductId()).contains(lowerQuery)) {

                    productsModel.addRow(new Object[]{
                            p.getProductId(),
                            p.getProductName(),
                            p.getUnitPrice(),
                            p.getStock().getCurrentQuantity()
                    });
                }
            }
        }
    }

    private void addToCart() {
        int selectedRow = productsTable.getSelectedRow();
        if (selectedRow == -1) return;

        long productId = (long) productsModel.getValueAt(selectedRow, 0);
        Product product = productService.findProductById(productId);

        if (product.getStock().getCurrentQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "Produit en rupture de stock !", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }


        for (SaleDetail detail : currentCartDetails) {
            if (detail.getProduct().getProductId() == productId) {

                if (detail.getQuantitySold() < product.getStock().getCurrentQuantity()) {
                    detail.setQuantitySold(detail.getQuantitySold() + 1);
                    updateCartTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Stock insuffisant pour ajouter plus.", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
                return;
            }
        }


        SaleDetail newDetail = new SaleDetail();
        newDetail.setProduct(product);
        newDetail.setQuantitySold(1);
        newDetail.setUnitSoldPrice(BigDecimal.valueOf(product.getUnitPrice()));

        currentCartDetails.add(newDetail);
        updateCartTable();
    }

    private void updateCartItemQuantity(int row) {
        try {
            SaleDetail detail = currentCartDetails.get(row);
            int newQuantity = Integer.parseInt(String.valueOf(cartModel.getValueAt(row, 2)).trim());

            int availableStock = detail.getProduct().getStock().getCurrentQuantity();
            if (newQuantity > availableStock) {
                JOptionPane.showMessageDialog(this, "Stock insuffisant. Disponible : " + availableStock, "Erreur", JOptionPane.WARNING_MESSAGE);

                cartModel.setValueAt(detail.getQuantitySold(), row, 2);
                return;
            }

            if (newQuantity <= 0) {

                currentCartDetails.remove(row);
            } else {
                detail.setQuantitySold(newQuantity);
            }


            updateCartTable();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Veuillez entrer un nombre valide pour la quantité.", "Erreur", JOptionPane.ERROR_MESSAGE);

            SaleDetail detail = currentCartDetails.get(row);
            cartModel.setValueAt(detail.getQuantitySold(), row, 2);
        }
    }

    private void updateCartTable() {

        int selectedRow = cartTable.getSelectedRow();

        cartModel.setRowCount(0);
        BigDecimal total = BigDecimal.ZERO;

        for (SaleDetail detail : currentCartDetails) {
            BigDecimal subTotal = detail.getUnitSoldPrice().multiply(BigDecimal.valueOf(detail.getQuantitySold()));
            total = total.add(subTotal);

            cartModel.addRow(new Object[]{
                    detail.getProduct().getProductName(),
                    detail.getUnitSoldPrice(),
                    detail.getQuantitySold(),
                    subTotal
            });
        }

        lblTotal.setText("TOTAL : " + total + " DA");


        if (selectedRow != -1 && selectedRow < cartModel.getRowCount()) {
            cartTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
    }

    private BigDecimal calculateTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (SaleDetail detail : currentCartDetails) {
            BigDecimal subtotal = detail.getUnitSoldPrice().multiply(new BigDecimal(detail.getQuantitySold()));
            total = total.add(subtotal);
        }
        return total;
    }

    private void clearCart() {
        currentCartDetails.clear();
        updateCartTable();
        txtCashGiven.setText("0");
    }


}
