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
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public enum Exporter {
    INSTANCE;
    String TAG = "Excel Export";

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    @RequiresApi(api = Build.VERSION_CODES.O)
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

    // todo incude categories
    /**
     * Date | title | totalAmount | Stakeholder | Registered by | Notes
     * -----|-------|-------------|-------------|---------------|-------
     *      |       |             |             |               |
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void transactionsSheet(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, List<ProcessedTransaction> processed) {
        HSSFSheet sheet = workbook.createSheet("Transactions");

        // income
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);

        cell.setCellValue("Income");

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("Date & Time");
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell = row.createCell(2);
        cell.setCellValue("Amount ($)");
        cell = row.createCell(3);
        cell.setCellValue("Stakeholder");
        cell = row.createCell(4);
        cell.setCellValue("Registered by");
        cell = row.createCell(5);
        cell.setCellValue("Notes");

        List<ProcessedTransaction> income = processed.stream()
                .filter(i -> !i.getIsDeleted())
                .filter(i -> i.getTotalAmount() > 0)
                .collect(Collectors.toList());

        int counter = 2;    // start from row 2

        for (ProcessedTransaction transaction : income) {
            row = sheet.createRow(counter);

            cell = row.createCell(0);
            cell.setCellValue(DatabaseDatesFunctions.INSTANCE.timestampToStringDetailed(transaction.getDueDate()));

            cell = row.createCell(1);
            cell.setCellValue(transaction.getTitle());

            cell = row.createCell(2);
            cell.setCellValue(transaction.getTotalAmount());
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(3);
            cell.setCellValue(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeInt()));

            cell = row.createCell(4);
            cell.setCellValue(transaction.getRegisteredBy());

            cell = row.createCell(5);
            cell.setCellValue(transaction.getNotes());

            counter++;
        }

        // expenses
        row = sheet.getRow(0);
        cell = row.createCell(7);

        cell.setCellValue("Expenses");

        row = sheet.getRow(1);
        cell = row.createCell(7);
        cell.setCellValue("Date & Time");
        cell = row.createCell(8);
        cell.setCellValue("Title");
        cell = row.createCell(9);
        cell.setCellValue("Amount ($)");
        cell = row.createCell(10);
        cell.setCellValue("Stakeholder");
        cell = row.createCell(11);
        cell.setCellValue("Registered by");
        cell = row.createCell(12);
        cell.setCellValue("Notes");

        List<ProcessedTransaction> expenses = processed.stream()
                .filter(i -> !i.getIsDeleted())
                .filter(i -> i.getTotalAmount() < 0)
                .collect(Collectors.toList());

        counter = 2;    // start from row 2

        for (ProcessedTransaction transaction : expenses) {
            row = sheet.getRow(counter);

            cell = row.createCell(7);
            cell.setCellValue(DatabaseDatesFunctions.INSTANCE.timestampToStringDetailed(transaction.getDueDate()));

            cell = row.createCell(9);
            cell.setCellValue(transaction.getTitle());

            cell = row.createCell(9);
            cell.setCellValue(transaction.getTotalAmount());
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(10);
            cell.setCellValue(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeInt()));

            cell = row.createCell(11);
            cell.setCellValue(transaction.getRegisteredBy());

            cell = row.createCell(12);
            cell.setCellValue(transaction.getNotes());

            counter++;
        }
    }
}
