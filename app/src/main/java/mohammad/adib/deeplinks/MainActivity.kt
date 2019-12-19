package mohammad.adib.deeplinks

import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var adapter: DeeplinkAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab.setOnClickListener {
            val input = EditText(this)
            input.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
            input.hint = "app://"
            val padding = resources.getDimensionPixelSize(R.dimen.padding_med)
            input.setPadding(padding, padding, padding, padding)
            val dialog = AlertDialog.Builder(this)
                    .setTitle("Add Deeplink")
                    .setView(input)
                    .setPositiveButton("Save") { d, i ->
                        adapter.addDeeplink(input.text.toString())
                    }
                    .create()
            dialog.show()
        }
        val manager = LinearLayoutManager(this)
        manager.orientation = RecyclerView.VERTICAL
        list.layoutManager = manager
        adapter = DeeplinkAdapter(this, empty)
        list.adapter = adapter
    }
}