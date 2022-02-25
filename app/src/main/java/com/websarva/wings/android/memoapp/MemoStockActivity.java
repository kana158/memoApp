package com.websarva.wings.android.memoapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemoStockActivity extends AppCompatActivity {
    private List<Map<String,Object>> memoList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memo_stock);

        DatabaseHelper _helper = new DatabaseHelper(MemoStockActivity.this);
        SQLiteDatabase db = _helper.getWritableDatabase();
        String sql = "SELECT * FROM summarizeMemos";
        Cursor cursor = db.rawQuery(sql,null);

        memoList = new ArrayList<>();
        Map<String,Object> memo;
        while(cursor.moveToNext()){
            int idNote = cursor.getColumnIndex("note");
            int idTitle = cursor.getColumnIndex("name");
            int idIndex = cursor.getColumnIndex("_id");
            String title = cursor.getString(idTitle);
            String note = cursor.getString(idNote);
            String id = cursor.getString(idIndex);
            memo = new HashMap<>();
            memo.put("title",title);
            memo.put("note",note);
            memo.put("id",id);

            char[] arr = note.trim().toCharArray();
            int i = 0;
            StringBuilder s = new StringBuilder();
            while(i < arr.length - 1){
                if(Character.isDigit(arr[i]) && arr[i + 1] == '.') {
                    int left = i;
                    StringBuilder str = new StringBuilder();
                    while (left < arr.length - 1 && arr[left] != '\n') {
                        str.append(arr[left]);
                        left++;
                    }
                    s.append(str);
                    s.append("\n");
                }
                i++;
            }
            memo.put("list",s.toString());
            memoList.add(memo);
        }
        cursor.close();
        db.close();

        String[] from = {"title","list"};
        int[] to = {android.R.id.text1,android.R.id.text2};
        SimpleAdapter adapter = new SimpleAdapter(MemoStockActivity.this,memoList,
                android.R.layout.simple_list_item_2,from,to);
        ListView lvMemo = findViewById(R.id.lvMemo);
        lvMemo.setAdapter(adapter);
        lvMemo.setOnItemClickListener(new ListItemClickListener());

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        registerForContextMenu(lvMemo);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        boolean returnVal = true;
        int itemId = item.getItemId();
        if(itemId == android.R.id.home){
            finish();
        }else{
            returnVal = super.onOptionsItemSelected(item);
        }
        return returnVal;
    }

    private class ListItemClickListener implements AdapterView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id){
            @SuppressWarnings("unchecked")
            Map<String,Object> item = (Map<String,Object>) parent.getItemAtPosition(position);
            String title = (String)item.get("title");
            String note = (String)item.get("note");
            String list = (String)item.get("list");
            String idx = (String)item.get("id");

            Intent intent = new Intent(MemoStockActivity.this,MemoShowActivity.class);
            intent.putExtra("title",title);
            intent.putExtra("note",note);
            intent.putExtra("list",list);
            intent.putExtra("id",idx);

            startActivity(intent);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo){
        super.onCreateContextMenu(menu,view,menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context_menu_list,menu);
        menu.setHeaderTitle(R.string.menu_list_context_title);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        boolean returnVal = true;
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)
                item.getMenuInfo();
        int listPosition = info.position;
        Map<String,Object> memo = memoList.get(listPosition);
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.menuListContextDelete:
                String idx = (String)memo.get("id");
                DatabaseHelper _helper = new DatabaseHelper(MemoStockActivity.this);
                SQLiteDatabase db = _helper.getWritableDatabase();
                String sqlDelete = "DELETE FROM summarizeMemos WHERE _id = ?";
                SQLiteStatement stmt = db.compileStatement(sqlDelete);
                stmt.bindString(1,idx);
                stmt.executeUpdateDelete();
                db.close();
                Intent intent = new Intent(MemoStockActivity.this,MemoStockActivity.class);
                startActivity(intent);
                String msg = "削除しました。";
                Toast.makeText(MemoStockActivity.this,msg,Toast.LENGTH_LONG).show();
                break;
            default:
                returnVal = super.onContextItemSelected(item);
                break;
        }
        return returnVal;
    }
}