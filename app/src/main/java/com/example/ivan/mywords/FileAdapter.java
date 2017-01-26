package com.example.ivan.mywords;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;


class FileAdapter  extends RecyclerView.Adapter<FileAdapter.FileViewHolder>{

    private ArrayList<File> data;
    private OnRecyclerItemClickListener listener;

    FileAdapter(File[] data) {
        this.data = new ArrayList<>();
        for (File aData : data) {
            if (aData.isFile() && aData.getName().contains(".txt")) {
                this.data.add(aData);
            }
        }
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_file, null, false);
        return new FileViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final FileViewHolder holder, final int position) {
        holder.tvFileName.setText(data.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void setListener(OnRecyclerItemClickListener listener) {
        this.listener = listener;
    }

    public String getFilePath(int position) {
        return data.get(position).getPath();
    }

    class FileViewHolder extends RecyclerView.ViewHolder{

        private TextView tvFileName;

        FileViewHolder(View itemView) {
            super(itemView);
            tvFileName = (TextView) itemView.findViewById(R.id.tv_filename);
        }
    }

    interface OnRecyclerItemClickListener {
        void onItemClick(int pos);
    }

}
