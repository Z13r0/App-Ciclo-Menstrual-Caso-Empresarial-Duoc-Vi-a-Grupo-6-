package com.myciclo.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.myciclo.app.registro.RegistroDao
import com.myciclo.app.registro.RegistroEntity

// base de datos local, aca queda todo el diario y no sale del celular
// version 2: le agregamos la columna foto a la tabla registros
@Database(entities = [RegistroEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun registroDao(): RegistroDao

    companion object {
        private var instancia: AppDatabase? = null

        // asi solo se crea una base de datos para toda la app
        fun obtener(context: Context): AppDatabase {
            if (instancia == null) {
                instancia = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "myciclo.db"
                )
                    // al subir de version room borra y crea la tabla de nuevo
                    // lo dejamos asi porque es un prototipo y los datos son de prueba
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instancia!!
        }
    }
}
