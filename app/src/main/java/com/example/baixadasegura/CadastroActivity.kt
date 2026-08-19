package com.example.baixadasegura

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class CadastroActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_cadastro)

        auth = FirebaseAuth.getInstance()

        val edtEmail = findViewById<EditText>(R.id.edtEmail)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)

        btnCadastrar.setOnClickListener {

            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString()

            if (email.isEmpty() || senha.isEmpty()) {

                Toast.makeText(
                    this,
                    "Preencha todos os campos",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (senha.length < 6) {

                Toast.makeText(
                    this,
                    "A senha deve ter pelo menos 6 caracteres",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            Log.d("FIREBASE_AUTH", "Tentando cadastrar: $email")

            auth.createUserWithEmailAndPassword(
                email,
                senha
            ).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Log.d(
                        "FIREBASE_AUTH",
                        "Cadastro realizado com sucesso"
                    )

                    Toast.makeText(
                        this,
                        "Conta criada!",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                } else {

                    val exception = task.exception

                    Log.e(
                        "FIREBASE_AUTH",
                        "Erro no cadastro",
                        exception
                    )

                    if (exception is FirebaseAuthException) {

                        Log.e(
                            "FIREBASE_AUTH",
                            "Código Firebase: ${exception.errorCode}"
                        )
                    }

                    Toast.makeText(
                        this,
                        exception?.message
                            ?: "Erro desconhecido",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}