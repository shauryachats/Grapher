package com.shauryachats.grapher;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText mainEdit = (EditText) findViewById(R.id.editText);
        final EditText valueOfX = (EditText) findViewById(R.id.valueX);
        final EditText valueOfY = (EditText) findViewById(R.id.valueY);
        Button submitButton = (Button) findViewById(R.id.button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EquationEvaluator equationEvaluator = new EquationEvaluator(mainEdit.getText().toString());

                HashMap<String, Double> h = new HashMap<String, Double>();

                //Checking if valueOfX is empty
                if (valueOfX.getText().toString().equals("")) {
                    valueOfX.setText("0.0");
                }

                //Checking if valueOfY is empty
                if (valueOfY.getText().toString().equals("")) {
                    valueOfY.setText("0.0");
                }

                double x = Double.parseDouble(valueOfX.getText().toString());
                double y = Double.parseDouble(valueOfY.getText().toString());
                Toast.makeText(MainActivity.this, Double.toString(equationEvaluator.eval(x)), Toast.LENGTH_SHORT).show();

                //Shift control to GraphActivity
                plotGraph(v, equationEvaluator);

            }
        });

    }

    public void plotGraph(View view, EquationEvaluator equationEvaluator)
    {
        Intent intent = new Intent(this, GraphActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("postfix", equationEvaluator.getPostfix());
        intent.putExtras(bundle);
        startActivity(intent);
    }


}
