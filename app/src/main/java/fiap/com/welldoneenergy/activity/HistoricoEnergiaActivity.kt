package fiap.com.welldoneenergy.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fiap.com.welldoneenergy.Adaptador.HistoricoAdapter
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.HistoricoEnergia

class HistoricoEnergiaActivity : AppCompatActivity() {

    private lateinit var recyclerHistorico: RecyclerView
    private lateinit var bancoFirebase: FirebaseFirestore
    private lateinit var progressBar: ProgressBar
    private lateinit var backIcon: ImageView

    private lateinit var adaptadorHistorico: HistoricoAdapter
    private val listaHistorico = mutableListOf<HistoricoEnergia>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historicolayout)

        recyclerHistorico = findViewById(R.id.recyclerViewHistorico)
        progressBar = findViewById(R.id.progressBar)
        backIcon = findViewById(R.id.backIcon)

        bancoFirebase = FirebaseFirestore.getInstance()

        backIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        configurarRecyclerView()
        carregarHistorico() // Inicializando com todos os dados
    }

    private fun configurarRecyclerView() {
        adaptadorHistorico = HistoricoAdapter(listaHistorico)
        recyclerHistorico.layoutManager = LinearLayoutManager(this)
        recyclerHistorico.adapter = adaptadorHistorico
    }

    private fun carregarHistorico() {
        progressBar.visibility = View.VISIBLE

        bancoFirebase.collection("consumo_energia")
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                progressBar.visibility = View.GONE

                if (snapshot != null && !snapshot.isEmpty) {
                    listaHistorico.clear()
                    for (document in snapshot.documents) {
                        val historico = document.toObject(HistoricoEnergia::class.java)
                        if (historico != null) {
                            historico.id = document.id // Salva o ID do documento
                            listaHistorico.add(historico)
                        }
                    }
                    adaptadorHistorico.notifyDataSetChanged()

                    Log.d("Firestore", "Itens carregados: ${listaHistorico.size}")
                } else {
                    Log.d("Firestore", "Nenhum dado encontrado.")
                    Toast.makeText(this, "Nenhum dado encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                progressBar.visibility = View.GONE
                Log.e("Firestore", "Erro ao carregar dados", exception)
                Toast.makeText(this, "Erro ao carregar dados: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
