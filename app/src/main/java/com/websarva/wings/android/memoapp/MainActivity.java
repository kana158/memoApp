package com.websarva.wings.android.memoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private int count = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnAdd = findViewById(R.id.btnAdd);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnSend = findViewById(R.id.btnSend);
        btnListener listener = new btnListener();
        btnAdd.setOnClickListener(listener);
        btnClear.setOnClickListener(listener);
        btnSend.setOnClickListener(listener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_options_menu_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;
        int itemId = item.getItemId();
        switch(itemId){
            case R.id.menuListOptionList:
                Intent intent = new Intent(MainActivity.this,MemoStockActivity.class);
                startActivity(intent);
                break;
            default:
                returnVal = super.onOptionsItemSelected(item);
                break;
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
                    StringBuilder str = new StringBuilder();
                    if(inputStr.length() != 0) {
                        str.append(inputStr);
                        str.append("\n");
                    }else{
                        count = 1;
                    }
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
                    TitleEnterDialogFragment dialogFragment = new TitleEnterDialogFragment();
                    Bundle args = new Bundle();
                    args.putString("dbMemo",inputStr);
                    dialogFragment.setArguments(args);
                    dialogFragment.show(getSupportFragmentManager(),"TitleEnterDialogFragment");
                    break;
            }
        }
    }
}