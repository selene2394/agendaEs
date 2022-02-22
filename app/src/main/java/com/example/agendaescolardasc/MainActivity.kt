package com.example.agendaescolardasc

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextClock
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val txt_hora = findViewById<TextView>(R.id.txt_hora)
        txt_hora.setOnClickListener {
            val reloj = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hora, minuto ->
                reloj.set(Calendar.HOUR_OF_DAY, hora)
                reloj.set(Calendar.MINUTE, minuto)
                txt_hora.setText(SimpleDateFormat("HH:mm").format(reloj.time))
            }
            TimePickerDialog(this, timeSetListener, reloj.get(Calendar.HOUR_OF_DAY), reloj.get(Calendar.MINUTE),
                true).show()
        }

        val cal = Calendar.getInstance()
        val anio = cal.get(Calendar.YEAR)
        val mes = cal.get(Calendar.MONTH)
        val dia = cal.get(Calendar.DAY_OF_MONTH)

        img_calendario.setOnClickListener {
            val cali = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { datePicker, year, monthofYear, dayofMonth  ->
                lbl_fecha.text = year.toString() + "/" + (monthofYear + 1).toString() + "/" + dayofMonth.toString()
                lbl_fecha.visibility = View.VISIBLE
            }, anio, mes, dia)
            cali.show()
        }

        btn_guardar.setOnClickListener {
            guardar_datos()
        }

    }

    fun guardar_datos(){
        val url = "http://192.168.100.46/androidApi/guardar_items.php"

        val datos = HashMap<String, Any>()
        datos["hora"] = txt_hora.text.toString()
        datos["pendiente"] = txt_pendiente.text.toString()
        datos["fecha"] = (SimpleDateFormat("y/MM/dd").parse(lbl_fecha.text.toString())).toString()

        val datos_enviar = JSONObject(datos as Map<*, *>)

        val solicitud = JsonObjectRequest(Request.Method.POST, url, datos_enviar,
            { response ->
                try {
                    val error_serv = response.getInt("error")

                    if (error_serv == 0) {
                        Toast.makeText(this, "Éxito. ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error. ${response.getString("mensaje")}", Toast.LENGTH_SHORT).show()
                    }

                } catch (e: Exception) {
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()

                }
            }, {
                Toast.makeText(this, "Error.$it", Toast.LENGTH_SHORT).show()
            })

        VolleySingleton.getInstance(this).addToRequestQueue(solicitud)

    }

}