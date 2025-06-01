package com.tarificompany.android_project;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tarificompany.android_project.R;

import java.util.Calendar;

public class AddAssignmentActivity extends AppCompatActivity {

    private EditText etAssignmentTitle, etAssignmentDesc, etDueDate;
    private Spinner spinnerClass;
    private Button btnAdd;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_assignment);

        // تهيئة العناصر
        initViews();
        setupSpinner();
        setupDatePicker();
        setupButtons();
    }

    private void initViews() {
        etAssignmentTitle = findViewById(R.id.et_assignment_title);
        etAssignmentDesc = findViewById(R.id.et_assignment_desc);
        etDueDate = findViewById(R.id.et_due_date);
        spinnerClass = findViewById(R.id.spinner_class);
        btnAdd = findViewById(R.id.btn_add);
        btnBack = findViewById(R.id.btn_back);
    }

    private void setupSpinner() {
        // بيانات وهمية للصفوف
        String[] classes = {"الصف الأول أ", "الصف الثاني ب", "الصف الثالث ج"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                classes
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etDueDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etDueDate.setText(date);
                    },
                    year, month, day
            );
            datePickerDialog.show();
        });
    }

    private void setupButtons() {
        btnBack.setOnClickListener(v -> finish());

        btnAdd.setOnClickListener(v -> {
            String title = etAssignmentTitle.getText().toString().trim();
            String desc = etAssignmentDesc.getText().toString().trim();
            String dueDate = etDueDate.getText().toString().trim();
            String selectedClass = spinnerClass.getSelectedItem().toString();

            if (title.isEmpty() || desc.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(this, "الرجاء إدخال جميع البيانات المطلوبة", Toast.LENGTH_SHORT).show();
                return;
            }

            // هنا يمكنك إضافة كود حفظ الواجب في قاعدة البيانات
            Toast.makeText(this, "تم إضافة الواجب بنجاح", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}