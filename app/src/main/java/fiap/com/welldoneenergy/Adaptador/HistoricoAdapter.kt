package fiap.com.welldoneenergy.Adaptador

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.HistoricoEnergia

class HistoricoAdapter(private val listaHistorico: List<HistoricoEnergia>) :
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

        fun bind(historico: HistoricoEnergia) {
            Log.d("HistoricoAdapter", "TipoEnergia: ${historico.tipoEnergia}, Quantidade: ${historico.quantidadeGerada}, Data: ${historico.dataRegistro}")
            textoTipoEnergia.text = historico.tipoEnergia
            textoQuantidade.text = historico.quantidadeGerada
            textoData.text = historico.dataRegistro
        }
    }
}
