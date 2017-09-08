package com.wazesounds;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SoundFileListAdapter extends ArrayAdapter<String> {

	private Context _context;
	private List<String> _soundFileList;

	public SoundFileListAdapter(Context context, int textViewResourceId, List<String> saleStoreList) {
		super(context, textViewResourceId, saleStoreList);
		_context = context;
		_soundFileList = saleStoreList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		try {

			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inflater.inflate(R.layout.sound_file_list_item, parent, false);
			}

			ImageView lockImg = (ImageView) rowView.findViewById(R.id.lock);
			TextView fileNameView = (TextView) rowView.findViewById(R.id.file_name);

			rowView.setBackgroundResource(R.drawable.simple_1);

			if (((WazeSoundsMain) _context).isSelected(position)) {
				rowView.setBackgroundResource(R.drawable.selected_1);
			}

			String fileName = _soundFileList.get(position);
			fileName = fileName.replace(".bin", "");

			if (Constant.isLockedItem(fileName)) {

				lockImg.setVisibility(View.VISIBLE);
			} else {
				lockImg.setVisibility(View.INVISIBLE);
			}

			String tmp = Constant.nameListFixer(fileName);

			Typeface tf = Typeface.createFromAsset(_context.getAssets(), "fonts/VAGLight.TTF");

			fileNameView.setTypeface(tf);
			fileNameView.setText(tmp);

			return rowView;
		}

		catch (Exception e) {
			Constant.Log_e("Failed in SoundFileListAdapter - getview e:" + e.getMessage());
			return null;
		}

	}

}
