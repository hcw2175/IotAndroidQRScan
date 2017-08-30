package com.hcw2175.qrscan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hcw2175.library.qrscan.QRScanUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QRScanUtil.test();
    }
}
