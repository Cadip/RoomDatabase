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
import week14.paba.roomdatabase.database.daftarBelanja
import week14.paba.roomdatabase.database.daftarBelanjaDB

class MainActivity : AppCompatActivity() {
    private lateinit var DB : daftarBelanjaDB
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
        adapterDaftar = adapterDaftar(arDaftar)

        var _fabAdd = findViewById<FloatingActionButton>(R.id.fabAdd)
        _fabAdd.setOnClickListener {
            startActivity(Intent(this, TambahDaftar::class.java))
        }

        var _rvNotes = findViewById<RecyclerView>(R.id.rvNotes)
        _rvNotes.layoutManager = LinearLayoutManager(this)
        _rvNotes.adapter = adapterDaftar
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