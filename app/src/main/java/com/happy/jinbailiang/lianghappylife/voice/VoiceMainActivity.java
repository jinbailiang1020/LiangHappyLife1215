package com.happy.jinbailiang.lianghappylife.voice;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.happy.jinbailiang.lianghappylife.R;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class VoiceMainActivity extends AppCompatActivity implements View.OnClickListener {

    Button startRecordingButton, stopRecordingButton;//开始录音、停止录音
    File recordingFile;//储存AudioRecord录下来的文件
    boolean isRecording = false; //true表示正在录音
    AudioRecord audioRecord = null;
    File parent = null;//文件目录
    int bufferSize = 0;//最小缓冲区大小
    int sampleRateInHz = 11025;//采样率
    int channelConfig = AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT; //量化位数
    String TAG = "AudioRecord";
    private AudioTrack player;
    private SeekBar seekBar;
    private int i = 0;
    private int length = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(VoiceMainActivity.this, "音频读取完成", Toast.LENGTH_SHORT).show();
                return;
            }
            if (length != 0) {
                Toast.makeText(VoiceMainActivity.this, "进度：" + i * 10000 / length, Toast.LENGTH_SHORT).show();
                seekBar.setProgress(i * 10000 / length);
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_voice_main);
        init();
        initListener();
        initSeekBarThread();
    }

    private void initSeekBarThread() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    handler.sendEmptyMessage(10);
              /*      handler.post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });*/
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    //初始化
    public void init() {
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        startRecordingButton = (Button) findViewById(R.id.StartRecording);
        stopRecordingButton = (Button) findViewById(R.id.StopRecording);

        bufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);//计算最小缓冲区
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelConfig, audioFormat, bufferSize);//创建AudioRecorder对象

        parent = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Jin_AudiioRecordTest");
        if (!parent.exists())
            parent.mkdirs();//创建文件夹
    }

    //初始化监听器
    public void initListener() {
        startRecordingButton.setOnClickListener(this);
        stopRecordingButton.setOnClickListener(this);
        findViewById(R.id.playVoice).setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            //开始录音
            case R.id.StartRecording:
                record();
                break;
            //停止录音
            case R.id.StopRecording:
                stopRecording();
                break;
            case R.id.playVoice:
                play();
                break;
        }

    }

    //开始录音
    public void record() {
        isRecording = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                isRecording = true;

                recordingFile = new File(parent, "audiotest.pcm");
                if (recordingFile.exists()) {
                    recordingFile.delete();
                }
                try {
                    recordingFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, "创建储存音频文件出错");
                }

                try {
                    DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordingFile)));
                    byte[] buffer = new byte[bufferSize];
                    audioRecord.startRecording();//开始录音
                    int r = 0;
                    while (isRecording) {
                        int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                        for (int i = 0; i < bufferReadResult; i++) {
                            dos.write(buffer[i]);
                        }
                        r++;
                    }
                    audioRecord.stop();//停止录音
                    dos.close();
                } catch (Throwable t) {
                    Log.e(TAG, "Recording Failed");
                }
            }
        }).start();

    }

    //停止录音
    public void stopRecording() {
        isRecording = false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    //播放音频（PCM）
    public void play() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                playThread();
            }
        }).start();

    }

    private void playThread() {
        DataInputStream dis = null;
        try {
            //从音频文件中读取声音
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(recordingFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //最小缓存区
        int bufferSizeInBytes = AudioTrack.getMinBufferSize(sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        //创建AudioTrack对象   依次传入 :流类型、采样率（与采集的要一致）、音频通道（采集是IN 播放时OUT）、量化位数、最小缓冲区、模式
        player = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSizeInBytes, AudioTrack.MODE_STREAM);

        byte[] data = new byte[bufferSizeInBytes];
        length = data.length;
        player.play();//开始播放
        while (true) {
            try {
                while (dis.available() > 0 && i < data.length) {
                    data[i] = dis.readByte();//录音时write Byte 那么读取时就该为readByte要相互对应
                    i++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.write(data, 0, data.length);

            if (i != bufferSizeInBytes) //表示读取完了
            {
                i = 0;
                player.stop();//停止播放
                player.release();//释放资源
                handler.sendEmptyMessage(1);
                break;
            }
        }
    }
}
