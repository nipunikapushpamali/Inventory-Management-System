
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InventoryFrame extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField tfName, tfCode, tfQuantity, tfPrice, tfSupplier, tfSearch;
    private JButton addButton, updateButton, deleteButton, clearButton;

    public InventoryFrame() {
        setTitle("Inventory Management");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        
        JPanel topPanel = new JPanel(new BorderLayout());
        tfSearch = new JTextField();
        JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search by Name or Code:"), BorderLayout.WEST);
        topPanel.add(tfSearch, BorderLayout.CENTER);
        topPanel.add(searchButton, BorderLayout.EAST);

        JPanel inputPanel = new JPanel(new GridLayout(6, 2));
        tfName = new JTextField();
        tfCode = new JTextField();
        tfQuantity = new JTextField();
        tfPrice = new JTextField();
        tfSupplier = new JTextField();

        inputPanel.add(new JLabel("Item Name:")); inputPanel.add(tfName);
        inputPanel.add(new JLabel("Item Code:")); inputPanel.add(tfCode);
        inputPanel.add(new JLabel("Quantity:")); inputPanel.add(tfQuantity);
        inputPanel.add(new JLabel("Price/Unit:")); inputPanel.add(tfPrice);
        inputPanel.add(new JLabel("Supplier:")); inputPanel.add(tfSupplier);
        inputPanel.add(new JLabel("")); inputPanel.add(new JLabel("")); // filler

        model = new DefaultTableModel(new String[]{"ID", "Item Name", "Code", "Qty", "Price", "Supplier"}, 0);
        table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        addButton = new JButton("Add");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
        clearButton = new JButton("Clear");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        mainPanel.add(topPanel);      
        mainPanel.add(inputPanel);   
        mainPanel.add(scrollPane);    
        mainPanel.add(buttonPanel);   

        add(mainPanel);

        loadData();

        addButton.addActionListener(e -> addItem());
        updateButton.addActionListener(e -> updateItem());
        deleteButton.addActionListener(e -> deleteItem());
        clearButton.addActionListener(e -> clearFields());
        searchButton.addActionListener(e -> searchItems());
        table.getSelectionModel().addListSelectionListener(e -> loadSelectedRow());

        setVisible(true);
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM inventory")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getString("item_code"),
                    rs.getInt("quantity"),
                    rs.getDouble("price_per_unit"),
                    rs.getString("supplier_details")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void addItem() {
        String name = tfName.getText();
        String code = tfCode.getText();
        String qty = tfQuantity.getText();
        String price = tfPrice.getText();
        String supplier = tfSupplier.getText();

        if (name.isEmpty() || code.isEmpty() || qty.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "");
             PreparedStatement ps = conn.prepareStatement("INSERT INTO inventory (item_name, item_code, quantity, price_per_unit, supplier_details) VALUES (?, ?, ?, ?, ?)")) {

            ps.setString(1, name);
            ps.setString(2, code);
            ps.setInt(3, Integer.parseInt(qty));
            ps.setDouble(4, Double.parseDouble(price));
            ps.setString(5, supplier);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item added.");
            loadData();
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void updateItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        int id = (int) model.getValueAt(selectedRow, 0);
        String name = tfName.getText();
        String code = tfCode.getText();
        String qty = tfQuantity.getText();
        String price = tfPrice.getText();
        String supplier = tfSupplier.getText();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "");
             PreparedStatement ps = conn.prepareStatement("UPDATE inventory SET item_name=?, item_code=?, quantity=?, price_per_unit=?, supplier_details=? WHERE id=?")) {

            ps.setString(1, name);
            ps.setString(2, code);
            ps.setInt(3, Integer.parseInt(qty));
            ps.setDouble(4, Double.parseDouble(price));
            ps.setString(5, supplier);
            ps.setInt(6, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item updated.");
            loadData();
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void deleteItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) return;

        int id = (int) model.getValueAt(selectedRow, 0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "");
             PreparedStatement ps = conn.prepareStatement("DELETE FROM inventory WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Item deleted.");
            loadData();
            clearFields();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void searchItems() {
        String keyword = tfSearch.getText();
        model.setRowCount(0);

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/inventory_db", "root", "");
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM inventory WHERE item_name LIKE ? OR item_code LIKE ?")) {

            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("item_name"),
                    rs.getString("item_code"),
                    rs.getInt("quantity"),
                    rs.getDouble("price_per_unit"),
                    rs.getString("supplier_details")
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void loadSelectedRow() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow >= 0) {
            tfName.setText(model.getValueAt(selectedRow, 1).toString());
            tfCode.setText(model.getValueAt(selectedRow, 2).toString());
            tfQuantity.setText(model.getValueAt(selectedRow, 3).toString());
            tfPrice.setText(model.getValueAt(selectedRow, 4).toString());
            tfSupplier.setText(model.getValueAt(selectedRow, 5).toString());
        }
    }

    private void clearFields() {
        tfName.setText("");
        tfCode.setText("");
        tfQuantity.setText("");
        tfPrice.setText("");
        tfSupplier.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        new InventoryFrame();
    }
}
