package fiap.com.welldoneenergy.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import fiap.com.welldoneenergy.R
import fiap.com.welldoneenergy.model.ButtonItem

class HomeActivity : AppCompatActivity() {

    private lateinit var cardViewStatus: View
    private lateinit var statusText: TextView
    private lateinit var logoutButton: MaterialButton
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homelayout)

        mAuth = FirebaseAuth.getInstance()

        cardViewStatus = findViewById(R.id.cardViewStatus)
        statusText = findViewById(R.id.statusText)
        logoutButton = findViewById(R.id.logoutButton)

        checkLoginStatus()

        setupButtons()

        val profileIcon = findViewById<ImageView>(R.id.profileIcon)

        profileIcon.setOnClickListener {
            val currentUser = mAuth.currentUser
            if (currentUser == null) {
                startActivity(Intent(this, LoginActivity::class.java))
            } else {
                Toast.makeText(this, "Você já está logado como ${currentUser.email}", Toast.LENGTH_SHORT).show()
            }
        }

        logoutButton.setOnClickListener {
            logoutUser()
        }
    }

    private fun setupButtons() {
        val buttonItems = listOf(
            ButtonItem("btnInserirDados", "Insira dados de consumo", R.drawable.job, FormActivity::class.java),
            ButtonItem("btnAtualizarDados", "Altere os dados de consumo", R.drawable.job, FormAtualizarActivity::class.java),
            ButtonItem("btnComoFunciona", "Como Funciona", R.drawable.light, ComoFuncionaActivity::class.java),


        )

        val buttonIds = listOf(
            R.id.btnInserirDados,
            R.id.btnAtualizarDados,
            R.id.btnComoFunciona
        )

        buttonItems.forEachIndexed { index, buttonItem ->
            val button = findViewById<MaterialButton>(buttonIds[index])
            button?.apply {
                setOnClickListener { navigateToActivity(buttonItem.targetActivity) }
            }
        }
    }

    private fun navigateToActivity(targetActivity: Class<*>) {
        val intent = Intent(this, targetActivity)
        startActivity(intent)
    }

    private fun checkLoginStatus() {
        val currentUser = mAuth.currentUser

        if (currentUser != null) {
            cardViewStatus.visibility = View.VISIBLE
            statusText.text = "Você está logado como ${currentUser.email}"
            logoutButton.visibility = View.VISIBLE
        } else {
            cardViewStatus.visibility = View.GONE
            logoutButton.visibility = View.GONE
        }
    }

    private fun logoutUser() {
        mAuth.signOut()
        cardViewStatus.visibility = View.GONE
        logoutButton.visibility = View.GONE
        Toast.makeText(this, "Você saiu com sucesso!", Toast.LENGTH_SHORT).show()
    }
}
