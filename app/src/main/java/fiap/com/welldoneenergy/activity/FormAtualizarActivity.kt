package fiap.com.welldoneenergy.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.ConsumoEnergia

class FormAtualizarActivity : AppCompatActivity() {

    private lateinit var tipoEnergiaEditText: EditText
    private lateinit var quantidadeGeradaEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var backIcon: ImageView
    private lateinit var consumoId: String

    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.formupdatelayout)

        tipoEnergiaEditText = findViewById(R.id.energyType)
        quantidadeGeradaEditText = findViewById(R.id.quantityGenerated)
        dataEditText = findViewById(R.id.inputDate)
        submitButton = findViewById(R.id.btnatualizar)
        backIcon = findViewById(R.id.backIcon)

        firestore = FirebaseFirestore.getInstance()

        consumoId = intent.getStringExtra("HISTORICO_ID") ?: ""
        val tipoEnergia = intent.getStringExtra("TIPO_ENERGIA") ?: ""
        val quantidade = intent.getStringExtra("QUANTIDADE") ?: ""
        val data = intent.getStringExtra("DATA") ?: ""

        // Preenchendo os campos com os dados recebidos
        tipoEnergiaEditText.setText(tipoEnergia)
        quantidadeGeradaEditText.setText(quantidade)
        dataEditText.setText(data)

        // Ação do botão de voltar
        backIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Ação do botão de atualizar dados
        submitButton.setOnClickListener {
            val tipoEnergiaAtualizado = tipoEnergiaEditText.text.toString()
            val quantidadeGeradaAtualizada = quantidadeGeradaEditText.text.toString()
            val dataAtualizada = dataEditText.text.toString()

            // Validação de campos
            if (tipoEnergiaAtualizado.isEmpty() || quantidadeGeradaAtualizada.isEmpty() || dataAtualizada.isEmpty()) {
                return@setOnClickListener
            }

            val consumoEnergia = ConsumoEnergia(tipoEnergiaAtualizado, quantidadeGeradaAtualizada, dataAtualizada)

            if (consumoId.isEmpty()) {
                // Criar novo registro
                firestore.collection("consumo_energia")
                    .add(consumoEnergia)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        // Tratar erro
                    }
            } else {
                // Atualizar registro existente
                firestore.collection("consumo_energia")
                    .document(consumoId)
                    .set(consumoEnergia)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {
                        // Tratar erro
                    }
            }
        }

    }
}
