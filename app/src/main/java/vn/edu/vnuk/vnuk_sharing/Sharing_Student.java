package vn.edu.vnuk.vnuk_sharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import vn.edu.vnuk.vnuk_sharing.DataStructure.Course;
import vn.edu.vnuk.vnuk_sharing.Functional.FunctionalScreen;
import vn.edu.vnuk.vnuk_sharing.FunctionalStudent.FunctionalScreenStudent;

/**
 * Created by HP on 11/8/2017.
 */

public class Sharing_Student extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sharing_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Courses");

        ArrayList<String> classes = new ArrayList<String>();
        for (Course course : Data.courseArrayList) {
            classes.add(course.getName());
        }

        ListView listView = (ListView) findViewById(R.id.list_view_classes);
        listView.setAdapter(null);

        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                classes
        );


        listView.setAdapter(listViewAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Data.currentCourse = Data.courseArrayList.get(position);
                Intent intent = new Intent(Sharing_Student.this, FunctionalScreenStudent.class);
                startActivity(intent);
            }
        });
    }
}