package fiap.com.welldoneenergy.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import fiap.com.welldoneenergy.Adaptador.HistoricoAdapter
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.HistoricoEnergia

class HistoricoEnergiaActivity : AppCompatActivity() {

    private lateinit var spinnerMes: Spinner
    private lateinit var spinnerAno: Spinner
    private lateinit var recyclerHistorico: RecyclerView
    private lateinit var bancoFirebase: FirebaseFirestore

    private lateinit var adaptadorHistorico: HistoricoAdapter
    private val listaHistorico = mutableListOf<HistoricoEnergia>()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.historicolayout)

        spinnerMes = findViewById(R.id.spinnerMes)
        spinnerAno = findViewById(R.id.spinnerAno)
        recyclerHistorico = findViewById(R.id.recyclerViewHistorico)

        bancoFirebase = FirebaseFirestore.getInstance()

        configurarSpinners()
        configurarRecyclerView()
        carregarDadosMaisRecentes()

        spinnerMes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                carregarDadosPorData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        spinnerAno.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                carregarDadosPorData()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun configurarSpinners() {
        val meses = listOf(
            "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
            "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
        )
        spinnerMes.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, meses)

        // Configurando o Spinner de anos
        val anos = (2000..2024).toList()
        spinnerAno.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, anos)
    }

    private fun configurarRecyclerView() {
        adaptadorHistorico = HistoricoAdapter(listaHistorico)
        recyclerHistorico.layoutManager = LinearLayoutManager(this)
        recyclerHistorico.adapter = adaptadorHistorico
    }

    private fun carregarDadosMaisRecentes() {
        bancoFirebase.collection("consumo_energia")
            .orderBy("dataRegistro", Query.Direction.DESCENDING)
            .limit(4)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tamanhoAnterior = listaHistorico.size
                listaHistorico.clear()

                for (document in querySnapshot) {
                    val historico = document.toObject(HistoricoEnergia::class.java)
                    listaHistorico.add(historico)
                }

                if (listaHistorico.size > tamanhoAnterior) {
                    adaptadorHistorico.notifyItemRangeInserted(tamanhoAnterior, listaHistorico.size - tamanhoAnterior)
                }
            }
            .addOnFailureListener { exception ->
                // Tratar erro
            }
    }

    private fun carregarDadosPorData() {
        val mesSelecionado = spinnerMes.selectedItemPosition + 1
        val anoSelecionado = spinnerAno.selectedItem.toString()

        bancoFirebase.collection("consumo_energia")
            .whereGreaterThanOrEqualTo("dataRegistro", "$anoSelecionado-${String.format("%02d", mesSelecionado)}-01")
            .whereLessThan("dataRegistro", "$anoSelecionado-${String.format("%02d", mesSelecionado + 1)}-01")
            .orderBy("dataRegistro", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { querySnapshot ->
                listaHistorico.clear()

                for (document in querySnapshot) {
                    val historico = document.toObject(HistoricoEnergia::class.java)
                    listaHistorico.add(historico)
                }

                // Notifica que os dados foram alterados
                adaptadorHistorico.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Tratar erro
            }
    }
}
