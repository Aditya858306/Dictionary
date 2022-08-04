package com.example.mydictionary

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.search_bar)
        onKeyboardSearch(editText)
    }

    fun search(view: View) {
        searchMeaning()
    }

    private fun searchMeaning() {
        val textView=findViewById<TextView>(R.id.definitions)
        val progressBar=findViewById<ProgressBar>(R.id.progressbar)
        textView.text=""
        progressBar.visibility=View.VISIBLE
        val editText = findViewById<EditText>(R.id.search_bar)
        val word=editText.text.toString()
        val queue = Volley.newRequestQueue(this)
        val url = "https://api.dictionaryapi.dev/api/v2/entries/en/$word"
//        Toast.makeText(this,"$url",Toast.LENGTH_SHORT).show()
        val definitionList=ArrayList<String>()
//        // Request a string response from the provided URL.
        val jsonRequest = JsonArrayRequest(
            Request.Method.GET, url,null,
            Response.Listener { response ->
                for (i in 0 until response.length())
                {
                    val definitionList=ArrayList<String>()
                    val results = response.getJSONObject(i)
                    val meanings = results.getJSONArray("meanings")
                    for (j in 0 until meanings.length())
                    {
                        val meaning = meanings.getJSONObject(j)
                        val pos = meaning.getString("partOfSpeech")

                        textView.append("$pos \n")
                        val definitions = meaning.getJSONArray("definitions")
                        for (k in 0 until definitions.length())
                        {
                            val definition1 = definitions.getJSONObject(k)
                            val definition = definition1.getString("definition")
                            definitionList.add(definition)
                        }
                        for (l in 0 until definitionList.size)
                        {
                            textView.append("${l + 1} ${definitionList.get(l)} \n")
                        }
                        textView.append("\n")

                    }

                }
                progressBar.visibility = View.GONE
//                Toast.makeText(this,"$definitionList",Toast.LENGTH_LONG).show()
            },
            Response.ErrorListener {
                progressBar.visibility=View.GONE
                textView.text="Sorry, we have found no matched result. Please check if you have misspelled the word"
            })

        // Add the request to the RequestQueue.
        queue.add(jsonRequest)
    }

    fun cross(view: View) {
        val editText = findViewById<EditText>(R.id.search_bar)
        editText.setText("")
        val textView = findViewById<TextView>(R.id.definitions)
        textView.text = ""
    }

    private fun onKeyboardSearch(search: EditText){
        search.setOnEditorActionListener(TextView.OnEditorActionListener{ _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMeaning()
                return@OnEditorActionListener true
            }
            false
        })
    }
}
