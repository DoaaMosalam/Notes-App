package com.application.notesapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import Models.Notes;

public  class Details_Notes extends AppCompatActivity {
    TextView contain_Note;
    ImageView image;
    private Notes notes ;
    private  List<Notes> notesList = new ArrayList<>();
    private FirebaseDatabase fDatabase;
    private DatabaseReference mRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_notes);

        contain_Note = findViewById(R.id.tv_noteDetailsContain);
        image=findViewById(R.id.image_notes_Details);
        notes = new Notes();
//=======================================================================================================
        //initialization action bar button back.
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        //=======================================================================================================
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null){
            contain_Note.setMovementMethod(new ScrollingMovementMethod());
            String content = bundle.getString("content");
            String id = bundle.getString("id");
            int image = bundle.getInt("image");

            notes.setId(id);
            contain_Note.setText(content);
            notes.setContents_Notes(content);
            notes.setImagePath(image);
        }
//        Intent data = getIntent();
//        contain_Note.setMovementMethod(new ScrollingMovementMethod());
//        String content = data.getStringExtra("content");
//        String id = data.getStringExtra("id");
//        String image = data.getStringExtra("image");
//
//        notes.setId(id);
//        contain_Note.setText(content);
//        notes.setContents_Notes(content);
//        notes.setImagePath(image);

        /*لتغير لون خلفيه الصفحه بنفس لون الملاحظه عند الضغط عليها
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            contain_Note.setBackgroundColor(getResources().getColor(bundle.getInt("code",0),null));
        }
        mRef=FirebaseDatabase.getInstance().getReference().child("Notes");

    }//End Method on Create

    //=======================================================================================================
    /*This method menu and action bar button back*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_option_details_notes,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.delela_note:
                delete_Notes();
                break;
            case R.id.archive_note:
                String key = mRef.push().getKey();
                String notesId= notes.getId();
                String archiveNotes = notes.getContents_Notes().toString();
                assert key != null;
                mRef.child(key).child(notesId).removeValue();
                Toast.makeText(this, "Archive Notes", Toast.LENGTH_SHORT).show();
                break;
            case R.id.share_note:
                startActivity(new Intent(Details_Notes.this,Share_Notes.class));
                break;
            case R.id.update_note:
                edit_Notes();
                break;
            case android. R.id.home:
                this.finish();
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }
//===============================================================================================

    /*this method is update notes and save to firebase
     * هذه الميثود للتعديل علي الملاحظه */
    private void edit_Notes(){
        String key = notes.getId();
        String notes_Content=contain_Note.getText().toString();
//         notes = new Notes(key,notes_Content);
        System.out.println(notes_Content + " is it null?");

        notes.setContents_Notes(notes_Content);

        assert key != null;
        mRef.child("notes").child(key).setValue(notes)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Details_Notes.this, "Updated!", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                });

    }
    /*هذه الميثود لحذف الملاحظه من القايربيز
     * Delete note from firebase and Recyclerview */
    private void delete_Notes() {
        String key = notes.getId();
        //  String id = notes.getId().toString();
        assert key != null;
        mRef.child("notes").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(Details_Notes.this, "Note delete", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Details_Notes.this, MainActivity.class));
                }
            }
        });
    }
}