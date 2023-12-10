package com.onurerdem.firebasefcm.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.onurerdem.firebasefcm.R
import com.onurerdem.firebasefcm.SignInActivity
import com.onurerdem.firebasefcm.databinding.ActivityMainBinding
import com.onurerdem.firebasefcm.model.NotificationData
import com.onurerdem.firebasefcm.model.PushNotification
import com.onurerdem.firebasefcm.service.RetrofitObject
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    //val topic = "/topics/generalInfo"
    private lateinit var db : FirebaseFirestore
    var token = ""
    //val dataMap = hashMapOf<String, String>()
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var documentReference: DocumentReference
    private lateinit var collectionReference: CollectionReference
    //var token2 = ""
    //var token3 = ""
    //var list = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //FirebaseMessaging.getInstance().subscribeToTopic(topic)

        db = FirebaseFirestore.getInstance()

        documentReference = db.collection("Users").document()
        collectionReference = db.collection("Users")


        /*val token = FirebaseMessaging.getInstance().token.addOnSuccessListener {
            //println("token: ${it}")

            //token = it

            //val dataMap = hashMapOf<String, String>()
            dataMap.put("token",it)
            dataMap.put("useremail", auth.currentUser?.email.toString())

            db.collection("Users").add(dataMap).addOnSuccessListener {
                //do some stuff
            }

            /*db.collection("Users").get().addOnSuccessListener { result ->
                for (document in result){
                    println("${document.id}" + "${document.data.get("token")}")
                }
            }*/

            //println("useremail: " + dataMap.get("token"))

        }*/

        auth = FirebaseAuth.getInstance()

        /*token2 = getToken("fb.onurerdem@gmail.com")
        token3 = getToken("fb-onurerdem@hotmail.com")
        println("token2: " + token2)
        println("token3: " + token3)*/

    }

    fun getToken(email: String): String{
        collectionReference.whereEqualTo("useremail", email)
            .addSnapshotListener { value, e ->
                //list.clear()
                if (e != null) {
                    println("hata: " + e)
                } else {
                    if (value != null) {
                        if (!value.isEmpty) {
                            val list = ArrayList<String>()
                            list.clear()
                            val documents = value.documents
                            for (doc in documents) {
                                doc.getString("token")?.let {
                                    list.add(it)
                                }
                            }
                            token = list.get(0)
                            //println("token1: " + token)
                            }
                        }
                    }
                }
        //println("token4: " + token)
        return token
    }

    fun send(view : View) {
        val title = titleText.text.toString()
        val message = messageText.text.toString()
        val email = emailText.text.toString()

        if (title.isNotEmpty() && message.isNotEmpty()) {
            val data = NotificationData(title,message)
            //val notification = PushNotification(data,"dnbNWZy4SoW02xmkvFBkSY:APA91bERvdZyGmvJsdxrQ3eyOHnc6BFHuVTx2a1NfVqmD_DGBLokphXcgyXu2uM8LrkZMukcl0mPBw7dTa_AOoW8W10Te4v5t2qJo__xArJHwkneZd_HLlTYhD3Ug27XTLDCFztyoHUz")
            //val notification = PushNotification(data, token)
            val notification = PushNotification(data, getToken(email))
            /*println("token: " + token)
            println("token5: " + getToken(email))*/
            sendNotification(notification)
        }

    }

    private fun sendNotification(pushNotification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try {

            val response = RetrofitObject.api.postNotification(pushNotification)
            if (response.isSuccessful) {
                println(response)
            } else {
                println(response.errorBody())
            }

        } catch (e : Exception) {
            e.printStackTrace()
        }
    }

    fun exit(view: View) {
        auth.signOut()
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

}