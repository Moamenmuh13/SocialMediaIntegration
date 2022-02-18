package mundroid.apps.socialmediaintegration

import android.app.AlertDialog
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.facebook.AccessToken
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import mundroid.apps.socialmediaintegration.databinding.ActivityProfileBinding
import org.json.JSONException


class ProfileActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityProfileBinding
    private var userName: String? = null
    private var email: String? = null
    private var gender: String? = null
    private var birthday: String? = null
    private var profileImg: String? = null

    private lateinit var googleSignInOptions: GoogleSignInOptions
    private lateinit var googleSignInClient: GoogleSignInClient
    private var accessToken: AccessToken? = null


    val TAG = "FACEBOOK_DATA"


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        initGoogle()
        initViews()
        getDataFromFacebook()
        getDataFromGoogle()
    }

    private fun initViews() {
        binding.signOutBtn.setOnClickListener(this)
        binding.progressBar.isClickable = false
    }

    private fun initGoogle() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        accessToken = AccessToken.getCurrentAccessToken();


    }

    private fun getDataFromGoogle() {
        val googleSignInAccount =
            GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount != null) {
            userName = googleSignInAccount.displayName
            email = googleSignInAccount.email
            profileImg = googleSignInAccount.photoUrl.toString()

            setData()
        }
    }


    private fun getDataFromFacebook() {
        binding.progressBar.visibility = View.VISIBLE
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`, _ ->
            try {
                email = `object`!!.getString("email")
                userName = `object`.getString("name")
                gender = `object`.getString("gender")
                birthday = `object`.getString("birthday")
                profileImg = `object`.getJSONObject("picture").getJSONObject("data")
                    .getString("url")

                setData()
            } catch (e: JSONException) {
                Toast.makeText(
                    this,
                    "Error: " + e.message,
                    Toast.LENGTH_SHORT
                ).show()
                Log.d(TAG, "onCompleted: Error ( " + e.message + ") ")
            }
        }
        request.parameters = setParameters()
        request.executeAsync()
        binding.progressBar.visibility = View.INVISIBLE
    }

    private fun setParameters(): Bundle {
        val parameters = Bundle()
        parameters.putString("fields", "email,name,gender,birthday,picture.type(large)")
        return parameters
    }


    private fun setData() {
        binding.email.text = email
        binding.name.text = userName
        binding.gender.text = gender
        binding.birthday.text = birthday
        Glide.with(this).load(profileImg).into(binding.image)
    }

    private fun showDialog() {
        val builder1 = AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to Logout?")
        builder1.setCancelable(true)

        builder1.setPositiveButton(
            "Yes"
        ) { _, _ -> logout() }

        builder1.setNegativeButton(
            "No"
        ) { dialog, _ -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener {
        backToLoginActivity()
        LoginManager.getInstance().logOut()
    }
    }
    override fun onClick(v: View?) {
        when (v) {
            binding.signOutBtn -> {
                showDialog()
            }
        }
    }
    private fun backToLoginActivity(){
        startActivity(Intent(this, LoginActivity::class.java))
    }
}