package com.shauryachats.grapher;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    EquationEvaluator equationEvaluator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText mainEdit = (EditText) findViewById(R.id.editText);
        final EditText valueOfX = (EditText) findViewById(R.id.valueX);
        final EditText valueOfY = (EditText) findViewById(R.id.valueY);
        final Button submitButton = (Button) findViewById(R.id.button);


        mainEdit.setText("0.2*sin(5*x)");
        valueOfY.setText("20");

        equationEvaluator = new EquationEvaluator(mainEdit.getText().toString());


        //To enable/disable the Evaluate button according to the validity of the postfix expression.
        mainEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                equationEvaluator = new EquationEvaluator(mainEdit.getText().toString());
                submitButton.setEnabled(equationEvaluator.isValid());
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                equationEvaluator = new EquationEvaluator(mainEdit.getText().toString());

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

                //Shift control to GraphicActivity
                graphicActivity(v, equationEvaluator, y);


            }
        });

    }

    public void graphicActivity(View view, EquationEvaluator equationEvaluator, double y)
    {
        Intent intent = new Intent(this, GraphicActivity.class);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("postfix", equationEvaluator.getPostfix());
        bundle.putDouble("precision", y);
        intent.putExtras(bundle);
        startActivity(intent);
    }

}
