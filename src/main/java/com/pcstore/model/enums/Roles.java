package com.pcstore.model.enums;

public enum Roles {
    ADMIN(1, "Admin"),
    MANAGER(2, "Manager"),
    SALES(3, "Sales"),
    STOCK(4, "Stock"),
    REPAIR(5, "Repair");

    private final int id;
    private final String roleName;

    Roles(int id, String roleName) {
        this.id = id;
        this.roleName = roleName;
    }

    public int getId() {
        return id;
    }

    public String getRoleName() {
        return roleName;
    }
    
    /**
     * Tìm role theo id
     * @param id ID của role
     * @return Role tương ứng hoặc null nếu không tìm thấy
     */
    public static Roles getById(int id) {
        for (Roles role : values()) {
            if (role.getId() == id) {
                return role;
            }
        }
        return null;
    }
    
    /**
     * Tìm role theo tên
     * @param roleName Tên của role
     * @return Role tương ứng hoặc null nếu không tìm thấy
     */
    public static Roles getByName(String roleName) {
        for (Roles role : values()) {
            if (role.getRoleName().equals(roleName)) {
                return role;
            }
        }
        return null;
    }
}
