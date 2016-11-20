package ru.andypunch.ssorganizer.fragments;

import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.andypunch.ssorganizer.R;
import ru.andypunch.ssorganizer.StudyResourcesActivity;
import ru.andypunch.ssorganizer.adapters.CommentAdapter;
import ru.andypunch.ssorganizer.databases.CommentDb;
import ru.andypunch.ssorganizer.utils.CustomSnack;


public class CommentResourceFragment extends DialogFragment implements View
        .OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
    View commentResourceDialog;
    private ListView lvComment;
    private EditText tvComment;
    private Toolbar toolbar;
    private String resourceName;
    private String fosTitle;
    private String explHeaderPosition;
    private CommentDb db;
    private TextView commentText;
    private RelativeLayout rlComment;
    private RelativeLayout lastColored;
    private CommentAdapter commentAdapter;
    private CardView cvSendComment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        commentResourceDialog = inflater.inflate(R.layout.comment_dialog, null);

        //get bundle extras
        getExtras();

        //initialize views
        initView();

        //set listview, adapter...
        setListSettings();
        // createLoader

        //toolbar settings
        initToolbar();

        //make dialog header invisible
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return commentResourceDialog;
    }

    //get bundle extras
    private void getExtras() {
        Bundle extras = getArguments();
        if (extras != null) {
            if (extras.containsKey("explHeaderPosition")) {
                explHeaderPosition = extras.getString("explHeaderPosition", "");
            }
            if (extras.containsKey("resourceName")) {
                resourceName = extras.getString("resourceName", "");
            }
            if (extras.containsKey("fosTitle")) {
                fosTitle = extras.getString("fosTitle", "");
            }
        }
    }

    //initialize views
    private void initView() {
        lvComment = (ListView) commentResourceDialog.findViewById(R.id
                .lvComment);
        toolbar = (Toolbar) commentResourceDialog.findViewById(R.id
                .toolbar_comment);
        tvComment = (EditText) commentResourceDialog.findViewById(R.id
                .commentaryText);
        ImageView sendComment = (ImageView) commentResourceDialog.findViewById(R.id
                .sendCommentaryText);
        TextView customDialogName = (TextView) commentResourceDialog.findViewById(R.id
                .tvCommentTitle);
        ImageView imgEditComment = (ImageView) commentResourceDialog.findViewById(R.id
                .img_comment_edit);
        ImageView imgDeleteComment = (ImageView) commentResourceDialog.findViewById(R.id
                .img_comment_delete);
        cvSendComment = (CardView) commentResourceDialog.findViewById(R.id
                .cardViewSendComment);

        tvComment.setTextColor(ContextCompat.getColor(getActivity(),
                R.color.custom_text_color));
        //set resource name
        customDialogName.setText(resourceName);

        sendComment.setOnClickListener(this);
        imgEditComment.setOnClickListener(this);
        imgDeleteComment.setOnClickListener(this);

        db = new CommentDb(getActivity());
        db.open();
    }

    //set listview, adapter...
    private void setListSettings() {
        String[] from = new String[]{CommentDb.COLUMN_RESOURCE_COMMENT_TIME, CommentDb
                .COLUMN_RESOURCE_COMMENT_BODY};
        int[] to = new int[]{R.id.commentDate, R.id.tvLvCommentBody};

        //create  CursorAdapter
        commentAdapter = new CommentAdapter(getActivity(), R.layout
                .comment_list_item, null, from, to, 0);

        if (lvComment != null) {
            lvComment.setAdapter(commentAdapter);
        }
        getLoaderManager().initLoader(0, null, this);

        //nandle listview clicks
        lvComment.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                commentText = (TextView) view.findViewById(R.id
                        .tvLvCommentBody);
                rlComment = (RelativeLayout) view.findViewById(R.id.rlComment);
                setHighlightSettings();
            }
        });

        //handle listview scroll
        setListOnScrollListener();
    }

    //handle listview scroll
    void setListOnScrollListener() {
        lvComment.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //activity original appearance
                setViewAfterAction();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

            }
        });
    }


    String getCommentText() {
        return commentText.getText().toString();
    }


    //higlight and actions of toolbar, listviewitem and action button
    private void setHighlightSettings() {
        if (lastColored == null) {
            toolbar.setVisibility(View.VISIBLE);
            cvSendComment.setVisibility(View.INVISIBLE);
            rlComment.setBackgroundResource(R.color.primary);
            lastColored = rlComment;
        } else if (lastColored == rlComment) {
            rlComment.setBackgroundResource(R.color.transparent);
            toolbar.setVisibility(View.INVISIBLE);
            cvSendComment.setVisibility(View.VISIBLE);
            lastColored = null;
        } else if (lastColored != rlComment) {
            lastColored.setBackgroundResource(R.color.transparent);
            rlComment.setBackgroundResource(R.color.primary);
            lastColored = rlComment;
        }
    }

    //set activity after delete, update...
    private void setViewAfterAction() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            toolbar.setVisibility(View.INVISIBLE);
            cvSendComment.setVisibility(View.VISIBLE);
            rlComment.setBackgroundResource(R.color.transparent);
            lastColored = null;
        }
    }

    private void initToolbar() {
        toolbar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        // safety check
        if (getDialog() == null)
            return;

        //size of dialog in percentage
        int width = (int) (getResources().getDisplayMetrics().widthPixels *
                0.99);
        int height = (int) (getResources().getDisplayMetrics().heightPixels *
                0.90);
        getDialog().getWindow().setLayout(width, height);

        onBackPressed();
    }

    //handle back button
    private void onBackPressed() {
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(android.content.DialogInterface dialog, int
                    keyCode, android.view.KeyEvent event) {
                // && event.getAction() == KeyEvent.ACTION_UP prevent
                // double call onkeylistener
                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK && event
                        .getAction() == KeyEvent.ACTION_UP)) {
                    if (toolbar.getVisibility() == View.VISIBLE) {
                        setViewAfterAction();

                    } else if (toolbar.getVisibility() == View.INVISIBLE) {
                        getDialog().cancel();
                    }
                    return true;
                } else
                    return false; // pass on to be processed as normal
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //send comment
            case R.id.sendCommentaryText:
                long millisDate = System.currentTimeMillis();
                String commentText = tvComment.getText().toString().trim();
                if (!commentText.isEmpty()) {
                    db.addComment(fosTitle, resourceName, explHeaderPosition, millisDate,
                            commentText);
                    getLoaderManager().restartLoader(0, null, this);
                    tvComment.setText("");
                    ((StudyResourcesActivity) getActivity()).restartExpList();
                } else {
                    final View snView = getDialog().findViewById(R.id.
                            RelLayoutComment);
                    CustomSnack.setSnack(snView, R.string.enter_text).show();
                }
                break;

            //edit comment
            case R.id.img_comment_edit:
                EditCommentFragment editComFragment = new
                        EditCommentFragment();
                Bundle args = new Bundle();
                String commentBody = getCommentText();
                args.putString("commentText", commentBody);
                editComFragment.setArguments(args);
                editComFragment.setTargetFragment
                        (CommentResourceFragment.this, 12346);
                editComFragment.show(getActivity()
                        .getFragmentManager(), "editComFragment");
                break;

            //delete comment
            case R.id.img_comment_delete:
                DeleteCommentFragment delComFragment = new
                        DeleteCommentFragment();
                delComFragment.setTargetFragment
                        (CommentResourceFragment.this, 12345);
                delComFragment.show(getActivity()
                        .getFragmentManager(), "delComFragment");
                break;
        }
    }

    //edit comment
    public void onCommentEditClicked(String editedCommentText) {
        String commentOldTex = commentText.getText().toString();
        db.editResourceComment(fosTitle, resourceName, explHeaderPosition, commentOldTex,
                editedCommentText);
        getLoaderManager().restartLoader(0, null, this);
        setViewAfterAction();
    }

    //delete comment
    public void onCommentDeleteClicked() {
        String commentBody = commentText.getText().toString();
        db.deleteResourceComment(fosTitle, resourceName, explHeaderPosition, commentBody);
        getLoaderManager().restartLoader(0, null, this);
        setViewAfterAction();
        ((StudyResourcesActivity) getActivity()).restartExpList();
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new CommentLoader(getActivity(), db, fosTitle, resourceName, explHeaderPosition);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        commentAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        commentAdapter.swapCursor(null);
    }

    // inner class to manage loader
    static class CommentLoader extends CursorLoader {
        CommentDb db;
        String fosTitle, resourceName, explHeaderPosition;

        public CommentLoader(Context context, CommentDb db, String fosTitle, String resourceName,
                             String explHeaderPosition) {
            super(context);
            this.db = db;
            this.fosTitle = fosTitle;
            this.resourceName = resourceName;
            this.explHeaderPosition = explHeaderPosition;
        }

        @Override
        public Cursor loadInBackground() {
            //get data for listview
            Cursor cursor = db.getCommentData(fosTitle, resourceName, explHeaderPosition);
            return cursor;
        }
    }
}


