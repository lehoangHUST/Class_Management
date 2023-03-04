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

    private DatabaseReference mDatabase_attendance; // Số học sinh dã có mặt vào buổi học ngày hôm đó
    private DatabaseReference mDatabase_student_class; // Số học sinh đã đăng kí trong lớp học đó
    private List<Attendance_Student> mListAttendance_Student; // Hiển thị ra bên ngoài màn hình số lượng buổi học sinh đó đi muộn, và có mặt trong lớp học
    private final ArrayList<Student> students = new ArrayList<>(); // Hiển thị thông tin chi tiết của học sinh đó
    private final ArrayList<String> id_students = new ArrayList<>();
    private final ArrayList<Integer> missed = new ArrayList<>(); // Lưu trữ số buổi không đi học tương ứng với từng thí sinh
    private final ArrayList<Integer> attend = new ArrayList<>(); // Lưu trữ số buổi đi học tương ứng với từng thí sinh
    private final ArrayList<String> nameCell = new ArrayList<>(); // Header của excel mà chúng ta xuất ra
    private final ArrayList<ArrayList<String>> listDate = new ArrayList<ArrayList<String>>();
    // Header nó sẽ như sau: - NAME | Date 1 in class | Date 2 in class | Date 3 in class | ....
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
        // Đọc dữ liệu từ database có tên là 'attendence'
        classID = getIntent().getStringExtra("idClassroom");
        mDatabase_attendance = FirebaseDatabase.getInstance().getReference("attendance");
        mDatabase_student_class = FirebaseDatabase.getInstance().getReference("student").child(classID);
        mListAttendance_Student = new ArrayList<>();
        matrix_attendance = new ArrayList<ArrayList<Integer>>();
        nameCell.add("FULL NAME");
        read_firebase_student();

        mAdapter = new AttendanceStudentAdapter(this, R.layout.student_attendance_row, mListAttendance_Student);
        list_attendence_students.setAdapter(mAdapter);

        // Sự kiện nút bấm tạo file Excel từ số buổi nghỉ và đi học của sinh viên
        export_excel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExcelWorkbook();
            }
        });

        // Sự kiện nút bấm tạo file Pdf để lấy thông tin lớp học và sinh viên trong lớp học đó
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
        // Lấy danh sách
        mDatabase_attendance.child(classID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListAttendance_Student.clear();
                // Lấy danh sách
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    day = dataSnapshot.getKey();
                    ArrayList<Integer> vector_attendance = new ArrayList<>();
                    Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                    // Lấy vào trong là giá trị tháng của lớp học
                    for (Map.Entry m: map.entrySet())
                    {
                        month = (String) m.getKey();
                        Map<String, Object> ye = (Map<String, Object>) m.getValue();
                        // Lấy vào trong là giá trị năm của lớp học
                        for (Map.Entry y: ye.entrySet())
                        {
                            year = (String) y.getKey();
                            // Đi vào trong là lấy toàn bộ dữ liệu của sinh viên đó
                            ArrayList<String> id_students_attendance = (ArrayList<String>) y.getValue();

                            // Tính toán số buổi đi hoc và buổi nghỉ của từng sinh viên
                            for (int i = 0; i < id_students.size(); i++){
                                boolean ans = id_students_attendance.contains(id_students.get(i));
                                // Kiểm tra xem liệu có hay không ?
                                if (ans){
                                    // Số buổi đi học cộng lên 1
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

                    // Thêm các ngày mới vào trong file excel sẽ được xuất ra
                    String date_concat = day + "/" + month + "/" + year;
                    nameCell.add(date_concat);
                    System.out.println(vector_attendance);
                    matrix_attendance.add(vector_attendance);
                }

                // Convert dạng student sang dạng attendance_student

                convert();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    // Lấy dữ liệu học sinh trong lớp học từ firebase xuống
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
                    id_students.add(student.getId()); // Lấy id
                    students.add(student); // Lấy toàn bộ thông tin
                }

                // Khởi tạo độ dài cho số buổi học và nghỉ của từng sinh viên trong lớp học
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


    // Chuyển dạng
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
        // Xử lý dữ liệu đầu vào
        Integer cols = nameCell.size(); // Số lượng cột xuất ra file excel
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
        // Xuất dữ liệu theo đúng format data đã được định sẵn
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
        //  Lấy tiêu đề (header)
        for (int i = 0; i < cols; i++){
            cell = row.createCell(i);
            cell.setCellValue(nameCell.get(i));
        }

        // Chuyển data từ firebase sang dạng excel để xem chi tiết điểm danh của học sinh đó
        // theo từng ngày
        for (int i = 0; i < listDate.size(); i++){
            Row rowData = sheet.createRow(i + 1);

            for (int j = 0; j < cols; j++){
                cell = rowData.createCell(j);
                cell.setCellValue(listDate.get(i).get(j));
            }
        }


        // Xuất file và lưu trong điện thoại android
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

    // Xuất file pdf
    public void createPdfWorkbook(ArrayList<Student> students) throws FileNotFoundException {
        String pdfpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfpath, "student_class.pdf");
        OutputStream outputStream = new FileOutputStream(file);

        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        String title = "Thông tin sinh viên mã lớp " + classID;
        Paragraph paragraph = new Paragraph(title).setBold().setFontSize(24).setTextAlignment(TextAlignment.CENTER);

        // Xây dựng bảng thông tin sinh viên trong lớp học đó
        float[] width = {100f, 100f};
        Table table = new Table(width);
        table.setHorizontalAlignment(HorizontalAlignment.CENTER);

        // Add tiêu đề của file pdf gồm: Họ tên, mã số sinh viên của sinh viên
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("Tên sinh viên")));
        table.addCell(new com.itextpdf.layout.element.Cell().add(new Paragraph("MSSV")));

        // add thông tin sinh viên vào file
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

