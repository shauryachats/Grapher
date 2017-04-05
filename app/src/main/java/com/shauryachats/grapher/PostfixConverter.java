package com.shauryachats.grapher;

import android.util.Log;
import android.widget.TabHost;

import com.shauryachats.grapher.android.util.LoggerConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Postfix class is meant to convert the Infix to Postfix notations.
 */

public class PostfixConverter {

    private String regexString = "";
    private static HashMap<String, Integer> operator;

    private static final String TAG = "PostfixConverter";

    private String mainStr;
    private ArrayList<String> splitStr;
    private ArrayList<String> postFix;

    private void initRegString()
    {
        regexString += "([0-9]{0,10}\\.[0-9]{0,10})|";  //decimal numbers
        regexString += "([0-9]{1,10})|";                //natural numbers
        regexString += "(sin)|(cos)|(tan)|(asin)|(acos)|(atan)|";            //trigonometric functions
        regexString += "(log)|(ln)|";                   //logarithmic functions
        regexString += ".";                             //any other character (assuming variables are one character)
    }

    public ArrayList<String> getPostfix()
    {
        return postFix;
    }

    public PostfixConverter(String str)
    {
        initializeOperators();
        initRegString();

        splitStr = new ArrayList<String>();
        postFix = new ArrayList<String>();

        mainStr = str;

        //if mainStr is starting with a negative sign, append a 0.
        if (mainStr.charAt(0) == '-')
            mainStr = '0' + mainStr;

        //pre-append and post-append brackets for easy eval.
        mainStr = "(" + mainStr + ')';

        //splitting mainStr into splitStr
        splitInfix();

        //converting infix to postfix
        infixToPostfix();
    }

    private void initializeOperators()
    {
        operator = new HashMap<String, Integer>();
        operator.put("+", 1);
        operator.put("-", 1);
        operator.put("*", 2);
        operator.put("/", 2);
        operator.put("^", 3);
        operator.put("√", 4);
        operator.put("log", 4);
        operator.put("sin", 4);
        operator.put("cos", 4);
        operator.put("tan", 4);
        operator.put("asin", 4);
        operator.put("acos", 4);
        operator.put("atan", 4);
    }

    // splitting infix terms.
    private void splitInfix() {

        if (LoggerConfig.ON)
            Log.d(TAG, "Inside splitInfix() with mainStr = " + mainStr);

        Matcher m = Pattern.compile(regexString).matcher(mainStr);

        while (m.find())
            splitStr.add(m.group());

        if (LoggerConfig.ON) {
            String debugStr = "";
            for (String s : splitStr)
                debugStr += s + ',';

            Log.d(TAG, debugStr);
        }
    }

    //Converts infix to postfix expression.

    //TODO: Check for invalid infix expressions.
    private void infixToPostfix() {
        if (LoggerConfig.ON)
            Log.d(TAG, "Inside infixToPostfix()");

        Stack<String> tokenStack = new Stack<String>();
        for (String token : splitStr) {
            if (LoggerConfig.ON)
                Log.d(TAG, "next token = " + token);

            //if token is an operator
            if (token.equals("(")) {
                tokenStack.push("(");
            } else if (token.equals(")")) {
                while (!tokenStack.peek().equals("(")) {
                    postFix.add(tokenStack.pop());
                }
                //remove the extra '('
                tokenStack.pop();
            } else if (operator.containsKey(token)) {
                while (!tokenStack.peek().equals("(") && operator.get(token) <= operator.get(tokenStack.peek()))
                    postFix.add(tokenStack.pop());
                tokenStack.push(token);
            } else //it is a variable
            {
                postFix.add(token);
            }
        }

        if (LoggerConfig.ON) {
            String debugString = "";

            for (String s : postFix)
                debugString += s + ',';

            Log.d(TAG, "postFix = " + debugString);

        }
    }


}
