package com.kostya_zinoviev.training_db;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String MY_TAG = "myTag";
    private EditText edName, edEmail, edId;
    private Button btnAdd, btnRead, btnDelete, btnUpdate,btnDeleteID;
    private DbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        dbHelper = new DbHelper(this);
    }

    private void init() {
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edId = findViewById(R.id.edId);
        btnAdd = findViewById(R.id.btnAdd);
        btnRead = findViewById(R.id.btnRead);
        btnDelete = findViewById(R.id.btnDelete);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDeleteID = findViewById(R.id.btnDeleteID);
        btnAdd.setOnClickListener(this);
        btnRead.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnDeleteID.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        String name = edName.getText().toString().trim();
        String email = edEmail.getText().toString().trim();
        String ID = edId.getText().toString().trim();
        ContentValues cv = new ContentValues();
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        switch (id) {
            case R.id.btnAdd:
                //По нажатию на добавить проверем наши поляна пустоту
                isEmptyMethod(name, email);
                //По нажатию на кнопку "Добавить"
                //Будем добалять данные key/value,где key - название столбца
                cv.put(DataTable.COLUMN_NAME, name);
                cv.put(DataTable.COLUMN_EMAIL, email);
                //Метод insert() вставляет записб в таблицу + возвращает id вставленной записи
                long rowId = db.insert(DataTable.TABLE_NAME, null, cv);
                Log.i(MY_TAG, "Добавлегна запись id = " + "" + rowId);
                break;
            case R.id.btnRead:
                //По нажатию на кнопку "Read" считываем данные из бд
                Cursor c = db.query(DataTable.TABLE_NAME, null, null,
                        null, null,
                        null, null, null);
                //if есть мы стоим на первоой строке,то получем номер колонки по наззванию колонки
                if (c.moveToFirst()) {
                    int idColumnIndex = c.getColumnIndex(DataTable.COLUMN_ID);
                    int nameColumnIndex = c.getColumnIndex(DataTable.COLUMN_NAME);
                    int emailColumnIndex = c.getColumnIndex(DataTable.COLUMN_EMAIL);
                    //Пока есть следующая строка,считываем данные
                    do {
                        int idValue = c.getInt(idColumnIndex);
                        String nameValue = c.getString(nameColumnIndex);
                        String emailValue = c.getString(emailColumnIndex);

                        Log.i(MY_TAG, "id = " + "" + idValue);
                        Log.i(MY_TAG, "name = " + "" + nameValue);
                        Log.i(MY_TAG, "email = " + "" + emailValue);
                    } while (c.moveToNext());
                } else {
                    Log.i(MY_TAG, "0 rows");
                }
                break;
            case R.id.btnDelete:
                //Если удаляем данные из таблицы
                //Сколько записей было удалено
                int countDelete = db.delete(DataTable.TABLE_NAME, null, null);
                Log.i(MY_TAG, "Записей было удалено " + "" + countDelete);
                break;
            case R.id.btnUpdate:
                //При обновлении записи по id
                //Проверяем наш id на пустоту
                isEmptyMethod(name,email);
                if(ID.equalsIgnoreCase("")){
                    //Если пустой,то break
                    break;
                }
                //Иначе обновляем запись
                cv.put(DataTable.COLUMN_NAME,name);
                cv.put(DataTable.COLUMN_EMAIL,email);
                //Указываем,что мы обновляем по id,с помощью "id = " + ID
                int updatedRows = db.update(DataTable.TABLE_NAME,cv,"id = " + ID,null);
                Log.i(MY_TAG,"Строк было обновлено " + ""  + updatedRows);
                break;
            case R.id.btnDeleteID:
                //Кнопка удаления по id
                //Как и в upDate проверяем id на пустоту
                if(ID.equalsIgnoreCase("")){
                    //Если пустой,то break
                    break;
                }
                //Иначе удаляем запись по id
                //Возвращает колличество удаленных записей
                //Указываем,что удаляем по id,с помощью "id = " + ID
                int countDeletedID = db.delete(DataTable.TABLE_NAME,"id = " + ID,null);
                Log.i(MY_TAG,"Строк по id было удалено " + "" + countDeletedID);
                break;
        }
        //Закрываем доступ к бд
        db.close();

    }


    private void isEmptyMethod(String name, String email) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email)) {
            Toast.makeText(MainActivity.this, "Зполните пустые поля", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context context) {
            super(context, DataTable.TABLE_NAME, null, DataTable.DATA_BASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.i(MY_TAG, "onCreateDB");
            String SQL = "create table " + DataTable.TABLE_NAME + "("
                    + DataTable.COLUMN_ID + " integer primary key autoincrement,"
                    + DataTable.COLUMN_NAME + " text,"
                    + DataTable.COLUMN_EMAIL + " text" + ");";
            db.execSQL(SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }

    }

}
