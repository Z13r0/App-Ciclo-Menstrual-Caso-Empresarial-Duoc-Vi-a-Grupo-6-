package com.myciclo.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.myciclo.app.registro.RegistroDao
import com.myciclo.app.registro.RegistroEntity

@Database(entities = [RegistroEntity::class], version = 4, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao

    companion object {
        private const val DB_NAME = "myciclo.db"

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE registros ADD COLUMN notasPrivacidad TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE registros RENAME TO registros_old")
                db.execSQL("""
                    CREATE TABLE registros (
                        userId TEXT NOT NULL,
                        fecha TEXT NOT NULL,
                        inicioPeriodo TEXT NOT NULL,
                        nivel TEXT NOT NULL,
                        notas TEXT NOT NULL,
                        foto TEXT NOT NULL,
                        notasPrivacidad TEXT NOT NULL,
                        PRIMARY KEY(userId, fecha)
                    )
                """)
                db.execSQL("""
                    INSERT INTO registros(userId, fecha, inicioPeriodo, nivel, notas, foto, notasPrivacidad)
                    SELECT 'legacy-local', fecha, inicioPeriodo, nivel, notas, foto, notasPrivacidad
                    FROM registros_old
                """)
                db.execSQL("DROP TABLE registros_old")
            }
        }

        @Volatile
        private var instancia: AppDatabase? = null

        fun obtener(context: Context): AppDatabase =
            instancia ?: synchronized(this) {
                instancia ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build()
                    .also { instancia = it }
            }
    }
}