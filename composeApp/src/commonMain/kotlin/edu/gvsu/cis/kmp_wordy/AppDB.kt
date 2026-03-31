package edu.gvsu.cis.kmp_wordy
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import edu.gvsu.cis.kmp_wordy.AppDBConstructor

@Dao
interface GameSessionDao{
    @Insert
    suspend fun insert(session: GameSession)
    @Delete
    suspend fun delete(session: GameSession)
    @Query("SELECT * FROM GameSession ORDER BY points ASC")
    suspend fun selectAllSortedByPoints(): List<GameSession>
    @Query("SELECT * FROM GameSession ORDER BY word ASC")
    suspend fun selectAllSortedAlphabetically(): List<GameSession>
    @Query("SELECT * FROM GameSession ORDER BY length(word) ASC")
    suspend fun selectAllSortedByLength(): List<GameSession>
    @Query("SELECT * FROM GameSession ORDER BY time ASC, numMoves DESC")
    suspend fun selectAllSortedByTimeAndMoves(): List<GameSession>
    @Query("SELECT * FROM GameSession")
    suspend fun selectAll(): List<GameSession>


}

@Database(entities = [GameSession::class], version=1)
@ConstructedBy(AppDBConstructor::class)
abstract class AppDB : RoomDatabase(){
    abstract fun gameSessionDao(): GameSessionDao
}

expect object AppDBConstructor : RoomDatabaseConstructor<AppDB>{
    override fun initialize(): AppDB
}
