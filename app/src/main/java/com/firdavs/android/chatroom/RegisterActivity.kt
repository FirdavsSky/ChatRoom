package com.firdavs.android.chatroom

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var register_btn: Button
    private lateinit var username_register: EditText
    private lateinit var email_register: EditText
    private lateinit var password_register: EditText

    private var firebaseUserID: String = ""
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUsers: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        register_btn = findViewById(R.id.register_btn)
        username_register = findViewById(R.id.username_register)
        email_register = findViewById(R.id.email_register)
        password_register = findViewById(R.id.password_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Регистрация"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            val intent = Intent(this@RegisterActivity,WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }
        mAuth = FirebaseAuth.getInstance()

        register_btn.setOnClickListener {
            registerUser()
        }

    }

    private fun registerUser() {
        val username: String = username_register.text.toString()
        val email: String = email_register.text.toString()
        val password: String = password_register.text.toString()

        if (username == ""){
            Toast.makeText(this@RegisterActivity,"Пожалуйста заполните имя", Toast.LENGTH_LONG).show()
        }else if (email == ""){
            Toast.makeText(this@RegisterActivity,"Пожалуйста заполните эмайл", Toast.LENGTH_LONG).show()
        }else if (password == ""){
            Toast.makeText(this@RegisterActivity,"Пожалуйста заполните пароль", Toast.LENGTH_LONG).show()
        }else{
            mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener {task ->
                if (task.isSuccessful)
                {
                    firebaseUserID = mAuth.currentUser!!.uid
                    refUsers = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUserID)

                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"] = firebaseUserID
                    userHashMap["username"] = username
                    userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chatroom-c3264.appspot.com/o/profile.jpg?alt=media&token=8b56a399-3fca-44cb-b56d-2bd6d7b9cc60"
                    userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chatroom-c3264.appspot.com/o/cover.jpg?alt=media&token=9636846d-d4d1-4eef-a8b9-ef4f0f9250f5"
                    userHashMap["status"] = "offline"
                    userHashMap["search"] = username.toLowerCase()
                    userHashMap["facebook"] = "https://m.facebook.com"
                    userHashMap["instagram"] = "https://m.instagram.com"
                    userHashMap["website"] = "https://www.google.com"

                    refUsers.updateChildren(userHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                val intent = Intent(this@RegisterActivity,MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                                finish()
                            }
                        }
                }
                else{
                    Toast.makeText(this@RegisterActivity,"Сообщение об ошибке: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}