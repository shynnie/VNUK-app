package vn.edu.vnuk.vnuk_sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import vn.edu.vnuk.vnuk_sharing.Functional.FunctionalScreen;


public class Sharing extends Fragment {


    public Sharing() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sharing, container, false);

        String[] classes = {"CSE15", "CSE16", "CSE17"};

        final ListView listView = (ListView) view.findViewById(R.id.list_view_classes);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                classes
        );

        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Intent intent = new Intent(getActivity(), FunctionalScreen.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
