package com.shauryachats.grapher;


import com.shauryachats.grapher.android.util.LoggerConfig;

import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.util.Log;

/**
 * Postfix class is meant to convert the Infix to Postfix notations.
 */


class InvalidPostfixException extends Exception
{
    public InvalidPostfixException()
    {

    }

    public InvalidPostfixException(String message)
    {
        super(message);
    }
}

public class EquationEvaluator {

    private String regexString = "";
    private static HashMap<String, Integer> operator;

    private static final String TAG = "PostfixConverter";

    private String mainStr;
    private ArrayList<String> splitStr;
    private ArrayList<String> postFix;

    private int paranthesesWeight;

    boolean isValidExpression;

    private void initRegString()
    {
        regexString += "([0-9]{0,10}\\.[0-9]{0,10})|";                       //decimal numbers
        regexString += "([0-9]{1,10})|";                                    //natural numbers
        regexString += "(sin)|(cos)|(tan)|(asin)|(acos)|(atan)|(mod)|";            //trigonometric functions
        regexString += "(log)|(ln)|";                                       //logarithmic functions
        regexString += ".";                                                 //any other character (assuming variables are one character)
    }

    public ArrayList<String> getPostfix()
    {
        return postFix;
    }

    public EquationEvaluator(String str)
    {
        if (str.isEmpty()) {
            mainStr = "";
            return;
        }

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

        isValidExpression = true; //Assuming that the expression given to us to be parsed is valid.

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
        operator.put("mod", 4);
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
        try {
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
        }
        catch (EmptyStackException e)
        {
            isValidExpression = false;
            return;
        }

        if (LoggerConfig.ON) {
            String debugString = "";

            for (String s : postFix)
                debugString += s + ',';

            Log.d(TAG, "postFix = " + debugString);

        }
    }

    public double eval(double x) throws InvalidPostfixException {

        if (LoggerConfig.ON) {
            Log.d(TAG, "Evaluating postfix.");
        }

        if (!isValidExpression)
        {
            throw new InvalidPostfixException();
        }

        Stack<Double> stack = new Stack<Double>();
        double var2, var1;

        try {
            for (String token : postFix) {
                try {
                    stack.push(Double.parseDouble(token));
                } catch (NumberFormatException nfe) {
                    if (operator.containsKey(token) && operator.get(token) < 4) {
                        //if stack is empty.
                        var2 = stack.pop();
                        var1 = stack.pop();

                        switch (token) {
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
                    } else if (operator.containsKey(token) && operator.get(token) == 4) {
                        var1 = stack.pop();

                        switch (token) {
                            case "√":
                                stack.push(Math.sqrt(var1));
                                break;
                            case "sin":
                                stack.push(Math.sin(var1));
                                break;
                            case "cos":
                                stack.push(Math.cos(var1));
                                break;
                            case "tan":
                                stack.push(Math.tan(var1));
                                break;
                            case "log":
                                stack.push(Math.log10(var1));
                                break;
                            case "ln":
                                stack.push(Math.log(var1));
                                break;
                            case "asin":
                                stack.push(Math.asin(var1));
                                break;
                            case "acos":
                                stack.push(Math.acos(var1));
                                break;
                            case "atan":
                                stack.push(Math.atan(var1));
                                break;
                            case "mod":
                                stack.push(Math.abs(var1));
                                break;
                        }
                    } else if (token.equals("x")) {
                        stack.push(x);
                    } else if (token.equals("e")) {
                        stack.push(Math.E);
                    } else if (token.equals("π")) {
                        stack.push(Math.PI);
                    }
                    else {
                        throw new InvalidPostfixException();
                    }
                }
            }
        }
        catch (EmptyStackException e)
        {
            throw new InvalidPostfixException();
        }

        if (stack.size() != 1)
            throw new InvalidPostfixException();

        if (LoggerConfig.ON)
            Log.d(TAG, "The result is " + stack.peek());

        return stack.pop();
    }

    public boolean isValid()
    {
        if (mainStr.isEmpty() || !isValidExpression)
            return false;

        //Check parantheses check.

        paranthesesWeight = 0;
        for (char c : mainStr.toCharArray())
        {
            if (c == '(')
                paranthesesWeight++;
            else if (c == ')')
                --paranthesesWeight;
        }

        if (paranthesesWeight != 0)
            return false;

        //Try evaluating.
        try {
            eval(0);
        }
        catch (InvalidPostfixException e) {
            return false;
        }

        return true;
    }

}
