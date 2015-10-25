package cmu.helpnearby.nearby.Adapter;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cmu.helpnearby.nearby.Model.User;
import cmu.helpnearby.nearby.R;

/**
 * Created by evan on 7/26/15.
 */
public class AudienceListAdapter extends RecyclerView.Adapter<AudienceListAdapter.ViewHolder> {
    private List<User> audienceList;
    private peopleClickListener mListener;
    private pitchButtonClickListener mPitchListener;

    public interface pitchButtonClickListener {
        void onRealTimePitchClicked(int position);
    }

    public interface peopleClickListener {
        void onPeopleClicked(int position);
    }

    public AudienceListAdapter(List<User> userList, peopleClickListener listener,
                               pitchButtonClickListener pitchListener) {
        audienceList = userList;
        mListener = listener;
        mPitchListener = pitchListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AudienceListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // create a new view
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.helper_list_item, viewGroup, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mListener, mPitchListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        viewHolder.mTextView.setText(audienceList.get(i).getmName());
        viewHolder.mHeadline.setText(audienceList.get(i).getmHeadline());
        Uri uri = Uri.parse(audienceList.get(i).getmPictureUrl());
        viewHolder.mProfileImage.setImageURI(uri);
        //new DownloadImageTask(viewHolder.mProfileImage).execute(audienceList.get(i).getmPictureUrl());
    }

    @Override
    public int getItemCount() {
        return audienceList.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder  {
        // each data item is just a string in this case
        @Bind(R.id.name) public TextView mTextView;
        @Bind(R.id.headline) public TextView mHeadline;
        @Bind(R.id.profile_image) public SimpleDraweeView mProfileImage;

        public peopleClickListener mListener;
        public pitchButtonClickListener mPitchListener;

        @OnClick(R.id.pitch_button_realtime) void realmTimePitch() {
            mPitchListener.onRealTimePitchClicked(getPosition());
        }

        public ViewHolder(View v, peopleClickListener listener, pitchButtonClickListener pitchListener) {
            super(v);
            ButterKnife.bind(this, v);
            mListener = listener;
            mPitchListener = pitchListener;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onPeopleClicked(getPosition());
                }
            });
        }
    }
}
