package com.pcstore.controller;

import com.pcstore.model.Category;
import com.pcstore.service.CategoryService;
import com.pcstore.view.CategoryFormNew;
import com.pcstore.utils.ErrorMessage;

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
                    String id = getString(row, 0);
                    view.getTxtCategoryCode().setText(id);
                    view.getTxtCategoryName().setText(getString(row, 1));
                    // Lấy mô tả đầy đủ từ service thay vì từ bảng
                    List<Category> categories = service.getAllCategories();
                    Category selected = categories.stream()
                            .filter(c -> c.getCategoryId().equals(id))
                            .findFirst().orElse(null);
                    if (selected != null) {
                        String desc = selected.getDescription();
                        view.getTxtDescription().setText(desc == null ? "" : desc.trim());
                    } else {
                        view.getTxtDescription().setText("");
                    }
                    view.getCbStatus().setSelectedItem(getString(row, 3));
                    view.getTxtDateCreate().setText(getString(row, 4));
                    view.getBtnUpdateCategory().setEnabled(true);
                    view.getBtnDeleteCategory().setEnabled(true);
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
        String name = view.getTxtCategoryName().getText().trim();
        String desc = view.getTxtDescription().getText().trim();
        String status = view.getCbStatus().getSelectedItem() != null ? view.getCbStatus().getSelectedItem().toString() : "";

        // Kiểm tra thông tin bắt buộc
        if (name.isEmpty() || desc.isEmpty() || status.isEmpty()) {
            JOptionPane.showMessageDialog(view,
                ErrorMessage.CATEGORY_ADD_REQUIRED.get(),
                ErrorMessage.INFO_TITLE.get(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Kiểm tra trùng tên danh mục
        List<Category> categories = service.getAllCategories();
        boolean isDuplicate = categories.stream()
                .anyMatch(c -> c.getCategoryName().equalsIgnoreCase(name));
        if (isDuplicate) {
            JOptionPane.showMessageDialog(view,
                ErrorMessage.CATEGORY_NAME_EXISTS.get(),
                ErrorMessage.INFO_TITLE.get(),
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Sinh mã danh mục mới
            String newId = service.generateCategoryId();
            Category category = new Category(newId, name, desc, status);

            service.addCategory(category); // Không gán vào boolean
            loadCategoryTable();
            refreshForm();
            JOptionPane.showMessageDialog(view, ErrorMessage.CATEGORY_ADD_SUCCESS.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, ErrorMessage.CATEGORY_ADD_ERROR.format(ex.getMessage()));
        }
    }

    private void updateCategory() {
        int row = view.getTableList().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(view, ErrorMessage.CATEGORY_SELECT_UPDATE.get());
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
                JOptionPane.showMessageDialog(view, ErrorMessage.CATEGORY_NOT_FOUND_UPDATE.get());
                return;
            }

            Category updated = new Category(id, name, desc, status, old.getCreatedAt(), java.time.LocalDateTime.now());
            service.updateCategory(updated);

            loadCategoryTable();
            JOptionPane.showMessageDialog(view, ErrorMessage.CATEGORY_UPDATE_SUCCESS.get());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, ErrorMessage.CATEGORY_UPDATE_ERROR.format(ex.getMessage()));
        }
    }

    private void deleteCategory() {
        String id = view.getTxtCategoryCode().getText().trim();
        int row = view.getTableList().getSelectedRow();

        // Kiểm tra đã chọn danh mục chưa
        if (id.isEmpty() || row < 0) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.CATEGORY_SELECT_DELETE.get(),
                    ErrorMessage.INFO_TITLE.get(),
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Kiểm tra liên kết sản phẩm
        if (service.hasProducts(id)) {
            JOptionPane.showMessageDialog(view,
                    ErrorMessage.CATEGORY_DELETE_CONSTRAINT.get(),
                    ErrorMessage.ERROR_TITLE.get(),
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Xác nhận xóa
        int confirm = JOptionPane.showConfirmDialog(view,
                ErrorMessage.CATEGORY_DELETE_CONFIRM.get(),
                ErrorMessage.CONFIRM_TITLE.get(),
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean deleted = service.deleteCategory(id);
                if (deleted) {
                    loadCategoryTable();
                    refreshForm();
                    JOptionPane.showMessageDialog(view,
                            ErrorMessage.CATEGORY_DELETE_SUCCESS.get(),
                            ErrorMessage.INFO_TITLE.get(),
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view,
                            ErrorMessage.CATEGORY_DELETE_FAIL.get(),
                            ErrorMessage.ERROR_TITLE.get(),
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(view,
                        ErrorMessage.CATEGORY_DELETE_ERROR.format(ex.getMessage()),
                        ErrorMessage.ERROR_TITLE.get(),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshForm() {
        view.getTxtCategoryCode().setText("");
        view.getTxtCategoryName().setText("");
        view.getTxtDescription().setText("");
        view.getTxtDateCreate().setText("");
        view.getTableList().clearSelection();
        // Disable nút cập nhật, xóa khi không chọn dòng nào
        view.getBtnUpdateCategory().setEnabled(false);
        view.getBtnDeleteCategory().setEnabled(false);
    }

   
}