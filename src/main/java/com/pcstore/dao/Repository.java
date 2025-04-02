package com.pcstore.Repository;

import java.util.List;
import java.util.Optional;

import com.pcstore.model.base.Entity;

/**
 * Interface định nghĩa các thao tác CRUD cơ bản cho tất cả các model
 * @param <T> kiểu Entity
 * @param <ID> kiểu của ID
 */
public interface Repository<T extends Entity, ID> {
    /**
     * Thêm một entity mới
     * @param entity đối tượng cần thêm
     * @return entity đã được thêm
     */
    T add(T entity);
    
    /**
     * Cập nhật thông tin entity
     * @param entity đối tượng cần cập nhật
     * @return entity đã được cập nhật
     */
    T update(T entity);
    
    /**
     * Xóa một entity theo ID
     * @param id ID của đối tượng cần xóa
     * @return true nếu xóa thành công, false nếu không tìm thấy
     */
    boolean delete(ID id);
    
    /**
     * Tìm entity theo ID
     * @param id ID của đối tượng cần tìm
     * @return Optional chứa entity nếu tìm thấy
     */
    Optional<T> findById(ID id);
    
    /**
     * Lấy tất cả các entity
     * @return Danh sách các entity
     */
    List<T> findAll();
    
    /**
     * Kiểm tra entity có tồn tại không
     * @param id ID của entity cần kiểm tra
     * @return true nếu entity tồn tại
     */
    boolean exists(ID id);
}