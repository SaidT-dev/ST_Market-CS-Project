package com.GUI;

import com.model.Sale;
import com.service.SaleService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SalesHistoryPanel extends JPanel {

    private SaleService saleService;

    private JTable salesTable;
    private DefaultTableModel salesModel;

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

        JScrollPane salesScroll = new JScrollPane(salesTable);
        salesScroll.setBorder(BorderFactory.createTitledBorder("Historique des Ventes"));
        salesScroll.getViewport().setBackground(Color.WHITE);

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
        salesList = saleService.findAllSales();

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
}
