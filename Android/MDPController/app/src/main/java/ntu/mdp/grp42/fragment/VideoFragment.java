package ntu.mdp.grp42.fragment;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import org.videolan.libvlc.util.VLCVideoLayout;

import ntu.mdp.grp42.R;
import ntu.mdp.grp42.bluetooth.RaspberryPiProtocol;


public class VideoFragment extends Fragment implements RaspberryPiProtocol, View.OnClickListener {

    LibVLC libVLC;
    MediaPlayer mediaPlayer;
    VLCVideoLayout videoLayout;
    Button playPauseBtn;

    private static final String url = RTSP_LINK;
    private static final String testUrl = "rtsp://192.168.0.101:8554/";

    public VideoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_video, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        libVLC = new LibVLC(getContext());
        mediaPlayer = new MediaPlayer(libVLC);
        videoLayout = view.findViewById(R.id.videoLayout);
        playPauseBtn = view.findViewById(R.id.playPauseBtn);
        playPauseBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playPauseBtn:
                if (playPauseBtn.getText().toString().equals("Play")) {
                    playPauseBtn.setText("Stop");
                    mediaPlayer.play();
                } else {
                    playPauseBtn.setText("Play");
                    mediaPlayer.pause();
                }
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mediaPlayer.attachViews(videoLayout, null, false, false);
        Media media = new Media (libVLC, Uri.parse(testUrl));
        media.setHWDecoderEnabled(true, false);
        mediaPlayer.setMedia(media);
        media.release();
    }
}