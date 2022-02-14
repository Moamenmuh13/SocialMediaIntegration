package mundroid.apps.socialmediaintegration

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import mundroid.apps.socialmediaintegration.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager
    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private val GOOGLE_REQUEST_CODE = 1000

    val KEY = "FACEBOOK_LOGIN"

    private val EMAIL = "email"
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)

        checkCurrentUser()

        callbackManager = CallbackManager.Factory.create()
        loginCallback()

        makeInstance()
        initViews()

    }

    private fun makeInstance() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun initViews() {
        binding.facebookBtn.setOnClickListener(this)
        binding.googleBtn.setOnClickListener(this)
        binding.loginBtn.isEnabled = false

//        binding.facebookBtn.setReadPermissions(listOf(EMAIL))
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.facebookBtn -> {
                LoginManager.getInstance()
                    .logInWithReadPermissions(this, listOf("public_profile"));
            }
            binding.googleBtn -> {
                signInWithGoogle()
            }
        }
    }

    private fun loginCallback() {
        val loginManager = LoginManager.getInstance()
            .registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    startingProfileActivity()
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    Log.d(KEY, "onError: " + error.message)
                }


            }
            )
    }

    private fun signInWithGoogle() {
        val intent = googleSignInClient.signInIntent
        startActivityForResult(intent, GOOGLE_REQUEST_CODE)
    }


    private fun checkCurrentUser() {

        val accessToken = AccessToken.getCurrentAccessToken()


        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null || accessToken != null && !accessToken.isExpired) {
            startingProfileActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_REQUEST_CODE) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                task.getResult(ApiException::class.java)
                startingProfileActivity()
            } catch (e: ApiException) {
                Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show();

            }
        }

    }

    private fun startingProfileActivity() {
        startActivity(Intent(this, ProfileActivity::class.java))
        finish()
    }
}