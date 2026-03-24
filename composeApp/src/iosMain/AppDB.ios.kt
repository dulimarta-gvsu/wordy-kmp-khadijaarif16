package edu.gvsu.cis.kmp_wordy
import androidx.room.Room
import androidx.room.RoomDatabase
import edu.gvsu.cis.kmp_roomdb.db.AppDB
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask


fun buildDatabase(): AppDB{
    val dbFile = NSHomeDirectory() + "/wordy.db"
    return Room.databaseBuilder<AppDB>(
        name = dbFile
    ).setDriver(BundledSQLiteDriver()).build()
}