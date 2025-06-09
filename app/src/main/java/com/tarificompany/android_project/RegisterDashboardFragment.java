package com.tarificompany.android_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class RegisterDashboardFragment extends Fragment {

    private RequestQueue queue;
    private String url = "http://10.0.2.2/AndroidProject/register_dashboard.php";

    // TextViews from activity_register_dashboard.xml
    private TextView totalStudentsCount, totalTeachersCount, totalClassesCount,
            class10thCount, class11thLitCount, class11thSciCount,
            class12thLitCount, class12thSciCount, announcementsText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_register_dashboard, container, false);

        // Initialize RequestQueue with fragment's context (similar to LoginActivity.this in activities)
        queue = Volley.newRequestQueue(requireContext());

        // Initialize TextViews
        totalStudentsCount = view.findViewById(R.id.total_students_count);
        totalTeachersCount = view.findViewById(R.id.total_teachers_count);
        totalClassesCount = view.findViewById(R.id.total_classes_count);
        class10thCount = view.findViewById(R.id.class_10th_count);
        class11thLitCount = view.findViewById(R.id.class_11th_lit_count);
        class11thSciCount = view.findViewById(R.id.class_11th_sci_count);
        class12thLitCount = view.findViewById(R.id.class_12th_lit_count);
        class12thSciCount = view.findViewById(R.id.class_12th_sci_count);
        announcementsText = view.findViewById(R.id.announcements_text);

        // Update welcome message based on user type
        TextView dashboardTitle = view.findViewById(R.id.dashboard_title);
        String userType = requireActivity().getIntent().getStringExtra("USER_TYPE");

        if (userType != null) {
            String title = userType.equalsIgnoreCase("teacher") ? "Teacher Dashboard" :
                    userType.equalsIgnoreCase("student") ? "Student Dashboard" :
                            "Registrar Dashboard";
            dashboardTitle.setText(title);
        } else {
            dashboardTitle.setText("Dashboard");
        }

        // Fetch dashboard data from the API
        fetchDashboardData();

        return view;
    }

    private void fetchDashboardData() {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                JSONObject data = response.getJSONObject("data");
                                totalStudentsCount.setText(String.valueOf(data.getInt("total_students")));
                                totalTeachersCount.setText(String.valueOf(data.getInt("total_teachers")));
                                totalClassesCount.setText(String.valueOf(data.getInt("total_classes")));

                                JSONObject classCounts = data.getJSONObject("class_counts");
                                class10thCount.setText(classCounts.getInt("10th") + " Students");
                                class11thLitCount.setText(classCounts.getInt("11th_literature") + " Students");
                                class11thSciCount.setText(classCounts.getInt("11th_science") + " Students");
                                class12thLitCount.setText(classCounts.getInt("12th_literature") + " Students");
                                class12thSciCount.setText(classCounts.getInt("12th_science") + " Students");

                                announcementsText.setText(data.getString("announcements"));
                            } else {
                                Toast.makeText(requireContext(),
                                        response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(requireContext(),
                                    "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(requireContext(),
                                "Network error: " + (error.getMessage() != null ? error.getMessage() : "Unknown error"),
                                Toast.LENGTH_LONG).show();
                    }
                });

        // Add the request to the queue
        queue.add(jsonObjectRequest);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Cancel all pending requests to prevent memory leaks
        if (queue != null) {
            queue.cancelAll(this);
        }
    }
}