package uz.gxteam.variantapp.adapters.mainViewAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.databinding.ItemReqBinding
import uz.gxteam.variantapp.models.getApplications.Data
import uz.gxteam.variantapp.models.mainClass.Request
import java.text.SimpleDateFormat
import java.util.*

class MainRvAdapter(var context: Context,var onItemClickListener: OnItemClickListener):ListAdapter<Data,MainRvAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemReqBinding: ItemReqBinding):RecyclerView.ViewHolder(itemReqBinding.root){
        @SuppressLint("SimpleDateFormat")
        fun onBind(data: Data, position: Int){
           when(data.status){
               0->{
                   itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status0))
               }
               1->{
                   itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status1))
               }
               2->{
                   itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status2))
               }
           }

            if (data.client==null){
                itemReqBinding.name.text = context.getString(R.string.no_client_name)
            }else{
                itemReqBinding.name.text = data.client
            }
            itemReqBinding.number.text = data.created_at.substring(0,10)
//            itemReqBinding.cardBtn.setCardBackgroundColor(Color.parseColor(request.colorCategory))
//            itemReqBinding.categoryName.text = request.category
            itemReqBinding.card.setOnClickListener {
                onItemClickListener.onItemClick(data,position)
            }
        }
    }

    class MyDiffUtil:DiffUtil.ItemCallback<Data>(){
        override fun areItemsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Data, newItem: Data): Boolean {
            return oldItem.equals(newItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemReqBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(getItem(position),position)
    }
    interface OnItemClickListener{
        fun onItemClick(request: Data,position: Int)
    }
}