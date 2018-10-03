package com.example.shourav.watchover.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.shourav.watchover.R;
import com.snatik.storage.Storage;

import java.io.File;
import java.util.List;

/**
 * Created by sromku on June, 2017.
 */
public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<File> mFiles;
    private OnFileItemListener mListener;
    private Storage mStorage;

    private static final int LIST_ITEM = 0;
    private static final int GRID_ITEM = 1;
    boolean isSwitchView = true;

    public FolderAdapter(Context context) {
        mStorage = new Storage(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View itemView;
        if (i == LIST_ITEM){
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate( R.layout.folder_view, null);
        }else{
            itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grid_view, null);
        }
        return new FileViewHolder(itemView);
    }

    @Override
    public int getItemViewType (int position) {
        if (isSwitchView){
            return LIST_ITEM;
        }else{
            return GRID_ITEM;
        }
    }

    public boolean toggleItemViewType () {
        isSwitchView = !isSwitchView;
        return isSwitchView;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final File file = mFiles.get(position);
        FileViewHolder fileViewHolder = (FileViewHolder) holder;
        fileViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onClick(file);
            }
        });
        fileViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onLongClick(file);
                return true;
            }
        });
        fileViewHolder.mName.setText(file.getName());
        fileViewHolder.mIcon.setImageResource(file.isDirectory() ? R.drawable.ic_action_folder : R.drawable
                .ic_action_file);
        if (file.isDirectory()) {
            fileViewHolder.mSize.setVisibility(View.GONE);
        } else {
            fileViewHolder.mSize.setVisibility(View.VISIBLE);
            fileViewHolder.mSize.setText(mStorage.getReadableSize(file));
        }

    }

    @Override
    public int getItemCount() {
        return mFiles != null ? mFiles.size() : 0;
    }

    public void setFiles(List<File> files) {
        mFiles = files;
    }

    public void setListener(OnFileItemListener listener) {
        mListener = listener;
    }

    static class FileViewHolder extends RecyclerView.ViewHolder {

        TextView mName;
        TextView mSize;
        ImageView mIcon;

        FileViewHolder(View v) {
            super(v);
            mName = (TextView) v.findViewById(R.id.name);
            mSize = (TextView) v.findViewById(R.id.size);
            mIcon = (ImageView) v.findViewById(R.id.icon);
        }
    }

    public interface OnFileItemListener {
        void onClick(File file);
        void onLongClick(File file);
    }
}
