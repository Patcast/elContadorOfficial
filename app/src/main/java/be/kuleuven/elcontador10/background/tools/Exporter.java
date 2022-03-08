package be.kuleuven.elcontador10.background.tools;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import org.apache.poi.hssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public enum Exporter {
    INSTANCE;
    String TAG = "Excel Export";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public File createFile(String fileName, List<ProcessedTransaction> processed, List<ScheduledTransaction> scheduled,
                           long startingBalance, long cashIn, long cashOut, long currentBalance, long receivables,
                           long payables, long scheduleBalance) {
        HSSFWorkbook workbook = formatExcel(processed, scheduled, startingBalance, cashIn, cashOut,
                currentBalance, receivables, payables, scheduleBalance);
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

    private HSSFWorkbook formatExcel(List<ProcessedTransaction> processed, List<ScheduledTransaction> scheduled,
                                     long startingBalance, long cashIn, long cashOut, long currentBalance, long receivables,
                                     long payables, long scheduleBalance) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFCellStyle styleCurrency = workbook.createCellStyle();
        styleCurrency.setDataFormat((short) 8);

        summarySheet(workbook, styleCurrency, startingBalance, cashIn, cashOut, currentBalance, receivables, payables, scheduleBalance);
        transactionsSheet(workbook, styleCurrency, processed);

        return workbook;
    }

    private void summarySheet(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, long startingBalance, long cashIn, long cashOut, long currentBalance, long receivables,
                              long payables, long scheduleBalance) {
        HSSFSheet sheet = workbook.createSheet("Summary");

        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Cash in the beginning:");
        cell = row.createCell(1);
        cell.setCellValue(startingBalance);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("Sum of cash in:");
        cell = row.createCell(1);
        cell.setCellValue(cashIn);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("Sum of cash out:");
        cell = row.createCell(1);
        cell.setCellValue(cashOut);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Cash at end:");
        cell = row.createCell(1);
        cell.setCellValue(currentBalance);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue("Sum of receivables:");
        cell = row.createCell(1);
        cell.setCellValue(receivables);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("Sum of payables:");
        cell = row.createCell(1);
        cell.setCellValue(payables);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(6);
        cell = row.createCell(0);
        cell.setCellValue("Schedule balance:");
        cell = row.createCell(1);
        cell.setCellValue(scheduleBalance);
        cell.setCellStyle(styleCurrency);
    }

    private void transactionsSheet(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, List<ProcessedTransaction> processed) {
        HSSFSheet sheet = workbook.createSheet("Transactions");


    }
}
