package com.pcstore.model.base;

import java.io.Serializable;

/**
 * Interface cơ bản cho tất cả các entities
 */
public interface Entity extends Serializable {
    // Mỗi entity phải có phương thức lấy ID
    Object getId();
}