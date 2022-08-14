package com.example.mynotesapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NotesEditor extends AppCompatActivity {

    EditText editText_title, editText_content;
    ImageView imageView_save;
    Notes notes;
    boolean isOldNote = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_editor);

        imageView_save = findViewById(R.id.imageView_save);
        editText_content = findViewById(R.id.editText_content);
        editText_title = findViewById(R.id.editText_title);
        notes = new Notes();

        try{
            notes = (Notes) getIntent().getSerializableExtra("old_note");
            editText_title.setText(notes.getTitle());
            editText_content.setText(notes.getNotes());
            isOldNote = true;
        }catch (Exception e){
            e.printStackTrace();
        }


        imageView_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editText_title.getText().toString();
                String description = editText_content.getText().toString();

                if(description.isEmpty()){
                    Toast.makeText(NotesEditor.this, "No text detected", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    SimpleDateFormat formatter = new SimpleDateFormat("EEE, hh:mm a MMM dd, yyyy");
                    Date date = new Date();

                    if(!isOldNote){
                        notes = new Notes();
                    }

                    notes.setTitle(title);
                    notes.setNotes(description);
                    notes.setDate(formatter.format(date));

                    Intent intent = new Intent();
                    intent.putExtra("note", notes);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            }
        });

    }
}