package com.V4Creations.FSMK.campfoodmanager.interfaces;

import java.util.ArrayList;

import com.V4Creations.FSMK.campfoodmanager.adapter.ResultListViewAdapter.ResultListViewItem;

public interface GetSingleItemDetailsInterface {
	void notify(int id, ArrayList<ResultListViewItem> resultListViewItems);
}
