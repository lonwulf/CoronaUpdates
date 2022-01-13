package com.example.coronaupdate.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.coronaupdate.R
import com.example.coronaupdate.retrofit.ApiClient
import com.example.coronaupdate.retrofit.ApiInterface
import com.example.coronaupdate.retrofit.structures.CoronaResponseDTO

class MainActivity : AppCompatActivity() {
    private lateinit var tvReported: TextView;
    private lateinit var tvTitle: TextView;
    private lateinit var tvRecovered: TextView;
    private lateinit var tvDeaths: TextView;
    private lateinit var searchView: SearchView;
    private lateinit var progress: ProgressBar;
    private lateinit var selectedCountry: String;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    private fun initViews() {
        tvReported = findViewById(R.id.tvReported)
        tvTitle = findViewById(R.id.tvRHeaders)
        tvDeaths = findViewById(R.id.tvDeaths)
        tvRecovered = findViewById(R.id.tvRecovered)
        searchView = findViewById(R.id.searchView)
        progress = findViewById(R.id.progressBar)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                startSearchAction(query)
                return false
            }

        })
    }

    private fun startSearchAction(country: String) {
        progress.visibility = View.VISIBLE;

        //get retrofit client instance
        val client: ApiClient = ApiClient()

        //Create Service
        val apiService = client.getClient()!!.create(ApiInterface::class.java)

        // Create Call
        val call: retrofit2.Call<CoronaResponseDTO> = apiService.getCoronaUpdates(country.trim())

        // Observe Call
        call.enqueue(object : retrofit2.Callback<CoronaResponseDTO> {
            override fun onFailure(call: retrofit2.Call<CoronaResponseDTO>?, t: Throwable?) {
                progress.visibility = View.INVISIBLE;
                val errorMsg = t!!.localizedMessage;
                showError(errorMsg)
            }

            override fun onResponse(
                call: retrofit2.Call<CoronaResponseDTO>?,
                responseDTO: retrofit2.Response<CoronaResponseDTO>?
            ) {
                progress.visibility = View.INVISIBLE;
                if (responseDTO!!.isSuccessful) {
                    selectedCountry = country;
                    setResponseResult((responseDTO.body()!!))


                } else {
                    showError("Error Finding Updates. Please Try Again Later")
                }
            }

        })

    }

    private fun setResponseResult(coronaDataResponseDTO: CoronaResponseDTO) =
        if (coronaDataResponseDTO.message!!.contains("Country not found")) {
            showError("Country not found. Please Ensure the first letter is in Capital")
        } else {
            if (coronaDataResponseDTO.data!!.covidStatsList!!.size > 1) {
                showError("Country Has Updates Per Province. We are currently able to show for whole country")
            } else {
                setCovidDataOnViews(coronaDataResponseDTO)
            }
        }

    private fun showError(message: String?) {
        val error: String;
        error = if (message!!.contains("Unable to resolve host")) {
            "Please Ensure Your Data is On"
        } else {
            message;
        }
        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    private fun setCovidDataOnViews(result: CoronaResponseDTO) {
        tvReported.visibility = View.VISIBLE
        tvTitle.text = "Confirmed Cases in $selectedCountry"
        tvReported.text = "${result.data!!.covidStatsList!![0].confirmed}"
        tvRecovered.text =
            "Recovered : ${result.data.covidStatsList!![0].recovered}"
        tvDeaths.text = "Deaths : ${result.data.covidStatsList!![0].deaths}"
    }
}
