package com.example.gridrates.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gridrates.data.local.dao.RateDao
import com.example.gridrates.data.local.entity.RatePlan
import com.example.gridrates.data.local.entity.RateSchedule
import com.example.gridrates.data.local.entity.UtilityProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalTime

@Database(
    entities = [UtilityProvider::class, RatePlan::class, RateSchedule::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rateDao(): RateDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grid_rates_database"
                )
                    .addCallback(AppDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.rateDao())
                }
            }
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    val dao = database.rateDao()
                    try {
                        val providers = dao.getAllProviders().first()
                        if (providers.isEmpty() || providers.none { it.id == "txu" }) {
                            populateDatabase(dao)
                        }
                    } catch (_: Exception) {
                        populateDatabase(dao)
                    }
                }
            }
        }

        suspend fun populateDatabase(dao: RateDao) {
            RateDataImporter.populate(dao)
        }
    }
}
