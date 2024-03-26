package com.firdavs.android.chatroom.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.firdavs.android.chatroom.ModelClasses.Users
import com.firdavs.android.chatroom.R
import com.firdavs.android.chatroom.databinding.FragmentSearchBinding
import com.firdavs.android.chatroom.databinding.FragmentSettingsBinding
import com.firdavs.android.chatroom.databinding.UserSearchItemLayoutBinding
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat



class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var coverChecker: String? = ""

    private var userReference: DatabaseReference? = null
    private var firebaseUser: FirebaseUser? = null
    private val REQUEST_STORAGE_PERMISSION = 1
    private lateinit var pLauncher: ActivityResultLauncher<Array<String>>



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        val view = binding.root


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")

        userReference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists())
                {
                    val user: Users? = p0.getValue(Users::class.java)

                    val profileImageUrl = user?.getProfile()

                    val coverImageUrl = user?.getСover()

                        if (context != null){
                            binding.usernameSettings.text = user!!.getUserName()
                            if (!profileImageUrl.isNullOrEmpty()) {
                                Picasso.get().load(profileImageUrl).into(binding.profileImageSettings)
                            }

                            if (!coverImageUrl.isNullOrEmpty()) {
                                Picasso.get().load(coverImageUrl).into(binding.coverImageSettings)
                            }
                        }


                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

        binding.profileImageSettings.setOnClickListener {

            pickImage()
        }

        binding.coverImageSettings.setOnClickListener {
            coverChecker = "cover"
            pickImage()
        }

        return view
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null)
        {
            imageUri = data.data
            Toast.makeText(context,"uploading....", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Изображение загружаеться...")
        progressBar.show()

        if (imageUri != null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")

            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask<Uri?>(Continuation <UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }

                return@Continuation fileRef.downloadUrl

            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover")
                    {
                        val mapCoverImg = HashMap<String, Any>()
                        mapCoverImg["cover"] = url
                        userReference!!.updateChildren(mapCoverImg)
                        coverChecker = ""
                    }
                    else{
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        userReference!!.updateChildren(mapProfileImg)
                        coverChecker = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }


    /*private fun checkCameraPermission(){
        when{
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                        Toast.makeText(requireContext(),"Разрешение на чтение есть", Toast.LENGTH_LONG).show()
            }
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED -> {
                Toast.makeText(requireContext(),"Разрешение на редактирование есть", Toast.LENGTH_LONG).show()
            }

            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ->{
                Toast.makeText(requireContext(),"Мы нуждаемся в Вашем разрешении", Toast.LENGTH_LONG).show()
            }
            else -> {
                pLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }
    }*/

    /*private fun registerPermissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
            if (it[Manifest.permission.READ_EXTERNAL_STORAGE] == true && it[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true){
                Toast.makeText(requireContext(),"Разрешение есть", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(requireContext(),"Разрешение нет", Toast.LENGTH_LONG).show()
            }
        }
    }*/


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}