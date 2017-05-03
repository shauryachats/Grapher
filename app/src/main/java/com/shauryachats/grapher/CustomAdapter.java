package com.shauryachats.grapher;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.shauryachats.grapher.android.util.LoggerConfig;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by shauryachats on 23/4/17.
 */

class CustomAdapter extends ArrayAdapter<Character> {

    private final String TAG = "CustomAdapter";

    ArrayList<String> expressions;
    ArrayList<Boolean> validExpressions;
    Button submitButton;

    int[] colors = {Color.RED, Color.BLUE, Color.GREEN};
    int[] hintColors = {Color.parseColor("#DD9999"), Color.parseColor("#9999DD"), Color.parseColor("#99DD99")};

    public CustomAdapter(@NonNull Context context, ArrayList<Character> data, Button button) {
        super(context, R.layout.items, data);

        expressions = new ArrayList<>();
        validExpressions = new ArrayList<>();

        submitButton = button;
    }


    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (LoggerConfig.ON)
        Log.d(TAG, "in getView(" + position + ")");

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        final View view = layoutInflater.inflate(R.layout.items, parent, false);

        final EditText editText = (EditText) view.findViewById(R.id.items_edittext);

        editText.setHint(CustomAdapter.this.getItem(position) + "(x)");
        editText.setHintTextColor(hintColors[position]);
        editText.setTextColor(colors[position]);

        try {
            editText.setText(expressions.get(position));
        } catch (Exception e)
        {

        }

        final String expressionText = editText.getText().toString();

        if (expressions.size() > position)
            expressions.set(position, expressionText);
        else
            expressions.add(expressionText);

        if (validExpressions.size() > position)
            validExpressions.set(position, new EquationEvaluator(expressionText).isValid());
        else
            validExpressions.add(new EquationEvaluator(expressionText).isValid());

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (LoggerConfig.ON)
                Log.d(TAG, "Editable = " + s.toString());

                expressions.set(position, s.toString());
                validExpressions.set(position, new EquationEvaluator(s.toString()).isValid());
                submitButton.setEnabled(allExpressionsValid());
            }
        });

        if (LoggerConfig.ON) {
            String debugStr = "";
            for (String str : expressions)
                debugStr += str + ";";

            Log.d(TAG, debugStr);

            debugStr = "";
            for (boolean b : validExpressions)
                debugStr += b + ";";

            Log.d(TAG, debugStr);
        }

        submitButton.setEnabled(allExpressionsValid());
        return view;
    }


    public boolean allExpressionsValid()
    {
        for (boolean b : validExpressions)
        {
            if (b == false)
                return b;
        }
        return true;
    }

    public ArrayList<String> getExpressions()
    {
        return expressions;
    }

    @Override
    public void remove(@Nullable Character object) {
        super.remove(object);

        if (LoggerConfig.ON)
            Log.d(TAG, "in remove(" + object+ ")");
        notifyDataSetChanged();
    }

    @Override
    public void add(@Nullable Character object) {
        super.add(object);

        if (LoggerConfig.ON)
            Log.d(TAG, "in add(" + object + ")");

        notifyDataSetChanged();
    }

    public void removeFromExpressions(int i) {
        expressions.remove(i);
        validExpressions.remove(i);
    }
}
