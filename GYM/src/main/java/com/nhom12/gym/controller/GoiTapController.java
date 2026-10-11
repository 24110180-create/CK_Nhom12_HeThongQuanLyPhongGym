package com.nhom12.gym.controller;

import com.nhom12.gym.dao.GoiTapDAO;
import com.nhom12.gym.entity.GoiTap;
import java.util.List;

/**
 * GoiTapController - Điều phối quản lý và cấu hình gói tập
 */
public class GoiTapController {
    private final GoiTapDAO goiTapDAO;

    public GoiTapController() {
        this.goiTapDAO = new GoiTapDAO();
    }

    public List<GoiTap> layDanhSachGoiTap() {
        return goiTapDAO.layTatCa();
    }

    public boolean themGoiTap(GoiTap gt) {
        return goiTapDAO.them(gt);
    }

    public boolean capNhatGoiTap(GoiTap gt) {
        return goiTapDAO.capNhat(gt);
    }
}
