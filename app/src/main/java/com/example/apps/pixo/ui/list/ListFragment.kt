package com.example.apps.pixo.ui.list

import android.graphics.Rect
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
import com.example.apps.pixo.model.MediaFile
import com.example.apps.pixo.ui.base.BaseFragment
import com.example.apps.pixo.viewmodel.MediaViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.concurrent.TimeUnit

class ListFragment : BaseFragment() {
    companion object {
        private const val TAG = "ListFragment"
        private const val SPAN_COUNT = 3
    }

    private val viewModel: MediaViewModel by viewModels { providerFactory }
    private val adapter: ListAdapter by lazy { ListAdapter(activity!!) }
    private val itemClickedSubject: Subject<MediaFile> by lazy { PublishSubject.create<MediaFile>() }

    override val layoutResourceId: Int
        get() = R.layout.fragment_list

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemClickedSubject
            .throttleLast(Const.INTERVAL_DURATION, TimeUnit.MICROSECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                val args = Bundle()
                args.putParcelable("data", it)

                findNavController().navigate(R.id.action_listFragment_to_detailsFragment, args)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.border_size)

        val layoutManager = object : GridLayoutManager(activity, SPAN_COUNT) {
            override fun checkLayoutParams(lp: RecyclerView.LayoutParams?): Boolean {
                val itemWidth = (width - spacingInPixels * 4) / spanCount
                lp?.width = itemWidth
                lp?.height = itemWidth
                return lp is LayoutParams
            }
        }
        layoutManager.isSmoothScrollbarEnabled = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                outRect.left = spacingInPixels
                outRect.top = spacingInPixels
                outRect.right = spacingInPixels
                outRect.bottom = spacingInPixels

                val position = parent.getChildLayoutPosition(view)
                when (position % SPAN_COUNT) {
                    0 -> outRect.left = 0
                    2 -> outRect.right = 0
                }
                if (position < SPAN_COUNT) {
                    outRect.top = spacingInPixels * 8
                }
            }
        }
        recyclerView.addItemDecoration(itemDecoration)

        this.adapter.setOnItemClickedListener { _, data ->
            itemClickedSubject.onNext(data as MediaFile)
        }
        recyclerView.adapter = this.adapter

        refreshLayout.setOnRefreshListener {
            viewModel.updateData()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data = arguments?.getParcelable("data") as MediaAlbum?
        data?.let {
            viewModel.setFilter(data.id)
            setTitle(data.name)
        }

        viewModel.mediaFilesLiveData.observe(viewLifecycleOwner, Observer { mediaFiles ->
            adapter.setItems(mediaFiles)
            refreshLayout.isRefreshing = false
        })
    }
}