package utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;

public class ExcelUtils {
    public static void checkLibrary() throws Exception {
        // Hàm này chỉ để kiểm tra xem thư viện có chạy không
        System.out.println("--- Đang kiểm tra thư viện Apache POI ---");
        FileInputStream fis = new FileInputStream(new File("excel/TestData.xlsx"));
        Workbook wb = new XSSFWorkbook(fis);
        Sheet sheet = wb.getSheetAt(0);
        String data = sheet.getRow(1).getCell(0).getStringCellValue();
        System.out.println("Dữ liệu đọc được từ file test: " + data);
        wb.close();
    }
}