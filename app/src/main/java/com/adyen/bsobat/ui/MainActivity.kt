package com.adyen.bsobat

import android.Manifest
import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.databinding.DataBindingUtil.setContentView
import android.databinding.ViewDataBinding
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.adyen.bsobat.dto.Venue
import com.adyen.bsobat.dto.Venues
import com.adyen.bsobat.livedata.LocationLiveData
import com.bol.instantapp.viewmodel.MainActivityViewModel
import com.bol.instantapp.viewmodel.Resource


class MainActivity : LifecycleActivity(), Observer<Location?> {
    val MY_PERMISSIONS_REQUEST_LOCATION = 10
    lateinit var binding: ViewDataBinding;
    lateinit var seekBar: SeekBar;
    lateinit var list: RecyclerView;
    lateinit var viewModel: MainActivityViewModel;
    lateinit var locationLiveData: LocationLiveData;

    var lat: String? = null
    var lon: String? = null
    var radius: Int = 250

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView<ViewDataBinding>(this, R.layout.activity_main)
        seekBar = binding.root.findViewById(R.id.seekBar) as SeekBar;
        binding.setVariable(BR.radius, "Radius $radius m")
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, progress: Int, p2: Boolean) {
                radius = progress
                binding.setVariable(BR.radius, "Radius $radius m")
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekbar: SeekBar?) {
                updateList();
            }

        })
        list = binding.root.findViewById(R.id.list) as RecyclerView;
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        list.adapter = VenueAdapter(listOf())

        permissionGranted(false)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            init()
        }
    }

    fun permissionGranted(granted: Boolean) {
        binding.setVariable(BR.permission, granted)
        if (!granted) {
            binding.setVariable(BR.location, getString(R.string.permission_required))
        } else {
            binding.setVariable(BR.location, "")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    showMessage(R.string.locationPermissionError);
                    finish();
                }
                return
            }
        }
    }

    private fun init() {
        showMessage(R.string.locating)
        permissionGranted(true)
        locationLiveData = App.appComponent.provideLocationLiveData()

        viewModel = MainActivityViewModel.create(this);
        App.appComponent.inject(viewModel)

        viewModel.venuesResult.observe(this, Observer<Resource<Venues>> {
            it?.let {
                when (it.status) {
                    Resource.Status.SUCCESS -> {
                        if(it.data != null){
                            var adapter = list.adapter as VenueAdapter
                            adapter.items = it.data.items
                            adapter.notifyDataSetChanged()
                            list.recycledViewPool.clear();
                        } else {
                            showMessage(R.string.emptyList)
                        }
                    }
                    Resource.Status.ERROR -> {
                        showMessage(it.exception?.message);
                    }
                    Resource.Status.LOADING -> {

                    }
                }
            }
        })
        updateList()
        locationLiveData.observe(this, this)
    }

    private fun updateList() {
        if (lat == null || lon == null) {
            binding.setVariable(BR.location, getString(R.string.defaultLocation))
        }
        viewModel.explore(lat ?: "52.3765043", lon ?: "4.9037063", radius)
    }

    private fun showMessage(@StringRes res: Int) {
        Toast.makeText(this, res, Toast.LENGTH_LONG).show();
    }

    private fun showMessage(str: String?) {
        str?.let {
            Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        }
    }

    override fun onChanged(it: Location?) {
        handleLocation(it)
    }

    private fun handleLocation(location: Location?) {
        location?.let {
            showMessage(R.string.located)

            val accuracy = location.accuracy;
            if (accuracy <= 250) {
                //if it is accurate enough, if just stop listening
                lat = location.latitude.toString()
                lon = location.longitude.toString()

                binding.setVariable(BR.location, "$lat, $lon")
                updateList()
                locationLiveData.removeObserver(this)
            } else {
                binding.setVariable(BR.location, "Not accurate enough $accuracy")
            }
        }
    }

    class VenueAdapter(var items: List<Venue>) : RecyclerView.Adapter<VenueAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, position: Int): MyViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.tem_venue, parent, false)
            return MyViewHolder(v)
        }

        override fun getItemCount() = items.size


        override fun onBindViewHolder(viewHolder: MyViewHolder?, position: Int) {
            val item = getItemForPosition(position)
            viewHolder?.bind(item)
            viewHolder?.binding?.root?.setOnClickListener {
                if(item.url != null) {
                    it.context.startActivity((Intent(Intent.ACTION_VIEW, Uri.parse(item.url))))
                }
            }
        }

        private fun getItemForPosition(position: Int) = items.get(position)

        inner class MyViewHolder : RecyclerView.ViewHolder {
            var binding: ViewDataBinding? = null

            constructor(rowView: View) : super(rowView) {
                binding = DataBindingUtil.bind(rowView)
            }

            fun bind(venue: Venue) {

                binding?.setVariable(BR.venue, venue)
                binding?.executePendingBindings()
            }
        }

    }
}
