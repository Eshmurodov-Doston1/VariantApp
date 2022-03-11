package uz.gxteam.variantapp.ui.chat

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.Channel
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.WebSocket
import tech.gusavila92.websocketclient.WebSocketClient
import uz.gxteam.variantapp.ActivityListener
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.adapters.chatAdapter.ChatAdapter
import uz.gxteam.variantapp.databinding.FragmentChatBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.chat.messages.resMessage.MessageX
import uz.gxteam.variantapp.models.getApplications.Data
import uz.gxteam.variantapp.models.mainClass.Request
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URI
import java.net.URISyntaxException
import java.util.*
import javax.inject.Inject


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChatFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@FlowPreview
@ExperimentalCoroutinesApi
class ChatFragment : Fragment(uz.gxteam.variantapp.R.layout.fragment_chat){
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent.inject(this)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }












    private  val TAG = "ChatFragment"
    lateinit var activityListener: ActivityListener
    var PICK_FROM_GALLERY = 101
    var position1:Int = 0
    @Inject
    lateinit var appViewModel:AppViewModel
    lateinit var imageView1: ImageView
    lateinit var cardView1: CardView
    private val binding:FragmentChatBinding by viewBinding()
    lateinit var chatAdapter: ChatAdapter
    lateinit var listChat:ArrayList<Request>
    var photoURI:Uri?=null
    var ImagePath:String?=null
    lateinit var client:OkHttpClient
    private lateinit var webSocketClient: tech.gusavila92.websocketclient.WebSocketClient


    var mWebSocket: WebSocket? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            activityListener.showBackIcon()
//            val data = arguments?.getSerializable("data") as Data
            val token = arguments?.getString("token")
            val serializable = arguments?.getSerializable("data")
            client = OkHttpClient()
            var data: Data? = null
            if (serializable!=null){
                data = serializable as Data
                Log.e("data", data.toString() )
            }


            val options = PusherOptions().setCluster("MyApp")
            val pusher = Pusher("ws://web.variantgroup.uz:6001/app/mykey?protocol=7&client=js&version=7.0.6&flash=false", options)





            pusher.connect(object : ConnectionEventListener {
                override fun onConnectionStateChange(change: ConnectionStateChange) {
                    println(
                        "State changed to " + change.currentState +
                                " from " + change.previousState
                    )
                }

                override fun onError(message: String?, code: String?, e: java.lang.Exception?) {
                    println("There was a problem connecting!")
                }
            }, ConnectionState.ALL)


            val channel: Channel = pusher.subscribe("my-channel")

            channel.bind("my-event") { event ->
                println("Received event with data: $event")
            }

            pusher.disconnect()

            pusher.connect()







            activityListener.hideLoading()
            lifecycleScope.launch {
                appViewModel.getAllMessage(ReqMessage(token.toString())).collect {
                    when(it){
                        is VariantResourse.Loading->{
                            activityListener.showLoading()
                        }
                        is VariantResourse.SuccessGetAllMessage->{
                            activityListener.hideLoading()
                          chatAdapter = ChatAdapter(object:ChatAdapter.OnItemClickImage{
                              override fun imageAddClick(
                                  request: MessageX,
                                  position: Int,
                                  imageView: ImageView,
                                  cardView: CardView
                              ) {

                              }
                          },data?.id?:0)
                            chatAdapter.submitList(it.message?.messages)
                            binding.rvChat.adapter = chatAdapter
                        }
                        is VariantResourse.Error->{
                            activityListener.showLoading()
                            if (it.appError.internetConnection==true){
                                noInternet(requireContext(),binding.root,lifecycle)
                            }else{
                                getError(requireContext(),it.appError)
                            }
                        }
                    }
                }
            }




            binding.send.setOnClickListener {
                val message = binding.text.toString().trim()
                if (message.isNotEmpty()){
                    lifecycleScope.launch {
                        appViewModel.sendMessageServer(SendMessageUser(message, appViewModel.getShared().token_socet.toString())).collect {
                            when(it){
                                is VariantResourse.Loading->{
                                    activityListener.showLoading()
                                }
                                is VariantResourse.SuccessSendChat->{
                                    activityListener.hideLoading()
                                    Log.e("Success KEtdi", it.resMessageUser?.toString().toString())
                                }
                                is VariantResourse.Error->{
                                    activityListener.hideLoading()
                                    if (it.appError.internetConnection==true){
                                        getError(requireContext(),it.appError)
                                    }else{
                                        noInternet(requireContext(),binding.root,lifecycle)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    private fun createWebSocketClient() {
        val uri: URI
        try {
            // Connect to local host
            uri = URI("ws://web.variantgroup.uz:6001/app/mykey?protocol=7&client=js&version=7.0.6&flash=false")
        } catch (e: URISyntaxException) {
            e.printStackTrace()
            return
        }


        webSocketClient = object : WebSocketClient(uri) {
            override fun onOpen() {
                Log.i("WebSocket", "Session is starting")
             //   webSocketClient.send("{token:${appViewModel.getShared().token_socet},message:Salom Brat}")
            }


            override fun onTextReceived(s: String) {
                Log.i("WebSocket", "Message received")
//                UiThreadStatement.runOnUiThread(Runnable {
//                    try {
//                        val textView: TextView = findViewById(R.id.animalSound)
//                        textView.text = s
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                    }
//                })
            }

            override fun onBinaryReceived(data: ByteArray?) {}
            override fun onPingReceived(data: ByteArray?) {}
            override fun onPongReceived(data: ByteArray?) {}
            override fun onException(e: Exception) {
                println(e.message)
            }


            override fun onCloseReceived() {
                Log.i("WebSocket", "Closed ")
                println("onCloseReceived")
            }

        }

        webSocketClient

        webSocketClient.addHeader("Authorization","${appViewModel.getShared().tokenType} ${appViewModel.getShared().accessToken}")
        webSocketClient.setConnectTimeout(10000)
        webSocketClient.setReadTimeout(60000)
        webSocketClient.enableAutomaticReconnection(5000)
        webSocketClient.connect()
    }


//    loadChat()


//    clearMyFiles()
//
//    chatAdapter = ChatAdapter(object:ChatAdapter.OnItemClickImage{
//        override fun imageAddClick(
//            request: Request,
//            position: Int,
//            imageView: ImageView,
//            cardView: CardView
//        ) {
//            PermissionX.init(activity)
//                .permissions(Manifest.permission.CAMERA)
//                .request { allGranted, grantedList, deniedList ->
//                    if (allGranted) {
//                        var alertDialog = AlertDialog.Builder(root.context, R.style.BottomSheetDialogThem)
//                        var itemBottomSheetDialog = DialogCameraBinding.inflate(LayoutInflater.from(root.context), null, false)
//                        val create = alertDialog.create()
//                        create.setView(itemBottomSheetDialog.root)
//                        itemBottomSheetDialog.close.setOnClickListener {
//                            create.dismiss()
//                        }
//
//                        itemBottomSheetDialog.consCamera.setOnClickListener {
//                            var imageFile = createImageFile()
//                            photoURI= FileProvider.getUriForFile(root.context,BuildConfig.APPLICATION_ID,imageFile)
//                            getTakeImageContent.launch(photoURI)
//                            create.dismiss()
//                            position1 = position
//                            imageView1 = imageView
//                            cardView1 = cardView
//                        }
//
//                        itemBottomSheetDialog.consGallery.setOnClickListener {
//                            picImageForNewGallery()
//                            imageView1 = imageView
//                            cardView1 = cardView
//                            create.dismiss()
//                        }
//                        create.setCancelable(false)
//                        create.show()
//                    } else {
//                        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
//                    }
//                }
//        }
//    },"+998994206278")
//    chatAdapter.submitList(listChat)
//    rvChat.adapter = chatAdapter
//











    @Throws(IOException::class)
    private fun createImageFile(): File {
        val date = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      var file = File.createTempFile("JPEG_$date",".jpg",externalFilesDir).apply {
            absolutePath
        }
        return file
    }

    fun clearMyFiles() {
        val files = requireActivity().filesDir.listFiles()
        if (files != null) for (file in files) {
            file.delete()
        }
    }

    private val getTakeImageContent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
               if (photoURI!=null){
                   listChat[position1].imageMain = photoURI
                   imageView1.setImageURI(photoURI)
                   cardView1.visibility = View.GONE
//                   fragmentAddZnakBinding.image.setImageURI(photoURI)
                   var openInputStream = activity?.contentResolver?.openInputStream(photoURI!!)
                   var format = java.text.SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
                   var file = File(activity?.filesDir, "$format.jpg")
                   var fileoutputStream = FileOutputStream(file)
                   openInputStream?.copyTo(fileoutputStream)
                   openInputStream?.close()
                   fileoutputStream.close()
                   var filAbsolutePath = file.absolutePath
                   ImagePath = filAbsolutePath
               }
        }
    }

    private fun picImageForNewGallery() {
        getImageContent.launch("image/*")
    }


    private var getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?:return@registerForActivityResult
        cardView1.visibility = View.GONE
        imageView1.setImageURI(uri)
        //fragmentAddZnakBinding.image.setImageURI(uri)
        var openInputStream =(activity)?.contentResolver?.openInputStream(uri)
        var filesDir = (activity)?.filesDir
        var format = java.text.SimpleDateFormat("yyyyMMdd_HHmmss",Locale.getDefault()).format(Date())
        var file = File(filesDir,"$format.jpg")
        val fileOutputStream = FileOutputStream(file)
        openInputStream!!.copyTo(fileOutputStream)
        openInputStream.close()
        fileOutputStream.close()
        var filAbsolutePath = file.absolutePath
        ImagePath = filAbsolutePath
//        var fileInputStream = FileInputStream(file)
//        val readBytes = fileInputStream.readBytes()
    }



    private fun loadChat() {
        listChat = ArrayList()
        listChat.add(Request("Dostonbek","+998994206275","SMS","null",false,"12:30","https://image.winudf.com/v2/image1/Y29tLk1vYmlsZVN0cmFuZ2VyLmFwcDA0MjBfc2NyZWVuXzFfMTU2NzE1OTA3MF8wMTk/screen-1.jpg?fakeurl=1&type=.jpg"))
        listChat.add(Request("Dostonbek","+998994206278","SMS","null",false,"12:30"))
        listChat.add(Request("Dostonbek","+998994206278","SMS","null",false,"12:30","https://image.winudf.com/v2/image1/Y29tLk1vYmlsZVN0cmFuZ2VyLmFwcDA0MjBfc2NyZWVuXzFfMTU2NzE1OTA3MF8wMTk/screen-1.jpg?fakeurl=1&type=.jpg"))
        listChat.add(Request("Dostonbek","+998994206275","SMS","null",false,"12:30"))
        listChat.add(Request("Dostonbek","+998994206275","SMS","null",false,"12:30","https://image.winudf.com/v2/image1/Y29tLk1vYmlsZVN0cmFuZ2VyLmFwcDA0MjBfc2NyZWVuXzFfMTU2NzE1OTA3MF8wMTk/screen-1.jpg?fakeurl=1&type=.jpg"))
        listChat.add(Request("Dostonbek","+998994206278","SMS","null",false,"12:30"))
        listChat.add(Request("Dostonbek","+998994206275","SMS","null",false,"12:30","https://avatars.mds.yandex.net/i?id=c1e110f05c7ae09634a2c0103ffe8b58-5869782-images-thumbs&n=13"))
        listChat.add(Request("Dostonbek","+998994206278","SMS","null",false,"12:30"))
        listChat.add(Request("Dostonbek","+998994206275","SMS","null",false,"12:30","https://avatars.mds.yandex.net/i?id=c1e110f05c7ae09634a2c0103ffe8b58-5869782-images-thumbs&n=13"))
        listChat.add(Request("Dostonbek","+998994206275","SMS","null",false,"12:30"))
        listChat.add(Request("Dostonbek","+998994206278","SMS","null",false,"12:30", type = 1))
        listChat.add(Request("Dostonbek","+998994206278","SMS","null",false,"12:30", type = 0))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityListener = activity as ActivityListener
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ChatFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}