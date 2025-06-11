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

public class DeleteStudentFragment extends Fragment implements DeleteStudentAdapter.OnStudentDeleteListener{

    private RecyclerView rvStudents;
    private EditText etStudentSearch;
    private DeleteStudentAdapter adapter;
    private List<Student> studentsList;
    private List<Student> filteredList;
    private static final String BASE_URL = "http://10.0.2.2/AndroidProject/";


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
        fetchStudents();

        return view;
    }

    public void setUpViews(View view){
        rvStudents = view.findViewById(R.id.rvStudents_Delete);
        rvStudents.setLayoutManager(new LinearLayoutManager(requireContext()));
        etStudentSearch = view.findViewById(R.id.etStudentSearch_Delete);

        studentsList = new ArrayList<>();

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

    private void fetchStudents(){
        String url = BASE_URL + "get_students.php";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.getString("status").equals("success")) {
                            JSONArray studentsArray = response.getJSONArray("students");
                            studentsList.clear();
                            for (int i = 0; i < studentsArray.length(); i++) {
                                JSONObject obj = studentsArray.getJSONObject(i);
                                studentsList.add(new Student(
                                        obj.getString("student_id"),
                                        obj.getString("name"),
                                        obj.getString("email"),
                                        obj.optString("class_name", ""),
                                        obj.optString("parent_phone", ""),
                                        obj.getString("DOB")
                                ));
                            }
                            filteredList.clear();
                            filteredList.addAll(studentsList);
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
                    Toast.makeText(requireContext(), "Error fetching students", Toast.LENGTH_SHORT).show();
                }
        );
        queue.add(jsonObjectRequest);
    }


    @Override
    public void onStudentDelete(int position) {
        if (position >= 0 && position < filteredList.size()) {
            Student toDelete = filteredList.get(position);
            String studentId = toDelete.getStdId();  // assuming getStdId() returns student ID
            String studentName = toDelete.getName();

            String url = BASE_URL + "delete_student.php";
            RequestQueue queue = Volley.newRequestQueue(requireContext());

            StringRequest request = new StringRequest(Request.Method.POST, url,
                    response -> {
                        try {
                            JSONObject json = new JSONObject(response);
                            if (json.getString("status").equals("success")) {
                                studentsList.remove(toDelete);
                                filteredList.remove(position);
                                adapter.notifyItemRemoved(position);
                                Toast.makeText(requireContext(), studentName + " deleted successfully", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(requireContext(), "Error deleting student", Toast.LENGTH_SHORT).show();
                    }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("student_id", studentId);
                    return params;
                }
            };

            queue.add(request);
        }
    }


}
