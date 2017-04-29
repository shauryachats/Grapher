package com.shauryachats.grapher;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ArrayList<Character> equationIndexes;

    final static int MAX_EXPRESSIONS_ALLOWED = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button submitButton = (Button) findViewById(R.id.button);
        final Button addButton = (Button) findViewById(R.id.add_button);
        final Button removeButton = (Button) findViewById(R.id.remove_button);

        equationIndexes = new ArrayList<Character>();
        equationIndexes.add('f');

        final ListView listView = (ListView) findViewById(R.id.listview);

        final CustomAdapter newAdapter = new CustomAdapter(this, equationIndexes, submitButton);
        listView.setAdapter(newAdapter);

        removeButton.setEnabled(false);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //equationIndexes.add(equationIndexes.size());

                removeButton.setEnabled(true);

                if (newAdapter.getCount() == MAX_EXPRESSIONS_ALLOWED){
                    return;
                }

                Character a = newAdapter.getItem(newAdapter.getCount()-1);
                a++;
                newAdapter.add(a);

                if (newAdapter.getCount() == MAX_EXPRESSIONS_ALLOWED) {
                    addButton.setEnabled(false);
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addButton.setEnabled(true);

                if (newAdapter.getCount() == 1) {
                    return;
                }

                Character a = newAdapter.getItem(newAdapter.getCount()-1);
                newAdapter.removeFromExpressions(newAdapter.getCount()-1);
                newAdapter.remove(a);

                if (newAdapter.getCount() == 1)
                {
                    removeButton.setEnabled(false);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Shift control to GraphicActivity
                graphicActivity(v, newAdapter.getExpressions(), 70f);


            }
        });


    }

    // Methdd to shift from MainActivity to GraphicActivity
    public void graphicActivity(View view, ArrayList<String> listOfExpressions, double precision)
    {
        Intent intent = new Intent(this, GraphicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("postfix", listOfExpressions);
        bundle.putDouble("precision", precision);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
