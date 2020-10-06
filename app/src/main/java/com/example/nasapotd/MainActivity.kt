package com.example.nasapotd

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import coil.api.load
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import retrofit2.http.GET
import retrofit2.http.Query

class MainActivity : AppCompatActivity() {
    private val retrofitImpl: RetrofitImpl = RetrofitImpl()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendServerRequest()
    }

    private fun sendServerRequest(){
        retrofitImpl.getRetrofit().getPictureOfTheDay("DEMO_KEY").enqueue(object :
            Callback<DataModel>{

            override fun onResponse(call: Call<DataModel>, response: Response<DataModel>) {
                if (response.isSuccessful && response.body() != null ){
                    renderData(response.body(), null)
                }else{
                    renderData(null, Throwable("Empty server answer"))
                }
            }

            override fun onFailure(call: Call<DataModel>, t: Throwable) {
                renderData(null, t)
            }
        })
    }

    private fun renderData(dataModel: DataModel?, error: Throwable?){
        if (dataModel==null||error!= null){
            Toast.makeText(this, error!!.message, Toast.LENGTH_LONG).show()
        }else{
            val url:String? = dataModel.url
            if(url.isNullOrEmpty()){

            }else {
                image_view.load(url)
            }

            val explanation: String? =dataModel.explanation
            if (explanation.isNullOrEmpty()){

            }else{
                text_view.text = explanation
            }
        }
    }
}


data class DataModel(
    val explanation: String?,
    val url: String?
)

interface PictureOfTheDayAPI{
    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String): Call<DataModel>
}

class RetrofitImpl{
    fun getRetrofit(): PictureOfTheDayAPI{
        val podRetrofit:Retrofit = Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().setLenient().create()
                )
            )
            .build()
        return podRetrofit.create(PictureOfTheDayAPI::class.java)
    }
}