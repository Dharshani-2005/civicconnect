package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.AuditLogDao
import com.example.data.dao.EscalationDao
import com.example.data.dao.GrievanceDao
import com.example.data.dao.ProjectDao
import com.example.data.dao.RTIDao
import com.example.data.dao.UserDao
import com.example.data.dao.WardDao
import com.example.data.model.AuditLog
import com.example.data.model.Escalation
import com.example.data.model.Grievance
import com.example.data.model.PublicProject
import com.example.data.model.RTIRequest
import com.example.data.model.User
import com.example.data.model.Ward

@Database(
    entities = [
        User::class,
        Ward::class,
        Grievance::class,
        Escalation::class,
        RTIRequest::class,
        PublicProject::class,
        AuditLog::class
    ],
    version = 2,
    exportSchema = false
)
abstract class CivicDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun wardDao(): WardDao
    abstract fun grievanceDao(): GrievanceDao
    abstract fun escalationDao(): EscalationDao
    abstract fun rtiDao(): RTIDao
    abstract fun projectDao(): ProjectDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        @Volatile
        private var INSTANCE: CivicDatabase? = null

        fun getDatabase(context: Context): CivicDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CivicDatabase::class.java,
                    "civicconnect.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
