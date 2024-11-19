package fiap.com.welldoneenergy.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.Usuario

class CadastroActivity : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etEmail: EditText
    private lateinit var etTelefone: EditText
    private lateinit var etSenha: EditText
    private lateinit var etConfirmarSenha: EditText
    private lateinit var btnCadastrar: Button
    private lateinit var backIcon: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.cadastrolayout)

        etNome = findViewById(R.id.et_nome)
        etEmail = findViewById(R.id.et_email)
        etTelefone = findViewById(R.id.et_telefone)
        etSenha = findViewById(R.id.et_senha)
        etConfirmarSenha = findViewById(R.id.et_confirmar_senha)
        btnCadastrar = findViewById(R.id.btn_cadastrar)
        backIcon = findViewById(R.id.backIcon)

        backIcon.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnCadastrar.setOnClickListener {
            realizarCadastro()
        }
    }

    private fun realizarCadastro() {
        val nome = etNome.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val telefone = etTelefone.text.toString().trim()
        val senha = etSenha.text.toString().trim()
        val confirmarSenha = etConfirmarSenha.text.toString().trim()

        if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty() || confirmarSenha.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            return
        }

        if (senha != confirmarSenha) {
            Toast.makeText(this, "As senhas não coincidem!", Toast.LENGTH_SHORT).show()
            return
        }

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        auth.createUserWithEmailAndPassword(email, senha)
            .addOnCompleteListener { task ->
                Log.d("CadastroActivity", "Resultado do cadastro: ${task.isSuccessful}")
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    val usuario = Usuario(nome, email, telefone)

                    if (uid != null) {
                        // Agora estamos salvando os dados no Firestore
                        firestore.collection("usuarios").document(uid).set(usuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()
                                Log.d("CadastroActivity", "Cadastro bem-sucedido. Redirecionando para HomeActivity")
                                startActivity(Intent(this@CadastroActivity, LoginActivity::class.java))
                                finish()  // Finaliza a atividade de cadastro
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao salvar dados: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "Erro no cadastro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    Log.d("CadastroActivity", "Erro no cadastro: ${task.exception?.message}")
                }
            }
    }
}
