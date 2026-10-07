package com.dao;

import com.db.DBConnectionManager;
import com.model.Employe;
import com.model.Role;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EmployeDAO {

    private Connection connection;

    public EmployeDAO() {
        this.connection = DBConnectionManager.getInstance().getSharedConnection();
    }


    public Employe insertEmploye(Employe employe) {
        String sql = "INSERT INTO EMPLOYE (firstName, familyName, birthDate, address, phoneNumber, passwordHash, roleId, username) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, employe.getFirstName());
            ps.setString(2, employe.getFamilyName());
            ps.setDate(3, Date.valueOf(employe.getBirthDate()));
            ps.setString(4, employe.getAddress());
            ps.setString(5, employe.getPhoneNumber());
            ps.setString(6, employe.getPasswordHash());
            ps.setInt(7, employe.getRole().getRoleId());
            ps.setString(8, employe.getUsername());

            int rowsAffected = ps.executeUpdate();

            if(rowsAffected > 0){
                rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int generatedId = rs.getInt(1);
                    employe.setEmployeId(generatedId);
                }
            }
        } catch (SQLException e){
            System.err.println("Erreur SQL lors de l'insertion de l'employé: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return employe;
    }

    public Employe getEmployeById(int id) {
        String sql = "SELECT e.*, r.roleName FROM EMPLOYE e JOIN ROLE r ON e.roleId = r.roleId WHERE e.employeId = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Employe employe = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                employe = extractEmployeFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de l'employé (ID: " + id + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return employe;
    }

    public Employe getEmployeByUsername(String username) {

        String sql = "SELECT e.*, r.roleName FROM EMPLOYE e JOIN ROLE r ON e.roleId = r.roleId WHERE e.username = ?";

        PreparedStatement ps = null;
        ResultSet rs = null;
        Employe employe = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setString(1, username);

            rs = ps.executeQuery();

            if (rs.next()) {
                employe = extractEmployeFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de l'employé (username: " + username + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }

        return employe;
    }

    public List<Employe> getAllEmployes() {
        String sql = "SELECT e.*, r.roleName FROM EMPLOYE e JOIN ROLE r ON e.roleId = r.roleId";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Employe> employes = new ArrayList<>();

        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                employes.add(extractEmployeFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de tous les employés: " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return employes;
    }

    private Employe extractEmployeFromResultSet(ResultSet rs) throws SQLException {
        int roleId = rs.getInt("roleId");
        String roleName = rs.getString("roleName");
        Role role = new Role(roleId, roleName);

        return new Employe(
                rs.getInt("employeId"),
                rs.getString("firstName"),
                rs.getString("familyName"),
                rs.getDate("birthDate").toLocalDate(),
                rs.getString("address"),
                rs.getString("phoneNumber"),
                rs.getString("passwordHash"),
                role,
                rs.getString("username")
        );
    }

    public boolean updateEmploye(Employe employe) {
        String sql = "UPDATE EMPLOYE SET firstName=?, familyName=?, birthDate=?, address=?, phoneNumber=?, passwordHash=?, roleId=? WHERE employeId=?";
        PreparedStatement ps = null;
        boolean updated = false;

        try {
            ps = connection.prepareStatement(sql);

            ps.setString(1, employe.getFirstName());
            ps.setString(2, employe.getFamilyName());
            ps.setDate(3, Date.valueOf(employe.getBirthDate()));
            ps.setString(4, employe.getAddress());
            ps.setString(5, employe.getPhoneNumber());
            ps.setString(6, employe.getPasswordHash());
            ps.setInt(7, employe.getRole().getRoleId());
            ps.setInt(8, employe.getEmployeId());

            if (ps.executeUpdate() > 0) {
                updated = true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour de l'employé (ID: " + employe.getEmployeId() + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(null, ps);
        }
        return updated;
    }

    public boolean deleteEmploye(int id) {
        String sqlSaleDetail = "DELETE sd FROM SALE_DETAIL sd JOIN SALE s ON sd.saleId = s.saleId WHERE s.cashierId = ?";
        String sqlTicket = "DELETE t FROM TICKET t JOIN SALE s ON t.saleId = s.saleId WHERE s.cashierId = ?";
        String sqlSale = "DELETE FROM SALE WHERE cashierId = ?";
        String sqlEmploye = "DELETE FROM EMPLOYE WHERE employeId = ?";
        PreparedStatement psSaleDetail = null;
        PreparedStatement psTicket = null;
        PreparedStatement psSale = null;
        PreparedStatement psEmploye = null;
        boolean deleted = false;

        try {
            connection.setAutoCommit(false);

            psSaleDetail = connection.prepareStatement(sqlSaleDetail);
            psSaleDetail.setInt(1, id);
            psSaleDetail.executeUpdate();

            psTicket = connection.prepareStatement(sqlTicket);
            psTicket.setInt(1, id);
            psTicket.executeUpdate();

            psSale = connection.prepareStatement(sqlSale);
            psSale.setInt(1, id);
            psSale.executeUpdate();

            psEmploye = connection.prepareStatement(sqlEmploye);
            psEmploye.setInt(1, id);

            if (psEmploye.executeUpdate() > 0) {
                connection.commit();
                deleted = true;
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression de l'employé (ID: " + id + "): " + e.getMessage());
            try {
                if (connection != null) connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            DBConnectionManager.close(null, psSaleDetail);
            DBConnectionManager.close(null, psTicket);
            DBConnectionManager.close(null, psSale);
            DBConnectionManager.close(null, psEmploye);
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return deleted;
    }
}