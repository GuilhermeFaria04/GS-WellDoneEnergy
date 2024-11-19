package fiap.com.welldoneenergy.Adaptador

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.activity.FormAtualizarActivity
import fiap.com.welldoneenergy.model.HistoricoEnergia

class HistoricoAdapter(private val listaHistorico: MutableList<HistoricoEnergia>) :
    RecyclerView.Adapter<HistoricoAdapter.HistoricoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoricoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemhistoricolayout, parent, false)
        return HistoricoViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoricoViewHolder, position: Int) {
        val historico = listaHistorico[position]
        holder.bind(historico)
    }

    override fun getItemCount(): Int = listaHistorico.size

    inner class HistoricoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textoTipoEnergia: TextView = itemView.findViewById(R.id.textoTipoEnergia)
        private val textoQuantidade: TextView = itemView.findViewById(R.id.textoQuantidade)
        private val textoData: TextView = itemView.findViewById(R.id.textoData)
        private val buttonAtualizar: Button = itemView.findViewById(R.id.buttonAtualizar)
        private val buttonExcluir: Button = itemView.findViewById(R.id.buttonExcluir)

        fun bind(historico: HistoricoEnergia) {
            Log.d("HistoricoAdapter", "TipoEnergia: ${historico.tipoEnergia}, Quantidade: ${historico.quantidadeGerada}, Data: ${historico.data}")
            textoTipoEnergia.text = historico.tipoEnergia
            textoQuantidade.text = "Consumo: ${historico.quantidadeGerada}"
            textoData.text = "Data: ${historico.data}"

            buttonAtualizar.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, FormAtualizarActivity::class.java).apply {
                    putExtra("HISTORICO_ID", historico.id) // Passe o ID do documento para atualizar
                    putExtra("TIPO_ENERGIA", historico.tipoEnergia)
                    putExtra("QUANTIDADE", historico.quantidadeGerada)
                    putExtra("DATA", historico.data)
                }
                context.startActivity(intent)
            }

            buttonExcluir.setOnClickListener {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("consumo_energia").document(historico.id)
                    .delete()
                    .addOnSuccessListener {
                        Log.d("HistoricoAdapter", "Documento excluído com sucesso!")
                        val position = adapterPosition
                        if (position != RecyclerView.NO_POSITION) {
                            listaHistorico.removeAt(position)
                            notifyItemRemoved(position)
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.e("HistoricoAdapter", "Erro ao excluir documento", exception)
                    }
            }
        }
    }
}
