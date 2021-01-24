package com.example.saving

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.*
import java.io.File
import java.lang.Exception
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {
    private lateinit var svPref: Button
    private lateinit var loadPref: Button
    private lateinit var editText: EditText
    private lateinit var textView: TextView
    private lateinit var inSave: Button
    private lateinit var inLoad: Button
    private lateinit var svDB: Button
    private lateinit var DBLoad: Button
    private lateinit var externalSave: Button
    private lateinit var externalLoad: Button



    companion object {
        private const val KEY = "KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        svPref = findViewById(R.id.svPref)
        loadPref = findViewById(R.id.loadPref)
        editText = findViewById(R.id.editText)
        textView = findViewById(R.id.textView3)
        inSave = findViewById(R.id.svInternal)
        inLoad = findViewById(R.id.loadInternal)
        svDB = findViewById(R.id.svDB)
        DBLoad = findViewById(R.id.loadDB)
        externalSave = findViewById(R.id.svExternal)
        externalLoad = findViewById(R.id.loadExternal)


        val pref = getPreferences(MODE_PRIVATE)
        val internalFile = File(filesDir, "qwerty.txt")
        val externalFile = File (filesDir, "external")



        svPref.setOnClickListener {
            pref.edit().apply {
                putString(KEY, editText.text.toString())
                apply()
            }
        }
        loadPref.setOnClickListener {
            val str: String? = pref.getString(KEY, "")
            textView.text = str
        }


        inSave.setOnClickListener {
            try {

                val output = internalFile.outputStream()

                output.write(editText.text.toString().toByteArray())

                output.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        inLoad.setOnClickListener {
            try {

                val input = internalFile.inputStream()

                val str = input.readBytes()

                textView.text = str.decodeToString()

                input.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        externalSave.setOnClickListener {
            try {

                val output = openFileOutput("external", Context.MODE_PRIVATE)

                output.write(editText.text.toString().toByteArray())

                output.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        externalLoad.setOnClickListener {
            try {

                val input = openFileInput("external")

                val str = input.readBytes()

                textView.text = str.decodeToString()

                input.close()

            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }



        val mineDB = Room.databaseBuilder(this, MineDB::class.java, "dataBase")
                .fallbackToDestructiveMigration()
                .build()

        svDB.setOnClickListener {
            thread {
                mineDB.getMineDao().insertText(Mine(text = editText.text.toString()))
            }
        }
        DBLoad.setOnClickListener {
            thread {
                try {
                    val sizee =  mineDB.getMineDao().getText().size
                    textView.text = mineDB.getMineDao().getText()[sizee-1].text
                } catch (ex: Exception){
                    ex.printStackTrace()
                }

            }
        }


    }
    @Entity(tableName = "table")
    data class Mine(
            @PrimaryKey(autoGenerate = true)
            val Key: Int = 0,
            val text: String
    )

    @Dao
    abstract class MineDao{
        @Insert
        abstract fun insertText(mine : Mine)

        @Query("SELECT * from `table`")
        abstract fun getText(): List<Mine>
    }

    @Database(entities = [Mine::class], version = 1)
    abstract class MineDB : RoomDatabase(){
        abstract fun getMineDao() : MineDao
    }

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString("TEXT_VIEW", textView.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        textView.text = savedInstanceState.getString("TEXT_VIEW")
        super.onRestoreInstanceState(savedInstanceState)
    }

}