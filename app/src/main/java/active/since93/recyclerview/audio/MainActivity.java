package active.since93.recyclerview.audio;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import active.since93.test.purpledocs.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements MediaPlayerUtils.Listener {

    private Context context;
    private List<String> contactList = new ArrayList<>();
    public List<AudioStatus> audioStatusList = new ArrayList<>();
    private Parcelable state;

    @BindView(R.id.recyclerViewContactsList)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = MainActivity.this;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactList.add("/storage/emulated/0/Download/A Sky Full Of Stars - Coldplay - [SongsPk.CC].mp3");
        contactList.add("/storage/emulated/0/Download/Coldplay - The Scientist.mp3");
        contactList.add("/storage/emulated/0/Android/data/com.android.email/files/Ringtones/EmailIncoming.ogg");
        contactList.add("/storage/emulated/0/Android/media/com.google.android.talk/Notifications/hangouts_message.ogg");
        contactList.add("/storage/emulated/0/Android/media/com.google.android.talk/Ringtones/hangouts_incoming_call.ogg");
        contactList.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Audio/Sent/AUD-20170224-WA0007.ogg");
        contactList.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Audio/Sent/AUD-20170224-WA0009.ogg");
        contactList.add("/storage/emulated/0/AirIt/AUD-20161118-WA00101493909137078.mp3");
        contactList.add("/storage/emulated/0/SyncApp/SyncApp Audio/Private/73kGKx2PSASBUc94bzBuFw.ogg");
        contactList.add("/storage/emulated/0/SyncApp/SyncApp Audio/Private/rm22LpN0RWqH1wC1ROlfnw.mp3");
        contactList.add("/storage/emulated/0/Download/A Sky Full Of Stars - Coldplay - [SongsPk.CC].mp3");
        contactList.add("/storage/emulated/0/Download/Coldplay - The Scientist.mp3");
        contactList.add("/storage/emulated/0/Android/data/com.android.email/files/Ringtones/EmailIncoming.ogg");
        contactList.add("/storage/emulated/0/Android/media/com.google.android.talk/Notifications/hangouts_message.ogg");
        contactList.add("/storage/emulated/0/Android/media/com.google.android.talk/Ringtones/hangouts_incoming_call.ogg");
        contactList.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Audio/Sent/AUD-20170224-WA0007.ogg");
        contactList.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Audio/Sent/AUD-20170224-WA0009.ogg");
        contactList.add("/storage/emulated/0/AirIt/AUD-20161118-WA00101493909137078.mp3");
        contactList.add("/storage/emulated/0/SyncApp/SyncApp Audio/Private/73kGKx2PSASBUc94bzBuFw.ogg");
        contactList.add("/storage/emulated/0/SyncApp/SyncApp Audio/Private/rm22LpN0RWqH1wC1ROlfnw.mp3");

        for(int i = 0; i < contactList.size(); i++) {
            audioStatusList.add(new AudioStatus(AudioStatus.AUDIO_STATE.IDLE.ordinal(), 0));
        }
        setRecyclerViewAdapter(contactList);
    }

    private void setRecyclerViewAdapter(List<String> contactList) {
        ContactsAdapter adapter = new ContactsAdapter(context, contactList);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onAudioComplete() {
        // Store its state
        state = recyclerView.getLayoutManager().onSaveInstanceState();

        audioStatusList.clear();
        for(int i = 0; i < contactList.size(); i++) {
            audioStatusList.add(new AudioStatus(AudioStatus.AUDIO_STATE.IDLE.ordinal(), 0));
        }
        setRecyclerViewAdapter(contactList);

        // Main position of RecyclerView when loaded again
        if (state != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Store its state
        state = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Main position of RecyclerView when loaded again
        if (state != null) {
            recyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public void onAudioUpdate(int currentPosition) {
        int playingAudioPosition = -1;
        for(int i = 0; i < audioStatusList.size(); i++) {
            AudioStatus audioStatus = audioStatusList.get(i);
            if(audioStatus.getAudioState() == AudioStatus.AUDIO_STATE.PLAYING.ordinal()) {
                playingAudioPosition = i;
                break;
            }
        }

        if(playingAudioPosition != -1) {
            ContactsAdapter.ViewHolder holder
                    = (ContactsAdapter.ViewHolder) recyclerView.findViewHolderForAdapterPosition(playingAudioPosition);
            if (holder != null) {
                holder.seekBarAudio.setProgress(currentPosition);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MediaPlayerUtils.releaseMediaPlayer();
    }
}
