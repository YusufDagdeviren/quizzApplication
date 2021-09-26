package com.yusufdagdeviren.quizzaplication;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.yusufdagdeviren.quizzaplication.databinding.ActivityDetailsBinding;

import java.io.ByteArrayOutputStream;

public class DetailsActivity extends AppCompatActivity {

    ActivityDetailsBinding binding;
    SQLiteDatabase sqLiteDatabase;
    ActivityResultLauncher<Intent> activityResultLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        registerLauncher();

        sqLiteDatabase = this.openOrCreateDatabase("Quizz",MODE_PRIVATE,null);

        Intent intent = getIntent();
        String info = intent.getStringExtra("info");


        if(info.matches("new")){

            binding.imageView.setImageResource(R.drawable.select);
            binding.questionNameText.setText("");
            binding.lessonNameText.setText("");
            binding.dateText.setText("");
            binding.button.setVisibility(View.VISIBLE);
            binding.deleteButton.setVisibility(View.INVISIBLE);


        }else{

             id = intent.getIntExtra("id",0);
            binding.button.setVisibility(View.INVISIBLE);
            binding.deleteButton.setVisibility(View.VISIBLE);
            try{

                Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM quizz WHERE  id = ?",new String[] {String.valueOf(id)});
                int questionNameTextIx = cursor.getColumnIndex("questionNameText");
                int lessonNameTextIx = cursor.getColumnIndex("lessonNameText");
                int dateTextIx = cursor.getColumnIndex("dateText");
                int selectImageIx = cursor.getColumnIndex("selectImage");

                while (cursor.moveToNext()){

                    binding.questionNameText.setText(cursor.getString(questionNameTextIx));
                    binding.lessonNameText.setText(cursor.getString(lessonNameTextIx));
                    binding.dateText.setText(cursor.getString(dateTextIx));
                    byte[] bytes = cursor.getBlob(selectImageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }
            cursor.close();

            }catch (Exception e){

            e.printStackTrace();

            }


        }


    }


    public void selectImage(View view){

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)){

                Snackbar.make(view,"Permission needed Galery",Snackbar.LENGTH_INDEFINITE).setAction("Give Permisson", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //request permission
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                    }
                }).show();

            }else{
                //request permission
                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            }



        }else{
            // go to galerry
            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            activityResultLauncher.launch(intentToGallery);

        }



    }
    public void save(View view){

        String questionNameText = binding.questionNameText.getText().toString();
        String lessonNameText = binding.lessonNameText.getText().toString();
        String dateText = binding.dateText.getText().toString();
        // Bitmap'i byte array'e çevir
        Bitmap smallerImage = makeSmallerImage(selectedImage,300);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        smallerImage.compress(Bitmap.CompressFormat.PNG,50,byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        try {
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS quizz (id INTEGER PRIMARY KEY, questionNameText VARCHAR, lessonNameText VARCHAR, dateText VARCHAR, selectImage BLOB)");

            String data = "INSERT INTO quizz (questionNameText, lessonNameText, dateText, selectImage) VALUES (?, ?, ?, ?)";
            SQLiteStatement sqLiteStatement = sqLiteDatabase.compileStatement(data);
            sqLiteStatement.bindString(1,questionNameText);
            sqLiteStatement.bindString(2,lessonNameText);
            sqLiteStatement.bindString(3,dateText);
            sqLiteStatement.bindBlob(4,byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){

            e.printStackTrace();

        }

        Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void delete(View view){

        try{

            sqLiteDatabase.execSQL("DELETE FROM quizz WHERE id = ?",new String[] {String.valueOf(id)});


        }catch (Exception e){

        e.printStackTrace();

        }
        Intent intent = new Intent(DetailsActivity.this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);





    }

    public Bitmap makeSmallerImage(Bitmap bitmap , int maxSize){

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        double bitmapRatio = (double) width / (double) height;

        if(bitmapRatio>1){
            width = maxSize;
            height = (int) (width/bitmapRatio);

        }else{
            height = maxSize;
            width = (int) (height*bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap,width,height,true);

    }




    public void registerLauncher(){


        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

            if(result.getResultCode() == Activity.RESULT_OK){

                Intent intentFromResult = result.getData();
                if(intentFromResult != null){

                    Uri imageData = intentFromResult.getData();
                    try{

                        if(Build.VERSION.SDK_INT >= 28){
                            // Uri'ı bitmap e çevirme işlemi
                            ImageDecoder.Source source = ImageDecoder.createSource(DetailsActivity.this.getContentResolver(),imageData);
                            selectedImage = ImageDecoder.decodeBitmap(source);
                            binding.imageView.setImageBitmap(selectedImage);

                        }else{
                            // Uri'ı bitmap e çevirme işlemi
                            selectedImage = MediaStore.Images.Media.getBitmap(DetailsActivity.this.getContentResolver(),imageData);
                            binding.imageView.setImageBitmap(selectedImage);

                        }



                    }catch (Exception e){
                        e.printStackTrace();
                    }



                }



            }









            }
        });












    permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {

            if(result){
                //permission granted
                Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                activityResultLauncher.launch(intentToGallery);


            }else{
                //permission denied
                Toast.makeText(DetailsActivity.this,"Permission Denied",Toast.LENGTH_LONG).show();
            }


        }
    });



    }

}