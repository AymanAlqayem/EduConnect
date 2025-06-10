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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeleteTeacherFragment extends Fragment implements DeleteTeacherAdapter.OnTeacherDeleteListener {

    private RecyclerView rvTeachers;
    private SearchView searchView;
    private DeleteTeacherAdapter adapter;
    private List<Teacher> teacherList;
    private List<Teacher> filteredTeacherList;

    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";

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

        // Initialize teacher list with 10 different teachers (with IDs) STATIC DATA
        teacherList = new ArrayList<>();

        // load teachers.
        fetchTeachers();

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

    private void fetchTeachers() {
        String url = BASE_URL + "get_teachers.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray teachersArray = response.getJSONArray("teachers");
                            teacherList.clear();
                            for (int i = 0; i < teachersArray.length(); i++) {
                                JSONObject obj = teachersArray.getJSONObject(i);
                                teacherList.add(new Teacher(
                                        obj.getString("id"),
                                        obj.getString("full_name"),
                                        obj.getString("email"),
                                        obj.optString("phone", ""),
                                        obj.optString("subject", ""),
                                        obj.getString("joining_date"),
                                        obj.optString("notes", "")
                                ));
                            }
                            filteredTeacherList.clear();
                            filteredTeacherList.addAll(teacherList);
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(requireContext(), "Error: " + response.optString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(requireContext(), "Error fetching teachers", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onTeacherDelete(int position) {
        if (position >= 0 && position < filteredTeacherList.size()) {
            Teacher deletedTeacher = filteredTeacherList.get(position);
            String teacherId = deletedTeacher.getId();
            String teacherName = deletedTeacher.getFullName();

            // Prepare request to soft-delete teacher
            String url = BASE_URL + "delete_teacher.php";
            RequestQueue queue = Volley.newRequestQueue(requireContext());

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                // Remove teacher from both lists and update RecyclerView
                                teacherList.remove(deletedTeacher);
                                filteredTeacherList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(requireContext(), teacherName + " deleted successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(requireContext(), "Error: " + json.optString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Error parsing response", Toast.LENGTH_SHORT).show();
                        }
                    },
                    error -> {
                        error.printStackTrace();
                        Toast.makeText(requireContext(), "Error deleting teacher", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("teacher_id", teacherId);
                    return params;
                }
            };

            queue.add(request);
        }
    }
}