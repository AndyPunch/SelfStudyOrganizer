package ru.andypunch.ssorganizer;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nononsenseapps.filepicker.AbstractFilePickerFragment;
import com.nononsenseapps.filepicker.FilePickerActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ru.andypunch.ssorganizer.adapters.MyExpandableListAdapter;
import ru.andypunch.ssorganizer.databases.ResourceDb;
import ru.andypunch.ssorganizer.fragments.CommentResourceFragment;
import ru.andypunch.ssorganizer.fragments.CopyPastUrlFragment;
import ru.andypunch.ssorganizer.fragments.DeleteResorceFragment;
import ru.andypunch.ssorganizer.fragments.RenameResourceFragment;
import ru.andypunch.ssorganizer.fragments.RunResourceFragment;
import ru.andypunch.ssorganizer.utils.CustomSnack;

import static ru.andypunch.ssorganizer.StudyArrays.THREE;


public class StudyResourcesActivity extends AppCompatActivity implements View
        .OnClickListener {

    private ExpandableListView expListView;
    private MyExpandableListAdapter expListAdapter;
    private String filename, filenameFull;
    private String fosTitle = "";
    private ResourceDb db;
    private FloatingActionButton actionButton;
    private FloatingActionMenu rightLowerMenu;
    private RelativeLayout lastColored;
    private RelativeLayout expRelLay;
    private Toolbar toolbar;
    private int lastExpandedPosition = -1;
    private Bundle args;
    private String explHeaderPosition;
    private String resourceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_resources_activity);

        //get extras
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("fosTitle")) {
                //get note title
                fosTitle = extras.getString("fosTitle", "");
                MyExpandableListAdapter.stFosTitle = fosTitle;

            }
        }

        db = new ResourceDb(this);
        db.open();
        args = new Bundle();

        //set arrays for expandable listview
        setArrays();

        //set listview, adapter...
        setListSettings();

        //toolbar initialisation
        initToolbar();

        //action button initialisation
        initActionButton();
        setTitle(fosTitle);
    }

    //set arrays for expandable listview
    private void setArrays() {
        StudyArrays studyArrays = new StudyArrays(this);
        studyArrays.setExpLvTitleArray();

        //clean collection
        StudyArrays.resourceData.clear();

        //avoid null
        StudyArrays.prepareExpListData();

        db.getResourceData(fosTitle);
    }

    private void setListSettings() {
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        expListAdapter = new MyExpandableListAdapter(this, StudyArrays
                .titleForExpLv, StudyArrays.resourceData);
        expListView.setAdapter(expListAdapter);
        expListView.setOnGroupClickListener(new ExpandableListView
                .OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                setViewAfterAction();
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView
                .OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                // open group and close others
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    expListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        //handle listview clicks
        expListView.setOnChildClickListener(new ExpandableListView
                .OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition,
                                        long id) {
                explHeaderPosition = String.valueOf(groupPosition);
                expRelLay = (RelativeLayout) v.findViewById(R.id.exp_Rel_Lay);
                TextView tvResourceName = (TextView) v.findViewById(R.id
                        .finish_exp_list_item);
                resourceName = tvResourceName.getText().toString();
                setHighlightSettings();
                return false;
            }
        });

        //handle listview scrloll
        expListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                //activity original appearance
                setViewAfterAction();
                //action button animating
                int fab_initPosY = actionButton.getScrollY();
                if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    actionButton.animate().cancel();
                    actionButton.animate().translationYBy(150);
                } else {
                    actionButton.animate().cancel();
                    actionButton.animate().translationY(fab_initPosY);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    //higlight and actions of toolbar, listviewitem and action button
    private void setHighlightSettings() {
        if (lastColored == null) {
            expRelLay.setBackgroundResource(R.color.primary);
            actionButton.setVisibility(View.INVISIBLE);
            rightLowerMenu.close(true);
            toolbar.setVisibility(View.VISIBLE);
            lastColored = expRelLay;
        } else if (lastColored == expRelLay) {
            expRelLay.setBackgroundResource(R.color.transparent);
            actionButton.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
            lastColored = null;
        } else if (lastColored != expRelLay) {
            lastColored.setBackgroundResource(R.color.transparent);
            expRelLay.setBackgroundResource(R.color.primary);
            lastColored = expRelLay;
        }
    }

    //toolbar initialization
    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_bottom);
        ImageView resourceRun = (ImageView) findViewById(R.id.resource_run);
        ImageView resourceRename = (ImageView) findViewById(R.id.resource_rename);
        ImageView resourceDelete = (ImageView) findViewById(R.id.resource_delete);
        ImageView resourceComment = (ImageView) findViewById(R.id.resource_add_comment);
        resourceRun.setOnClickListener(this);
        resourceRename.setOnClickListener(this);
        resourceDelete.setOnClickListener(this);
        resourceComment.setOnClickListener(this);
        toolbar.setVisibility(View.INVISIBLE);
    }

    //action button initialisation
    private void initActionButton() {
        ImageView icon = new ImageView(this);
        icon.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.ic_add_white_24dp));

        actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setBackgroundDrawable(R.drawable.fab)
                .build();

        //SubActionButtons
        SubActionButton.Builder rLSubBuilder = new SubActionButton.Builder(this)
                .setBackgroundDrawable(ContextCompat.getDrawable(this, R
                        .drawable.fab));
        ImageView iconReadResource = new ImageView(this);
        ImageView iconWatchResource = new ImageView(this);
        ImageView iconListenResource = new ImageView(this);
        ImageView iconInternetResource = new ImageView(this);

        //small icons for SubActionButtons
        iconReadResource.setImageDrawable(ContextCompat.getDrawable(this, R
                .mipmap.ic_action_read));
        iconWatchResource.setImageDrawable(ContextCompat.getDrawable(this, R
                .mipmap.ic_action_watch));
        iconListenResource.setImageDrawable(ContextCompat.getDrawable(this, R
                .mipmap.ic_action_listen));
        iconInternetResource.setImageDrawable(ContextCompat.getDrawable(this,
                R.mipmap.ic_action_internet));

        // Build the menu with default options: light theme, 90 degrees, 72dp
        // radius.
        // Set 4 default SubActionButtons
        rightLowerMenu = new FloatingActionMenu
                .Builder(this)
                .addSubActionView(rLSubBuilder.setContentView
                        (iconReadResource).build())
                .addSubActionView(rLSubBuilder.setContentView
                        (iconWatchResource).build())
                .addSubActionView(rLSubBuilder.setContentView
                        (iconListenResource).build())
                .addSubActionView(rLSubBuilder.setContentView
                        (iconInternetResource).build())
                .attachTo(actionButton)
                .build();

        //id for SubActionButtons
        iconReadResource.setId(R.id.readResource);
        iconWatchResource.setId(R.id.watchResource);
        iconListenResource.setId(R.id.listenResource);
        iconInternetResource.setId(R.id.internetResource);

        iconReadResource.setOnClickListener(this);
        iconWatchResource.setOnClickListener(this);
        iconListenResource.setOnClickListener(this);
        iconInternetResource.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.readResource:
                getFileChooser(0);
                break;

            case R.id.watchResource:
                getFileChooser(1);
                break;

            case R.id.listenResource:
                getFileChooser(2);
                break;

            case R.id.internetResource:
                CopyPastUrlFragment cpFragment = new CopyPastUrlFragment();
                cpFragment.show(getFragmentManager(), "copyPastUrl");
                break;

            case R.id.resource_run:
                RunResourceFragment runResourceFragment = new
                        RunResourceFragment();
                args.putString("explHeaderPosition", explHeaderPosition);
                args.putString("resourceName", resourceName);
                args.putString("fosTitle", fosTitle);
                runResourceFragment.setArguments(args);
                runResourceFragment.show(getFragmentManager(),
                        "RunResourceFragment");
                break;

            case R.id.resource_add_comment:
                CommentResourceFragment commentResourceFragment = new
                        CommentResourceFragment();
                args.putString("explHeaderPosition", explHeaderPosition);
                args.putString("resourceName", resourceName);
                args.putString("fosTitle", fosTitle);
                commentResourceFragment.setArguments(args);
                commentResourceFragment.show(getFragmentManager(),
                        "RenameResourceFragment");
                break;

            case R.id.resource_rename:
                args.putString("resourceName", resourceName);
                RenameResourceFragment renameResourceFragment = new
                        RenameResourceFragment();
                renameResourceFragment.setArguments(args);
                renameResourceFragment.show(getFragmentManager(),
                        "RenameResourceFragment");
                break;

            case R.id.resource_delete:
                DeleteResorceFragment deleteResourceFragment = new
                        DeleteResorceFragment();
                deleteResourceFragment.show(getFragmentManager(),
                        "RenameResourceFragment");
                break;
        }
    }


    //call filepicker
    public void getFileChooser(int REQUEST_CODE) {
        Intent i = new Intent(this, FilePickerActivity.class);
        i.setAction(Intent.ACTION_GET_CONTENT);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, true);
        int mode = AbstractFilePickerFragment.MODE_FILE_AND_DIR;
        i.putExtra(FilePickerActivity.EXTRA_MODE, mode);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(i, REQUEST_CODE);
    }

    //onactivityresult for filepicker
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        String strRequestCode = String.valueOf(requestCode);
        List<String> fullNameList;
        List<String> fullNameListForDb;
        List<String> shortNameListForDb;
        if (resultCode == Activity.RESULT_OK) {
            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE,
                    false))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ClipData clip = data.getClipData();
                    StringBuilder sb = new StringBuilder();
                    if (clip != null) {
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            sb.append(clip.getItemAt(i).getUri().toString());
                            sb.append("\n");
                        }
                    }
                    filenameFull = sb.toString();

                    try {
                        // decode string
                        filenameFull = URLDecoder.decode(filenameFull, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //создаём временный arrayList  в который помещаем строку
                    // разделённую по слову "file"
                    fullNameList = new ArrayList<>(Arrays.asList
                            (filenameFull.split("(?=file)")));
                    fullNameListForDb = new ArrayList<>();
                    shortNameListForDb = new ArrayList<>();
                    //удаляем 1 эллемент массива который был нужен для
                    // инициализации
                    fullNameList = fullNameList.subList(1, fullNameList.size());
                    //добавляем названия ресурсов(без полного пути) в
                    // основной массив с данными
                    for (String s : fullNameList) {
                        filename = s.substring(s.lastIndexOf("/") + 1).trim();
                        filename = filename.replaceAll("'", "`");
                        if (StudyArrays.resourceData.get(strRequestCode).
                                contains(filename)) {
                            final View view = findViewById(R.id
                                    .rellay_post_finish_activity);
                            CustomSnack.setSnack(view, R.string
                                    .name_already_exists).show();
                        } else if (!StudyArrays.resourceData.get
                                (strRequestCode).contains(filename)) {
                            StudyArrays.resourceData.get(strRequestCode).add
                                    (filename);
                            fullNameListForDb.add(s);
                            shortNameListForDb.add(filename);
                        }
                    }

                    db.addResourceData(fosTitle, strRequestCode, fullNameListForDb,
                            shortNameListForDb);
                    expListView.expandGroup(requestCode);
                    restartExpList();

                } else {
                    ArrayList<String> paths = data.getStringArrayListExtra(
                            FilePickerActivity.EXTRA_PATHS);
                    StringBuilder sb = new StringBuilder();

                    if (paths != null) {
                        for (String path : paths) {
                            sb.append(path);
                            sb.append("\n");
                        }
                    }
                    filenameFull = sb.toString();

                    try {
                        filenameFull = URLDecoder.decode(filenameFull, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    fullNameList = new ArrayList<>(Arrays.asList
                            (filenameFull.split("(?=file)")));
                    fullNameListForDb = new ArrayList<>();
                    shortNameListForDb = new ArrayList<>();
                    //удаляем 1 эллемент массива который был нужен для
                    // инициализации
                    fullNameList = fullNameList.subList(1, fullNameList.size());

                    for (String s : fullNameList) {
                        filename = s.substring(s.lastIndexOf("/") + 1).trim();
                        filename = filename.replaceAll("'", "`");
                        if (StudyArrays.resourceData.get(strRequestCode).
                                contains(filename)) {
                            final View view = findViewById(R.id
                                    .rellay_post_finish_activity);
                            CustomSnack.setSnack(view, R.string
                                    .name_already_exists).show();
                        } else if (!StudyArrays.resourceData.get
                                (strRequestCode).contains(filename)) {
                            StudyArrays.resourceData.get(strRequestCode).add
                                    (filename);
                            fullNameListForDb.add(s);
                            shortNameListForDb.add(filename);
                        }
                    }
                    db.addResourceData(fosTitle, strRequestCode, fullNameListForDb,
                            shortNameListForDb);
                    expListView.expandGroup(requestCode);
                    restartExpList();
                }
            else {
                final View view = findViewById(R.id
                        .rellay_post_finish_activity);
                CustomSnack.setSnack(view, R.string.something_went_wrong)
                        .show();
                restartExpList();

            }
        }
    }

    public void addUrl(String link, String urlTitle) {
        if (urlTitle == null) {
            if (StudyArrays.resourceData.get(THREE).
                    contains(link)) {
                final View view = findViewById(R.id
                        .rellay_post_finish_activity);
                CustomSnack.setSnack(view, R.string.name_already_exists).show();

            } else if (!StudyArrays.resourceData.get(THREE).contains(link)) {
                StudyArrays.resourceData.get(THREE).add(link);
                urlTitle = link;
                db.addInternetResourceData(fosTitle, THREE, link, urlTitle);
                expListView.expandGroup(3);
                restartExpList();
            }


        } else {
            if (StudyArrays.resourceData.get(THREE).
                    contains(urlTitle)) {
                final View view = findViewById(R.id
                        .rellay_post_finish_activity);
                CustomSnack.setSnack(view, R.string.name_already_exists).show();

            } else if (!StudyArrays.resourceData.get(THREE).contains(urlTitle)) {
                StudyArrays.resourceData.get(THREE).add(urlTitle);
                db.addInternetResourceData(fosTitle, THREE, link, urlTitle);
                expListView.expandGroup(3);
                restartExpList();
            }


        }
    }

    @Override
    public void onBackPressed() {
        //hide toolbar
        if (toolbar.getVisibility() == View.VISIBLE) {
            setViewAfterAction();
        } else {

            super.onBackPressed();
        }
    }

    //set activity after delete, update...
    private void setViewAfterAction() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            actionButton.setVisibility(View.VISIBLE);
            toolbar.setVisibility(View.INVISIBLE);
            expRelLay.setBackgroundResource(R.color.transparent);
            lastColored = null;
        }
    }

    //rename resource
    public void onRenameResourceClicked(String newResourceName) {
        db.renameResource(fosTitle, resourceName, explHeaderPosition, newResourceName);
        setArrays();
        restartExpList();
        setViewAfterAction();
    }

    //delete resource
    public void onDeleteResourceClicked() {
        db.deleteResource(fosTitle, resourceName, explHeaderPosition);
        setArrays();
        restartExpList();
        setViewAfterAction();
    }

    public void restartExpList() {
        expListAdapter.notifyDataSetChanged();
    }
}
