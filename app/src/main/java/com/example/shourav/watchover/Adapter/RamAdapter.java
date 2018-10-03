package com.example.shourav.watchover.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shourav.watchover.Pojo.Ram;
import com.example.shourav.watchover.R;

import java.util.List;

public class RamAdapter extends RecyclerView.Adapter<RamAdapter.RamViewHolder> {


    Context context;
    private List<Ram> listProcess;


    public RamAdapter(Context context, List<Ram> listProcess) {
        this.context = context;
        this.listProcess=listProcess;
    }

    @Override
    public RamViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.process_list,parent,false);
        return new RamViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RamViewHolder holder, int position) {
        holder.appIcon.setImageDrawable(listProcess.get(position).getAppIcon());
        holder.appName.setText(listProcess.get(position).getAppName());
    }

    @Override
    public int getItemCount() {
        return listProcess.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class RamViewHolder extends RecyclerView.ViewHolder{

        TextView appName;
        ImageView appIcon;
        public RamViewHolder(View itemView) {
            super(itemView);

            appIcon = (ImageView) itemView.findViewById(R.id.appIcon);
            appName = (TextView) itemView.findViewById(R.id.appName);

        }
    }

}
