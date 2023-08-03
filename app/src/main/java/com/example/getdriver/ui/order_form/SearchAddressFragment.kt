package com.example.getdriver.ui.order_form



import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import com.example.getdriver.R
import com.example.getdriver.utils.AndroidBus
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.BoundingBox
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.search.*
import com.yandex.mapkit.search.SuggestSession.SuggestListener
import com.yandex.runtime.Error
import com.yandex.runtime.network.NetworkError
import com.yandex.runtime.network.RemoteError
import io.reactivex.rxjava3.observers.DisposableObserver
import org.json.JSONObject


class SearchAddressFragment : Fragment(), SuggestListener {

    private val RESULT_NUMBER_LIMIT = 5

    private var searchManager: SearchManager? = null
    private var suggestSession: SuggestSession? = null
    private var suggestResultView: ListView? = null
    private var resultAdapter: ArrayAdapter<*>? = null
    private var suggestResult: ArrayList<String>? = null

    private val CENTER = Point(62.0338900, 129.7330600)
    private val BOX_SIZE = 0.2
    private val BOUNDING_BOX = BoundingBox(
        Point(CENTER.latitude - BOX_SIZE, CENTER.longitude - BOX_SIZE),
        Point(CENTER.latitude + BOX_SIZE, CENTER.longitude + BOX_SIZE)
    )
    private val SEARCH_OPTIONS = SuggestOptions().setSuggestTypes(
        SuggestType.GEO.value or
                SuggestType.BIZ.value or
                SuggestType.TRANSIT.value

    )

    private val city = "Якутск"
    private var is_one_addr = false
    private var is_one_click = 0
    private var queryEdit : EditText? = null

    private var fromStreet = 0


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        SearchFactory.initialize(activity)

        super.onCreate(savedInstanceState)

        searchManager = SearchFactory.getInstance().createSearchManager(SearchManagerType.COMBINED)
        suggestSession = searchManager!!.createSuggestSession()

        val myFragmentView: View = inflater.inflate(
            R.layout.suggest,
            container, false
        )

        queryEdit = myFragmentView.findViewById<View>(R.id.suggest_query) as EditText
        //queryEdit!!.requestFocus()


        suggestResultView = myFragmentView.findViewById<View>(R.id.suggest_result) as ListView
        suggestResult = ArrayList()

        resultAdapter = ArrayAdapter<Any?>(
            requireActivity(),
            android.R.layout.simple_list_item_2,
            android.R.id.text1,
            suggestResult!! as List<Any?>
        )

        suggestResultView!!.setAdapter(resultAdapter)


        queryEdit!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                    requestSuggest(city + " " + editable.toString())
            }
        })

        suggestResultView!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(
                parent: AdapterView<*>?, itemClicked: View, position: Int,
                id: Long
            ) {

                val textView1: TextView = itemClicked!!.findViewById(android.R.id.text1)
                val strText = textView1.text.toString() // получаем текст нажатого элемента

                val delim = ","
                val list = strText.split(delim)
                is_one_click = 1

                try {
                    if(list.size == 1) {
                        queryEdit!!.setText(strText)
                        queryEdit!!.setSelection(strText.length);
                        is_one_addr = false
                    }
                    else{
                        if(!strText.equals("")) {
                            resultAdapter!!.notifyDataSetChanged()
                            //queryEdit!!.setText("")

                            val getStreet = JSONObject()
                            if(fromStreet == 0) {
                                getStreet.put("getStreet1", strText)
                            }
                            else{
                                getStreet.put("getStreet2", strText)
                            }

                            AndroidBus.behaviorSubject.onNext(getStreet)

                        }
                    }

                }catch (e : Exception){

                }

            }
        })


        AndroidBus.behaviorSubject.subscribeWith(object : DisposableObserver<JSONObject>() {

            override fun onNext(value: JSONObject) {

                try{
                    var open_window = value.getInt("openWindow")
                    var street_name1 = value.getString("streetName")
                    //var street_name2 = value.getString("streetName2")


                    if(open_window == 0) {
                        fromStreet = 0
                        showSoftKeyboard(queryEdit!!)
                    }
                    else{
                        fromStreet = 1
                    }

                    if(!street_name1.equals("")){
                        queryEdit!!.setText(street_name1)
                        queryEdit!!.setSelection(street_name1.length)
                    }




                }catch (e : Exception){

                }

            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {

            }

        })


        return myFragmentView
    }

    fun showSoftKeyboard(view: View) {
        val inputMethodManager =
            requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        inputMethodManager.showSoftInput(view, 0)
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onResume() {


        super.onResume()
    }


    override fun onResponse(suggest: List<SuggestItem>) {
        suggestResult?.clear()
        for (i in 0 until Math.min(RESULT_NUMBER_LIMIT, suggest.size)) {

            val delim = ","
            val list = suggest[i].displayText.toString().split(delim)

            try{

                if(list.size == 1) {
                    suggestResult?.add(list[0])
                }
                else if(list.size == 2){
                    suggestResult?.add(list[0]+","+list[1])
                }
                else if(list.size == 3){
                    suggestResult?.add(list[2])
                }
                else if(list.size == 4){
                    suggestResult?.add(list[2]+","+list[3])
                }
                else if(list.size == 5){
                    suggestResult?.add(list[2]+","+list[3]+list[4])
                }

                //suggestResult?.add(suggest[i].displayText.toString())

            }catch(e : java.lang.IndexOutOfBoundsException){

            }
        }
        resultAdapter!!.notifyDataSetChanged()
        suggestResultView!!.visibility = View.VISIBLE
    }

    override fun onError(error: Error) {
        var errorMessage = getString(R.string.unknown_error_message)
        if (error is RemoteError) {
            errorMessage = getString(R.string.remote_error_message)
        } else if (error is NetworkError) {
            errorMessage = getString(R.string.network_error_message)
        }
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
    }

    private fun requestSuggest(query: String) {
        //suggestResultView!!.visibility = View.INVISIBLE
        suggestSession!!.suggest(query, BOUNDING_BOX, SEARCH_OPTIONS, this)
    }


}

