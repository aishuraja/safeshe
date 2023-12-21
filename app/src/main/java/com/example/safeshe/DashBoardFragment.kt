package com.example.safeshe

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.findNavController
import androidx.room.Room
import com.example.safeshe.database.Guardian
import com.example.safeshe.database.GuardianDatabase
import com.example.safeshe.databinding.FragmentDashBoardBinding
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashBoardFragment<LoginViewModel : ViewModel> : Fragment() {

    private lateinit var binding: FragmentDashBoardBinding

    private var viewModelJob = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + viewModelJob)

    companion object {
        const val TAG = "DashBoardFragment"
        const val SIGN_IN_RESULT_CODE = 1001
        private const val PERMISSION_SEND_SMS = 2
    }

    private val viewModel by viewModels<com.example.safeshe.auth.LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_dash_board, container, false
        )

//        getLocation()

        binding.guardianButton.setOnClickListener { view: View ->
            view.findNavController()
                .navigate((DashBoardFragmentDirections.actionDashBoardFragmentToGuardianInfo()))
        }

        binding.emerButton.setOnClickListener {
//            getLocation()
            if (ActivityCompat.checkSelfPermission(activity!!, Manifest.permission.SEND_SMS)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity!!, arrayOf(Manifest.permission.SEND_SMS), PERMISSION_SEND_SMS)
                } else {
                    uiScope.launch {
                        withContext(Dispatchers.IO) {
                            emergencyFun()
                        }
                    }
                }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeAuthenticationState()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGN_IN_RESULT_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (resultCode == Activity.RESULT_OK) {
                //User successfully signed in
            } else {
                // Sign in failed. If response is null, the user canceled the
                // sign-in flow using the back button. Otherwise, check
                // the error code and handle the error.
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun observeAuthenticationState() {
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState) {
                com.example.safeshe.auth.LoginViewModel.AuthenticationState.AUTHENTICATED -> {
                    binding.textView.text =
                        ("Welcome, " + FirebaseAuth.getInstance().currentUser?.displayName)
                }
                else -> {
                    launchSignInFlow()
                }
            }
        })
    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
            //
        )

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(
                providers
            ).setTheme(R.style.LoginTheme_NoActionBar)
                .setLogo(R.drawable.women)
                .build(), DashBoardFragment.SIGN_IN_RESULT_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == PERMISSION_SEND_SMS){
            uiScope.launch {
                withContext(Dispatchers.IO) {
                    emergencyFun()
                }
            }
        }
    }


    private fun emergencyFun() {
        val db =
            Room.databaseBuilder(activity!!, GuardianDatabase::class.java, "GuardianDB").build()
        val emailList: List<Guardian> = db.guardianDatabaseDao().getEmail()

        var maillist: String = ""
        val subject: String = "From SafeShe App"
        val text: String = resources.getString(R.string.problem)
        emailList.forEach(){
            maillist = emailList.joinToString(separator = ",") { it ->"${it.guardianEmail}"}
        }
        emailList.forEach() {
            val smsManager = SmsManager.getDefault() as SmsManager
            smsManager.sendTextMessage("${it.guardianPhoneNo}", null, "$text", null, null)
        }

        val shareIntent = Intent(Intent.ACTION_SEND)

        shareIntent.setType("message/rfc822")
            .putExtra(Intent.EXTRA_EMAIL, arrayOf(maillist))
            .putExtra(Intent.EXTRA_SUBJECT, subject)
            .putExtra(Intent.EXTRA_TEXT, text)
        startActivity(Intent.createChooser(shareIntent, "Send mail using.."))
    }

}
