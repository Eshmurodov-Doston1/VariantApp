package uz.gxteam.variantapp.ui.chat

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.viewbinding.library.fragment.viewBinding
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.rajat.pdfviewer.PdfViewerActivity
import kotlinx.coroutines.*
import net.gotev.uploadservice.UploadServiceConfig
import net.gotev.uploadservice.data.UploadInfo
import net.gotev.uploadservice.data.UploadNotificationAction
import net.gotev.uploadservice.data.UploadNotificationConfig
import net.gotev.uploadservice.data.UploadNotificationStatusConfig
import net.gotev.uploadservice.exceptions.UploadError
import net.gotev.uploadservice.exceptions.UserCancelledUploadException
import net.gotev.uploadservice.extensions.getCancelUploadIntent
import net.gotev.uploadservice.network.ServerResponse
import net.gotev.uploadservice.observer.request.RequestObserverDelegate
import net.gotev.uploadservice.protocols.multipart.MultipartUploadRequest
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import uz.gxteam.camera.model.DocType
import uz.gxteam.variantapp.*
import uz.gxteam.variantapp.adapters.chatAdapter.ChatAdapter
import uz.gxteam.variantapp.databinding.DialogCameraBinding
import uz.gxteam.variantapp.databinding.FragmentChatBinding
import uz.gxteam.variantapp.error.getError
import uz.gxteam.variantapp.error.noInternet
import uz.gxteam.variantapp.models.chat.messages.reqMessage.ReqMessage
import uz.gxteam.variantapp.models.chat.messages.resMessage.MessageX
import uz.gxteam.variantapp.models.chat.messages.resMessage.Photo
import uz.gxteam.variantapp.models.getApplications.ClientApplication
import uz.gxteam.variantapp.models.getApplications.DataApplication
import uz.gxteam.variantapp.models.getApplications.StatusName
import uz.gxteam.variantapp.models.oneApplication.OneApplication
import uz.gxteam.variantapp.models.oneApplication.sendToken.SendToken
import uz.gxteam.variantapp.models.sendMessage.SendMessageUser
import uz.gxteam.variantapp.models.webSocket.sendSocket.SendSocketData
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import uz.gxteam.webSocet.SocketData
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
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

class ChatFragment : Fragment(R.layout.fragment_chat){
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
    private val binding:FragmentChatBinding by viewBinding()
    lateinit var chatAdapter: ChatAdapter
    lateinit var gson:Gson
    var photoURI:Uri?=null
    var ImagePath:String?=null

    var WEBSOCKET_TOPIC=""
    var listMessage:ArrayList<MessageX> = ArrayList()
    var dataApplication: DataApplication?=null
    var count = 0
    var webSocketApp: WebSocket? = null
    var client: OkHttpClient? = null
    var token:String?=null
    var clickCounter = 0
    var counterRes = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {



            chatAdapter = ChatAdapter(object:ChatAdapter.OnItemClickImage{
                override fun imageAddClick(
                    request: MessageX,
                    position: Int,
                    imageView: ImageView,
                    cardView: CardView
                ) {
                    var bundle = Bundle()
                    bundle.putSerializable("type",DocType.PASSPORT)
                    findNavController().navigate(R.id.action_chatFragment_to_cameraFragment,bundle)
                    Toast.makeText(requireContext(), "$request", Toast.LENGTH_SHORT).show()
                }

                override fun clickFile(messageX: MessageX) {

                    startActivity(
                        PdfViewerActivity.launchPdfFromUrl(
                            requireContext(), "$BASE_URL/${messageX.photo?.file_link}", messageX.photo?.file_name, "dir",true)
                    )
//                    var bundle = Bundle()
//                    var string = gson.toJson(messageX)
//                    bundle.putString("messageX",string)
//                    findNavController().navigate(R.id.action_chatFragment_to_PDFFragment,bundle)
                }

                override fun progressView(view: View) {

                }
            },appViewModel.getDatabase().getUserInfo().id.toLong())


            //noInternet()

            activityListener.showBackIcon()
            activityListener.hideToolbar()
            clearMyFiles()
            back.setOnClickListener {
                findNavController().popBackStack()
            }
            token = arguments?.getString("token")
            var tokenApp = arguments?.getString("dataClass")
            gson = Gson()
            if (arguments?.getSerializable("data")!=null){
                val tojson = arguments?.getString("data")
                val oneApplication = gson.fromJson(tojson, OneApplication::class.java)
                var client:Any?=null
                if (oneApplication.client!=null){
                    client = oneApplication.client
                }
                dataApplication = DataApplication(
                    client,
                    oneApplication.created_at,
                    oneApplication.id,
                    oneApplication.status,
                    StatusName(oneApplication.status_name.id,oneApplication.status_name.status,oneApplication.status_name.title),
                    oneApplication.token)
            }else{
                dataApplication = gson.fromJson(tokenApp, DataApplication::class.java)
            }
            if (dataApplication?.client!=null){
                val clientApplication = gson.fromJson(dataApplication?.client.toString(), ClientApplication::class.java)
                textUser.text = "${clientApplication.name} ${clientApplication.surname}"
                btnUser.visibility = View.VISIBLE
            }else{
                btnUser.visibility = View.GONE
            }
            client = OkHttpClient()
            WEBSOCKET_TOPIC = "chatApplications.${dataApplication?.token}"
            activityListener.hideLoading()
            text.addTextChangedListener {
               if ((it!=null || it.toString().trim().isNotEmpty()) &&  it.toString().trim().isNotBlank()){
                   send.visibility = View.GONE
                   sendText.visibility = View.VISIBLE
               }

              if (it.toString().trim().isEmpty()) {
                   send.visibility = View.VISIBLE
                   sendText.visibility = View.GONE
               }

            }


            Log.e("UserId------------------->", appViewModel.getDatabase().getUserInfo().id.toLong().toString())
            binding.sendText.setOnClickListener {
                    val message = binding.text.text.toString().trim()
                    if (message.isNotEmpty()){
                        lifecycleScope.launch {
                            appViewModel.sendMessageServer(SendMessageUser(message, dataApplication?.token.toString())).collect {
                                when(it){
                                    is VariantResourse.SuccessSendChat->{
                                        activityListener.hideLoading()
                                        Log.e("Success KEtdi", it.resMessageUser?.toString().toString())
                                        GlobalScope.launch(Dispatchers.Main) {
                                            val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                                            listMessage.add(MessageX(null,currentTime,appViewModel.getDatabase().getUserInfo().id,message,null
                                                ,null,dataApplication?.status,null,currentTime,appViewModel.getDatabase().getUserInfo().id))
                                            chatAdapter.notifyItemInserted(listMessage.size)
                                            binding.rvChat.smoothScrollToPosition(listMessage.size)
                                            binding.text.text.clear()
                                            Log.e("SaveData", listMessage[listMessage.size-1].toString())
                                        }
                                    }
                                    is VariantResourse.Error->{
                                        activityListener.hideLoading()
                                        if (it.appError.internetConnection==true){
                                            getError(requireContext(),it.appError,findNavController())
                                        }else{
                                            noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            send.setOnClickListener {
                PermissionX.init(activity)
                    .permissions(Manifest.permission.CAMERA)
                    .request { allGranted, grantedList, deniedList ->
                        if (allGranted){
                            Log.e("Data_Status_Blah+Blah", "Status :${dataApplication?.status} Client: ${dataApplication?.client} clickCounter: $clickCounter" )
                            if (dataApplication?.client==null){
                                clickCounter++
                                var bundle = Bundle()
                                bundle.putSerializable("type",DocType.PASSPORT)
                                bundle.putString("data",gson.toJson(dataApplication))
                                findNavController().navigate(R.id.action_chatFragment_to_cameraFragment,bundle)
                            }else if(dataApplication?.client!=null){
                                var alertDialog = AlertDialog.Builder(root.context, R.style.BottomSheetDialogThem)
                                var itemBottomSheetDialog = DialogCameraBinding.inflate(LayoutInflater.from(root.context), null, false)
                                val create = alertDialog.create()
                                create.setView(itemBottomSheetDialog.root)
                                itemBottomSheetDialog.close.setOnClickListener {
                                    create.dismiss()
                                }

                                itemBottomSheetDialog.consCamera.setOnClickListener {
                                    var imageFile = createImageFile()
                                    counterRes = 0
                                    photoURI= FileProvider.getUriForFile(root.context, BuildConfig.APPLICATION_ID,imageFile)
                                    getTakeImageContent.launch(photoURI)
                                    create.dismiss()
                                }

                                itemBottomSheetDialog.consGallery.setOnClickListener {
                                    counterRes = 0
                                    picImageForNewGallery()
                                    create.dismiss()
                                }
                                create.setCancelable(false)
                                create.show()

                            }
                        }else{
                            startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)))
                        }
                    }
            }



//            if (dataApplication?.status==1 && listMessage.isEmpty()){
//                val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
//                listMessage.add(MessageX(null,currentTime,appViewModel.getDatabase().getUserInfo().id,null,null
//                    ,null,dataApplication?.status,null,currentTime,appViewModel.getDatabase().getUserInfo().id))
//
//                chatAdapter.submitList(listMessage)
//                rvChat.adapter = chatAdapter
//                rvChat.smoothScrollToPosition(chatAdapter.itemCount)
//            }

        }
    }

    private fun loadDataMessage() {
        binding.apply {
            lifecycleScope.launch {
                appViewModel.getAllMessage(ReqMessage(token.toString())).collect {
                    when(it){
                        is VariantResourse.Loading->{
                            activityListener.showLoading()
                        }
                        is VariantResourse.SuccessGetAllMessage->{
                            activityListener.hideLoading()
                            chatAdapter.dataStatus = dataApplication?.status?.toLong()?:-1
                            listMessage.addAll(it.message?.messages?: emptyList())
                            chatAdapter.submitList(listMessage)
                            binding.rvChat.adapter = chatAdapter
                            rvChat.smoothScrollToPosition(chatAdapter.itemCount)
                            it.message?.messages?.get(0)?.user_id = it.message?.messages?.get(0)?.user_id?.plus(1)
                            if (it.message?.messages?.isEmpty() == true){
                                noInfo.visibility =View.VISIBLE
                            }else{
                                noInfo.visibility = View.GONE
                            }
                            Log.e("Messages", it.message?.messages.toString() )
                        }
                        is VariantResourse.Error->{
                            activityListener.showLoading()
                            if (it.appError.internetConnection==true){
                                getError(requireContext(),it.appError,findNavController())
                            }else{
                                noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                            }
                        }
                    }
                }
            }
        }
    }


    override fun onResume() {
        super.onResume()
        listMessage.clear()
        try {
            if (dataApplication?.client!=null){
                binding.btnUser.visibility = View.VISIBLE
//                val clientApplication = gson.fromJson(dataApplication?.client.toString(), ClientApplication::class.java)
//                binding.textUser.text = "${clientApplication.name} ${clientApplication.surname}"
            }else{
                binding.btnUser.visibility = View.GONE
            }

        }catch (e:Exception){
            e.printStackTrace()
        }
        // https://web.variantgroup.uz/api/broadcasting/auth
        loadDataMessage()
        getSocket()
        getapplicationStatu()
    }


    fun getSocket(){
        try {
            val request: okhttp3.Request =
                okhttp3.Request.Builder().url("ws://web.variantgroup.uz:6001/app/mykey?protocol=7&client=js&version=7.0.6&flash=false").build()
            var listener = object:WebSocketListener(){
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    super.onOpen(webSocket, response)
                    //  Log.e("Response socket", response.body.toString() )
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    super.onMessage(webSocket, text)
                    Log.e("Socket Message---->>>", text)
                    val socketData = gson.fromJson(text, SocketData::class.java)

                    if (count==0){
                        val dataSocket = gson.fromJson(socketData.data, uz.gxteam.webSocet.Data::class.java)
                        Log.e("Data Socket","$socketData $dataSocket")

                        lifecycleScope.launch {
                            appViewModel.broadCatAuth(SendSocketData("private-ChatNewMessage.${dataApplication?.token}", dataSocket.socket_id.toString())).collect {
                                when(it){
                                    is VariantResourse.Loading->{
                                        activityListener.showLoading()
                                    }
                                    is VariantResourse.SuccessBroadCastingAuth->{
                                        activityListener.hideLoading()

                                        webSocket.send(" {\"event\":\"pusher:subscribe\",\"data\":{\"auth\":\"${it.resSocket?.auth}\",\"channel\":\"private-ChatNewMessage.${dataApplication?.token}\"}}")
                                        count++
                                    }
                                    is VariantResourse.Error->{
                                        activityListener.hideLoading()
                                        if (it.appError.internetConnection==true){
                                            getError(requireContext(),it.appError,findNavController())
                                        }else{
                                            noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                                        }
                                    }
                                }
                            }
                        }

                        lifecycleScope.launch {
                            appViewModel.broadCatAuthApp(SendSocketData("ChatAppStatus", dataSocket.socket_id.toString())).collect {
                                when(it){
                                    is VariantResourse.Loading->{
                                        activityListener.showLoading()
                                    }
                                    is VariantResourse.SuccessBroadCastingAuthApp->{
                                        activityListener.hideLoading()
                                        Log.e("Data Status",it.chatAppStatus?.auth.toString())
                                        webSocket.send("{\"event\":\"pusher:subscribe\",\"data\":{\"auth\":\"${it.chatAppStatus?.auth}\",\"channel\":\"ChatAppStatus\"}}")
                                        count++
                                    }
                                    is VariantResourse.Error->{
                                        activityListener.hideLoading()
                                        if (it.appError.internetConnection==true){
                                            getError(requireContext(),it.appError,findNavController())
                                        }else{
                                            noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                                        }
                                    }
                                }
                            }
                        }
                    }else{
                        if (socketData.data!=null){
                            Log.e("SocketData", socketData.toString() )
                            val dataSocket1 = gson.fromJson(socketData.data, uz.gxteam.variantapp.models.webSocket.socketMessage.Data::class.java)
                            val currentTime: String = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                            if (dataSocket1.type.toInt() == 1){
                                Log.e("Data Message Socket 1>","$dataSocket1")
                                GlobalScope.launch(Dispatchers.Main) {
                                    if (dataSocket1.link!=null){
                                        listMessage.add(
                                            MessageX(dataSocket1.app_id,currentTime,null,
                                                dataSocket1.message,
                                                Photo(dataApplication?.id?:1,
                                                    dataApplication?.created_at?:0,
                                                    dataSocket1.ext,
                                                    dataSocket1.link.toString(),
                                                    dataSocket1.message,
                                                    listMessage.size+1,
                                                    dataSocket1.type.toInt(),
                                                    dataApplication?.created_at.toString()
                                                ),listMessage.size+1,null,dataSocket1.app_id,currentTime,listMessage.size+1)
                                        )
                                    }else{
                                        listMessage.add(
                                            MessageX(dataSocket1.app_id,currentTime,null,
                                                dataSocket1.message,null,listMessage.size+1,null,dataSocket1.app_id,currentTime,listMessage.size+1)
                                        )
                                    }
                                    chatAdapter.notifyItemInserted(listMessage.size)
                                    binding.rvChat.smoothScrollToPosition(listMessage.size)
                                    Log.e("SaveData", listMessage[listMessage.size-1].toString())
                                }
                            }else if (dataSocket1.type.toInt()==2){

                            }
                            count++
                        }
                    }

                }
                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    super.onMessage(webSocket, bytes)
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    super.onClosed(webSocket, code, reason)
                    webSocket.close(code,reason)
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    super.onFailure(webSocket, t, response)
                }

            }
            webSocketApp = client!!.newWebSocket(request, listener)
            client!!.dispatcher.executorService.shutdown()
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        webSocketApp?.close(1000,"Close Socket")
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val date = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val externalFilesDir = activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
      var file = File.createTempFile("JPEG_$date",".jpg",externalFilesDir).apply {
           ImagePath = absolutePath
        }
        return file
    }

    fun clearMyFiles() {
        val files = activity?.filesDir?.listFiles()
        if (files != null) for (file in files) {
            file.delete()
        }
    }


    private val getTakeImageContent = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
               if (photoURI!=null){
                   Log.e("Poto URi", photoURI.toString())
//                   listChat[position1].imageMain = photoURI
//                   imageView1.setImageURI(photoURI)
                 //  cardView1.visibility = View.GONE
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
                   uploadImage(filAbsolutePath,file)
               }
        }
    }

    private fun picImageForNewGallery() {
        getImageContent.launch("image/*")
    }


    private var getImageContent = registerForActivityResult(ActivityResultContracts.GetContent()){ uri->
        uri?:return@registerForActivityResult
//        cardView1.visibility = View.GONE
//        imageView1.setImageURI(uri)
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

        uploadImage(filAbsolutePath,file)






//        var fileInputStream = FileInputStream(file)
//        val readBytes = fileInputStream.readBytes()
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        activityListener = activity as ActivityListener
    }




    fun uploadImage(imagePath:String,imageFileApp:File){
        Log.e("SendImagePath", imagePath.toString() )
        GlobalScope.launch(Dispatchers.Main) {
                MultipartUploadRequest(context = requireContext(),serverUrl = "${BASE_URL}/api/chat/upload")
                    .addHeader("Authorization","${appViewModel.getShared().tokenType} ${appViewModel.getShared().accessToken}")
                    .addHeader("Accept","application/json")
                    .setMethod("POST")
                    .setNotificationConfig { context, uploadId ->
                        UploadNotificationAction(
                            icon = android.R.drawable.ic_menu_close_clear_cancel,
                            title = "Cancel",
                            intent = context.getCancelUploadIntent(uploadId))
                        UploadNotificationConfig(
                            notificationChannelId = UploadServiceConfig.defaultNotificationChannel!!,
                            isRingToneEnabled = false,
                            progress = UploadNotificationStatusConfig(
                                title = getString(R.string.please_wait),
                                message = ""
                            ),
                            success = UploadNotificationStatusConfig(
                                title = getString(R.string.photo_send_success),
                                message = ""
                            ),
                            error = UploadNotificationStatusConfig(
                                title = getString(R.string.error_app),
                                message = getString(R.string.no_data)
                            ),
                            cancelled = UploadNotificationStatusConfig(
                                title = "cancelled",
                                message = "some cancelled message"
                            )
                        )
                    }
                    .addParameter("token", dataApplication?.token.toString()) //Adding text parameter to the request
                    .addParameter("type", "${dataApplication?.status}") //Adding text parameter to the request
                    .addFileToUpload(imagePath, "photo") //Adding file
                    .subscribe(requireContext(),viewLifecycleOwner, delegate = object:
                        RequestObserverDelegate {
                        override fun onCompleted(context: Context, uploadInfo: UploadInfo) {}
                        @SuppressLint("LongLogTag")
                        override fun onCompletedWhileNotObserving() {
                           activityListener.showLoading()
                        }

                        override fun onError(
                            context: Context,
                            uploadInfo: UploadInfo,
                            exception: Throwable
                        ) {
                            when (exception) {
                                is UserCancelledUploadException -> {
                                    Toast.makeText(requireContext(), "Xatolik:${exception.message}", Toast.LENGTH_SHORT).show()
                                }
                                is UploadError -> {
                                    activityListener.showLoadingCamera()
                                    Log.e("Error_UPLOAD",  exception.serverResponse.bodyString)
                                    getapplicationStatu()
                                    // errorMain(requireActivity(), exception.serverResponse.bodyString,  exception.serverResponse.code,registerListener)
                                }
                                else -> {}
                            }
                        }


                        override fun onProgress(context: Context, uploadInfo: UploadInfo) {
                            activityListener.hideLoadingCamera()
                        }

                        override fun onSuccess(
                            context: Context,
                            uploadInfo: UploadInfo,
                            serverResponse: ServerResponse
                        ) {
                            if (serverResponse.isSuccessful ){
                                getSocket()
                                loadDataMessage()
                                clearMyFiles()
                                counterRes++
                            }
                        }
                    })
            }
    }



    private fun getapplicationStatu() {
        lifecycleScope.launch {
            appViewModel.getOneApplication(SendToken(dataApplication?.token.toString())).collect{
                when(it){
                    is VariantResourse.SuccessGetApplication->{
                        dataApplication = DataApplication(
                            it.oneApplication?.client,
                            it.oneApplication?.created_at,
                            it.oneApplication?.id,
                            it.oneApplication?.status,
                            StatusName(
                                it.oneApplication?.status_name?.id?:0,
                                it.oneApplication?.status_name?.status.toString(),
                                it.oneApplication?.status_name?.title.toString()),
                            it.oneApplication?.token.toString())
                        Log.e(TAG, dataApplication.toString() )
                    }
                    is VariantResourse.Error->{
                        if (it.appError.internetConnection==true){
                            getError(requireContext(),it.appError,findNavController())
                        }else{
                            noInternet(requireActivity() as MainActivity,requireContext(),binding.root,lifecycle)
                        }
                    }
                }
            }
        }
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
