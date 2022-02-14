package mundroid.apps.socialmediaintegration

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
        initBtns()
        getDataFromFacebook()
        getDataFromGoogle()
    }

    private fun initBtns() {
        binding.signOutBtn.setOnClickListener(this)
    }

    private fun initGoogle() {
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        accessToken = AccessToken.getCurrentAccessToken();


    }

    private fun getDataFromGoogle() {
        var googleSignInAccount =
            GoogleSignIn.getLastSignedInAccount(this)
        if (googleSignInAccount != null) {
            userName = googleSignInAccount.displayName
            email = googleSignInAccount.email
            profileImg = googleSignInAccount.photoUrl.toString()

            setData()
        }
    }


    private fun getDataFromFacebook() {
        val request = GraphRequest.newMeRequest(
            accessToken
        ) { `object`, response ->
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
        val parameters = Bundle()
        parameters.putString("fields", "email,name,gender,birthday,picture.type(large)")
        request.parameters = parameters
        request.executeAsync()
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
        ) { dialog, id -> logout() }

        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }

        val alert11 = builder1.create()
        alert11.show()
    }

    private fun logout() {
        googleSignInClient.signOut().addOnCompleteListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        LoginManager.getInstance().logOut()
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.signOutBtn -> {
                showDialog()
            }
        }
    }
}