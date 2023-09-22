package com.example.upload_firebase

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.upload_firebase.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    //ATRIBUTOS

    //REPRESENTAÇÃO DA CLASSE DE MANIPULAÇÃO DE OBJETOS DE VIEWS DAS TELAS
    private lateinit var binding: ActivityMainBinding

    //REPRESENTAÇÃO DA CLASSE DE MANIPULAÇÃO DE ENDEREÇO (LOCAL) DE ARQUIVOS
    private var imageUri: Uri? = null

    //REFERÊNCIA PARA ACESSO DE MANIPULAÇÃO DO CLOUD STORAGE
    private lateinit var storageRef: StorageReference

    //REFERÊNCIA PARA ACESSO DE MANIPULAÇÃO DO CLOUD FIRESTORE
    private lateinit var firebaseFirestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        registerClickEvents()
    }

    //INICIALIZAÇÃO DOS ATRIBUTOS DO FIREBASE
    private fun initVars() {
        storageRef = FirebaseStorage.getInstance().reference.child("images")
        firebaseFirestore = FirebaseFirestore.getInstance()
    }

    //LANÇADOR DE  RECURSOS EXTERNOS DA APLICAÇÃO
    private val resultLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {

        imageUri = it
        binding.imageView.setImageURI(it)
    }

    //TRATAMENTO DE EVENTOS DE CLICK
    private fun registerClickEvents() {

        binding.imageView.setOnClickListener {
            resultLauncher.launch("image/*")
        }

        binding.uploadBtn.setOnClickListener {
            uploadImage()
        }
    }

    //UPLOAD DE IMAGENS DO FIREBASE
    private fun uploadImage() {

        binding.progressBar.visibility = View.VISIBLE

        storageRef = storageRef.child(System.currentTimeMillis().toString())

//        //UPLOAD V1 INÍCIO
//        imageUri?.let {
//            storageRef.putFile(it).addOnCompleteListener { task ->
//                if (task.isSuccessful) {
//                    Toast.makeText(this, "UPLOAD CONCLUIDO COM SUCESSO", Toast.LENGTH_LONG).show()
//                } else {
//                    Toast.makeText(this, "ERRO AO REALIZAR O UPLOAD", Toast.LENGTH_LONG).show()
//                }
//                binding.progressBar.visibility = View.GONE
//            }
//        }

        ///// PROCESSO DE UPLOAD - V2 /////
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    storageRef.downloadUrl.addOnSuccessListener { uri ->

                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()

                        //Aqui é onde pode se fazer a chamada para o banco de dados, podendo chamar até o próprio endpoint
                        firebaseFirestore.collection("images").add(map)
                            .addOnCompleteListener { firestoreTask ->

                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "Upload realizado com sucesso",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Erro ao tentar realizar o upload da imagem",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                }
                                binding.progressBar.visibility = View.GONE
                                binding.imageView.setImageResource(R.drawable.upload)

                            }
                    }

                } else {

                    Toast.makeText(
                        this,
                        "Erro ao tentar realizar o upload da imagem",
                        Toast.LENGTH_SHORT
                    ).show()

                }

                //BARRA DE PROGRESSO DO UPLOAD
                binding.progressBar.visibility = View.GONE

                //TROCA A IMAGEM PARA A IMAGEM PADRÃO
                binding.imageView.setImageResource(R.drawable.upload)

            }
        }
    }
}