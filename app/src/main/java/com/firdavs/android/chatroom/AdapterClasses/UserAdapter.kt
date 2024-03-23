package com.firdavs.android.chatroom.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firdavs.android.chatroom.ModelClasses.Users
import com.firdavs.android.chatroom.R
import com.firdavs.android.chatroom.databinding.UserSearchItemLayoutBinding
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class UserAdapter(
    mContext: Context,
    mUsers: List<Users>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<UserAdapter.UseViewHolder>() {
    private val mContext: Context
    private val mUsers: List<Users>
    private val isChatCheck: Boolean

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onBindViewHolder(holder: UseViewHolder, position: Int) {
        val user: Users = mUsers[position]
        holder.binding.username.text = user?.getUserName() ?: ""
        Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile)
            .into(holder.binding.profileImage)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): UseViewHolder {
        val binding = UserSearchItemLayoutBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return UseViewHolder(binding)
    }

    override fun getItemCount() = mUsers.size


    inner class UseViewHolder(val binding: UserSearchItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineTxtView: CircleImageView
        var offlineTxtView: CircleImageView
        var lastMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.user_name)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineTxtView = itemView.findViewById(R.id.image_online)
            offlineTxtView = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }
    }

}