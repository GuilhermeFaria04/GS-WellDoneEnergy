import android.os.Bundle
import android.util.Log
import android.view.View
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

class HistoricoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoricoAdapter
    private lateinit var listaHistorico: MutableList<HistoricoEnergia>
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historicolayout)

        // Inicializando o RecyclerView
        recyclerView = findViewById(R.id.recyclerViewHistorico)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        // Inicializando a lista e o adaptador
        listaHistorico = mutableListOf()
        adapter = HistoricoAdapter(listaHistorico)
        recyclerView.adapter = adapter

        // Inicializando o ProgressBar
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE // Exibe o ProgressBar no início

        // Carregar histórico de dados
        carregarHistorico()
    }

    private fun carregarHistorico() {
        val firestore = FirebaseFirestore.getInstance()

        // Mostrar ProgressBar enquanto carrega os dados
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        firestore.collection("consumo_energia")
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                // Esconde ProgressBar após carregar os dados
                progressBar.visibility = View.GONE

                if (snapshot != null && !snapshot.isEmpty) {
                    listaHistorico.clear()
                    for (document in snapshot.documents) {
                        val historico = document.toObject(HistoricoEnergia::class.java)
                        if (historico != null) {
                            listaHistorico.add(historico)
                        }
                    }
                    adapter.notifyDataSetChanged()

                    // Log para depurar a quantidade de itens carregados
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
