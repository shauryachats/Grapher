package com.shauryachats.grapher;

import android.content.Context;
import android.graphics.Shader;
import android.opengl.GLSurfaceView.Renderer;
import android.util.Log;

import com.shauryachats.grapher.android.util.LoggerConfig;
import com.shauryachats.grapher.android.util.ShaderHelper;
import com.shauryachats.grapher.android.util.TextResourceReader;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINE_STRIP;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static javax.microedition.khronos.opengles.GL10.GL_LINES;
import static javax.microedition.khronos.opengles.GL10.GL_POINTS;
import static javax.microedition.khronos.opengles.GL10.GL_TRIANGLE_FAN;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by shauryachats on 1/4/17.
 */

class GraphRenderer implements Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2;
    private static final int BYTES_PER_FLOAT = 4;
    private FloatBuffer vertexData = null;

    private final Context context;

    private int program;

    private static final String A_POSITION = "a_Position";
    private int aPositionLocation;

    private static final String A_COLOR = "a_Color";
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int STRIDE =(POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT;
    private int aColorLocation;

    private static final double PRECISION = 0.01;

    private int count = 0;

    private float[] convertFloatArrayListToFloatArray(List<Float> floatList)
    {
        float[] floatArray = new float[floatList.size()];
        int iterator = 0;

        for (Float f : floatList)
            floatArray[iterator++] = f;

        return floatArray;
    }

    public GraphRenderer(Context context, PostfixEvaluator postfixEvaluator)
    {
        this.context = context;
        Float[] axes = {

                // X, Y, R, G, B
                -1f, 0f, 1f, 1f, 1f,    //The X axis
                1f, 0f, 1f, 1f, 1f,

                0f, -1f, 1f, 1f, 1f,    //The Y axis
                0f, 1f, 1f, 1f, 1f
        };

        //Constructing a dynamic array out of a static one.
        ArrayList<Float> arrayList = new ArrayList<Float>(
                Arrays.asList(axes)
        );

        //Run postfixEvaluator here.
        for (float i = -1.0f; i <= 1.0f; i += PRECISION)
        {
            arrayList.add(i);
            arrayList.add((float)postfixEvaluator.eval((double)i));
            arrayList.add(1f);
            arrayList.add(0f);
            arrayList.add(1f);
            ++count;
        }


        float[] trueCoordinates = convertFloatArrayListToFloatArray(arrayList);

        vertexData = ByteBuffer
                .allocateDirect(trueCoordinates.length * BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        vertexData.put(trueCoordinates);
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config)
    {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_vertex_shader);
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, R.raw.simple_fragment_shader);

        Log.d("Shader", vertexShaderSource);
        Log.d("Shader", fragmentShaderSource);

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource);
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource);

        Log.d("Shader", " " + vertexShader + " " + fragmentShader);

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);
        if (LoggerConfig.ON)
        {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT, false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height)
    {
        glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 glUnused)
    {
        glClear(GL_COLOR_BUFFER_BIT);

        glDrawArrays(GL_LINES, 0, 2);
        glDrawArrays(GL_LINES, 2, 2);
        glDrawArrays(GL_LINE_STRIP, 4, count);

    }
}
