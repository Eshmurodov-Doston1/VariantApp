package uz.gxteam.variantapp.adapters.mainViewAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.zerobranch.layout.SwipeLayout
import com.zerobranch.layout.SwipeLayout.SwipeActionsListener
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.databinding.ItemReqBinding
import uz.gxteam.variantapp.models.getApplications.ClientApplication
import uz.gxteam.variantapp.models.getApplications.DataApplication


class MainRvAdapter(var context: Context,var onItemClickListener: OnItemClickListener,var viewPosition:Int):ListAdapter<DataApplication,MainRvAdapter.Vh>(MyDiffUtil()) {
    inner class Vh(var itemReqBinding: ItemReqBinding):RecyclerView.ViewHolder(itemReqBinding.root){
        fun onBind(dataApplication: DataApplication, position: Int){
            itemReqBinding.delete.setOnClickListener {
                onItemClickListener.onItemClickDelete(dataApplication,position)
            }
            if (viewPosition == 0){
                itemReqBinding.delete.visibility = View.VISIBLE
                when(dataApplication.status){
                    1->{
                        itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status0))
                    }
                    2->{
                        itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status1))
                    }
                    3->{
                        itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status2))
                    }
                }
            }else{
                itemReqBinding.delete.visibility = View.GONE
                itemReqBinding.cardBtn.setCardBackgroundColor(context.getColor(R.color.status_success))
            }

            if (dataApplication.client==null){
                itemReqBinding.name.text = "${context.getString(R.string.no_client_name)}"
            }else{
                val clientApplication = Gson().fromJson(dataApplication.client.toString(), ClientApplication::class.java)
                itemReqBinding.name.text = "${clientApplication.name} ${clientApplication.surname}"
            }
            itemReqBinding.number.text = dataApplication.created_at?.substring(0,10)
//            itemReqBinding.cardBtn.setCardBackgroundColor(Color.parseColor(request.colorCategory))
//            itemReqBinding.categoryName.text = request.category
            itemReqBinding.consView.setOnClickListener {
                onItemClickListener.onItemClick(dataApplication,position)
            }

            itemReqBinding.categoryName.text = dataApplication.status_name.status
        }
    }

    class MyDiffUtil:DiffUtil.ItemCallback<DataApplication>(){
        override fun areItemsTheSame(oldItem: DataApplication, newItem: DataApplication): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: DataApplication, newItem: DataApplication): Boolean {
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
        fun onItemClick(request: DataApplication, position: Int)
        fun onItemClickDelete(request: DataApplication, position: Int)
    }
}