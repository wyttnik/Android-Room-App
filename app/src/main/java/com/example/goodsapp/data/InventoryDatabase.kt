package com.example.goodsapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.commonsware.cwac.saferoom.SQLCipherUtils
import com.example.goodsapp.security.MasterFiles
import net.sqlcipher.database.SupportFactory

@Database(entities = [Item::class], version = 1, exportSchema = false)
abstract class InventoryDatabase: RoomDatabase() {

    abstract fun itemDao(): ItemDao
    companion object{

        @Volatile
        private var Instance: InventoryDatabase? = null

//        fun getDatabase(context: Context):InventoryDatabase {
//            return Instance ?: synchronized(this) {
//                Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
//                    .build()
//                    .also {
//                        Instance = it
//                    }
//            }
//        }
        fun getDatabase(context: Context):InventoryDatabase {
            val passphrase = MasterFiles.dbSharedPreferences.getString("db_password", "null")!!.toByteArray()
            val state = SQLCipherUtils.getDatabaseState(context, "item_database")
            if (state == SQLCipherUtils.State.UNENCRYPTED) {

                SQLCipherUtils.encrypt(context, "item_database", passphrase)
            }

            return Room.databaseBuilder(context, InventoryDatabase::class.java, "item_database")
                .openHelperFactory(SupportFactory(passphrase))
                .build()
        }
    }
}