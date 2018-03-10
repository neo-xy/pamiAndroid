package pami.com.pami


import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import pami.com.pami.R.drawable.ic_settings
import pami.com.pami.R.drawable.loginbg
import pami.com.pami.R.mipmap.ic_launcher_round


/**
 * A simple [Fragment] subclass.
 */
class PersonalInformationFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_personal_information, container, false);
     var  profileViews = view.findViewById<ImageView>(R.id.profile_img);


        Glide.with(this)
                .load(User.imgUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(profileViews)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }
}


