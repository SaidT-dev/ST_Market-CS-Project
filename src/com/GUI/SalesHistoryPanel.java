package com.GUI;

import com.model.Sale;
import com.model.SaleDetail;
import com.service.SaleService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SalesHistoryPanel extends JPanel {

    private SaleService saleService;

    // Tableaux
    private JTable salesTable;
    private DefaultTableModel salesModel;
    private JTable detailsTable;
    private DefaultTableModel detailsModel;

    // Données
    private List<Sale> salesList;

    public SalesHistoryPanel() {
        this.saleService = new SaleService();
        this.salesList = new ArrayList<>();
        initUI();
        loadData();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setBackground(StyleUtils.COLOR_BACKGROUND);

        String[] salesColumns = {"ID", "Date", "Caissier", "Total"};
        salesModel = new DefaultTableModel(salesColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        salesTable = new JTable(salesModel);
        configureTable(salesTable);

        /*salesTable.getSelectionModel().addListSelectionListener(event -> {
            if (!event.getValueIsAdjusting() && salesTable.getSelectedRow() != -1) {
                updateDetails(salesTable.getSelectedRow());
            }
        });*/

        JScrollPane salesScroll = new JScrollPane(salesTable);
        salesScroll.setBorder(BorderFactory.createTitledBorder("Historique des Ventes"));
        salesScroll.getViewport().setBackground(Color.WHITE);


/*
        String[] detailsColumns = {"Produit", "Prix Unitaire", "Qté", "Sous-Total"};
        detailsModel = new DefaultTableModel(detailsColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        detailsTable = new JTable(detailsModel);
        configureTable(detailsTable);

        JScrollPane detailsScroll = new JScrollPane(detailsTable);
        detailsScroll.setBorder(BorderFactory.createTitledBorder("Détails de la sélection"));
        detailsScroll.getViewport().setBackground(Color.WHITE);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, salesScroll, detailsScroll);
        splitPane.setDividerLocation(500);
        splitPane.setResizeWeight(0.6);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);*/
        add(salesScroll, BorderLayout.CENTER);
    }

    private void configureTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(StyleUtils.FONT_REGULAR);

        StyleUtils.applyTableHeaderStyle(table);

        table.setShowVerticalLines(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.setDefaultRenderer(Object.class, centerRenderer);
    }

    public void loadData() {
        salesModel.setRowCount(0);
        salesList = saleService.findAllSales(); // On charge depuis la BDD

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm");

        for (Sale s : salesList) {
            Object[] row = {
                    s.getSaleId(),
                    s.getSaleDate().format(formatter),
                    s.getCashier().getFamilyName() + " " + s.getCashier().getFirstName(),
                    s.getTotalPrice() + " DA"
            };
            salesModel.addRow(row);
        }
    }


    private void updateDetails(int viewRow) {
        detailsModel.setRowCount(0);

        if (viewRow >= 0) {
            int modelRow = salesTable.convertRowIndexToModel(viewRow);
            int saleId = (int) salesModel.getValueAt(modelRow, 0);

            List<SaleDetail> details = saleService.getSaleDetails(saleId);

            if (details != null) {
                for (SaleDetail detail : details) {
                    BigDecimal subTotal = detail.getUnitSoldPrice().multiply(new BigDecimal(detail.getQuantitySold()));
                    Object[] row = {
                            detail.getProduct().getProductName(),
                            detail.getUnitSoldPrice() + " DA",
                            detail.getQuantitySold(),
                            subTotal + " DA"
                    };
                    detailsModel.addRow(row);
                }
            }
        }
    }
}