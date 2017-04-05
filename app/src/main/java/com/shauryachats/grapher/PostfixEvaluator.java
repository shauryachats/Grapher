package com.shauryachats.grapher;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Evaluate the postfix expression.
 */

public class PostfixEvaluator {

    private ArrayList<String> postFix;
    private HashMap<String, Integer> operator;

    private static final String TAG = "Postfix Evaluator";

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

    private void initializeOperators()
    {
        operator = new HashMap<String, Integer>();
        operator.put("+", 1);
        operator.put("-", 1);
        operator.put("*", 2);
        operator.put("/", 2);
        operator.put("^", 3);
        operator.put("√", 3);
        operator.put("log", 4);
        operator.put("sin", 4);
        operator.put("cos", 4);
        operator.put("tan", 4);
        operator.put("asin", 4);
        operator.put("acos", 4);
        operator.put("atan", 4);
    }

    public PostfixEvaluator(ArrayList<String> postfix)
    {
        initializeOperators();
        postFix = postfix;
    }

    public double eval(double x) {

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
                    Log.d(TAG, "stack.size() = " + stack.size());
                    double var = stack.pop();
                    Log.d(TAG, "variable = " + var);
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

        Log.d(TAG, "The result is " + Double.toString(stack.peek()));
        return stack.pop();
    }

}
