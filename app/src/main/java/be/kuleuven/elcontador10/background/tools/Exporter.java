package be.kuleuven.elcontador10.background.tools;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public enum Exporter {
    INSTANCE;
    String TAG = "Excel Export";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public File createFile(String fileName) {
        HSSFWorkbook workbook = formatExcel();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), fileName);
        FileOutputStream fileOutputStream = null;

        try {
            fileOutputStream = new FileOutputStream(file);
            workbook.write(fileOutputStream);
            Log.e(TAG, "Write successful!");
        } catch (IOException e) {
            Log.e(TAG, "I/O exception: ", e);
        } catch (Exception e) {
            Log.e(TAG, "Failed to save file due to: ", e);
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        return file;
    }

    private HSSFWorkbook formatExcel() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("Transactions");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Test");

        return workbook;
    }
}
