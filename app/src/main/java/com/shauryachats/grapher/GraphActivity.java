package com.shauryachats.grapher;

import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

/**
 * Created by shauryachats on 2/4/17.
 */

public class GraphActivity extends AppCompatActivity
{
    private GLSurfaceView glSurfaceView;
    private boolean rendererSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        ArrayList<String> postfix = bundle.getStringArrayList("postfix");
        EquationEvaluator equationEvaluator = new EquationEvaluator(postfix);

        glSurfaceView = new GLSurfaceView(this);

        glSurfaceView.setEGLContextClientVersion(2);
        glSurfaceView.setRenderer(new GraphRenderer(this, equationEvaluator));
        rendererSet = true;

        setContentView(glSurfaceView);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (rendererSet)
            glSurfaceView.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (rendererSet)
            glSurfaceView.onResume();
    }
}