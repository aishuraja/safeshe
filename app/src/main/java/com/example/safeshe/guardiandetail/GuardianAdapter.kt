package com.example.safeshe.guardiandetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.safeshe.database.Guardian
import com.example.safeshe.databinding.ListViewBinding


class GuardianAdapter(val guardians: List<Guardian>)
    : RecyclerView.Adapter<GuardianAdapter.ViewHolder>(){
//    private val binding by lazy { ListViewBinding.inflate(LayoutInflater.from(parent.context)) }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ViewHolder {
        val v = ListViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = guardians[position].guardianName
        holder.relation.text = guardians[position].guardianRelation
        holder.phone.text = guardians[position].guardianPhoneNo
        holder.email.text = guardians[position].guardianEmail

    }

    override fun getItemCount(): Int {
        return guardians.size
    }

    override  fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    class ViewHolder(itemView: ListViewBinding): RecyclerView.ViewHolder(itemView.root){
        var name = itemView.textName
        var relation = itemView.textRelation
        var phone = itemView.textPhone
        var email = itemView.textEmail
    }

}