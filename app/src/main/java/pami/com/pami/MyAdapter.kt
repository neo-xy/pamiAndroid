package pami.com.pami

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by Pawel on 14/02/2018.
 */
class MyAdapter() : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {

    var context: Context? = null;

    constructor(context: Context) : this() {
        this.context = context;
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyAdapter.MyViewHolder {
        var t = TextView(context)
        var my = MyViewHolder(t);
        return my;
    }

    override fun getItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindViewHolder(holder: MyAdapter.MyViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}