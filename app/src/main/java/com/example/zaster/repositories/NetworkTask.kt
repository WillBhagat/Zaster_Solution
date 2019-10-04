package com.example.zaster.repositories

import android.content.res.Resources
import android.provider.Settings.Global.getString
import android.widget.Toast
import com.example.zaster.MainActivity
import com.example.zaster.R
import com.example.zaster.models.Internal
import com.example.zaster.models.Response
import com.example.zaster.models.ResponseCode
import com.example.zaster.models.Valid
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

class NetworkTask {


    fun runTask(password : String, email : String) : Response
    {
        //the requirement was for the URL to be from application configuration but I have not heard of this before and could not find this in my research
        val request = Request.Builder()
                .url("www.google.com")

        val mediaType = "application/x-www-form-urlencoded".toMediaType()
        val body = Resources.getSystem().getString(R.string.login_info, password, email).toRequestBody(mediaType)
        request.post(body)
        val client = OkHttpClient.Builder().build()
        //return this as ResponseCode
        val execute = client.newCall(request.build()).execute()

        return handleResponseCode(execute.code, execute.body)

    }

    //using when as an expression body to simplify the code
    //this causes duplicated code as values cannot be instantiated in the body of the method
    private fun handleResponseCode(code : Int, body : ResponseBody?) : Response = when(code) {
        200 ->
        {
            val gson = GsonBuilder().create()
            val message = gson.fromJson(body?.string(), Valid::class.java)
            Response(message.token, ResponseCode.VALID)
        }
        401 ->
        {
            Response("", ResponseCode.INVALID)
        }
        500 ->
        {
            val gson = GsonBuilder().create()
            val message = gson.fromJson(body?.string(), Internal::class.java)
            Response(message.error, ResponseCode.INTERNAL)
        }
        else -> Response("Unexpected error code", ResponseCode.INTERNAL)
    }
}