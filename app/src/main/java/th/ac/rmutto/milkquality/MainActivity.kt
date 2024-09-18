package th.ac.rmutto.milkquality

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    @SuppressLint("DefaultLocale")


    private lateinit var editpH: EditText
    private lateinit var editTemp: EditText
    private lateinit var spinTaste: Spinner
    private lateinit var spinOdor: Spinner
    private lateinit var spinFat: Spinner
    private lateinit var spinTurbidity: Spinner
    private lateinit var button: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //To run network operations on a main thread or as an synchronous task.
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        editpH = findViewById<EditText>(R.id.editpH)
        editTemp = findViewById<EditText>(R.id.editTemp)
        spinTaste = findViewById<Spinner>(R.id.spinTaste)
        spinOdor = findViewById<Spinner>(R.id.spinOdor)
        spinFat = findViewById<Spinner>(R.id.spinFat)
        spinTurbidity = findViewById<Spinner>(R.id.spinTurbidity)
        button = findViewById<Button>(R.id.button)


        val adapterTaste = ArrayAdapter.createFromResource(
            this,
            R.array.taste,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item
        )
        adapterTaste.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTaste.setAdapter(adapterTaste);

        val adapterOdor = ArrayAdapter.createFromResource(
            this,
            R.array.odor,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item
        )
        adapterOdor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinOdor.setAdapter(adapterOdor);

        val adapterFat = ArrayAdapter.createFromResource(
            this,
            R.array.fat,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item
        )
        adapterFat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinFat.setAdapter(adapterFat);

        val adapterTurbidity = ArrayAdapter.createFromResource(
            this,
            R.array.turbidity,  // ใส่ชื่อของ array ที่ต้องการใช้
            android.R.layout.simple_spinner_item
        )
        adapterTurbidity.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinTurbidity.setAdapter(adapterTurbidity);


        button.setOnClickListener {
            val pH = editpH.text.toString()
            val temp = editTemp.text.toString()
            val taste = spinTaste.selectedItemId.toString()
            val odor = spinOdor.selectedItemId.toString()
            val fat = spinFat.selectedItemId.toString()
            val turbidity = spinTurbidity.selectedItemId.toString()
            var message = ""
            val url: String = getString(R.string.root_url)

            if(pH.isEmpty()){
                Toast.makeText(this, "กรุณากรอกค่า pH", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if(temp.isEmpty()){
                Toast.makeText(this, "กรุณากรอกค่า อุณหภูมิ", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val okHttpClient = OkHttpClient()
            val formBody: RequestBody = FormBody.Builder()
                .add("pH", pH)
                .add("temprature", temp)
                .add("taste", taste)
                .add("odor", odor)
                .add("fat", fat)
                .add("turbidity", turbidity)
                .build()

            val request: Request = Request.Builder()
                .url(url)
                .post(formBody)
                .build()

            val response = okHttpClient.newCall(request).execute()

            if (response.isSuccessful) {
                val data = JSONObject(response.body!!.string())
                if (data.length() > 0) {
                    val target = data.getString("Milk Quality")

                    message = "MilK Grade is $target"
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("MilK Grade!!")
                    builder.setMessage(message)
                    builder.setNeutralButton("OK", clearText())
                    val alert = builder.create()
                    alert.show()

                }
            } else {
                Toast.makeText(applicationContext, "ไม่สามารถเชื่อต่อกับเซิร์ฟเวอร์ได้", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun clearText(): DialogInterface.OnClickListener? {
        return DialogInterface.OnClickListener { dialog, which ->
            editpH.text.clear()
            editTemp.text.clear()
            spinTaste.setSelection(0)
            spinOdor.setSelection(0)
            spinFat.setSelection(0)
            spinTurbidity.setSelection(0)
        }
    }


}