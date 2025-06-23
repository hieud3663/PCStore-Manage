package com.pcstore.controller;

import com.pcstore.model.Category;
import com.pcstore.service.CategoryService;
import com.pcstore.view.CategoryFormNew;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class CategoryController {
    private final CategoryFormNew view;
    private final CategoryService service;
    private final DefaultTableModel tableModel;

    public CategoryController(CategoryFormNew view, CategoryService service) {
        this.view = view;
        this.service = service;
        this.tableModel = (DefaultTableModel) view.getTableList().getModel(); // Sửa ở đây
        initController();
        loadCategoryTable();
    }

    private void initController() {
        view.getBtnAddCategory().addActionListener(e -> addCategory());
        view.getBtnUpdateCategory().addActionListener(e -> updateCategory());
        view.getBtnDeleteCategory().addActionListener(e -> deleteCategory());
        view.getBtnRefreshCategory().addActionListener(e -> refreshForm());
        view.getTableList().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                int row = view.getTableList().getSelectedRow();
                if (row >= 0) {
                    view.getTxtCategoryCode().setText(getString(row, 0));
                    view.getTxtCategoryName().setText(getString(row, 1));
                    view.getTxtDescription().setText(getString(row, 2));
                    view.getCbStatus().setSelectedItem(getString(row, 3));
                    view.getTxtDateCreate().setText(getString(row, 4));
                }
            }
        });
    }

    private String getString(int row, int col) {
        Object val = tableModel.getValueAt(row, col);
        return val == null ? "" : val.toString();
    }

    private void loadCategoryTable() {
        List<Category> categories = service.getAllCategories();
        tableModel.setRowCount(0); // Xóa dữ liệu cũ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Category c : categories) {
            String createdAtStr = "";
            if (c.getCreatedAt() != null) {
                createdAtStr = c.getCreatedAt().format(formatter);
            }
            tableModel.addRow(new Object[]{
                c.getCategoryId(),
                c.getCategoryName(),
                c.getDescription(),
                c.getStatus() == null ? "Hoạt Động" : c.getStatus(), // Lấy trạng thái thực tế
                createdAtStr
            });
        }
    }

    private void addCategory() {
        try {
            String newId = service.generateCategoryId();
            view.getTxtCategoryCode().setText(newId);

            String name = view.getTxtCategoryName().getText().trim();
            String desc = view.getTxtDescription().getText().trim();
            String status = view.getCbStatus().getSelectedItem().toString();

            Category category = new Category(newId, name, desc, status);

            service.addCategory(category);
            loadCategoryTable();
            refreshForm();
            JOptionPane.showMessageDialog(view, "Thêm danh mục thành công!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi thêm danh mục: " + ex.getMessage());
        }
    }

    private void updateCategory() {
        int row = view.getTableList().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, "Vui lòng chọn danh mục cần cập nhật!");
            return;
        }
        try {
            String id = view.getTxtCategoryCode().getText().trim();
            String name = view.getTxtCategoryName().getText().trim();
            String desc = view.getTxtDescription().getText().trim();
            String status = view.getCbStatus().getSelectedItem().toString();

            List<Category> categories = service.getAllCategories();
            Category old = categories.stream()
                    .filter(c -> c.getCategoryId().equals(id))
                    .findFirst().orElse(null);

            if (old == null) {
                JOptionPane.showMessageDialog(view, "Không tìm thấy danh mục để cập nhật!");
                return;
            }

            Category updated = new Category(id, name, desc, status, old.getCreatedAt(), java.time.LocalDateTime.now());
            service.updateCategory(updated);

            loadCategoryTable();
            JOptionPane.showMessageDialog(view, "Cập nhật thành công!");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, "Lỗi khi cập nhật: " + ex.getMessage());
        }
    }

    private void deleteCategory() {
        try {
            String id = view.getTxtCategoryCode().getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Vui lòng chọn danh mục để xóa!");
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(view, "Bạn có chắc muốn xóa danh mục này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean deleted = service.deleteCategory(id);
                if (deleted) {
                    JOptionPane.showMessageDialog(view, "Xóa danh mục thành công!");
                    loadCategoryTable();
                    refreshForm();
                } else {
                    JOptionPane.showMessageDialog(view, "Không thể xóa danh mục!");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(view, "Lỗi xóa danh mục: " + ex.getMessage());
        }
    }

    private void refreshForm() {
        view.getTxtCategoryCode().setText("");
        view.getTxtCategoryName().setText("");
        view.getTxtDescription().setText("");
        view.getTxtDateCreate().setText("");
        view.getTableList().clearSelection();
    }

}