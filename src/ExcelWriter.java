
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package Scheduling_First_Try;
public class ExcelWriter {

    private static String[] columns = {"Process", "Patient", "WaitingTime", "Duration"};
    private static ArrayList<String> fullColumns = new ArrayList<>(Arrays.asList(columns));

    public void write(List<Patient> lp) {

        // Create a Workbook
//             // new HSSFWorkbook() for generating `.xls` file
        try {
            /* CreationHelper helps us create instances of various things like DataFormat, 
           Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way */
//            CreationHelper createHelper = workbook.getCreationHelper();

           
            
            Workbook workbook = new XSSFWorkbook();
            

            // Create a Sheet
            Sheet sheet = workbook.createSheet("Patient Scheduling");

            int nbr = getMaxNumberOfTasks(lp) - 2;
            for (int i = 4; i <= nbr; i += 2) {
                fullColumns.add(i, "WaitingTime");
                fullColumns.add(i + 1, "Duration");
            }
            // Create a Row
            Row headerRow = sheet.createRow(0);

            // Create cells
            for (int i = 0; i < fullColumns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(fullColumns.get(i));
            }

            // Create Other rows and cells with  data
            int rowNum = 1;
            for (Patient p : lp) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(p.getProcessID());

                row.createCell(1)
                        .setCellValue(p.getPatientID());

              for (int i = 0; i < p.getDiagramValues().size(); i++) {

                    row.createCell(i+2).setCellValue(p.getDiagramValues().get(i));
                }

            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            FileOutputStream fileOut = new FileOutputStream("C:\\Users\\Hayat\\Desktop\\Scheduling_Diagram.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (IOException e) {
            System.out.println("Error :" + e);
        }
    }

    public int getMaxNumberOfTasks(List<Patient> lp) {
        int nbr = lp.get(0).getDiagramValues().size() / 2;
        for (int i = 1; i < lp.size(); i++) {
            int currentNbr = lp.get(i).getDiagramValues().size() / 2;
            if (currentNbr > nbr) {
                nbr = currentNbr;
            }
        }
        return nbr;
    }
}
