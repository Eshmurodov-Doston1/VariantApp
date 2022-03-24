package uz.gxteam.variantapp.adapters.chatAdapter

import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import uz.gxteam.variantapp.databinding.ItemChatFromBinding
import uz.gxteam.variantapp.databinding.ItemChatToBinding
import uz.gxteam.variantapp.models.chat.messages.resMessage.MessageX
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL


class ChatAdapter(var onItemClickListener: OnItemClickImage, var datId:Long):ListAdapter<MessageX,RecyclerView.ViewHolder>(MyDiffUtil()){
    var dataStatus: Long = -1
    inner class FromVh(var itemChatFromBinding: ItemChatFromBinding):RecyclerView.ViewHolder(itemChatFromBinding.root){
        fun fromBind(request: MessageX, position: Int){
            try {
                if (request.photo!=null){
                    itemChatFromBinding.textChat.visibility = View.GONE
                    itemChatFromBinding.cardChatFrom.visibility =View.VISIBLE
                    itemChatFromBinding.image.visibility = View.GONE
                    itemChatFromBinding.imageSend.visibility = View.GONE
                    itemChatFromBinding.image.visibility = View.GONE
                    itemChatFromBinding.imageSendUser.visibility = View.VISIBLE
                    if (request.created_at?.length!!>=10){
                        itemChatFromBinding.messageFrom.text = request.message
                        itemChatFromBinding.timePhoto.text = request.created_at.substring(request.created_at.length-16,request.created_at.length-11)
                    }else{
                        itemChatFromBinding.timePhoto.text = request.created_at
                        itemChatFromBinding.messageFrom.text = request.message
                    }

                    itemChatFromBinding.imageSendUser.load("$BASE_URL/${request.photo.file_link}")
                }else
                {
                    itemChatFromBinding.cardChatFrom.visibility = View.GONE
                    itemChatFromBinding.textChat.visibility = View.VISIBLE
                }
                onItemClickListener.progressView(itemChatFromBinding.progress)
            }catch (e:Exception){
                e.printStackTrace()
            }

            if (request.created_at?.length!!>=10){
                itemChatFromBinding.messageFrom.text = request.message
                itemChatFromBinding.time.text = request.created_at.substring(request.created_at.length-16,request.created_at.length-11)
            }else{
                itemChatFromBinding.time.text = request.created_at
                itemChatFromBinding.messageFrom.text = request.message
            }

        }
    }

    inner class ToVh(var itemChatToBinding: ItemChatToBinding):RecyclerView.ViewHolder(itemChatToBinding.root){
        fun toBind(request: MessageX, position: Int){
            try {
                if (request.photo!=null){
                    itemChatToBinding.cons1.visibility = View.GONE
                    itemChatToBinding.cardChatFrom.visibility =View.VISIBLE
                    itemChatToBinding.textNull.visibility = View.GONE
                    itemChatToBinding.iconAdd.visibility = View.GONE
                    itemChatToBinding.imageSend.visibility = View.VISIBLE
                    itemChatToBinding.image.visibility = View.GONE
                    if (request.photo?.file_link!=null){
                        itemChatToBinding.messageFrom.text = request.photo.file_name
                    }else{
                        itemChatToBinding.cons1.visibility = View.GONE
                    }

                    if (request.created_at?.length!! >10){
                        itemChatToBinding.timePhoto.text = request.created_at.substring(request.created_at.length-16,request.created_at.length-11)
                    }else{
                        itemChatToBinding.timePhoto.text = request.created_at
                    }
                    val substring = request.photo.file_link.substring(request.photo.file_link.length - 4)
                    if (substring == ".jpg" || substring == ".png"){
                        itemChatToBinding.cardChatFrom.visibility = View.VISIBLE
                        itemChatToBinding.cons1.visibility = View.GONE
                        itemChatToBinding.consFile.visibility = View.GONE
                        itemChatToBinding.imageToSend.load("$BASE_URL/${request.photo.file_link}")
                    }else if (substring == ".pdf"){
                        itemChatToBinding.consFile.visibility = View.VISIBLE
                        itemChatToBinding.cons1.visibility = View.GONE
                        itemChatToBinding.cardChatFrom.visibility = View.GONE
                        itemChatToBinding.messageFromFile.text = request.photo.file_name
                        itemChatToBinding.consFile.setOnClickListener {
                            onItemClickListener.clickFile(request)
                        }
                    }
//                Picasso.get().load(request.file.toString()).into(itemChatToBinding.imageToSend)
                }else {
                    if (request.message!=null){
                        itemChatToBinding.cardChatFrom.visibility = View.GONE
                        itemChatToBinding.cons1.visibility = View.VISIBLE
                        itemChatToBinding.consFile.visibility = View.GONE
                        if (request.message.length >=4){
                            val substring = request.message.substring(request.message.length-4)
                            if (substring == ".jpg" || substring == ".png" || substring == ".pdf"){
                                itemChatToBinding.cons1.visibility = View.GONE
                                itemChatToBinding.consFile.visibility = View.GONE
                            }else{
                                itemChatToBinding.cons1.visibility = View.VISIBLE
                            }
                            Log.e("SubString", substring)
                        }
                        itemChatToBinding.messageFrom.text = request.message
                        if (request.created_at?.length?:0>=10){
                            itemChatToBinding.time.text = request.created_at?.substring(request.created_at.length-16,request.created_at.length-11)
                        }else{
                            itemChatToBinding.time.text = request.created_at
                        }
                    }

                }
            }catch (e:Exception){
                e.printStackTrace()
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
            if (getItem(position).user_id?.toLong()?:0==datId){
                return 1
            }
            return 2
        }else{
            return 1
        }
    }

    interface OnItemClickImage{
        fun imageAddClick(request: MessageX,position: Int,imageView: ImageView,cardView: CardView)
        fun clickFile(messageX: MessageX)
        fun progressView(view:View)
    }
}