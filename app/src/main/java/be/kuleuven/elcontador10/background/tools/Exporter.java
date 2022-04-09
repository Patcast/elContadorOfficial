package be.kuleuven.elcontador10.background.tools;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.Timestamp;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Font;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

import be.kuleuven.elcontador10.background.database.Caching;
import be.kuleuven.elcontador10.background.model.ProcessedTransaction;
import be.kuleuven.elcontador10.background.model.contract.ScheduledTransaction;

public enum Exporter {
    INSTANCE;
    String TAG = "Excel Export";

    @RequiresApi(api = Build.VERSION_CODES.O)
    public File createFile(String monthYear, List<ProcessedTransaction> processed, List<ScheduledTransaction> scheduled,
                           long startingBalance, long cashIn, long cashOut, long currentBalance, long receivables,
                           long payables, long scheduleBalance) {
        HSSFWorkbook workbook = formatExcel(monthYear, processed, scheduled, startingBalance, cashIn, cashOut,
                currentBalance, receivables, payables, scheduleBalance);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), monthYear + ".xls");
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
    private HSSFWorkbook formatExcel(String monthYear, List<ProcessedTransaction> processed, List<ScheduledTransaction> scheduled,
                                     long startingBalance, long cashIn, long cashOut, long currentBalance, long receivables,
                                     long payables, long scheduleBalance) {
        HSSFWorkbook workbook = new HSSFWorkbook();

        HSSFCellStyle styleCurrency = workbook.createCellStyle();
        styleCurrency.setDataFormat((short) 8);

        HSSFCellStyle styleTitle = workbook.createCellStyle();
        HSSFFont font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontHeight((short) (20 * 20));
        styleTitle.setFont(font);

        HSSFCellStyle styleBold = workbook.createCellStyle();
        font = workbook.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        styleBold.setFont(font);

        summarySheet(workbook, monthYear, styleCurrency, styleTitle, styleBold,
                startingBalance, cashIn, cashOut, currentBalance, receivables, payables, scheduleBalance);
        cashIn(workbook, styleCurrency, styleBold, processed);
        cashOut(workbook, styleCurrency, styleBold, processed);
        List<ScheduledTransaction> late = scheduled.stream()
                .filter(t -> t.getStatus() == ScheduledTransaction.ScheduledTransactionStatus.LATE)
                .collect(Collectors.toList());
        lateReceivables(workbook, styleCurrency, styleBold, late);
        latePayables(workbook, styleCurrency, styleBold, late);

        return workbook;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void summarySheet(HSSFWorkbook workbook, String monthYear, HSSFCellStyle styleCurrency, HSSFCellStyle styleTitle, HSSFCellStyle styleBold,
                              long startingBalance, long cashIn, long cashOut, long currentBalance, long receivables,
                              long payables, long scheduleBalance) {
        HSSFSheet sheet = workbook.createSheet("Summary");

        sheet.setColumnWidth(0, (int) (30 * 1.14388) * 256);
        sheet.setColumnWidth(1, (int) (15 * 1.14388) * 256);

        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) -1);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Summary of " + monthYear);
        cell.setCellStyle(styleTitle);

        row = sheet.createRow(2);
        cell = row.createCell(0);
        cell.setCellValue("Cash in the beginning:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(startingBalance);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Sum of cash in:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(cashIn);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(4);
        cell = row.createCell(0);
        cell.setCellValue("Sum of cash out:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(cashOut);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(5);
        cell = row.createCell(0);
        cell.setCellValue("Cash at end:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(currentBalance);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(6);
        cell = row.createCell(0);
        cell.setCellValue("Sum of receivables:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(receivables);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(7);
        cell = row.createCell(0);
        cell.setCellValue("Sum of payables:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(payables);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(8);
        cell = row.createCell(0);
        cell.setCellValue("Schedule balance:");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(scheduleBalance);
        cell.setCellStyle(styleCurrency);

        row = sheet.createRow(10);
        cell = row.createCell(0);
        cell.setCellValue(MessageFormat.format("Exported by {0} at {1}",
                Caching.INSTANCE.getAccountName(), DatabaseDatesFunctions.INSTANCE.timestampToString(Timestamp.now())));
    }

    /**
     * Date | title | totalAmount | Category | Stakeholder | Registered by | Notes
     * -----|-------|-------------|----------|-------------|---------------|-------
     *      |       |             |          |             |               |
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cashIn(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, HSSFCellStyle styleBold,
                        List<ProcessedTransaction> processed) {
        HSSFSheet sheet = workbook.createSheet("Cash In");

        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) -1);

        sheet.setColumnWidth(0, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(1, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(2, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(3, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(4, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(5, (int) (20 * 1.14388) * 256);
        sheet.setColumnWidth(6, (int) (30 * 1.14388) * 256);

        row = sheet.createRow(1);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Date & Time");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell.setCellStyle(styleBold);
        cell = row.createCell(2);
        cell.setCellValue("Amount ($)");
        cell.setCellStyle(styleBold);
        cell = row.createCell(3);
        cell.setCellValue("Category");
        cell.setCellStyle(styleBold);
        cell = row.createCell(4);
        cell.setCellValue("Stakeholder");
        cell.setCellStyle(styleBold);
        cell = row.createCell(5);
        cell.setCellValue("Registered by");
        cell.setCellStyle(styleBold);
        cell = row.createCell(6);
        cell.setCellValue("Notes");
        cell.setCellStyle(styleBold);

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
            cell.setCellValue(Caching.INSTANCE.getCategoryTitle(transaction.getIdOfCategoryInt()));

            cell = row.createCell(4);
            cell.setCellValue(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeInt()));

            cell = row.createCell(5);
            cell.setCellValue(transaction.getRegisteredBy());

            cell = row.createCell(6);
            cell.setCellValue(transaction.getNotes());

            counter++;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void cashOut(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, HSSFCellStyle styleBold,
                         List<ProcessedTransaction> processed) {
        HSSFSheet sheet = workbook.createSheet("Cash Out");

        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) -1);

        sheet.setColumnWidth(0, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(1, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(2, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(3, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(4, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(5, (int) (20 * 1.14388) * 256);
        sheet.setColumnWidth(6, (int) (30 * 1.14388) * 256);


        row = sheet.createRow(1);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Date & Time");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell.setCellStyle(styleBold);
        cell = row.createCell(2);
        cell.setCellValue("Amount ($)");
        cell.setCellStyle(styleBold);
        cell = row.createCell(3);
        cell.setCellValue("Category");
        cell.setCellStyle(styleBold);
        cell = row.createCell(4);
        cell.setCellValue("Stakeholder");
        cell.setCellStyle(styleBold);
        cell = row.createCell(5);
        cell.setCellValue("Registered by");
        cell.setCellStyle(styleBold);
        cell = row.createCell(6);
        cell.setCellValue("Notes");
        cell.setCellStyle(styleBold);

        List<ProcessedTransaction> expenses = processed.stream()
                .filter(i -> !i.getIsDeleted())
                .filter(i -> i.getTotalAmount() < 0)
                .collect(Collectors.toList());

        int counter = 2;    // start from row 2

        for (ProcessedTransaction transaction : expenses) {
            row = sheet.createRow(counter);

            cell = row.createCell(0);
            cell.setCellValue(DatabaseDatesFunctions.INSTANCE.timestampToStringDetailed(transaction.getDueDate()));

            cell = row.createCell(1);
            cell.setCellValue(transaction.getTitle());

            cell = row.createCell(2);
            cell.setCellValue(Math.abs(transaction.getTotalAmount()));
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(3);
            cell.setCellValue(Caching.INSTANCE.getCategoryTitle(transaction.getIdOfCategoryInt()));

            cell = row.createCell(4);
            cell.setCellValue(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeInt()));

            cell = row.createCell(5);
            cell.setCellValue(transaction.getRegisteredBy());

            cell = row.createCell(6);
            cell.setCellValue(transaction.getNotes());

            counter++;
        }
    }

    /**
     * Stakeholder | title | amountPaid | totalAmount | dueDate | Category | Registered by
     * ------------|-------|------------|-------------|---------|----------|---------------
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void lateReceivables(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, HSSFCellStyle styleBold,
                                 List<ScheduledTransaction> late) {
        HSSFSheet sheet = workbook.createSheet("Incomplete Receivables");

        List<ScheduledTransaction> receivables = late.stream()
                .filter(t -> t.getTotalAmount() > 0)
                .collect(Collectors.toList());

        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) -1);

        sheet.setColumnWidth(0, (int) (20 * 1.14388) * 256);
        sheet.setColumnWidth(1, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(2, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(3, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(4, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(5, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(6, (int) (20 * 1.14388) * 256);

        sheet.setColumnWidth(8, (int) (20 * 1.14388) * 256);
        sheet.setColumnWidth(9, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(10, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(11, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(12, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(13, (int) (15 * 1.14388) * 256);
        sheet.setColumnWidth(14, (int) (20 * 1.14388) * 256);

        row = sheet.createRow(1);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Stakeholder");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell.setCellStyle(styleBold);
        cell = row.createCell(2);
        cell.setCellValue("Amount Paid ($)");
        cell.setCellStyle(styleBold);
        cell = row.createCell(3);
        cell.setCellValue("Amount to Pay ($)");
        cell.setCellStyle(styleBold);
        cell = row.createCell(4);
        cell.setCellValue("Due date");
        cell.setCellStyle(styleBold);
        cell = row.createCell(5);
        cell.setCellValue("Category");
        cell.setCellStyle(styleBold);
        cell = row.createCell(6);
        cell.setCellValue("Registered by");
        cell.setCellStyle(styleBold);

        int totalAmountToReceive = 0;
        int counter = 2;

        for (ScheduledTransaction transaction : receivables) {
            row = sheet.createRow(counter);

            cell = row.createCell(0);
            cell.setCellValue(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeholder()));

            cell = row.createCell(1);
            cell.setCellValue(transaction.getTitle());

            cell = row.createCell(2);
            cell.setCellValue(transaction.getAmountPaid());
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(3);
            cell.setCellValue(transaction.getTotalAmount());
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(4);
            cell.setCellValue(DatabaseDatesFunctions.INSTANCE.timestampToStringDetailed(transaction.getDueDate()));

            cell = row.createCell(5);
            cell.setCellValue(Caching.INSTANCE.getCategoryTitle(transaction.getCategory()));

            cell = row.createCell(6);
            cell.setCellValue(transaction.getIdOfAccount());

            totalAmountToReceive += transaction.getTotalAmount() - transaction.getAmountPaid();
            counter++;
        }

        row = sheet.createRow(counter + 1);
        cell = row.createCell(0);
        cell.setCellValue("Total late receivables ($):");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(totalAmountToReceive);
        cell.setCellStyle(styleCurrency);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void latePayables(HSSFWorkbook workbook, HSSFCellStyle styleCurrency, HSSFCellStyle styleBold,
                              List<ScheduledTransaction> late) {

        HSSFSheet sheet = workbook.createSheet("Incomplete Payables");

        List<ScheduledTransaction> payables = late.stream()
                .filter(t ->t.getTotalAmount() < 0)
                .collect(Collectors.toList());

        HSSFRow row = sheet.createRow(0);
        row.setHeight((short) -1);

        row = sheet.createRow(1);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("Stakeholder");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue("Title");
        cell.setCellStyle(styleBold);
        cell = row.createCell(2);
        cell.setCellValue("Amount Paid ($)");
        cell.setCellStyle(styleBold);
        cell = row.createCell(3);
        cell.setCellValue("Amount to Pay ($)");
        cell.setCellStyle(styleBold);
        cell = row.createCell(4);
        cell.setCellValue("Due date");
        cell.setCellStyle(styleBold);
        cell = row.createCell(5);
        cell.setCellValue("Category");
        cell.setCellStyle(styleBold);
        cell = row.createCell(6);
        cell.setCellValue("Registered by");
        cell.setCellStyle(styleBold);

        int totalAmountToPay = 0;
        int counter = 2;

        for (ScheduledTransaction transaction : payables) {
            row = sheet.createRow(counter);

            cell = row.createCell(0);
            cell.setCellValue(Caching.INSTANCE.getStakeholderName(transaction.getIdOfStakeholder()));

            cell = row.createCell(1);
            cell.setCellValue(transaction.getTitle());

            cell = row.createCell(2);
            cell.setCellValue(transaction.getAmountPaid());
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(3);
            cell.setCellValue(transaction.getTotalAmount());
            cell.setCellStyle(styleCurrency);

            cell = row.createCell(4);
            cell.setCellValue(DatabaseDatesFunctions.INSTANCE.timestampToStringDetailed(transaction.getDueDate()));

            cell = row.createCell(5);
            cell.setCellValue(Caching.INSTANCE.getCategoryTitle(transaction.getCategory()));

            cell = row.createCell(6);
            cell.setCellValue(transaction.getIdOfAccount());

            totalAmountToPay += transaction.getTotalAmount() - transaction.getAmountPaid();
            counter++;
        }

        row = sheet.createRow(counter);
        cell = row.createCell(0);
        cell.setCellValue("Total late payables ($):");
        cell.setCellStyle(styleBold);
        cell = row.createCell(1);
        cell.setCellValue(totalAmountToPay);
        cell.setCellStyle(styleCurrency);
    }
}