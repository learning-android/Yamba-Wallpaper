package com.marakana.android.yamba;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailsFragment extends Fragment {
	private TextView textUser, textMessage;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater
				.inflate(R.layout.fragment_details, container, false);

		textUser = (TextView) view.findViewById(R.id.text_user);

		textMessage = (TextView) view.findViewById(R.id.text_msg);

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		String user = (String) getActivity().getIntent().getCharSequenceExtra("user");
		String msg = (String) getActivity().getIntent().getCharSequenceExtra("msg");		

		updateView(user, msg);
	}
	
	public void updateView(String user, String msg) {		
		if(user == null) {
			user = "No User";
		} else {
			user = "User: "+user;
		}
		if(msg == null) {
			msg = "No Message";
		} else {
			msg = "Msg: "+msg;
		}
		textUser.setText(user);
		textMessage.setText(msg);
	}
}
