package fiap.com.welldoneenergy.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.ConsumoEnergia

class FormActivity : AppCompatActivity() {

    private lateinit var tipoEnergiaEditText: EditText
    private lateinit var quantidadeGeradaEditText: EditText
    private lateinit var dataEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var backIcon: ImageView

    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.formlayout)

        tipoEnergiaEditText = findViewById(R.id.energyType)
        quantidadeGeradaEditText = findViewById(R.id.quantityGenerated)
        dataEditText = findViewById(R.id.inputDate)
        submitButton = findViewById(R.id.submitButton)
        backIcon = findViewById(R.id.backIcon)

        firestore = FirebaseFirestore.getInstance()

        backIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        submitButton.setOnClickListener {
            val tipoEnergia = tipoEnergiaEditText.text.toString()
            val quantidadeGerada = quantidadeGeradaEditText.text.toString()
            val data = dataEditText.text.toString()

            if (tipoEnergia.isEmpty() || quantidadeGerada.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val consumoEnergia = ConsumoEnergia(tipoEnergia, quantidadeGerada, data)

            firestore.collection("consumo_energia")
                .add(consumoEnergia)
                .addOnSuccessListener {
                    Toast.makeText(this, "Dados inseridos com sucesso", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao inserir os dados: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
