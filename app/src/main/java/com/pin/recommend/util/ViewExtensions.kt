package com.pin.recommend.util

import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.ListView

fun setListViewHeightBasedOnChildren(listView: ListView) {

    //ListAdapterを取得
    val listAdapter: ListAdapter = listView.getAdapter()
        ?: // nullチェック
        return

    //初期化
    var totalHeight = 0

    //個々のアイテムの高さを測り、加算していく
    for (i in 0 until listAdapter.getCount()) {
        val listItem: View = listAdapter.getView(i, null, listView)
        listItem.measure(0, 0)
        totalHeight += listItem.getMeasuredHeight()
    }

    //LayoutParamsを取得
    val params: ViewGroup.LayoutParams = listView.getLayoutParams()

    //(区切り線の高さ * 要素数の数)だけ足してあげる
    params.height = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1)

    //LayoutParamsにheightをセットしてあげる
    listView.setLayoutParams(params)
}