package com.nhom12.gym.controller;

import com.nhom12.gym.service.CheckInService;
import com.nhom12.gym.service.CheckInService.CheckInResult;

/**
 * CheckInController - Điều phối luồng xử lý quét mã QR / Barcode Check-in
 */
public class CheckInController {
    private final CheckInService checkInService;

    public CheckInController() {
        this.checkInService = new CheckInService();
    }

    public CheckInResult xuLyCheckIn(String rawBarcode, String branch) {
        return checkInService.thucHienCheckIn(rawBarcode, branch);
    }

    public CheckInResult xuLyCheckIn(String rawBarcode) {
        return checkInService.thucHienCheckIn(rawBarcode, "FitLife Central");
    }
}
