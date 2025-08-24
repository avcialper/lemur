package com.avcialper.lemur.ui.team.members.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.avcialper.lemur.data.UserManager
import com.avcialper.lemur.data.model.local.MemberCard
import com.avcialper.lemur.databinding.ItemMemberCardBinding

class MemberAdapter(
    members: List<MemberCard>,
    private val removeMember: (MemberCard) -> Unit,
    private val teamLeadId: String
) :
    RecyclerView.Adapter<MemberViewHolder>() {

    private var loggedUser: MemberCard? = null

    private var data = members
        set(value) {
            field = value
            loggedUser = value.find { user ->
                user.id == UserManager.user!!.id
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemMemberCardBinding.inflate(layoutInflater, parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val memberCard = data[position]
        holder.bind(memberCard, loggedUser, removeMember, teamLeadId)
    }

    override fun getItemCount(): Int = data.size

    fun changeList(data: List<MemberCard>) {
        val diffUtil = MemberDiffUtil(this.data, data)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        this.data = data
        diffResult.dispatchUpdatesTo(this)
    }
}