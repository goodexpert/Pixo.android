package com.example.apps.pixo.ui.details

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.apps.pixo.Const
import com.example.apps.pixo.R
import com.example.apps.pixo.model.MediaFile
import com.example.apps.pixo.ui.base.BaseFragment
import com.example.apps.pixo.util.saveMediaFile
import com.example.apps.pixo.util.takeScreenShot
import com.example.apps.pixo.viewmodel.StickerViewModel
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.subjects.Subject
import kotlinx.android.synthetic.main.fragment_details.*
import java.util.concurrent.TimeUnit

class DetailsFragment : BaseFragment() {
    companion object {
        private const val TAG = "DetailsFragment"
        private const val SPAN_COUNT = 1
    }

    private val viewModel: StickerViewModel by viewModels { providerFactory }
    private val adapter: StickerAdapter by lazy { StickerAdapter(activity!!) }

    private val itemClickedSubject: Subject<Int> by lazy { PublishSubject.create<Int>() }
    private val overlayClickedSubject: Subject<View> by lazy { PublishSubject.create<View>() }

    private var overlayImage: Int = -1

    override val layoutResourceId: Int
        get() = R.layout.fragment_details

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        itemClickedSubject
            .throttleLast(Const.INTERVAL_DURATION, TimeUnit.MICROSECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                imageView.setOverlayResource(it)
                setHasOptionsMenu(true)
                overlayImage = it
            }

        overlayClickedSubject
            .throttleLast(Const.INTERVAL_DURATION, TimeUnit.MICROSECONDS)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                onOverlayClicked(it)
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val paddingInPixels = resources.getDimensionPixelSize(R.dimen.margin_large)
        val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_small)

        val layoutManager = GridLayoutManager(activity, SPAN_COUNT, RecyclerView.HORIZONTAL, false)
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
                outRect.right = spacingInPixels

                val position = parent.getChildLayoutPosition(view)
                if (position == 0) {
                    outRect.left = paddingInPixels
                }
            }
        }
        recyclerView.addItemDecoration(itemDecoration)

        this.adapter.setOnItemClickedListener { _, data ->
            itemClickedSubject.onNext(data as Int)
        }
        recyclerView.adapter = this.adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val data = arguments?.getParcelable("data") as MediaFile?
        data?.let {
            Picasso.get()
                .load(it.getContentUri())
                .into(imageView)
            setTitle(data.name)
        }

        viewModel.stickersLiveData.observe(viewLifecycleOwner, Observer { stickerList ->
            adapter.setItems(stickerList)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.forEach { item ->
            if (item.itemId == R.id.action_overlay) {
                if (item.actionView == null) {
                    item.actionView = layoutInflater.inflate(R.layout.view_action_overlay, null)
                }

                item.actionView?.let { view ->
                    view.findViewById<View>(R.id.overlay).apply {
                        setOnClickListener {
                            overlayClickedSubject.onNext(it)
                        }
                    }
                }
            }
        }
    }

    private fun onOverlayClicked(view: View) {
        imageView.takeScreenShot()
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .subscribe { flie ->
                saveMediaFile(activity!!, flie)
                Snackbar.make(view, R.string.message_save_image, Snackbar.LENGTH_SHORT).show()
            }
    }
}