package active.since93.recyclerview.audio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by myzupp on 08-04-2017.
 *
 * @author Darshan Parikh (parikhdarshan36@gmail.com)
 */

public class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> {

    private Context context;
    private List<String> contactList = new ArrayList<>();
    private MainActivity mainActivity;

    public AudioListAdapter(Context context, List<String> contactList) {
        this.context = context;
        this.contactList = contactList;
        this.mainActivity = (MainActivity) context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String songPath = contactList.get(position);
        String songName = songPath.substring(songPath.lastIndexOf("/") + 1);
        holder.txtSongName.setText(songName);

        if(mainActivity.audioStatusList.get(position).getAudioState() != AudioStatus.AUDIO_STATE.IDLE.ordinal()) {
            holder.seekBarAudio.setMax(mainActivity.audioStatusList.get(position).getTotalDuration());
            holder.seekBarAudio.setProgress(mainActivity.audioStatusList.get(position).getCurrentValue());
            holder.seekBarAudio.setEnabled(true);
        } else {
            holder.seekBarAudio.setProgress(0);
            holder.seekBarAudio.setEnabled(false);
        }

        if(mainActivity.audioStatusList.get(position).getAudioState() == AudioStatus.AUDIO_STATE.IDLE.ordinal()
                || mainActivity.audioStatusList.get(position).getAudioState() == AudioStatus.AUDIO_STATE.PAUSED.ordinal()) {
            holder.btnPlay.setText(context.getString(R.string.play));
        } else {
            holder.btnPlay.setText(context.getString(R.string.pause));
        }
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.btnPlay)
        Button btnPlay;

        @BindView(R.id.seekBarAudio)
        SeekBar seekBarAudio;

        @BindView(R.id.txtSongName)
        TextView txtSongName;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser) MediaPlayerUtils.applySeekBarValue(progress);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final int position = getAdapterPosition();

                    if(mainActivity.audioStatusList.get(position).getAudioState()
                            == AudioStatus.AUDIO_STATE.IDLE.ordinal()) {
                        MediaPlayerUtils.Listener listener = (MediaPlayerUtils.Listener) context;
                        listener.onAudioComplete();
                    }

                    String audioPath = contactList.get(position);
                    final AudioStatus audioStatus = mainActivity.audioStatusList.get(position);
                    int currentAudioState = audioStatus.getAudioState();

                    if(currentAudioState == AudioStatus.AUDIO_STATE.PLAYING.ordinal()) {
                        btnPlay.setText(context.getString(R.string.play));
                        MediaPlayerUtils.pauseMediaPlayer();

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PAUSED.ordinal());
                        mainActivity.audioStatusList.set(position, audioStatus);
                    } else if(currentAudioState == AudioStatus.AUDIO_STATE.PAUSED.ordinal()) {
                        btnPlay.setText(context.getString(R.string.pause));
                        MediaPlayerUtils.playMediaPlayer();

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PLAYING.ordinal());
                        mainActivity.audioStatusList.set(position, audioStatus);
                    } else {
                        btnPlay.setText(context.getString(R.string.pause));

                        audioStatus.setAudioState(AudioStatus.AUDIO_STATE.PLAYING.ordinal());
                        mainActivity.audioStatusList.set(position, audioStatus);

                        try {
                            MediaPlayerUtils.startAndPlayMediaPlayer(audioPath, (MediaPlayerUtils.Listener) context);

                            audioStatus.setTotalDuration(MediaPlayerUtils.getTotalDuration());
                            mainActivity.audioStatusList.set(position, audioStatus);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }
}
