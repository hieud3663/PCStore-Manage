package com.pcstore.utils;

/**
 * Giao diện định nghĩa các phương thức cần thiết để một form 
 * có thể cập nhật ngôn ngữ khi locale thay đổi.
 */
public interface LanguageChangeable {
    /**
     * Cập nhật tất cả các thành phần UI để phản ánh ngôn ngữ hiện tại
     * Phương thức này sẽ được gọi khi ngôn ngữ thay đổi
     */
    void updateLanguage();
} 