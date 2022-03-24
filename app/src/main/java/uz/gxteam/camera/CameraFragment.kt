package uz.gxteam.camera

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.viewbinding.library.fragment.viewBinding
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentContainer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.common.util.concurrent.ListenableFuture
import com.google.gson.Gson
import com.permissionx.guolindev.PermissionX
import com.squareup.okhttp.MediaType
import com.squareup.okhttp.RequestBody
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jmrtd.lds.icao.MRZInfo
import uz.gxteam.camera.model.DocType
import uz.gxteam.camera.other.GraphicOverlay
import uz.gxteam.camera.text.TextRecognitionProcessor
import uz.gxteam.variantapp.ActivityListener
import uz.gxteam.variantapp.App
import uz.gxteam.variantapp.R
import uz.gxteam.variantapp.databinding.FragmentCameraBinding
import uz.gxteam.variantapp.models.getApplications.DataApplication
import uz.gxteam.variantapp.utils.AppConstant.BASE_URL
import uz.gxteam.variantapp.utils.VariantResourse
import uz.gxteam.variantapp.viewModels.appViewModel.AppViewModel
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CameraFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CameraFragment : Fragment(R.layout.fragment_camera),TextRecognitionProcessor.ResultListener {
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
    @Inject
    lateinit var appViewModel:AppViewModel
    var _beep: ToneGenerator? = null
    private var cameraSource:CameraSource? = null
    private var preview: CameraSourcePreview?=null
    private var graphicOverlay: GraphicOverlay? = null
    private var actionBar: ActionBar? = null

    val MRZ_RESULT = "MRZ_RESULT"
    val DOC_TYPE = "DOC_TYPE"

    private var docType: DocType = DocType.OTHER
    lateinit var  cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private var cameraProvider:ProcessCameraProvider?=null
    private var imageCapture: ImageCapture?=null


    private val executor: Executor = Executors.newSingleThreadExecutor()
    lateinit var activityListener:ActivityListener
    var dataApplication:DataApplication?=null
    private val binding:FragmentCameraBinding by viewBinding()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            activityListener.hideToolbar()
           dataApplication = Gson().fromJson(arguments?.getString("data"),DataApplication::class.java)
            PermissionX.init(activity)
                .permissions(Manifest.permission.CAMERA)
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        val cameraProviderFuture = ProcessCameraProvider.getInstance(
                            requireContext()
                        )
                        cameraProviderFuture.addListener({
                            try {
                                cameraProvider = cameraProviderFuture.get()
                            } catch (e: ExecutionException) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                            } catch (e: InterruptedException) {
                                Toast.makeText(requireContext(), e.message, Toast.LENGTH_SHORT).show()
                            }
                        }, ContextCompat.getMainExecutor(requireContext()))

                        _beep = ToneGenerator(AudioManager.STREAM_ALARM, 50)

                        val intent = arguments

                        docType = (intent?.getSerializable("type") as DocType?)!!
                        if (docType === DocType.PASSPORT) {
                            binding.view.visibility = View.VISIBLE
                            binding.view1.visibility = View.GONE
                            // requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                        }else if (docType === DocType.ID_CARD){
                            binding.view.visibility = View.GONE
                            binding.view1.visibility = View.VISIBLE
                        }


                        preview = binding.cameraSourcePreview
                        if (preview == null) {
                            Log.d("Image", "Preview is null")
                        }
                        graphicOverlay = binding.graphicsOverlay
                        if (graphicOverlay == null) {
                            Log.d("Log", "graphicOverlay is null")
                        }

                        createCameraSource()
                        startCameraSource()
                    } else {
                        Toast.makeText(requireContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun createCameraSource() {
        if (cameraSource == null) {
            cameraSource = CameraSource(requireActivity(), graphicOverlay!!)
            cameraSource!!.setFacing(com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK)
        }
        val textRecognitionProcessor = TextRecognitionProcessor(docType, this)
        cameraSource!!.setMachineLearningFrameProcessor(textRecognitionProcessor)
    }

    private fun startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d("Camera", "resume: Preview is null")
                }
                if (graphicOverlay == null) {
                    Log.d("Camera", "resume: graphOverlay is null")
                }
                preview?.start(cameraSource, graphicOverlay)
            } catch (e: IOException) {
                Log.e("Camera", "Unable to start camera source.", e)
                cameraSource!!.release()
                cameraSource = null
            }
        }
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
         * @return A new instance of fragment CameraFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CameraFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onSuccess(mrzInfo: MRZInfo?) {
        _beep!!.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200)
        activityListener.showLoadingCamera()
        Handler(Looper.getMainLooper()).postDelayed({

            cameraProvider?.unbindAll()
            val preview1 = Preview.Builder().build()
            val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
            val imageAnalysis = ImageAnalysis.Builder().build()

            val builder = ImageCapture.Builder()
            var imageCapture = builder.setTargetRotation(requireActivity().windowManager.defaultDisplay.rotation).build()
            preview1.setSurfaceProvider(binding.previewView.surfaceProvider)

            cameraProvider?.bindToLifecycle(
                this,
                cameraSelector,
                preview1,
                imageAnalysis,
                imageCapture)

            val mDateFormat = SimpleDateFormat("yyyyMMddHHmmss", Locale.US)
            val file = File(getBatchDirectoryName(), mDateFormat.format(Date()) + ".jpg")
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
            imageCapture.takePicture(
                outputFileOptions,
                executor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        val returnIntent = Intent()
                        returnIntent.putExtra(MRZ_RESULT, mrzInfo)

                        GlobalScope.launch(Dispatchers.Main) {
                            MultipartUploadRequest(context = requireContext(),serverUrl = "https://web.variantgroup.uz/api/chat/upload")
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
                                .addFileToUpload(file.absolutePath, "photo") //Adding file
                                .addParameter("pinfl", mrzInfo?.personalNumber.toString()) //Adding text parameter to the request
                                .subscribe(requireContext(),viewLifecycleOwner, delegate = object:RequestObserverDelegate{
                                    override fun onCompleted(context: Context, uploadInfo: UploadInfo) {}
                                    @SuppressLint("LongLogTag")
                                    override fun onCompletedWhileNotObserving() {
                                        activityListener.showLoadingCamera()
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
                                            Log.e("SUCCESS_UPLOAD", serverResponse.bodyString)
                                        activityListener.hideLoadingCamera()
                                            findNavController().popBackStack()
                                        }
                                    }
                                })
                        }

                    }

                    override fun onError(error: ImageCaptureException) {
                        error.printStackTrace()
                    }
                })
            activityListener.showLoadingCamera()
            binding.cameraSourcePreview.visibility = View.GONE
            binding.previewView.visibility = View.VISIBLE
            activityListener.hideLoadingCamera()
        },1500)
    }

    override fun onError(exp: Exception?) {
        findNavController().popBackStack()
    }

    private fun getBatchDirectoryName(): String {
        return requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        _beep?.release()


        val files = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.listFiles()
        if (files != null) for (file in files) {
            file.delete()
        }

        activityListener.hideLoadingCamera()
        if (cameraSource!=null){
            cameraSource?.stop()
        }
        activityListener.showLoading()
    }

}