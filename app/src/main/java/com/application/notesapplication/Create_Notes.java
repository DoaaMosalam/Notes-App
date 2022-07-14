package com.application.notesapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

import Models.Notes;

public class Create_Notes extends AppCompatActivity {
    EditText writeNotes;
    ImageButton btn_Choose_Images;
    ImageView imagesNotes;
    //realtime
    private FirebaseDatabase fDatabase;
    private DatabaseReference mRef;

    FirebaseStorage storage;
    StorageReference storageReference=null;
    //Storage Image
    private static final int REQUEST_CODE_STORAGE_PERMISSION=1;
    private static final int REQUEST_CODE_SELECT_IMAGES=71;
    private String selectedImagePath;
    private Uri filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_notes);

//initialization item in activity_create_Notes(done in menu,create notes in Edit Text).
        writeNotes = findViewById(R.id.edit_input_create_notes);
        btn_Choose_Images=findViewById(R.id.btn_Choose_Images);
        imagesNotes = findViewById(R.id.imageNotes);
//action bar button arrow back
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
//firebase Realtime
        mRef = FirebaseDatabase.getInstance().getReference().child("Notes");

        storageReference = FirebaseStorage.getInstance().getReference();
        addImage_Notes();
        selectedImagePath="";


    }//End Method on Create

//==================================================================================================
    public String getCurrentDate(){
        Calendar calendar = Calendar.getInstance();
        //   SimpleDateFormat mdFormat = new SimpleDateFormat("EEEE hh :mm a");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat mdFormat = new SimpleDateFormat("E, dd MMM yyyy\tHH:mm:ss");
        String strDate = mdFormat.format(calendar.getTime());
        return strDate;
    }
//==================================================================================================
    /*this method to initialization title (done) and arrow aback in menu
     * then bone to save notes
     * and arrow back activity.class */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_options,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.done_note:
                WriteNotesRealTime();
                uploadImage();
                return true;
            case android. R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
//==================================================================================================
    /*This method to write notes in firebase by RealTime.
     * كتابه الملاحظه وحفظها في القايربيز */
    private void WriteNotesRealTime() {
        Notes notes = new Notes();
        String n = writeNotes.getText().toString();
//        int images = Integer.parseInt(imagesNotes.toString());
        int image = imagesNotes.getId();
        if (n.isEmpty()){
            writeNotes.setText(" ");
        }
        else{
            String key = mRef.push().getKey();
            notes = new Notes(key,n,getCurrentDate(),image);
//           notes.setImagePath(selectedImagePath);
            assert key != null;
            mRef.child("notes").child(key).setValue(notes)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        }
                    });
        }
    }
    /*===============================================================================================
     * This method to add image to note from gallery */
    private void addImage_Notes(){
        btn_Choose_Images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Create_Notes.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_CODE_STORAGE_PERMISSION);
                }else{
                    selectImage();
                }

            }
        });
    }
/*These method to storage images in firebase */
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),REQUEST_CODE_SELECT_IMAGES);
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGES);

        if (intent.resolveActivity(getPackageManager())!=null){
            startActivityForResult(intent,REQUEST_CODE_SELECT_IMAGES);
        }
    }
    //Handle result for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==REQUEST_CODE_STORAGE_PERMISSION &&grantResults.length>0){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
                selectImage();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*Handle result for select image */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // checking request code and result code
        // if request code is PICK_IMAGE_REQUEST and
        // resultCode is RESULT_OK
        // then set image in the image view
        if (requestCode == REQUEST_CODE_SELECT_IMAGES
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData();
            try {

                // Setting image on image view using Bitmap
                Bitmap bitmap = MediaStore
                        .Images
                        .Media
                        .getBitmap(
                                getContentResolver(),
                                filePath);
                imagesNotes.setImageBitmap(bitmap);
            } catch (IOException e) {
                // Log the exception
                e.printStackTrace();
            }
        }

    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri,null,null,null,null);
        if (cursor==null){
            filePath=contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int index =cursor.getColumnIndex(" data");
            filePath=cursor.getColumnName(index);
            cursor.close();
        }
        return filePath;
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(Create_Notes.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Create_Notes.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

}