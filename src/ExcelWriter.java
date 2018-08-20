
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.filechooser.FileSystemView;
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
/**
 *
 * @author Hayat This class aims create 2 excel writer files to display Resource
 * allocation and patient appointments
 */
public class ExcelWriter {

    private static String[] columns = {"Process", "Patient", "WaitingTime", "Duration"};
    private static ArrayList<String> fullColumns = new ArrayList<>(Arrays.asList(columns));

    /**
     *
     * Method to create the excelwriter diagramm of patient appointments
     */
    public void write(List<Patient> lp) {

        // Create a Workbook
        try {

            Workbook workbook = new XSSFWorkbook();

            // Create a Sheet
            Sheet sheet = workbook.createSheet("Patient Scheduling");

            int nbr = getMaxNumberOfTasks(lp);
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

                for (int e = 0; e < p.getDiagramValues().size(); e++) {
                    Row row = sheet.createRow(rowNum++);

                    row.createCell(0)
                            .setCellValue(p.getProcessID());

                    row.createCell(1)
                            .setCellValue(p.getPatientID());

                    for (int i = 0; i < p.getDiagramValues().get(e).size(); i++) {
                        row.createCell(i + 2).setCellValue(p.getDiagramValues().get(e).get(i));
                    }
                }
            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            File home = FileSystemView.getFileSystemView().getHomeDirectory();
            FileOutputStream fileOut = new FileOutputStream(home.getAbsolutePath() + "\\Scheduling_Diagram.xlsx");
            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();
        } catch (IOException e) {
            System.out.println("Error :" + e);
        }
    }

    /**
     *
     * Method to create the excelwriter diagram of resource allocation
     */
    public void update(List<Resource> lr) {

        ArrayList<String> columns = new ArrayList<>();
        columns.add("Resource");

        try {
            Workbook workbook = new XSSFWorkbook();

            // Create a Sheet
            Sheet sheet = workbook.createSheet("Resource Scheduling");

            int nbr = getMaxNumberOfTasksForResources(lr);
            for (int i = 1; i <= nbr; i += 2) {
                columns.add(i, "WaitingTime");
                columns.add(i + 1, "Duration");
            }

            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < columns.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns.get(i));
            }

            int rowNum = 1;
            for (Resource r : lr) {
                Row row = sheet.createRow(rowNum++);

                row.createCell(0)
                        .setCellValue(r.getResourceID());

                for (int i = 0; i < r.getDiagramValues().size(); i++) {

                    row.createCell(i + 1).setCellValue(r.getDiagramValues().get(i));
                }

            }

            // Resize all columns to fit the content size
            for (int i = 0; i < columns.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // Write the output to a file
            File home = FileSystemView.getFileSystemView().getHomeDirectory();
            FileOutputStream fileOut = new FileOutputStream(home.getAbsolutePath() + "\\Scheduling_Diagram_Resource.xlsx");

            workbook.write(fileOut);
            fileOut.close();

            // Closing the workbook
            workbook.close();

        } catch (Exception e) {
            System.out.println("Error :" + e);
        }
    }

    /**
     *
     * Method for Patient diagram :returns the maximum number of tasks from a process, to fix the number of columns in excel file
     */
    public int getMaxNumberOfTasks(List<Patient> lp) {
        int nbr = lp.get(0).getDiagramValues().get(0).size();
        for (int i = 1; i < lp.size(); i++) {
            int currentNbr = lp.get(i).getDiagramValues().get(0).size();
            if (currentNbr > nbr) {
                nbr = currentNbr;
            }
        }
        return nbr;
    }

    /**
     *
     * Method For Resource diagram : returns the maximum number of tasks, to fix the number of columns in excel file
     */
    public int getMaxNumberOfTasksForResources(List<Resource> lr) {
        int nbr = lr.get(0).getDiagramValues().size();
        for (int i = 1; i < lr.size(); i++) {
            int currentNbr = lr.get(i).getDiagramValues().size();
            if (currentNbr > nbr) {
                nbr = currentNbr;
            }
        }
        return nbr;
    }
}
