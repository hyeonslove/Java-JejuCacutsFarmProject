package SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;

import Entity.CactusForm;

public class SQLiteControl implements Serializable {
    SQLiteHelper helper;
    SQLiteDatabase sqlite;

    public SQLiteControl(SQLiteHelper _helper) {
        this.helper = _helper;
    }

    public Object GetData(String sql) {
        ArrayList<CactusForm> temp = new ArrayList<CactusForm>();
        sqlite = helper.getReadableDatabase();
        Cursor c = sqlite.rawQuery(sql, null);
        if (c.moveToFirst()) {
            do {
                temp.add(new CactusForm(c.getInt(0), c.getString(1), c.getInt(2)));
            } while (c.moveToNext());
        }
        c.close();
        return temp;
    }

    public boolean Insert(CactusForm cactus) {
        try {
            if (cactus.getIndex() < 0)
                return false;
            int max_uid = GetMaxUid();
            if(cactus.getIndex() > max_uid){
                cactus.setIndex(max_uid);
            }
            ExecuteSQL("UPDATE CACTUSLIST set cactus_uid = cactus_uid + 1 WHERE cactus_uid >= " + cactus.getIndex());
            ArrayList<CactusForm> temp = (ArrayList<CactusForm>) GetData("SELECT * FROM CACTUSLIST where cactus_uid = " + cactus.getIndex());
            if (temp.size() == 0) {
                ExecuteSQL("INSERT INTO CACTUSLIST(cactus_uid, cactus_name, cactus_price) VALUES " +
                        "(" + cactus.getIndex() + ",'" + cactus.getTitle() + "'," + cactus.getPrice() + ");");
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public boolean Delete(int index) {
        try {
            if (ExecuteSQL("DELETE FROM CACTUSLIST WHERE cactus_uid = " + index)) {
                ExecuteSQL("UPDATE CACTUSLIST set cactus_uid = cactus_uid - 1 WHERE cactus_uid > " + index);
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public boolean Update(CactusForm cactus) {
        try {
            if (cactus.getIndex() < 0)
                return false;
            ExecuteSQL("SELECT * FROM CACTUSLIST where cactus_uid = " + cactus.getIndex());
            ArrayList<CactusForm> temp = (ArrayList<CactusForm>) GetData("SELECT * FROM CACTUSLIST where cactus_uid = " + cactus.getIndex());
            if (temp.size() > 0) {
                ExecuteSQL("UPDATE CACTUSLIST SET cactus_name = '" + cactus.getTitle() + "'," +
                        "cactus_price = " + cactus.getPrice() + " WHERE cactus_uid = " + cactus.getIndex() + ";");
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public int GetMaxUid(){
        try {
            sqlite = helper.getReadableDatabase();
            Cursor c = sqlite.rawQuery("SELECT MAX(cactus_uid) FROM CACTUSLIST", null);
            int temp = 0;
            if (c.moveToFirst()) {
                temp = c.getInt(0);
            }else{
                throw new Exception();
            }
            c.close();
            return temp + 1;
        } catch (Exception ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public boolean ExecuteSQL(String sql) {
        try {
            sqlite = helper.getWritableDatabase();
            sqlite.execSQL(sql);
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

}
