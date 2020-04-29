package edu.cmu.project4getlyricsandroid;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;


public class AndroidApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Flickr, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final AndroidApp aa = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button) findViewById(R.id.submit);




        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String singer = ((EditText) findViewById(R.id.singer)).getText().toString();
                String track = ((EditText) findViewById(R.id.track)).getText().toString();
                GetLyrics gl = new GetLyrics();
                gl.search(singer, track, aa); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the picture is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */
    public void lyricsReady(String lyrics) {
        TextView singerView = (EditText)findViewById(R.id.singer);
        TextView trackView = (EditText)findViewById(R.id.track);
        TextView lyricsView = (TextView) findViewById(R.id.lyrics);

        TextView b = (TextView)findViewById(R.id.resultString);
        if (lyrics != null) {
            lyricsView.setText(lyrics);
            b.setText("Here is the lyrics of " + trackView.getText() + " from " + singerView.getText());
        } else {
            lyricsView.setText("");
            b.setText("Sorry, I could not find the lyrics of " + trackView.getText() + " from " + singerView.getText());
        }
        singerView.setText("");
        trackView.setText("");
    }
}

