package com.example.upload_firebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.images_firebase.ImagesAdapter
import com.example.upload_firebase.databinding.ActivityImagesFeedBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class ImagesFeed : AppCompatActivity() {

    private lateinit var binding: ActivityImagesFeedBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var mList = mutableListOf<String>()
    private lateinit var adapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityImagesFeedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVars()
        getImages()

    }

    //INICIALIZAÇÃO DAS VARIÁVEIS DO FIREBASE
    private fun initVars() {

        firebaseFirestore = FirebaseFirestore.getInstance()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImagesAdapter(mList)
        binding.recyclerView.adapter = adapter

    }

    //RECUPERA AS IMAGENS DO FIRESTORE:
    private fun getImages(){
        binding.progressBar.visibility = View.VISIBLE
        firebaseFirestore.collection("images")
            .get().addOnSuccessListener {
                for(i in it){
                    mList.add(i.data["pic"].toString())
                }
                adapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }
    }

}