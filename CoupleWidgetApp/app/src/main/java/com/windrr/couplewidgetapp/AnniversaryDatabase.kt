package com.windrr.couplewidgetapp

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "anniversary_items")
data class AnniversaryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, //자동 업데이트 ID
    val title: String, //기념일 제목 (필수)
    val dateMillis: Long = 0, //목표 일자
    val dateCount: Int = 0 //사용자가 지정한 기념일 수
)

@Dao
interface AnniversaryDao {
    @Query("SELECT * FROM anniversary_items")
    suspend fun getAll(): List<AnniversaryItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AnniversaryItem)

    @Query("DELETE FROM anniversary_items WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Database(entities = [AnniversaryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun anniversaryDao(): AnniversaryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "couple-widget-db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}