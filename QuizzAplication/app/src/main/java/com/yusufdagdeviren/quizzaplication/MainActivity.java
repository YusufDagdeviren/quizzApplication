package com.yusufdagdeviren.quizzaplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.yusufdagdeviren.quizzaplication.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<Quizz> quizzArrayList;
    QuizzAdapter quizzAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);






        quizzArrayList = new ArrayList<Quizz>();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizzAdapter = new QuizzAdapter(quizzArrayList);
        binding.recyclerView.setAdapter(quizzAdapter);



        getData();
    }
    public void getData(){


        try{

            SQLiteDatabase database = this.openOrCreateDatabase("Quizz",MODE_PRIVATE,null);

            Cursor cursor = database.rawQuery("SELECT * FROM quizz",null);

            int nameIx = cursor.getColumnIndex("questionNameText");
            int idIx = cursor.getColumnIndex("id");


            while (cursor.moveToNext()){

                int id = cursor.getInt(idIx);
                String name = cursor.getString(nameIx);
                Quizz quizz = new Quizz(id,name);
                quizzArrayList.add(quizz);


            }
            quizzAdapter.notifyDataSetChanged();

            cursor.close();





        }catch (Exception e){

        }




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.quizz_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.add_question){
            Intent intent = new Intent(MainActivity.this,DetailsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}