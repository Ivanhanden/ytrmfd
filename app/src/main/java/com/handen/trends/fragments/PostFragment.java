package com.handen.trends.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.handen.trends.ClientInterface;
import com.handen.trends.R;
import com.handen.trends.adapters.CommentAdapter;
import com.handen.trends.data.Post;
import com.handen.trends.userActivity.UserProfileActivity;

import java.text.SimpleDateFormat;

public class PostFragment extends Fragment {

    private static final String ARGS_POST = "post";
    private static final String ARGS_USER_ID = "userId";

    private Post post;
    private Long userId;

    private TextView nicknameTextView;
    private TextView dateTextView;
    private TextView postTextTextView;
    private TextView categoryTextView;
    private TextView likesTextView;
    private ImageView likesImageView;
    private TextView viewTextView;
    private LinearLayout userDescriptionLinearLayout;
    private ImageButton editImageButton;
    private RecyclerView recycler;

    PostFragmentListener mListener;

    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance(Post post) {
        PostFragment fragment = new PostFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARGS_POST, (Parcelable) post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            post = (Post) getArguments().getParcelable(ARGS_POST);
            userId = post.getUserId();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post, container, false);
        mListener.setTitle(post.getTitle());

        nicknameTextView = (TextView) view.findViewById(R.id.text_view_nickname);
        nicknameTextView.setText(post.getUserNickname());

        dateTextView = (TextView) view.findViewById(R.id._text_view_date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd.MM.yyyy");
        dateTextView.setText(dateFormat.format(post.getPostDate()));

        postTextTextView = (TextView) view.findViewById(R.id.text_view_post_text);
        postTextTextView.setText(post.getText());

        categoryTextView = (TextView) view.findViewById(R.id.category_text_view);
        categoryTextView.setText(post.getCategory().getName());

        likesTextView = (TextView) view.findViewById(R.id.text_view_likes);
        likesTextView.setText(Long.toString(post.getLikes()));
        likesImageView = (ImageView) view.findViewById(R.id.like_image_view);
        LinearLayout likesView = (LinearLayout) view.findViewById(R.id.likes_view);
        likesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ClientInterface.isLiked(post.getId())) {
                    ClientInterface.unlikePost(post.getId());
                    likesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_like));
                    post.setLikes(post.getLikes() - 1);
                    likesTextView.setText(Long.toString(post.getLikes()));
                }
                else {
                    ClientInterface.likePost(post.getId());
                    likesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_filled));
                    post.setLikes(post.getLikes() + 1);
                    likesTextView.setText(Long.toString(post.getLikes()));
                }
            }
        });

        viewTextView = (TextView) view.findViewById(R.id.text_view_views);
        viewTextView.setText(Long.toString(post.getViews()));

        userDescriptionLinearLayout = (LinearLayout) view.findViewById(R.id.linear_layout_user_description);
        userDescriptionLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                intent.putExtra(ARGS_USER_ID, post.getUserId());
                startActivity(intent);
            }
        });

        if (ClientInterface.isLiked(post.getId())) {
            likesImageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_like_filled));
        }

        editImageButton = (ImageButton) view.findViewById(R.id.edit_post_image_button);
        if (post.getUserId() == ClientInterface.currentUserId) {
            editImageButton.setVisibility(View.VISIBLE);
        }
        else {
            editImageButton.setVisibility(View.INVISIBLE);
        }

        editImageButton.setOnClickListener(v -> mListener.startEditionActivity(post));

        recycler = (RecyclerView) view.findViewById(R.id.commentsRecyclerView);
        CommentAdapter adapter = new CommentAdapter(ClientInterface.getComments(post.getId()), mListener);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PostFragmentListener) {
            mListener = (PostFragmentListener) context;
        }
    }

    public interface PostFragmentListener {
        void setTitle(String postTitle);
        void startEditionActivity(Post post);
        void startUserProfileActivity(long userId);
    }
}
