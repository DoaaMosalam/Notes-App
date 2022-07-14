package com.application.notesapplication;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Models.Notes;
import Models.Notes_Adapter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ImageButton add_notes;
    private EditText ed_Search;
    private CircularProgressIndicator progressIndicator;
    //=============================================================
    RecyclerView notes_rv;
    Notes_Adapter adapter;
    List<Notes> notes_list = new ArrayList<>();
    List<Notes> displayList = new ArrayList<>();
    //==============================================================
    FirebaseDatabase fDatabase;
    DatabaseReference mRef;
    //=============================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        add_notes = findViewById(R.id.btn_add_notes);
        ed_Search = findViewById(R.id.edit_search);
        progressIndicator=findViewById(R.id.progress_circular);
//====================================================================================================
        initRecycler();
        new ItemTouchHelper(itemTouchHelper).attachToRecyclerView(notes_rv);
       setAdd_Notes();
//====================================================================================================
        /*This method search notes on list
         * هذه الميثود للبحث عن الملاحظه موجوده في الليست*/
        ed_Search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString()); }
        });
//====================================================================================================
        mRef = FirebaseDatabase.getInstance().getReference().child("Notes");
        addNotesEventListener(mRef);
    }//End method onCreate
//This method go to page write to add notes by used to object Intent..
    private void setAdd_Notes(){
        add_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowDialogAddNotes();
            }
        });
    }
    private void filter(String text) {
        List<Notes> filterList = new ArrayList<>();
        for (Notes item:notes_list){
            if (item.getContents_Notes().contains(text.toLowerCase())){
                filterList.add(item);
            }
        }
        adapter.FilterList(filterList);
    }
//==================================================================================================
    //initialization recycler view notes.
    private void initRecycler() {
        notes_rv = findViewById(R.id.rv_notes);
        notes_rv.setLayoutManager(new LinearLayoutManager(this));
        notes_rv.setHasFixedSize(true);
        notes_rv.setItemAnimator(new DefaultItemAnimator());
        notes_rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        adapter = new Notes_Adapter(getApplicationContext(), notes_list);
        notes_rv.setAdapter(adapter);
    }
//==================================================================================================

    //This method when click button (add_notes) go to write notes(Create_Notes.class).
    private void ShowDialogAddNotes() {
        startActivity(new Intent(getApplicationContext(),Create_Notes.class));
    }
    //==============================================================================================
    /*This method is to retrieve data from firebase and arrange it in the list so that the last note appears in the first list
     * هذه الميثود لاسترجاع البيانات من الفايربيز وترتيبها في الليست بحيث النوت الاخيره تظهر في اول الليست*/
    public void addNotesEventListener(DatabaseReference mPostReference) {
        // [START post_value_event_listener]
        ValueEventListener notesListener = new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                notes_list.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot noteData:dataSnapshot.getChildren()) {
                        Notes notes = noteData.getValue(Notes.class);
//                        notes.setImagePath(Integer.parseInt(noteData.child("imagePath").getValue().toString()));
                        notes_list.add(notes);
                        adapter.notifyDataSetChanged();
                    }
                }
                //reverse list add last note in top list.
                Collections.reverse(notes_list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        };
        mPostReference.child("notes").addValueEventListener(notesListener);
        // [END post_value_event_listener]
    }
    //==================================================================================================
    private final ItemTouchHelper.SimpleCallback itemTouchHelper = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
            |ItemTouchHelper.DOWN,ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT) {

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int startPosition = viewHolder.getAdapterPosition();
            int endPosition = target.getAdapterPosition();
            Collections.swap(notes_list, startPosition, endPosition);
            adapter.notifyItemMoved(startPosition, endPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            // Notes notes = new Notes(key, n, getCurrentDate(), String.valueOf(selectedImagePath));
            Notes notes = new Notes();
            int position = viewHolder.getAdapterPosition();
            switch (direction) {
                case ItemTouchHelper.RIGHT:
                    /*Archive notes from recyclerview.*/
                    String deleteNotes = String.valueOf(notes_list.get(position));
                    notes_list.remove(position);
                    adapter.notifyItemRemoved(position);
                    String content_notes = notes.getContents_Notes();
                    Snackbar.make(notes_rv,  "Note is Archive", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    addNotesEventListener(mRef);
                                    //   notes_list.add(position,notes_list.get(Integer.parseInt(deleteNotes.toString())));
                                    //   adapter.notifyItemInserted(position);
                                }
                            }).show();
                    break;
                /*this code to  Archive item */
                case ItemTouchHelper.LEFT:
                    /*update notes by alert dialog*/
                    EditText editText = new EditText(MainActivity.this);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Update").setIcon(R.drawable.edit);
                    builder.setCancelable(true);
                    builder.setView(editText);

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            displayList.clear();
                            displayList.addAll(notes_list);
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String key = notes.getId();
                            String note_contents = editText.getText().toString();
//                            String note_contents = notes.getContents_Notes();
                            notes.setContents_Notes(note_contents);
                            mRef.child("notes").child(key).setValue(notes)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            addNotesEventListener(mRef);
                                            Toast.makeText(MainActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                                            onBackPressed();
                                        }
                                    });
                            notes_list.add(notes);
                            adapter.notifyItemChanged(position);
                        }
                    });
                    builder.show();
                    break;
            }
        }
    };
}