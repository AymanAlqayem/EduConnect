//package com.tarificompany.android_project;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ScheduleAdapter extends RecyclerView.Adapter<ClassViewHolder> {
//
//    private Context context;
//    private List<ClassSchedule> classSchedules;
//
//    public ScheduleAdapter(Context context) {
//        this.context = context;
//        this.classSchedules = new ArrayList<>();
//    }
//
//    public ScheduleAdapter(Context context, List<ClassSchedule> classSchedules) {
//        this.context = context;
//        this.classSchedules = classSchedules != null ? classSchedules : new ArrayList<>();
//    }
//
//    @NonNull
//    @Override
//    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.schedual, parent, false);
//        return new ClassViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
//        ClassSchedule classSchedule = classSchedules.get(position);
//        holder.bind(classSchedule);
//    }
//
//    @Override
//    public int getItemCount() {
//        return classSchedules.size();
//    }
//
//    // دالة تحديث البيانات
//    public void updateData(List<ClassSchedule> newList) {
//        if (newList == null) return;
//        classSchedules.clear();
//        classSchedules.addAll(newList);
//        notifyDataSetChanged();
//    }
//}
