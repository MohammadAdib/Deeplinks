package mohammad.adib.deeplinks

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import mohammad.adib.deeplinks.DeeplinkAdapter.DeeplinkViewHolder


class DeeplinkAdapter(private val context: Context, private val emptyView: View) : RecyclerView.Adapter<DeeplinkViewHolder>() {

    private val deeplinks = mutableListOf<String>()
    val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeeplinkViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_deeplink, parent, false)
        return DeeplinkViewHolder(v)
    }

    override fun onBindViewHolder(holder: DeeplinkViewHolder, position: Int) {
        val deeplink = deeplinks[position]
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(deeplink)

        val results = context.packageManager.queryIntentActivities(intent, 0)
        if (results.isNotEmpty()) {
            val info = results.get(0).activityInfo
            holder.appIcon.setImageDrawable(getActivityIcon(info.packageName, info.name))
        }

        holder.text.text = deeplink
        holder.remove.setOnClickListener {
            deeplinks.removeAt(holder.adapterPosition)
            notifyItemRemoved(itemCount)
            save()
        }
        holder.itemView.setOnClickListener {
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        emptyView.visibility = if (deeplinks.isEmpty()) View.VISIBLE else View.GONE
        return deeplinks.size
    }

    fun addDeeplink(deeplink: String) {
        deeplinks.add(deeplink)
        notifyItemInserted(itemCount)
        save()
    }

    inner class DeeplinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.text)
        var remove: ImageView = itemView.findViewById(R.id.remove)
        var appIcon: ImageView = itemView.findViewById(R.id.appIcon)
    }

    private fun save() {
        val data = StringBuilder()
        for (i in 0 until deeplinks.size) {
            data.append(deeplinks[i])
            if (i != itemCount - 1)
                data.append("\n")
        }
        preferences.edit().putString("data", data.toString()).apply()
    }

    fun getActivityIcon(packageName: String, activityName: String): Drawable {
        val packageManager = context.packageManager
        val intent = Intent()
        intent.component = ComponentName(packageName, activityName)
        val resolveInfo = packageManager.resolveActivity(intent, 0)
        return resolveInfo.loadIcon(packageManager)
    }

    init {
        val data = preferences.getString("data", "")
        data?.let {
            if (it.contains("\n")) {
                deeplinks.addAll(it.split("\n").toMutableList())
            } else if (it.isNotBlank()) {
                deeplinks.add(it)
            }
            true
        }
    }
}