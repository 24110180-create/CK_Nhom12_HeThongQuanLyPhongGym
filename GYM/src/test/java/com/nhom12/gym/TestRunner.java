package com.nhom12.gym;

import java.io.PrintWriter;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;
import org.junit.platform.launcher.listeners.TestExecutionSummary;

/**
 * Trình chạy Kiểm thử Tự động JUnit 5 độc lập cho Hệ Thống FitLife Gym
 * Hỗ trợ chạy toàn bộ Test Suites từ dòng lệnh hoặc IDE mà không phụ thuộc cấu hình Maven phức tạp
 */
public class TestRunner {

    public static void main(String[] args) {
        LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
                .selectors(DiscoverySelectors.selectPackage("com.nhom12.gym"))
                .build();

        Launcher launcher = LauncherFactory.create();
        SummaryGeneratingListener listener = new SummaryGeneratingListener();
        launcher.registerTestExecutionListeners(listener);
        launcher.execute(request);

        TestExecutionSummary summary = listener.getSummary();
        long duration = summary.getTimeFinished() - summary.getTimeStarted();

        System.out.println("\n========================================================");
        System.out.println("      KẾT QUẢ KIỂM THỬ TỰ ĐỘNG JUNIT 5 (FITLIFE GYM)    ");
        System.out.println("========================================================");
        System.out.println(" Tổng số ca kiểm thử phát hiện (Found):    " + summary.getTestsFoundCount());
        System.out.println(" Số ca kiểm thử THÀNH CÔNG (Passed):       " + summary.getTestsSucceededCount());
        System.out.println(" Số ca kiểm thử THẤT BẠI (Failed):         " + summary.getTestsFailedCount());
        System.out.println(" Số ca kiểm thử BỎ QUA (Skipped):          " + summary.getTestsSkippedCount());
        System.out.println(" Tổng thời gian thực thi:                  " + duration + " ms");
        System.out.println("========================================================");

        if (summary.getTestsFailedCount() > 0) {
            System.err.println("\nDANH SÁCH CÁC CA KIỂM THỬ THẤT BẠI:");
            summary.printFailuresTo(new PrintWriter(System.err));
            System.exit(1);
        } else {
            System.out.println(" >>> TẤT CẢ CÁC CA KIỂM THỬ ĐÃ ĐẠT 100% (ALL TESTS PASSED) <<<\n");
        }
    }
}
