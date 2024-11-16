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
    private lateinit var atualizarButton: Button
    private lateinit var excluirButton: Button
    private lateinit var consumoId: String

    private lateinit var firestore: FirebaseFirestore

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.formupdatelayout)

        tipoEnergiaEditText = findViewById(R.id.energyType)
        quantidadeGeradaEditText = findViewById(R.id.quantityGenerated)
        dataEditText = findViewById(R.id.inputDate)
        submitButton = findViewById(R.id.submitButton)
        backIcon = findViewById(R.id.backIcon)
        atualizarButton = findViewById(R.id.btnatualizar)
        excluirButton = findViewById(R.id.btnexcluir)

        firestore = FirebaseFirestore.getInstance()

        consumoId = intent.getStringExtra("consumoId") ?: ""

        backIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (consumoId.isNotEmpty()) {
            carregarDados(consumoId)
        }

        submitButton.setOnClickListener {
            val tipoEnergia = tipoEnergiaEditText.text.toString()
            val quantidadeGerada = quantidadeGeradaEditText.text.toString()
            val data = dataEditText.text.toString()

            if (tipoEnergia.isEmpty() || quantidadeGerada.isEmpty() || data.isEmpty()) {
                return@setOnClickListener
            }

            val consumoEnergia = ConsumoEnergia(tipoEnergia, quantidadeGerada, data)

            if (consumoId.isEmpty()) {
                firestore.collection("consumo_energia")
                    .add(consumoEnergia)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {

                    }
            } else {
                firestore.collection("consumo_energia")
                    .document(consumoId)
                    .set(consumoEnergia)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {

                    }
            }
        }

        atualizarButton.setOnClickListener {
            val tipoEnergia = tipoEnergiaEditText.text.toString()
            val quantidadeGerada = quantidadeGeradaEditText.text.toString()
            val data = dataEditText.text.toString()

            val consumoEnergia = ConsumoEnergia(tipoEnergia, quantidadeGerada, data)

            if (consumoId.isNotEmpty()) {
                firestore.collection("consumo_energia")
                    .document(consumoId)
                    .set(consumoEnergia)
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {

                    }
            }
        }

        excluirButton.setOnClickListener {
            if (consumoId.isNotEmpty()) {
                firestore.collection("consumo_energia")
                    .document(consumoId)
                    .delete()
                    .addOnSuccessListener {
                        finish()
                    }
                    .addOnFailureListener {

                    }
            }
        }
    }

    private fun carregarDados(consumoId: String) {
        firestore.collection("consumo_energia")
            .document(consumoId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot.exists()) {
                    val consumoEnergia = documentSnapshot.toObject(ConsumoEnergia::class.java)
                    tipoEnergiaEditText.setText(consumoEnergia?.tipoEnergia)
                    quantidadeGeradaEditText.setText(consumoEnergia?.quantidadeGerada)
                    dataEditText.setText(consumoEnergia?.data)
                }
            }
            .addOnFailureListener {

            }
    }
}
