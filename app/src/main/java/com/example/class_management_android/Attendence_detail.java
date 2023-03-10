package com.example.class_management_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.class_management_android.adapter.AttendanceStudentAdapter;
import com.example.class_management_android.adapter.StudentAdapter;
import com.example.class_management_android.model.Student;
import com.example.class_management_android.model.Attendance_Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Tab;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;

// Excel
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

// Pdf


// Input / Output
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Attendence_detail extends AppCompatActivity {

    private DatabaseReference mDatabase_attendance; // S??? h???c sinh d?? c?? m???t v??o bu???i h???c ng??y h??m ????
    private DatabaseReference mDatabase_student_class; // S??? h???c sinh ???? ????ng k?? trong l???p h???c ????
    private List<Attendance_Student> mListAttendance_Student; // Hi???n th??? ra b??n ngo??i m??n h??nh s??? l?????ng bu???i h???c sinh ???? ??i mu???n, v?? c?? m???t trong l???p h???c
    private final ArrayList<Student> students = new ArrayList<>(); // Hi???n th??? th??ng tin chi ti???t c???a h???c sinh ????
    private final ArrayList<String> id_students = new ArrayList<>();
    private final ArrayList<Integer> missed = new ArrayList<>(); // L??u tr??? s??? bu???i kh??ng ??i h???c t????ng ???ng v???i t???ng th?? sinh
    private final ArrayList<Integer> attend = new ArrayList<>(); // L??u tr??? s??? bu???i ??i h???c t????ng ???ng v???i t???ng th?? sinh
    private final ArrayList<String> nameCell = new ArrayList<>(); // Header c???a excel m?? ch??ng ta xu???t ra
    private final ArrayList<ArrayList<String>> listDate = new ArrayList<ArrayList<String>>();
    // Header n?? s??? nh?? sau: - NAME | Date 1 in class | Date 2 in class | Date 3 in class | ....
    private ArrayList<ArrayList<Integer>> matrix_attendance;
    private final ArrayList<Student> students_attendance_class = new ArrayList<>();
    private ListView list_attendence_students;
    private Button export_excel;
    private Button export_pdf;
    private AttendanceStudentAdapter mAdapter;
    private String classID;
    private String day, month, year;

    private static Cell cell;
    private static Sheet sheet;

    private static final String EXCEL_SHEET_NAME = "Sheet1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendence_detail);

        // calling the action bar
        ActionBar actionBar = getSupportActionBar();
        // Customize the back button
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back_action);
        // showing the back button in action bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        // edit title in action bar
        actionBar.setTitle("");
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#1E313E"))); // dark_blue

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        list_attendence_students = (ListView) findViewById(R.id.list_attendence_students);
        export_excel = (Button) findViewById(R.id.export_excel);
        export_pdf = (Button) findViewById(R.id.export_pdf);
        // ?????c d??? li???u t??? database c?? t??n l?? 'attendence'
        classID = getIntent().getStringExtra("idClassroom");
        mDatabase_attendance = FirebaseDatabase.getInstance().getReference("attendance");
        mDatabase_student_class = FirebaseDatabase.getInstance().getReference("student").child(classID);
        mListAttendance_Student = new ArrayList<>();
        matrix_attendance = new ArrayList<ArrayList<Integer>>();
        nameCell.add("FULL NAME");
        read_firebase_student();

        mAdapter = new AttendanceStudentAdapter(this, R.layout.student_attendance_row, mListAttendance_Student);
        list_attendence_students.setAdapter(mAdapter);

        // S??? ki???n n??t b???m t???o file Excel t??? s??? bu???i ngh??? v?? ??i h???c c???a sinh vi??n
        export_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExcelWorkbook();
            }
        });

        // S??? ki???n n??t b???m t???o file Pdf ????? l???y th??ng tin l???p h???c v?? sinh vi??n trong l???p h???c ????
        export_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    createPdfWorkbook(students);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // L???y danh s??ch
        mDatabase_attendance.child(classID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListAttendance_Student.clear();
                // L???y danh s??ch
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    day = dataSnapshot.getKey();
                    ArrayList<Integer> vector_attendance = new ArrayList<>();
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    // L???y v??o trong l?? gi?? tr??? th??ng c???a l???p h???c
                    for (Map.Entry m: map.entrySet())
                    {
                        month = (String) m.getKey();
                        Map<String, Object> ye = (Map<String, Object>) m.getValue();
                        // L???y v??o trong l?? gi?? tr??? n??m c???a l???p h???c
                        for (Map.Entry y: ye.entrySet())
                        {
                            year = (String) y.getKey();
                            // ??i v??o trong l?? l???y to??n b??? d??? li???u c???a sinh vi??n ????
                            ArrayList<String> id_students_attendance = (ArrayList<String>) y.getValue();

                            // T??nh to??n s??? bu???i ??i hoc v?? bu???i ngh??? c???a t???ng sinh vi??n
                            for (int i = 0; i < id_students.size(); i++){
                                boolean ans = id_students_attendance.contains(id_students.get(i));
                                // Ki???m tra xem li???u c?? hay kh??ng ?
                                if (ans){
                                    // S??? bu???i ??i h???c c???ng l??n 1
                                    attend.set(i, attend.get(i) + 1);
                                    vector_attendance.add(1);
                                }else
                                {
                                    missed.set(i, missed.get(i) + 1);
                                    vector_attendance.add(0);
                                }

                            }
                        }
                    }

                    // Th??m c??c ng??y m???i v??o trong file excel s??? ???????c xu???t ra
                    String date_concat = day + "/" + month + "/" + year;
                    nameCell.add(date_concat);
                    System.out.println(vector_attendance);
                    matrix_attendance.add(vector_attendance);
                }

                // Convert d???ng student sang d???ng attendance_student

                convert();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // L???y d??? li???u h???c sinh trong l???p h???c t??? firebase xu???ng
    private void read_firebase_student(){
        mDatabase_student_class.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                students.clear();
                id_students.clear();
                missed.clear();
                attend.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Student student = dataSnapshot.getValue(Student.class);
                    id_students.add(student.getId()); // L???y id
                    students.add(student); // L???y to??n b??? th??ng tin
                }

                // Kh???i t???o ????? d??i cho s??? bu???i h???c v?? ngh??? c???a t???ng sinh vi??n trong l???p h???c
                for (int i = 0; i < id_students.size(); i++){
                    missed.add(0);
                    attend.add(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Chuy???n d???ng
    private void convert(){
        int number_student = students.size();
        for (int i = 0; i < number_student; i++){
            String id_student = students.get(i).getId();
            String name_student = students.get(i).getName();
            int miss = missed.get(i);
            int att = attend.get(i);

            Attendance_Student as = new Attendance_Student();
            as.setId(id_student);
            as.setName(name_student);
            as.setMissed(miss);
            as.setAttend(att);
            mListAttendance_Student.add(as);
        }


    }


    public void createExcelWorkbook(){
        // X??? l?? d??? li???u ?????u v??o
        Integer cols = nameCell.size(); // S??? l?????ng c???t xu???t ra file excel
        Integer rows = matrix_attendance.get(0).size();

        for (int i = 0; i < rows; i++){
            ArrayList<String> date = new ArrayList<>();
            for (int j = 0; j < cols; j++){
                if (j == 0){
                    String nameStudent = students.get(i).getName();
                    date.add(nameStudent);
                }else
                {
                    Integer attend = matrix_attendance.get(j - 1).get(i);
                    date.add(Integer.toString(attend));
                }
            }
            listDate.add(date);
        }
        // Xu???t d??? li???u theo ????ng format data ???? ???????c ?????nh s???n
        Workbook workbook = new HSSFWorkbook();
        cell = null;

        // Cell style for header row
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // New sheet
        sheet = null;
        sheet = workbook.createSheet(EXCEL_SHEET_NAME);

        // Generate column headings
        Row row = sheet.createRow(0);
        //  L???y ti??u ????? (header)
        for (int i = 0; i < cols; i++){
            cell = row.createCell(i);
            cell.setCellValue(nameCell.get(i));
        }

        // Chuy???n data t??? firebase sang d???ng excel ????? xem chi ti???t ??i???m danh c???a h???c sinh ????
        // theo t???ng ng??y
        for (int i = 0; i < listDate.size(); i++){
            Row rowData = sheet.createRow(i + 1);

            for (int j = 0; j < cols; j++){
                cell = rowData.createCell(j);
                cell.setCellValue(listDate.get(i).get(j));
            }
        }


        // Xu???t file v?? l??u trong ??i???n tho???i android
        File file = new File(getExternalFilesDir(null), "demo.xls");
        FileOutputStream outputStream = null;
        try{
                outputStream = new FileOutputStream(file);
                workbook.write(outputStream);
                Toast.makeText(getApplicationContext(), "OK", Toast.LENGTH_SHORT).show();
            }catch (FileNotFoundException e){
                e.printStackTrace();

                Toast.makeText(getApplicationContext(), "NOT OK", Toast.LENGTH_SHORT).show();
                try{
                    outputStream.close();
                } catch (IOException ex){
                    ex.printStackTrace();
                }
            } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Xu???t file pdf
    public void createPdfWorkbook(ArrayList<Student> students) throws FileNotFoundException {
        String pdfpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfpath, "student_class.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        String title = "Th??ng tin sinh vi??n m?? l???p " + classID;
        Paragraph paragraph = new Paragraph(title).setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);

        // X??y d???ng b???ng th??ng tin sinh vi??n trong l???p h???c ????
        float[] width = {100f, 100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Add ti??u ????? c???a file pdf g???m: H??? t??n, m?? s??? sinh vi??n c???a sinh vi??n
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("T??n sinh vi??n")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("MSSV")));

        // add th??ng tin sinh vi??n v??o file
        for (Student i: students){
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(i.getName())));
            table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph(i.getId())));
        }

        document.add(paragraph);
        document.add(table);
        document.close();
        Toast.makeText(this, "Pdf created", Toast.LENGTH_LONG).show();
    }
}

