package mundroid.apps.socialmediaintegration

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.*
import com.facebook.appevents.AppEventsLogger
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.ProfilePictureView
import mundroid.apps.socialmediaintegration.databinding.ActivityLoginBinding
import org.json.JSONException
import org.json.JSONObject
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var callbackManager: CallbackManager
    private val EMAIL = "email"
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        FacebookSdk.fullyInitialize()
        AppEventsLogger.activateApp(application)

        callbackManager = CallbackManager.Factory.create()

        initViews()

    }


    private fun initViews() {
        binding.loginButton.setOnClickListener(this)
//        binding.facebookBtn.setReadPermissions(listOf(EMAIL))
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.loginButton -> {
                loginCallback()
            }
        }
    }

    private fun loginCallback() {
        binding.loginButton.registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {

                override fun onSuccess(result: LoginResult?) {
                    val request: GraphRequest = GraphRequest.newMeRequest(
                        result?.accessToken
                    ) { obj, response -> setProfileView(obj) }

                    val bundle = Bundle()
                    bundle.putString("fields", "id,name,email,gender,birthday")
                    request.parameters = bundle
                    request.executeAsync()
                }

                override fun onCancel() {
                    TODO("Not yet implemented")
                }

                override fun onError(error: FacebookException) {
                    Toast.makeText(
                        this@LoginActivity,
                        "error to Login Facebook",
                        Toast.LENGTH_SHORT
                    )
                        .show();
                }
            })
    }

    private fun setProfileView(obj: JSONObject?) {
        try {
            binding.email.text = obj?.getString("email")
            binding.name.text = obj?.getString("name")
            binding.gender.text = obj?.getString("gender")
            binding.birthday.text = obj?.getString("birthday")

            binding.profilePicture.presetSize = ProfilePictureView.NORMAL
            binding.profilePicture.profileId = obj?.getString("id")

            binding.layoutInfo.visibility = View.VISIBLE
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}