package week14.paba.roomdatabase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import week14.paba.roomdatabase.database.daftarBelanja
import week14.paba.roomdatabase.database.daftarBelanjaDB
import week14.paba.roomdatabase.database.historyBarang
import week14.paba.roomdatabase.database.historyBarangDB
import week14.paba.roomdatabase.database.historyBarangDAO

class MainActivity : AppCompatActivity() {
    private lateinit var DB : daftarBelanjaDB
    private lateinit var DBhistoryBarang : historyBarangDB
    private lateinit var adapterDaftar: adapterDaftar
    private var arDaftar: MutableList<daftarBelanja> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        DB = daftarBelanjaDB.getDatabase(this)
        DBhistoryBarang = historyBarangDB.getDatabase(this)
        adapterDaftar = adapterDaftar(arDaftar)

        var _fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        _fabAdd.setOnClickListener {
            startActivity(Intent(this, TambahDaftar::class.java))
        }

        var _rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        _rvNotes.layoutManager = LinearLayoutManager(this)
        _rvNotes.adapter = adapterDaftar

        adapterDaftar.setOnItemClickCallback(
            object : adapterDaftar.OnItemClickCallback {
                override fun delData(dtBelanja: daftarBelanja) {
                    CoroutineScope(Dispatchers.IO).async {
                        DB.fundaftarBelanjaDAO().delete(dtBelanja)
                        val daftar = DB.fundaftarBelanjaDAO().selectAll()
                        withContext(Dispatchers.Main) {
                            adapterDaftar.isiData(daftar)
                        }
                    }
                }

                override fun selesaiData(dtBelanja: daftarBelanja) {
                    CoroutineScope(Dispatchers.IO).async {
                        // Create an object for the historyBarang table
                        val history = historyBarang(
                            tanggal = dtBelanja.tanggal,
                            item = dtBelanja.item,
                            jumlah = dtBelanja.jumlah,
                            status = 1
                        )

                        // Insert into the historyBarang table
                        DBhistoryBarang.funhistoryBarangDAO().insert(history)

                        // Delete the item from the daftarBelanja table
                        DB.fundaftarBelanjaDAO().delete(dtBelanja)

                        // Update the RecyclerView UI with the new data
                        val daftar = DB.fundaftarBelanjaDAO().selectAll()

                        withContext(Dispatchers.Main) {
                            // Update the adapter with the latest data
                            adapterDaftar.isiData(daftar)
                        }
                    }

                }
            }
        )
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Main).async {
            val daftarBelanja = DB.fundaftarBelanjaDAO().selectAll()
            adapterDaftar.isiData(daftarBelanja)
            Log.d("data ROOM", daftarBelanja.toString())
        }
    }
}