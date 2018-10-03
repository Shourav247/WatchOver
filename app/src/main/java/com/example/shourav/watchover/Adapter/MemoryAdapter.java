package com.example.shourav.watchover.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.shourav.watchover.Pojo.Memory;
import com.example.shourav.watchover.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Musa on 9/19/2018.
 */

public class MemoryAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;
    private List<Memory> listStorage;

    public MemoryAdapter(Context context, List<Memory> customizedListView) {
        layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listStorage = customizedListView;
    }

    @Override
    public int getCount() {
        return listStorage.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder listViewHolder;
        if(convertView == null){
            listViewHolder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.memory_list, parent, false);

            listViewHolder.heading = (TextView)convertView.findViewById(R.id.memory_title);
            listViewHolder.usedSpace = (TextView)convertView.findViewById(R.id.used_space);
            listViewHolder.freeSpace = (TextView)convertView.findViewById(R.id.free_space);
            listViewHolder.totalSpace = (TextView)convertView.findViewById(R.id.total_space);
            listViewHolder.pieChart = (PieChart) convertView.findViewById(R.id.pieChart);

            convertView.setTag(listViewHolder);
        }else{
            listViewHolder = (ViewHolder)convertView.getTag();
        }
        listViewHolder.heading.setText(listStorage.get(position).getTitle());
        listViewHolder.usedSpace.setText(listStorage.get(position).getUsedSpace()+" MB");
        listViewHolder.freeSpace.setText(listStorage.get(position).getFreeSpace()+" MB");
        listViewHolder.totalSpace.setText(listStorage.get(position).getTotalSpace()+" MB");
        // listViewHolder.imageIcon.setImageResource(listStorage.get(position).getImage());
        listViewHolder.pieChart.setUsePercentValues(true);
        listViewHolder.pieChart.getDescription().setEnabled(false);
        listViewHolder.pieChart.setExtraOffsets(5,10,5,5);

        listViewHolder.pieChart.setDragDecelerationFrictionCoef(0.95f);

        listViewHolder.pieChart.setDrawHoleEnabled(true);
        listViewHolder.pieChart.setHoleColor(Color.BLACK);
        listViewHolder.pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();

        float freeMemory = Float.parseFloat(listStorage.get(position).getFreeSpace());
        float totalMemoey = Float.parseFloat(listStorage.get(position).getTotalSpace());
        float usedMemory = Float.parseFloat(listStorage.get(position).getUsedSpace());

        yValues.add(new PieEntry(freeMemory,"Free"));
        yValues.add(new PieEntry(totalMemoey,"Total"));
        yValues.add(new PieEntry(usedMemory,"Uesd"));


        PieDataSet dataSet = new PieDataSet(yValues,"Memory");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);

        listViewHolder.pieChart.setData(data);

        return convertView;
    }

    static class ViewHolder{

        TextView heading;
        TextView usedSpace;
        TextView freeSpace;
        TextView totalSpace;
        PieChart pieChart;
    }
}
