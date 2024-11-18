import android.os.Bundle
import android.util.Log
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historicolayout)

        recyclerView = findViewById(R.id.recyclerViewHistorico)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        listaHistorico = mutableListOf()
        adapter = HistoricoAdapter(listaHistorico)
        recyclerView.adapter = adapter

        carregarHistorico()
    }

    private fun carregarHistorico() {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("consumo_energia")
            .orderBy("dataRegistro", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("Firestore", "Dados carregados com sucesso!")
                listaHistorico.clear()
                for (document in snapshot.documents) {
                    val historico = document.toObject(HistoricoEnergia::class.java)
                    if (historico != null) {
                        listaHistorico.add(historico)
                    }
                }
                Log.d("Firestore", "Total de itens carregados: ${listaHistorico.size}")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao carregar dados", exception)
            }
    }
}
