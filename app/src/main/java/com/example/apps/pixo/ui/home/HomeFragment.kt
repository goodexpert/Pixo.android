package com.example.apps.pixo.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps.pixo.Const
import com.example.apps.pixo.R
import com.example.apps.pixo.model.MediaAlbum
import com.example.apps.pixo.ui.base.BaseFragment
import com.example.apps.pixo.viewmodel.AlbumViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.android.synthetic.main.fragment_home.*
import java.util.concurrent.TimeUnit

class HomeFragment : BaseFragment() {
    companion object {
        private const val TAG = "HomeFragment"
        private const val SPAN_COUNT = 2
    }

    private val viewModel: AlbumViewModel by viewModels { providerFactory }
    private val adapter: HomeAdapter by lazy { HomeAdapter(activity!!) }
    private val itemClickedSubject: Subject<MediaAlbum> by lazy { PublishSubject.create<MediaAlbum>() }

    override val layoutResourceId: Int
        get() = R.layout.fragment_home

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemClickedSubject
            .throttleLast(Const.INTERVAL_DURATION, TimeUnit.MICROSECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val args = Bundle()
                args.putParcelable("data", it)

                findNavController().navigate(R.id.action_homeFragment_to_listFragment, args)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val albumLabelHeightInPixels = resources.getDimensionPixelSize(R.dimen.view_album_label_height)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_tiny)

        val layoutManager = object : GridLayoutManager(activity, SPAN_COUNT) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                val itemWidth = (width - spacingInPixels * 2) / spanCount
                lp?.width = itemWidth
                lp?.height = itemWidth + albumLabelHeightInPixels
                return lp is LayoutParams
            }
        }
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        this.adapter.setOnItemClickedListener { _, data ->
            itemClickedSubject.onNext(data as MediaAlbum)
        }
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener {
            this.viewModel.updateData()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.viewModel.albumsLiveData.let { albumsLiveData ->
            albumsLiveData.observe(viewLifecycleOwner, Observer { albumList ->
                adapter.setItems(albumList)
                refreshLayout.isRefreshing = false
            })
        }
    }
}