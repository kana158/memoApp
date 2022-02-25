package com.websarva.wings.android.memoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MemoShowActivity extends AppCompatActivity {
    private int count;
    String idx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_show);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");
        String list = intent.getStringExtra("list");
        idx = intent.getStringExtra("id");
        EditText titleText = findViewById(R.id.titleText);
        EditText memoText = findViewById(R.id.memoText);
        titleText.setText(title);
        if(note.length() != 0){
            memoText.setText(note.substring(0,note.length()-1));
        }

        count = list.split("\n").length + 1;

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnSend = findViewById(R.id.btnSend);
        MemoShowActivity.btnListener listener = new MemoShowActivity.btnListener();
        btnAdd.setOnClickListener(listener);
        btnClear.setOnClickListener(listener);
        btnSend.setOnClickListener(listener);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;
        int itemId = item.getItemId();
        if(itemId == android.R.id.home) {
            finish();
        }else if(itemId == R.id.menuListGoTopList){
            Intent intent = new Intent(MemoShowActivity.this,MainActivity.class);
            startActivity(intent);
        }else{
            returnVal = super.onOptionsItemSelected(item);
        }
        return returnVal;
    }

    private class btnListener implements View.OnClickListener{
        @Override
        public void onClick(View view){
            EditText input = findViewById(R.id.memoText);
            String inputStr = input.getText().toString();
            int id = view.getId();

            switch(id){
                case R.id.btnAdd:
                    if(inputStr.length() == 0){
                        count = 1;
                    }
                    StringBuilder str = new StringBuilder();
                    str.append(inputStr);
                    str.append("\n");
                    str.append(count++);
                    str.append(".");
                    input.setText(str.toString());
                    input.setSelection(str.length());
                    break;
                case R.id.btnClear:
                    input.setText("");
                    count = 1;
                    break;
                case R.id.btnSend:
                    EditText titleText = findViewById(R.id.titleText);
                    String titleTextStr = titleText.getText().toString();
                    DatabaseHelper _helper = new DatabaseHelper(MemoShowActivity.this);
                    SQLiteDatabase db = _helper.getWritableDatabase();
                    String inputStr1 = inputStr + "a";
                    String sqlUpdate = "UPDATE summarizeMemos SET name = '" +titleTextStr + "', note = '" + inputStr1 +  "' WHERE _id = " + idx;
                    db.execSQL(sqlUpdate);
                    db.close();
                    Intent intent = new Intent(MemoShowActivity.this,MainActivity.class);
                    startActivity(intent);
                    String msg = "更新しました。";
                    Toast.makeText(MemoShowActivity.this,msg,Toast.LENGTH_LONG).show();
                    finish();
                    break;
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_gotop_menu_list,menu);
        return true;
    }
}