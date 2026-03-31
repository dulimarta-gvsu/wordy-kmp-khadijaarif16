package edu.gvsu.cis.kmp_wordy
import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver



fun buildDatabase(context: Context): AppDB {
    val dbFile = context.getDatabasePath("wordy.db")
    return Room.databaseBuilder<AppDB>(
        context= context,
        name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver()).build()
}