package com.example;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
/// Users/imac/Desktop/un_mail.csv
// /Users/imac/Desktop/text_file_.txt
// /Users/imac/Desktop/test_excel.xlsx

public class parse_determine {

    public static String file_path;

    public static String getFileTypeByProbeContentType(String fileName) {

        System.out.println("Gachi program begins...");

        String fileType = "Undetermined";
        final File file = new File(fileName);
        try {
            fileType = Files.probeContentType(file.toPath());
        } catch (IOException ioException) {
            System.out.println("File type not detected for " + fileName);
        }


        if (fileType.contains("csv")) {
            System.out.println("CSV!");

            File fileread = new File(file_path);
            List<List<String>> records = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(fileread))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(", ");
                    records.add(Arrays.asList(values));
                }
                System.out.println(records);
            } catch (Exception excep) {
                excep.printStackTrace();
            }

        } else if (fileType.contains("xlsx") || fileType.contains("xml")) {
            System.out.println("Excel!");
            File exel_file_first = new File(file_path);
            try {

                FileInputStream fis = new FileInputStream(exel_file_first);
                XSSFWorkbook wb = new XSSFWorkbook(fis);
                XSSFSheet file_sheet = wb.getSheetAt(0);

                Iterator<Row> rownum = file_sheet.iterator();

                while (rownum.hasNext()) {
                    Row rows = rownum.next();
                    Iterator<Cell> cellIterator = rows.cellIterator();
                    while (cellIterator.hasNext()) {
                        Cell cell = cellIterator.next();
                        String excel_values = cell.toString();
                        System.out.print(excel_values);
                        System.out.println();
                    }
                }


                wb.close();
                fis.close();

            } catch (Exception except) {
                except.printStackTrace();
                System.err.println(except.getClass().getName() + ": " + except.getMessage());
                System.exit(111);
            }

            // /Users/imac/Desktop/test_excel.XLSX


        } else if (fileType.contains("plain")) {
            System.out.println("Text!");
            try {
                String txt_file = file_path;

                File file_txt = new File(txt_file);
                Scanner sc = new Scanner(file_txt);

                sc.useDelimiter("\\Z");

                System.out.println(sc.next());

            } catch (FileNotFoundException file_except) {
                file_except.getStackTrace();
            }
        }

        return "Shrek";
    }


    public static void main(String[] args) {

        Scanner scanin = new Scanner(System.in);
        String file_given = scanin.next();
        file_path = file_given;
        System.out.println(getFileTypeByProbeContentType(file_given));

    }
}