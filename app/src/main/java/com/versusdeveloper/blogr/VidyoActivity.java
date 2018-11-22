package com.versusdeveloper.blogr;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.vidyo.VidyoClient.Connector.Connector;
import com.vidyo.VidyoClient.Connector.ConnectorPkg;

public class VidyoActivity extends AppCompatActivity implements Connector.IConnect{

    private Connector vc;
    private FrameLayout videoFrame;
    private FloatingActionButton start, connect, disconnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vidyo);

        ConnectorPkg.setApplicationUIContext(this);
        ConnectorPkg.initialize();
        videoFrame = findViewById(R.id.videoFrame);

        start = findViewById(R.id.fabStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Start();
            }
        });

        connect = findViewById(R.id.fabConnect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connect();
            }
        });

        disconnect = findViewById(R.id.fabDisconnect);
        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Disconnect();
            }
        });
    }

    protected void Start() {
        vc = new Connector(videoFrame, Connector.ConnectorViewStyle.VIDYO_CONNECTORVIEWSTYLE_Default, 15, "warning info@VidyoClient info@VidyoConnector", "", 0);
        vc.showViewAt(videoFrame, 0, 0, videoFrame.getWidth(), videoFrame.getHeight());
        Toast.makeText(VidyoActivity.this, "Starting", Toast.LENGTH_LONG).show();
        String token = "cHJvdmlzaW9uAHVzZXIxQGJmY2YwYS52aWR5by5pbwA2MzcxMDAyOTMxNQAAMTBhNDdjYjdkYWIxNDUyZDRmZTgzOWYyNjlmZmE2NTI4MWU5YmY1YmU2OGUxZjJjNjJjY2VhNzc0NWYyZGNiYjkyYzVkNTYwNmZjZDEyZTQ3NmRlZWI0NTE1MGU3OTc2";
//        vc.connect("prod.vidyo.io", token, "DemoUser", "DemoRoom", this);
        vc.connect("https://developer.vidyo.io/join/uTwfkLPB", token, "DemoUser", "DemoRoom", VidyoActivity.this);
        Toast.makeText(VidyoActivity.this, "Connecting", Toast.LENGTH_LONG).show();
    }

    public void Connect() {
    }

    public void Disconnect() {
        vc.disconnect();
        Toast.makeText(VidyoActivity.this, "Disconnecting", Toast.LENGTH_LONG).show();
        startActivity(new Intent(VidyoActivity.this, Tabs.class));
    }

    @Override
    public void onSuccess() {
        start.hide();
        Toast.makeText(VidyoActivity.this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Connector.ConnectorFailReason connectorFailReason) {
        Toast.makeText(VidyoActivity.this, "Failure Occurred:" + connectorFailReason, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected(Connector.ConnectorDisconnectReason connectorDisconnectReason) {
        Toast.makeText(VidyoActivity.this, "Disconnected:" + connectorDisconnectReason, Toast.LENGTH_LONG).show();
    }

}
