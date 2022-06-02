package id.piusanggoro.cuacahariini

import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import id.piusanggoro.cuacahariini.databinding.ActivityMainBinding
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    val CITY: String = "bantul,id"
    val API: String = "--API-Key-dari-openweathermap--"
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        weatherTask().execute()
    }

    inner class weatherTask() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            mainBinding.loader.visibility = View.VISIBLE
            mainBinding.txtError.visibility = View.GONE
        }

        override fun doInBackground(vararg p0: String?): String? {
            var response: String?
            try {
                response =
                    URL("https://api.openweathermap.org/data/2.5/weather?q=$CITY&units=metric&appid=$API")
                        .readText(Charsets.UTF_8)
            } catch (e: Exception) {
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                // proses awal untuk parsing
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val address = jsonObj.getString("name") + ", " + sys.getString("country")
                val updatedAt: Long = jsonObj.getLong("dt")
                val updatedAtText =
                    "Updated at: " + SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.ENGLISH).format(
                        Date(updatedAt * 1000)
                    )

                // proses untuk json parsing
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val weatherDescription = weather.getString("description")
                val temp = main.getString("temp") + "°C"
                val wind = jsonObj.getJSONObject("wind")
                val windSpeed = wind.getString("speed")
                val pressure = main.getString("pressure")
                val humidity = main.getString("humidity")

                // menampilkan data hasil parsing ke view/tampilan
                mainBinding.txtAddress.setText(address)
                mainBinding.txtUpdatedAt.setText(updatedAtText)
                mainBinding.txtStatus.setText(weatherDescription.capitalize())
                mainBinding.txtTemp.setText(temp)
                mainBinding.txtWind.setText(windSpeed +"kmph")
                mainBinding.txtPressure.setText(pressure +"mmHg")
                mainBinding.txtHumidity.setText(humidity +"%")

                mainBinding.loader.visibility = View.GONE
            } catch (e: Exception) {
                mainBinding.loader.visibility = View.GONE
                mainBinding.txtError.visibility = View.VISIBLE
            }
        }
    }
}