package com.example.baixadasegura

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        if (FirebaseAuth.getInstance().currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        auth = FirebaseAuth.getInstance()

        val edtEmail =
            findViewById<EditText>(R.id.edtEmail)

        val edtSenha =
            findViewById<EditText>(R.id.edtSenha)

        val btnEntrar =
            findViewById<Button>(R.id.btnEntrar)

        val btnCadastrar =
            findViewById<Button>(R.id.btnCadastrar)

        btnEntrar.setOnClickListener {

            val email =
                edtEmail.text.toString().trim()

            val senha =
                edtSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {

                Toast.makeText(
                    this,
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(
                email,
                senha
            ).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    val intent =
                        Intent(
                            this,
                            MainActivity::class.java
                        )

                    startActivity(intent)

                    finish()

                } else {

                    Toast.makeText(
                        this,
                        "E-mail ou senha incorretos",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        btnCadastrar.setOnClickListener {

            val intent =
                Intent(
                    this,
                    CadastroActivity::class.java
                )

            startActivity(intent)
        }
    }
}