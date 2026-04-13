package com.luvina.la.config;
/**
 * Copyright(C) [2026] [Luvina Software Company]
 *
 * [MessageConstants.java], [Apr ,2026] [ntlong]
 */

/**
 * Định nghĩa mã lỗi và mã thông báo thành công cho hệ thống.
 * author: [ntlong].
 */
public class MessageConstants {

    private MessageConstants() {
    }

    // ── Code lỗi ──
    public static final String ER001 = "ER001"; // Không nhập (required field)
    public static final String ER002 = "ER002"; // Không chọn (required select)
    public static final String ER003 = "ER003"; // Đã tồn tại
    public static final String ER004 = "ER004"; // Không tồn tại
    public static final String ER005 = "ER005"; // Sai format
    public static final String ER006 = "ER006"; // Vượt quá maxlength
    public static final String ER007 = "ER007"; // Độ dài ngoài khoảng
    public static final String ER008 = "ER008"; // Phải là ký tự 1 byte
    public static final String ER009 = "ER009"; // Phải là kana
    public static final String ER010 = "ER010"; // Phải là hiragana
    public static final String ER011 = "ER011"; // Ngày không hợp lệ
    public static final String ER012 = "ER012"; // Ngày hết hạn < ngày cấp chứng chỉ
    public static final String ER013 = "ER013"; // User không tồn tại (biên tập)
    public static final String ER014 = "ER014"; // User không tồn tại (xóa)
    public static final String ER015 = "ER015"; // Lỗi thao tác database
    public static final String ER016 = "ER016"; // Sai tên đăng nhập hoặc mật khẩu
    public static final String ER017 = "ER017"; // Mật khẩu xác nhận không đúng
    public static final String ER018 = "ER018"; // Phải là số halfsize
    public static final String ER019 = "ER019"; // Tên đăng nhập không đúng định dạng
    public static final String ER020 = "ER020"; // Không thể xóa user admin
    public static final String ER021 = "ER021"; // Thứ tự sắp xếp phải là ASC/DESC
    public static final String ER022 = "ER022"; // Page not found
    public static final String ER023 = "ER023"; // Lỗi hệ thống

    // ── Code thành công ──
    public static final String MSG001 = "MSG001"; // Đăng ký thành công
    public static final String MSG002 = "MSG002"; // Cập nhật thành công
    public static final String MSG003 = "MSG003"; // Xóa thành công
    public static final String MSG004 = "MSG004"; // Xác nhận trước khi xóa
    public static final String MSG005 = "MSG005"; // Không tìm thấy user
}
