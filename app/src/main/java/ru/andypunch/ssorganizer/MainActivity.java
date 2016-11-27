package ru.andypunch.ssorganizer;

import android.app.FragmentManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import ru.andypunch.ssorganizer.adapters.MySimpleCursorAdapter;
import ru.andypunch.ssorganizer.databases.Db;
import ru.andypunch.ssorganizer.databases.MainDb;
import ru.andypunch.ssorganizer.fragments.AddFieldOfStudyFragment;
import ru.andypunch.ssorganizer.settings.About;


public class MainActivity extends AppCompatActivity implements LoaderManager
        .LoaderCallbacks<Cursor> {
    private FloatingActionButton fab;
    private MainDb db;
    private MySimpleCursorAdapter scAdapter;
    private ListView lvMain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //action button
        fab = (FloatingActionButton) findViewById(R.id
                .fab_main);


        db = new MainDb(this);
        db.open();

        String[] from = new String[]{Db.COLUMN_STUDY_FIELD_TITLE, Db
                .COLUMN_STUDY_FIELD_DATE};
        int[] to = new int[]{R.id.studyFieldTitle, R.id.startStudyField_date};

        //create  CursorAdapter
        scAdapter = new MySimpleCursorAdapter(this, R.layout
                .study_field_list_item, null, from, to, 0);

        lvMain = (ListView) findViewById(R.id.lvMain);
        if (lvMain != null) {
            lvMain.setAdapter(scAdapter);
        }

        // createLoader
        getLoaderManager().initLoader(0, null, this);

        //handle listview click
        setListViewOnClick();

        //handle scroll of listview
        setListOnScrollListener();

        //initialize top toolbar
        initTopToolbar();
    }

    //open dialog on action_button click
    public void onAddStudyFieldClick(View v) {
        FragmentManager manager = getFragmentManager();
        AddFieldOfStudyFragment addFieldOfStudy = new AddFieldOfStudyFragment();
        addFieldOfStudy.show(manager, "addStudyFieldFragment");
    }

    //handle listview click
    private void setListViewOnClick() {
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                TextView studyFieldTitle = (TextView) view.findViewById(R.id
                        .studyFieldTitle);
                //field of study title
                String fosTitle = studyFieldTitle.getText().toString();
                Intent intent = new Intent(MainActivity.this, StudyResourcesActivity.class);
                Bundle args = new Bundle();
                args.putString("fosTitle", fosTitle);
                intent.putExtras(args);
                startActivity(intent);
            }
        });
    }

    //handle scroll of listview
    private void setListOnScrollListener() {
        lvMain.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //action button animating
                int fab_initPosY = fab.getScrollY();
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    fab.animate().cancel();
                    fab.animate().translationYBy(150);
                } else {
                    fab.animate().cancel();
                    fab.animate().translationY(fab_initPosY);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    //initialize top toolbar
    private void initTopToolbar() {
        Toolbar topToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(topToolbar);
        if (topToolbar != null) {
            topToolbar.setBackgroundColor(ContextCompat.getColor(this, R.color
                    .custom_action_bar_color));
        }
    }

    //add to database fieldOfStudy name and date
    public void addStudyField(String fielsOfStudyName) {
        db.addMainTitleRec(fielsOfStudyName);
        restartLoader();
        getLoaderManager().restartLoader(0, null, this);
        //scroll listview to the last item
        scrollMyListViewToBottom();
    }

    //scroll listview to the last item
    private void scrollMyListViewToBottom() {
        lvMain.post(new Runnable() {
            @Override
            public void run() {
                lvMain.smoothScrollToPosition(scAdapter.getCount() - 1);
            }
        });
    }

    //if listview empty
    @Override
    public void onContentChanged() {
        super.onContentChanged();
        CardView empty = (CardView) findViewById(R.id.cardNoFosNotif);
        ListView list = (ListView) findViewById(R.id.lvMain);
        list.setEmptyView(empty);
    }

    //update field of study name
    public void onUpdateClicked(String oldTitle, String newTitle) {
        db.updateFOS(oldTitle, newTitle);
        restartLoader();
    }

    //delete field of study
    public void onDeleteClicked(String fosTitle) {
        db.delFieldOfStudy(fosTitle);
        restartLoader();
    }

    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {

            case R.id.action_about:
                intent = new Intent(this, About.class);
                startActivity(intent);
                return true;
           /* case R.id.action_settings:
                intent = new Intent(this, SettingsPrefActivity.class);
                startActivity(intent);
                return true;*/
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bndl) {
        return new MyCursorLoader(this, db);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        scAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        scAdapter.swapCursor(null);
    }

    // inner class to manage loader
    static class MyCursorLoader extends CursorLoader {
        MainDb db;

        public MyCursorLoader(Context context, MainDb db) {
            super(context);
            this.db = db;
        }

        @Override
        public Cursor loadInBackground() {
            //get data for main activity listview
            return db.getTitleData();
        }
    }
}
