package com.avcialper.lemur.ui.team.members.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.Member
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.ItemMemberCardBinding

class MemberAdapter(members: List<MemberCard>, private val removeMember: (Member) -> Unit) :
    RecyclerView.Adapter<MemberViewHolder>() {

    private var data = members

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMemberCardBinding.inflate(layoutInflater, parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val memberCard = data[position]
        val isAdmin = isLoggedUserIsAdmin()
        holder.bind(memberCard, isAdmin, removeMember)
    }

    override fun getItemCount(): Int = data.size

    private fun isLoggedUserIsAdmin(): Boolean {
        val user = data.find { it.id == UserManager.user!!.id }
        return user?.roleCodes?.find { code -> code == "ADMIN" } != null
    }

    fun changeList(data: List<MemberCard>) {
        val diffUtil = MemberDiffUtil(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.data = data
        diffResult.dispatchUpdatesTo(this)
    }
}