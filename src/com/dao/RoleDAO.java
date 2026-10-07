package com.dao;

import com.db.DBConnectionManager;
import com.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoleDAO {
    private Connection connection;

    public RoleDAO() {
        this.connection = DBConnectionManager.getInstance().getSharedConnection();
    }

    public Role insertRole(Role role){
        String sql = role.getRoleId() > 0
                ? "INSERT INTO ROLE (roleId, roleName) VALUES (?, ?)"
                : "INSERT INTO ROLE (roleName) VALUES (?)";

        PreparedStatement ps = null;
        ResultSet rs = null;

        try{
            ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            if (role.getRoleId() > 0) {
                ps.setInt(1, role.getRoleId());
                ps.setString(2, role.getRoleName());
            } else {
                ps.setString(1, role.getRoleName());
            }

            int rowsAffected = ps.executeUpdate();

            if(rowsAffected > 0){
                rs = ps.getGeneratedKeys();
                if(rs.next()){
                    int generatedId = rs.getInt(1);
                    role.setRoleId(generatedId);
                }
            }
        } catch (SQLException e){
            System.err.println("Erreur lors de l'insertion du role: " + e.getMessage());
            e.printStackTrace();
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return role;
    }

    public Role getRoleById(int id) {
        String sql = "SELECT * FROM ROLE WHERE roleId = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Role role = null;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                role = extractRoleFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture du rôle (ID: " + id + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return role;
    }

    public List<Role> getAllRole() {
        String sql = "SELECT * FROM ROLE";
        PreparedStatement ps = null;
        ResultSet rs = null;
        List<Role> roles = new ArrayList<>();

        try {
            ps = connection.prepareStatement(sql);
            rs = ps.executeQuery();

            while (rs.next()) {
                roles.add(extractRoleFromResultSet(rs));
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la lecture de tous les rôles: " + e.getMessage());
        } finally {
            DBConnectionManager.close(rs, ps);
        }
        return roles;
    }

    private Role extractRoleFromResultSet(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("roleId"),
                rs.getString("roleName")
        );
    }

    public boolean updateRole(Role role) {
        String sql = "UPDATE ROLE SET roleName = ? WHERE roleId = ?";
        PreparedStatement ps = null;
        boolean updated = false;

        try {
            ps = connection.prepareStatement(sql);

            ps.setString(1, role.getRoleName());
            ps.setInt(2, role.getRoleId());

            if (ps.executeUpdate() > 0) {
                updated = true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la mise à jour du rôle (ID: " + role.getRoleId() + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(null, ps);
        }
        return updated;
    }

    public boolean deleteRole(int id) {
        String sql = "DELETE FROM ROLE WHERE roleId = ?";
        PreparedStatement ps = null;
        boolean deleted = false;

        try {
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            if (ps.executeUpdate() > 0) {
                deleted = true;
            }

        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la suppression du rôle (ID: " + id + "): " + e.getMessage());
        } finally {
            DBConnectionManager.close(null, ps);
        }
        return deleted;
    }
}