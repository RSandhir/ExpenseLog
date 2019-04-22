package com.rsandhir.expenselog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String Database_Name = "FinanceDb.db";
    public static final String Table_Name = "Spendings";
    public static final String Col1="Description";
    public static final String Col2 = "Amount";
    public DatabaseHelper(Context context) {
        super(context,Database_Name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql_cmd = "CREATE TABLE " + Table_Name + "(Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,Description TEXT,Amount INTEGER)";
        sqLiteDatabase.execSQL(sql_cmd);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " +Table_Name);
        onCreate(sqLiteDatabase);
    }

    public boolean insertdata(String Description, int Amount) {
        SQLiteDatabase sqLiteDatabase=this.getWritableDatabase();
        ContentValues cv=new ContentValues();
        cv.put(Col1,Description);
        cv.put(Col2, Amount);
        long result =sqLiteDatabase.insert(Table_Name,null,cv);
        sqLiteDatabase.close();
        return result != -1;
    }
    public Cursor getAllData(){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor result=sqLiteDatabase.rawQuery("select * from " +Table_Name,null);
        return result;
    }

    public void deleteSelected(String[] arrTempList) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        for (int i = 0; i < arrTempList.length; i++) {
            String exec_sql = "DELETE FROM " + Table_Name + " WHERE Id=" + arrTempList[i];
            sqLiteDatabase.execSQL(exec_sql);
        }
    }
}
