package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class DeleteStudentFragment extends Fragment implements DeleteStudentAdapter.OnStudentDeleteListener{

    private RecyclerView rvStudents;
    private EditText etStudentSearch;
    private DeleteStudentAdapter adapter;
    private List<Student> studentsList;
    private List<Student> filteredList;

    public static DeleteStudentFragment newInstance() {
        return new DeleteStudentFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_student, container, false);
        setUpViews(view);

        setUpSearch();

        return view;
    }

    public void setUpViews(View view){
        rvStudents = view.findViewById(R.id.rvStudents_Delete);
        rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        etStudentSearch = view.findViewById(R.id.etStudentSearch_Delete);

        studentsList = new ArrayList<>();
        studentsList.add(new Student("101", "Adam Smith", 89, "11L", "0595237162", "29-7-2008"));
        studentsList.add(new Student("101", "Bob Smith", 89, "11L", "0595237162", "8-6-2008"));
        studentsList.add(new Student("102", "Chris Smith", 89, "12L", "0595237162", "7-7-2007"));
        studentsList.add(new Student("103", "Dan Smith", 89, "12S", "0595237162","8-3-2007"));
        studentsList.add(new Student("104", "Evans Smith", 89, "12S", "0595237162","17-9-2007"));
        studentsList.add(new Student("105", "Fadi Smith", 89, "10", "0595237162","19-12-2009"));
        studentsList.add(new Student("106", "Grayson Smith", 89, "11S", "0595237162","8-3-2008"));
        studentsList.add(new Student("107", "Hill Smith", 89, "11S", "0595237162","8-9-2008"));
        studentsList.add(new Student("108", "Ian Smith", 89, "11S", "0595237162","3-1-2008"));

        filteredList = new ArrayList<>(studentsList);

        adapter = new DeleteStudentAdapter(filteredList, this);
        rvStudents.setAdapter(adapter);
    }

    void setUpSearch() {
        etStudentSearch.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //no need
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStudent(s.toString());
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                // no need
            }
        });
    }


    private void filterStudent(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(studentsList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Student e : studentsList) {
                if (e.getName().toLowerCase().contains(lowerCaseQuery) || e.getStdId().equals(query)) {
                    filteredList.add(e);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStudentDelete(int position) {
        Student toDelete = filteredList.get(position);
        studentsList.remove(toDelete);
        filteredList.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(requireContext(), "Student deleted successfully", Toast.LENGTH_SHORT).show();
    }

}
