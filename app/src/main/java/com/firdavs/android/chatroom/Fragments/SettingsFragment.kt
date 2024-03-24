package com.firdavs.android.chatroom.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firdavs.android.chatroom.ModelClasses.Users
import com.firdavs.android.chatroom.R
import com.firdavs.android.chatroom.databinding.FragmentSearchBinding
import com.firdavs.android.chatroom.databinding.FragmentSettingsBinding
import com.firdavs.android.chatroom.databinding.UserSearchItemLayoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    var userReference: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        _binding = FragmentSettingsBinding.inflate(inflater,container,false)
        val view = binding.root


        firebaseUser = FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        userReference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
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

        return view
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}