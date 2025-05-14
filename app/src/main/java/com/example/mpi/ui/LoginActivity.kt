package com.example.mpi.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mpi.R
import com.example.mpi.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val loginButton = findViewById<Button>(R.id.btnEntrar)
        val usernameEditText = findViewById<EditText>(R.id.editEmail)
        val passwordEditText = findViewById<EditText>(R.id.editSenha)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateUser(username, password)) {
                //startActivity(Intent(this, MenuActivity::class.java))
                val x = Intent(this, MenuActivity::class.java)
                //x.putExtra(Intent.EXTRA_TEXT, username)
                x.putExtra("tipo", username)
                startActivity(x)
            } else {
                Toast.makeText(this, "Credenciais inválidas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateUser(username: String, password: String): Boolean {
        return (username == "analista@gmail.com" && password == "1234") ||
                (username == "gestor@gmail.com" && password == "5678") ||
                (username == "coordenador@gmail.com" && password == "abcd")
    }
}
