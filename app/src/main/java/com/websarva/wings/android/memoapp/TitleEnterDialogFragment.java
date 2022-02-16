package com.websarva.wings.android.memoapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class TitleEnterDialogFragment extends DialogFragment {
    EditText editText;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState){
        editText = new EditText(getActivity());
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title);
        builder.setMessage(R.string.dialog_msg);
        builder.setView(editText);
        builder.setPositiveButton(R.string.dialog_btn_ok,new DialogButtonClickListener());
        builder.setNegativeButton(R.string.dialog_btn_ng,new DialogButtonClickListener());
        return builder.create();
    }

    private class DialogButtonClickListener implements DialogInterface.OnClickListener{
        @Override
        public void onClick(DialogInterface dialog,int which){
            String msg = "";

            switch(which){
                case DialogInterface.BUTTON_POSITIVE:
                    String dbTitle = editText.getText().toString();

                    String dbMemoStr = getArguments().getString("dbMemo", "") + "。";
                    if(dbMemoStr.length() > 1){
                        DatabaseHelper _helper = new DatabaseHelper(getActivity());
                        SQLiteDatabase db = _helper.getWritableDatabase();
                        String sqlInsert = "INSERT INTO summarizeMemos (_id,name,note) VALUES (?,?,?)";
                        SQLiteStatement stmt = db.compileStatement(sqlInsert);
                        stmt.bindString(2,dbTitle);
                        stmt.bindString(3,dbMemoStr);
                        stmt.executeInsert();

                        msg = dbTitle + getString(R.string.dialog_ok_toast);
                        db.close();

                        Intent intent = new Intent(getActivity(),MemoStockActivity.class);
                        startActivity(intent);
                    }else{
                        msg = getString(R.string.dialog_attention);
                    }
                    break;
                case DialogInterface.BUTTON_NEGATIVE:
                    msg = getString(R.string.dialog_ng_toast);
                    break;
            }
            Toast.makeText(getActivity(),msg,Toast.LENGTH_LONG).show();
        }
    }
}
