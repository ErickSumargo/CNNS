package com.app.cnns.view.activity;

import android.content.DialogInterface;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.cnns.R;
import com.app.cnns.helper.Internet;
import com.app.cnns.helper.Session;
import com.app.cnns.helper.Utils;
import com.app.cnns.server.interfaces.Response;
import com.app.cnns.server.model.Comment;
import com.app.cnns.server.model.News;
import com.app.cnns.server.model.User;
import com.app.cnns.server.presenter.BasePresenter;
import com.app.cnns.server.response.BaseResponse;
import com.app.cnns.server.response.CommentResponse;
import com.app.cnns.server.response.NewsResponse;
import com.app.cnns.view.adapter.CommentListAdapter;
import com.app.cnns.view.adapter.NewsListAdapter;
import com.app.cnns.view.custom.ProgressDialogCustom;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentActivity extends AppCompatActivity implements Response {
    private Toolbar toolbar;
    private ImageView post;

    private NestedScrollView mainLayout;
    private ImageView image;
    private TextView title, date;
    private RecyclerView commentListView;
    private CommentListAdapter commentListAdapter;

    private ProgressBar loadingView;
    private ProgressDialogCustom progressDialog;

    private BasePresenter basePresenter;

    private User user;
    private List<Comment> comments;

    private String dateFormat = "EEEE, dd MMM ''yy HH.mm";
    private int userId, newsId, commentId, like = -1;
    private String content;
    private boolean loadingComments = true, settingLike = false, postingComment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        initView();
        setEvent();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        post = findViewById(R.id.post);

        mainLayout = findViewById(R.id.main_layout);
        loadingView = findViewById(R.id.loading_view);

        image = findViewById(R.id.image);
        title = findViewById(R.id.title);
        date = findViewById(R.id.date);

        commentListView = findViewById(R.id.comment_list_view);
        commentListView.setLayoutManager(new LinearLayoutManager(this));
        commentListView.setNestedScrollingEnabled(false);

        post.setColorFilter(getResources().getColor(R.color.white));

        user = Session.with(this).getUser();
        userId = user != null ? user.getId() : -1;

        Bundle extras = getIntent().getExtras();
        newsId = extras.getInt("news_id");

        basePresenter = new BasePresenter(getApplicationContext(), this);
        basePresenter.loadComments(userId, newsId);
    }

    private void setEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    showCommentInput();
                } else {
                    showErrorDialog("Harap login terlebih dahulu");
                }
            }
        });
    }

    private void loadData(News news, List<Comment> comments) {
        getSupportActionBar().setTitle("Komentar (" + comments.size() + ")");
        post.setVisibility(View.VISIBLE);

        Picasso.with(this).load(Utils.with(this).getURLMediaImage(news.getImage(), "news"))
                .placeholder(R.drawable.placeholder)
                .fit()
                .centerCrop()
                .into(image);
        title.setText(news.getTitle());
        date.setText(Utils.with(this).formatDate(news.getDate(), dateFormat) + " WIB");

        commentListAdapter = new CommentListAdapter(user, comments);
        commentListView.setAdapter(commentListAdapter);
    }

    public void setCommentLike(int commentId, int like) {
        this.commentId = commentId;
        this.like = like;

        basePresenter.setCommentLike(commentId, like);
    }

    private void showCommentInput() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
        final AlertDialog dialog = builder.create();
        dialog.setView(dialog.getLayoutInflater().inflate(R.layout.custom_comment_input, null));
        dialog.show();

        final EditText input = dialog.findViewById(R.id.input);
        ImageView send = dialog.findViewById(R.id.send);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().trim().length() > 0) {
                    content = input.getText().toString();
                    dialog.dismiss();

                    postingComment = true;
                    attemptPosting();
                }
            }
        });
    }

    private void attemptPosting() {
        progressDialog = new ProgressDialogCustom(this);
        progressDialog.setMessage("Mengirimkan komentar...");

        if (Internet.isConnected(this)) {
            basePresenter.postComment(newsId, content);
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Tidak ada koneksi internet", Toast.LENGTH_SHORT).show();
        }
    }

    private void showErrorDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CommentActivity.this);
        builder.setTitle("Kesalahan")
                .setMessage(message)
                .setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void addComment() {
        Comment comment = new Comment();
        comment.setId(-1);
        comment.setContent(content);
        comment.setLike(-1);
        comment.setUps(0);
        comment.setDowns(0);
        comment.setCreatedAt(Utils.with(this).getCurrentDate());
        comment.setUser(user);

        comments.add(comment);
        commentListAdapter.notifyItemRangeInserted(comments.size() - 1, 1);

        getSupportActionBar().setTitle("Komentar (" + comments.size() + ")");
        mainLayout.post(new Runnable() {
            @Override
            public void run() {
                mainLayout.fullScroll(View.FOCUS_DOWN);
            }
        });
    }

    @Override
    public void onSuccess(BaseResponse base) {
        if (base instanceof CommentResponse) {
            CommentResponse r = (CommentResponse) base;
            News news = r.getData().getNews();
            comments = r.getData().getComments();

            loadData(news, comments);

            mainLayout.setVisibility(View.VISIBLE);
            loadingView.setVisibility(View.GONE);

            loadingComments = false;
        } else {
            if (settingLike) {
                if (base.isSuccess()) {
                    settingLike = false;
                } else {
                    basePresenter.setCommentLike(commentId, like);
                }
            } else if (postingComment) {
                if (base.isSuccess()) {
                    if (progressDialog != null) {
                        progressDialog.dismiss();
                    }
                    addComment();
                    postingComment = false;
                } else {
                    basePresenter.postComment(newsId, content);
                }
            }
        }
    }

    @Override
    public void onFailure(String message) {
        if (loadingComments) {
            basePresenter.loadComments(userId, newsId);
        } else if (settingLike) {
            basePresenter.setCommentLike(commentId, like);
        } else if (postingComment) {
            basePresenter.postComment(newsId, content);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        basePresenter.disposables.clear();
    }
}