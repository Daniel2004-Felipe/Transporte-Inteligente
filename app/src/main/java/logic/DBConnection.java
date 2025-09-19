package logic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import Modelo.Usuario;

public class DBConnection extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TransporteInteligente.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USUARIOS = "usuarios";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NOMBRE = "nombre";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    public DBConnection(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USUARIOS_TABLE = "CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NOMBRE + " TEXT NOT NULL,"
                + COLUMN_EMAIL + " TEXT UNIQUE NOT NULL,"
                + COLUMN_PASSWORD + " TEXT NOT NULL" + ")";

        db.execSQL(CREATE_USUARIOS_TABLE);

        String Paradero= "CREATE TABLE Paraderos(id_paradero INTEGER PRIMARY KEY AUTOINCREMENT,Posicion INTEGER,nombre TEXT, tipo TEXT,lon REAL,lat REAL )";
        db.execSQL(Paradero);

        String CREATE_RUTAS_TABLE = "CREATE TABLE rutas("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "usuario_id INTEGER,"
                + "nombre_ruta TEXT,"
                + "destino TEXT,"
                + "hora_llegada TEXT,"
                + "distancia REAL,"
                + "tiempo REAL,"
                + "paradero_origen TEXT,"
                + "paradero_destino TEXT,"
                + "FOREIGN KEY(usuario_id) REFERENCES usuarios(id))";
        db.execSQL(CREATE_RUTAS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    public boolean registrarUsuario(Usuario usuario) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        try {
            values.put(COLUMN_NOMBRE, usuario.getNombre());
            values.put(COLUMN_EMAIL, usuario.getEmail());
            values.put(COLUMN_PASSWORD, usuario.getPassword());

            long result = db.insert(TABLE_USUARIOS, null, values);

            return result != -1;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_EMAIL + " = ?";

        try {
            android.database.Cursor cursor = db.rawQuery(query, new String[]{email});
            boolean exists = cursor.getCount() > 0;
            cursor.close();
            return exists;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean verificarCredenciales(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USUARIOS + " WHERE " + COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWORD + " = ?";

        try {
            android.database.Cursor cursor = db.rawQuery(query, new String[]{email, password});
            boolean credencialesCorrectas = cursor.getCount() > 0;
            cursor.close();
            return credencialesCorrectas;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public Usuario obtenerUsuarioPorEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM usuarios WHERE email = ?";

        try {
            Cursor cursor = db.rawQuery(query, new String[]{email});
            if (cursor.moveToFirst()) {
                Usuario usuario = new Usuario();
                usuario.setId(cursor.getInt(0)); // id
                usuario.setNombre(cursor.getString(1)); // nombre
                usuario.setEmail(cursor.getString(2)); // email
                usuario.setPassword(cursor.getString(3)); // password
                cursor.close();
                return usuario;
            }
            cursor.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

}