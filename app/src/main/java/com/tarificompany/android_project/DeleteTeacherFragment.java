package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DeleteTeacherFragment extends Fragment implements DeleteTeacherAdapter.OnTeacherDeleteListener {

    private RecyclerView rvTeachers;
    private SearchView searchView;
    private DeleteTeacherAdapter adapter;
    private List<Teacher> teacherList;
    private List<Teacher> filteredTeacherList;

    public static DeleteTeacherFragment newInstance() {
        return new DeleteTeacherFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_teacher, container, false);

        // Initialize RecyclerView
        rvTeachers = view.findViewById(R.id.rvTeachers);
        rvTeachers.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Initialize SearchView
        searchView = view.findViewById(R.id.searchView);

        // Initialize teacher list with 10 different teachers (with IDs)
        teacherList = new ArrayList<>();
        teacherList.add(new Teacher("122", "Alice Johnson", "alice.j@example.com", "1234567890", "Female", "Mathematics", "2023-01-15", "Math expert"));
        teacherList.add(new Teacher("123", "Bob Smith", "bob.smith@example.com", "2345678901", "Male", "English", "2022-06-20", "Literature enthusiast"));
        teacherList.add(new Teacher("124", "Carol White", "carol.w@example.com", "3456789012", "Female", "Science", "2021-09-10", "Physics specialist"));
        teacherList.add(new Teacher("125", "David Brown", "david.b@example.com", "4567890123", "Male", "History", "2020-03-05", "World history buff"));
        teacherList.add(new Teacher("126", "Emma Davis", "emma.d@example.com", "5678901234", "Female", "Geography", "2023-04-22", "Map enthusiast"));
        teacherList.add(new Teacher("127", "Frank Wilson", "frank.w@example.com", "6789012345", "Male", "Computer Science", "2022-11-30", "Coding mentor"));
        teacherList.add(new Teacher("128", "Grace Lee", "grace.l@example.com", "7890123456", "Female", "Physical Education", "2021-07-15", "Sports coach"));
        teacherList.add(new Teacher("129", "Henry Moore", "henry.m@example.com", "8901234567", "Male", "Art", "2020-12-01", "Creative artist"));
        teacherList.add(new Teacher("130", "Isabella Taylor", "isabella.t@example.com", "9012345678", "Female", "Music", "2023-02-28", "Piano virtuoso"));
        teacherList.add(new Teacher("131", "James Anderson", "james.a@example.com", "0123456789", "Male", "Foreign Language", "2022-08-17", "Fluent in Spanish"));

        // Initialize filtered list (initially same as full list)
        filteredTeacherList = new ArrayList<>(teacherList);

        // Set up adapter with filtered list
        adapter = new DeleteTeacherAdapter(filteredTeacherList, this);
        rvTeachers.setAdapter(adapter);

        // Set up search functionality
        setupSearchView();

        return view;
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTeachers(newText);
                return true;
            }
        });
    }

    private void filterTeachers(String query) {
        filteredTeacherList.clear();
        if (query.isEmpty()) {
            filteredTeacherList.addAll(teacherList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Teacher teacher : teacherList) {
                if (teacher.getFullName().toLowerCase().contains(lowerCaseQuery) || teacher.getId().equals(query)) {
                    filteredTeacherList.add(teacher);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTeacherDelete(int position) {
        // Remove teacher from both lists and update RecyclerView
        Teacher deletedTeacher = filteredTeacherList.get(position);
        String teacherName = deletedTeacher.getFullName();
        teacherList.remove(deletedTeacher);
        filteredTeacherList.remove(position);
        adapter.notifyItemRemoved(position);
        Toast.makeText(requireContext(), teacherName + " deleted successfully", Toast.LENGTH_SHORT).show();
    }
}