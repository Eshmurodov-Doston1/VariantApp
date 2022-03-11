package uz.gxteam.variantapp.adapters.chatAdapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import uz.gxteam.variantapp.databinding.ItemChatFromBinding
import uz.gxteam.variantapp.databinding.ItemChatToBinding
import uz.gxteam.variantapp.models.chat.messages.resMessage.MessageX
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ChatAdapter(var onItemClickListener: OnItemClickImage, var datId:Long):ListAdapter<MessageX,RecyclerView.ViewHolder>(MyDiffUtil()){
    inner class FromVh(var itemChatFromBinding: ItemChatFromBinding):RecyclerView.ViewHolder(itemChatFromBinding.root){
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromBind(request: MessageX, position: Int){
            if (request.file!=null){
                itemChatFromBinding.cons1.visibility = View.GONE
                itemChatFromBinding.cardChatFrom.visibility =View.VISIBLE
                itemChatFromBinding.textNull.visibility = View.GONE
                itemChatFromBinding.iconAdd.visibility = View.GONE
                itemChatFromBinding.imageSend.visibility = View.GONE
                itemChatFromBinding.image.visibility = View.GONE
                Picasso.get().load(request.file.toString()).into(itemChatFromBinding.imageSend)
                Picasso.get().load(request.file.toString()).into(itemChatFromBinding.imageSendUser)
            }else{
                itemChatFromBinding.cardChatFrom.visibility = View.GONE
                itemChatFromBinding.cons1.visibility = View.VISIBLE
            }
            if (request.status==0){
//                itemChatFromBinding.cardChatFrom.visibility = View.VISIBLE
//                itemChatFromBinding.cons1.visibility = View.GONE
                itemChatFromBinding.cardChatFrom.setOnClickListener {
                    onItemClickListener.imageAddClick(request,position,itemChatFromBinding.imageSendUser,itemChatFromBinding.image)
                }
            }
            itemChatFromBinding.messageFrom.text = request.message
            itemChatFromBinding.time.text = request.created_at.substring(request.created_at.length-16,request.created_at.length-11)


        }
    }

    inner class ToVh(var itemChatToBinding: ItemChatToBinding):RecyclerView.ViewHolder(itemChatToBinding.root){
        fun toBind(request: MessageX,position: Int){
            if (request.file!=null){
                itemChatToBinding.cons1.visibility = View.GONE
                itemChatToBinding.cardChatFrom.visibility =View.VISIBLE
                itemChatToBinding.textNull.visibility = View.GONE
                itemChatToBinding.iconAdd.visibility = View.GONE
                itemChatToBinding.imageSend.visibility = View.VISIBLE
                itemChatToBinding.image.visibility = View.GONE
                Picasso.get().load(request.file.toString()).into(itemChatToBinding.imageSend)
                Picasso.get().load(request.file.toString()).into(itemChatToBinding.imageToSend)
            }else{
                itemChatToBinding.cardChatFrom.visibility = View.GONE
                itemChatToBinding.cons1.visibility = View.VISIBLE
            }
        }
    }

    class MyDiffUtil:DiffUtil.ItemCallback<MessageX>(){
        override fun areItemsTheSame(oldItem: MessageX, newItem: MessageX): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MessageX, newItem: MessageX): Boolean {
            return oldItem.equals(newItem)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType==1){
            return FromVh(ItemChatFromBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }else{
            return ToVh(ItemChatToBinding.inflate(LayoutInflater.from(parent.context),parent,false))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position)==1){
            val fromVh = holder as FromVh
            fromVh.fromBind(getItem(position),position)
        }else{
            val toVh = holder as ToVh
            toVh.toBind(getItem(position),position)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (!datId.equals(0)){
            if (getItem(position).chat_application_id.toLong()==datId){
                return 1
            }
            return 2
        }else{
            return 1
        }
    }

    interface OnItemClickImage{
        fun imageAddClick(request: MessageX,position: Int,imageView: ImageView,cardView: CardView)
    }
}