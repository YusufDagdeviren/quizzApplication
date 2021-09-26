package com.yusufdagdeviren.quizzaplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.yusufdagdeviren.quizzaplication.databinding.RecylerRowBinding;

import java.util.ArrayList;

public class QuizzAdapter extends RecyclerView.Adapter<QuizzAdapter.QuizzHolder> {

    ArrayList<Quizz> quizzArrayList;

    public QuizzAdapter(ArrayList<Quizz> quizzArrayList) {
        this.quizzArrayList = quizzArrayList;
    }

    @NonNull
    @Override
    public QuizzHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecylerRowBinding binding = RecylerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new QuizzHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizzHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.binding.recyclerTextView.setText(quizzArrayList.get(position).name);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailsActivity.class);
                intent.putExtra("info","old");
                intent.putExtra("id",quizzArrayList.get(position).id);
                holder.itemView.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return quizzArrayList.size();
    }

    public class QuizzHolder extends RecyclerView.ViewHolder{

        RecylerRowBinding binding;

        public QuizzHolder(RecylerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }




}
