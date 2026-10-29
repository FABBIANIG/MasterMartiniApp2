package cl.duoc.dsy1105.mastermartini.data.local

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import cl.duoc.dsy1105.mastermartini.model.ClienteProspecto

/**
 * Gestor de Base de Datos Local SQLite (IE 2.3.1).
 * Almacena de forma persistente en el dispositivo los perfiles de clientes/prospectos
 * y los IDs de las recetas/cursos marcados como favoritos.
 */
class MasterMartiniDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "master_martini_local.db"
        private const val DATABASE_VERSION = 1

        // Tabla de Clientes / Prospectos segmentados (Punto 3.2 del caso)
        private const val TABLE_CLIENTES = "clientes_prospectos"
        private const val COL_ID = "id"
        private const val COL_NOMBRE = "nombre_completo"
        private const val COL_RUT = "rut_empresa"
        private const val COL_CORREO = "correo"
        private const val COL_TELEFONO = "telefono"
        private const val COL_DIRECCION = "direccion"
        private const val COL_TIPO_NEGOCIO = "tipo_negocio"
        private const val COL_TAMANO_NEGOCIO = "tamano_negocio"
        private const val COL_CATEGORIAS = "categorias_interes"
        private const val COL_FOTO_URI = "foto_uri"

        // Tabla de Recetas/Cursos Favoritos
        private const val TABLE_FAVORITOS = "favoritos_recetas"
        private const val COL_RECETA_ID = "receta_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Creación de tabla para perfiles de usuarios gastronómicos
        val createClientesTable = """
            CREATE TABLE $TABLE_CLIENTES (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_RUT TEXT NOT NULL,
                $COL_CORREO TEXT NOT NULL,
                $COL_TELEFONO TEXT NOT NULL,
                $COL_DIRECCION TEXT NOT NULL,
                $COL_TIPO_NEGOCIO TEXT NOT NULL,
                $COL_TAMANO_NEGOCIO TEXT NOT NULL,
                $COL_CATEGORIAS TEXT NOT NULL,
                $COL_FOTO_URI TEXT
            )
        """.trimIndent()

        // Creación de tabla para persistir cursos y recetas favoritas
        val createFavoritosTable = """
            CREATE TABLE $TABLE_FAVORITOS (
                $COL_RECETA_ID INTEGER PRIMARY KEY
            )
        """.trimIndent()

        db.execSQL(createClientesTable)
        db.execSQL(createFavoritosTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CLIENTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_FAVORITOS")
        onCreate(db)
    }

    /**
     * Guarda un nuevo perfil de cliente/prospecto en SQLite local.
     */
    fun guardarCliente(cliente: ClienteProspecto): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COL_NOMBRE, cliente.nombreCompleto)
            put(COL_RUT, cliente.rutEmpresa)
            put(COL_CORREO, cliente.correo)
            put(COL_TELEFONO, cliente.telefono)
            put(COL_DIRECCION, cliente.direccion)
            put(COL_TIPO_NEGOCIO, cliente.tipoNegocio)
            put(COL_TAMANO_NEGOCIO, cliente.tamanoNegocio)
            put(COL_CATEGORIAS, cliente.categoriasInteres.joinToString(","))
            put(COL_FOTO_URI, cliente.fotoLocalUri)
        }
        return db.insert(TABLE_CLIENTES, null, values)
    }

    /**
     * Obtiene todos los clientes registrados localmente (ordenados del más reciente al más antiguo).
     */
    fun obtenerClientesRegistrados(): List<ClienteProspecto> {
        val lista = mutableListOf<ClienteProspecto>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_CLIENTES ORDER BY $COL_ID DESC", null)

        cursor.use {
            while (it.moveToNext()) {
                val categoriasStr = it.getString(it.getColumnIndexOrThrow(COL_CATEGORIAS))
                val cliente = ClienteProspecto(
                    id = it.getInt(it.getColumnIndexOrThrow(COL_ID)),
                    nombreCompleto = it.getString(it.getColumnIndexOrThrow(COL_NOMBRE)),
                    rutEmpresa = it.getString(it.getColumnIndexOrThrow(COL_RUT)),
                    correo = it.getString(it.getColumnIndexOrThrow(COL_CORREO)),
                    telefono = it.getString(it.getColumnIndexOrThrow(COL_TELEFONO)),
                    direccion = it.getString(it.getColumnIndexOrThrow(COL_DIRECCION)),
                    tipoNegocio = it.getString(it.getColumnIndexOrThrow(COL_TIPO_NEGOCIO)),
                    tamanoNegocio = it.getString(it.getColumnIndexOrThrow(COL_TAMANO_NEGOCIO)),
                    categoriasInteres = if (categoriasStr.isNotEmpty()) categoriasStr.split(",") else emptyList(),
                    fotoLocalUri = it.getString(it.getColumnIndexOrThrow(COL_FOTO_URI))
                )
                lista.add(cliente)
            }
        }
        return lista
    }

    /**
     * Alterna (agrega o quita) una receta o curso de la tabla de favoritos en SQLite.
     */
    fun alternarFavorito(recetaId: Int): Boolean {
        val db = writableDatabase
        val esFavorito = obtenerFavoritosIds().contains(recetaId)
        return if (esFavorito) {
            db.delete(TABLE_FAVORITOS, "$COL_RECETA_ID = ?", arrayOf(recetaId.toString()))
            false
        } else {
            val values = ContentValues().apply { put(COL_RECETA_ID, recetaId) }
            db.insert(TABLE_FAVORITOS, null, values)
            true
        }
    }

    /**
     * Devuelve el conjunto de IDs de recetas marcadas como favoritas en SQLite.
     */
    fun obtenerFavoritosIds(): Set<Int> {
        val ids = mutableSetOf<Int>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT $COL_RECETA_ID FROM $TABLE_FAVORITOS", null)
        cursor.use {
            while (it.moveToNext()) {
                ids.add(it.getInt(0))
            }
        }
        return ids
    }
}