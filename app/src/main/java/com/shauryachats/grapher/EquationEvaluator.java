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

public class EquationEvaluator {

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

    public EquationEvaluator(String str)
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

    public EquationEvaluator(ArrayList<String> postfixList)
    {
        postFix = postfixList;
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

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");
    }

    private static boolean isBinaryOperator(String str)
    {
        switch (str)
        {
            case "+":
            case "-":
            case "*":
            case "/":
            case "^": return true;
            default: return false;
        }
    }


    public double eval(double x) {

        if (LoggerConfig.ON)
            Log.d(TAG, "Evaluating postfix.");

        Stack<Double> stack = new Stack<Double>();

        for (String token : postFix)
        {
            if (isNumeric(token))
            {
                stack.push(Double.parseDouble(token));
            }
            else if (operator.containsKey(token))
            {
                if (isBinaryOperator(token))
                {
                    double var2 = stack.pop();
                    double var1 = stack.pop();
                    switch (token)
                    {
                        case "+":
                            stack.push(var1 + var2);
                            break;
                        case "-":
                            stack.push(var1 - var2);
                            break;
                        case "*":
                            stack.push(var1 * var2);
                            break;
                        case "/":
                            stack.push(var1 / var2);
                            break;
                        case "^":
                            stack.push(Math.pow(var1, var2));
                            break;
                    }
                }
                else
                {
                    double var = stack.pop();
                    switch (token)
                    {
                        case "√":
                            stack.push(Math.sqrt(var));
                            break;
                        case "sin":
                            stack.push(Math.sin(var));
                            break;
                        case "cos":
                            stack.push(Math.cos(var));
                            break;
                        case "tan":
                            stack.push(Math.tan(var));
                            break;
                        case "log":
                            stack.push(Math.log10(var));
                            break;
                        case "ln":
                            stack.push(Math.log(var));
                            break;
                        case "asin":
                            stack.push(Math.asin(var));
                            break;
                        case "acos":
                            stack.push(Math.acos(var));
                            break;
                        case "atan":
                            stack.push(Math.atan(var));
                            break;
                    }
                }
            }
            else //it is a variable.
            {
                if (token.equals("x"))
                {
                    stack.push(x);
                }
                else
                {
                    return -1;
                }
            }
        }

        if (LoggerConfig.ON)
            Log.d(TAG, "The result is " + Double.toString(stack.peek()));

        return stack.pop();
    }

}
