package com.application.notesapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import Models.Notes;

public class Share_Notes extends AppCompatActivity {
    private TextView name_Notes;
    private ImageButton facebook;
    private ImageButton google;
    private  ImageButton instagram;
    private ImageButton linkedIn;
    private ImageButton office;
    private Notes notes= new Notes();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_notes);
        name_Notes = findViewById(R.id.tv_content_Note_share);
        facebook = findViewById(R.id.facebook);
        google=findViewById(R.id.google);
        instagram=findViewById(R.id.instagram);
        linkedIn=findViewById(R.id.linkedin);
        office=findViewById(R.id.office);
        //========================================================================================
        setFacebook();
        setGoogle();
        setInstagram();
        setLinkedIn();
        setOffice();
        //action bar button arrow back
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        sendText();
    }//End Method On create
    //==============================================================================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case android. R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sendText(){
        Bundle bundle = getIntent().getExtras();
        if (bundle!=null) {
//            name_Notes.setMovementMethod(new ScrollingMovementMethod());
            String content = bundle.getString("content");
            String id = bundle.getString("id");
            int image = bundle.getInt("image");

            notes.setId(id);
            name_Notes.setText(content);
            notes.setContents_Notes(content);
            notes.setImagePath(image);
        }

    }
    private void setFacebook(){
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("fb-messenger://user/101631428274763");
//                Uri uri = Uri.parse("https://www.facebook.com/");
                Intent sendFacebook = new Intent(Intent.ACTION_SEND,uri);
                sendFacebook.setType("text/plain");
                sendFacebook.putExtra(Intent.EXTRA_SUBJECT,notes.getContents_Notes());
                // Adding the text to share using putExtra
                sendFacebook.putExtra(Intent.EXTRA_TEXT, notes.getContents_Notes());
                startActivity(Intent.createChooser(sendFacebook, "Share Via"));

            }
        });
    }
    //==============================================================================================
    private void setGoogle(){
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendGoogle = new Intent(Intent.ACTION_SEND);
                sendGoogle.setType("text/plain");
                sendGoogle.putExtra(Intent.EXTRA_SUBJECT,notes.getContents_Notes());

                // Adding the text to share using putExtra
                sendGoogle.putExtra(Intent.EXTRA_TEXT, notes.getContents_Notes());
                startActivity(Intent.createChooser(sendGoogle, "Share Via"));

            }
        });
    }
    //==============================================================================================

    private void setInstagram(){
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendInstagram = new Intent(Intent.ACTION_SEND);
                sendInstagram.setType("text/plain");
                sendInstagram.putExtra(Intent.EXTRA_SUBJECT,notes.getContents_Notes());

                // Adding the text to share using putExtra
                sendInstagram.putExtra(Intent.EXTRA_TEXT, notes.getContents_Notes());
                startActivity(Intent.createChooser(sendInstagram, "Share Via"));

            }
        });
    }
    //==============================================================================================
    private void setLinkedIn(){
        linkedIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent sendLinkedIn = new Intent(Intent.ACTION_SEND);
                sendLinkedIn.setType("text/plain");
                sendLinkedIn.putExtra(Intent.EXTRA_SUBJECT,notes.getContents_Notes());

                // Adding the text to share using putExtra
                sendLinkedIn.putExtra(Intent.EXTRA_TEXT, notes.getContents_Notes());
                startActivity(Intent.createChooser(sendLinkedIn, "Share Via"));

            }
        });
    }
    //==============================================================================================
    private void setOffice(){
        office.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 Uri uri = Uri.parse("fb-messenger://user/101631428274763");
                Intent sendOffice = new Intent(Intent.ACTION_VIEW,uri);
                sendOffice.setType("text/plain");
                sendOffice.putExtra(Intent.EXTRA_SUBJECT,notes.getContents_Notes());
                // Adding the text to share using putExtra
                sendOffice.putExtra(Intent.EXTRA_TEXT, notes.getContents_Notes());
                startActivity(Intent.createChooser(sendOffice, "Share Via"));

            }
        });
    }

}