package com.example.shourav.watchover.Activity;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.shourav.watchover.Dialogs.AddItemsDialog;
import com.example.shourav.watchover.Dialogs.ConfirmDeleteDialog;
import com.example.shourav.watchover.Dialogs.NewFolderDialog;
import com.example.shourav.watchover.Dialogs.NewTextFileDialog;
import com.example.shourav.watchover.Dialogs.RenameDialog;
import com.example.shourav.watchover.Dialogs.UpdateItemDialog;
import com.example.shourav.watchover.Adapter.FolderAdapter;
import com.example.shourav.watchover.Fragment.BatteryFragment;
import com.example.shourav.watchover.Fragment.MemoryFragment;
import com.example.shourav.watchover.Fragment.RamFragment;
import com.example.shourav.watchover.R;
import com.example.shourav.watchover.SnackberView;
import com.example.shourav.watchover.UserPreferences;
import com.ram.speed.booster.RAMBooster;
import com.snatik.storage.Storage;
import com.snatik.storage.helpers.OrderType;
import com.snatik.storage.helpers.SizeUnit;

import java.io.File;
import java.util.Collections;
import java.util.List;

import static com.example.shourav.watchover.SnackberView.fileExt;


public class MainActivity extends AppCompatActivity implements
        FolderAdapter.OnFileItemListener,
        AddItemsDialog.DialogListener,
        UpdateItemDialog.DialogListener,
        NewFolderDialog.DialogListener,
        NewTextFileDialog.DialogListener,
        ConfirmDeleteDialog.ConfirmListener,
        RenameDialog.DialogListener,
        NavigationView.OnNavigationItemSelectedListener{

    private static final int PERMISSION_REQUEST_CODE = 1000;
    private RecyclerView mRecyclerView;
    private FolderAdapter mFilesAdapter;
    private Storage mStorage;
    private TextView mPathView;
    private TextView mMovingText;
    private Button btnNewFile;
    private FloatingActionButton newFile;
    private boolean mCopy;
    private View mMovingLayout;
    private int mTreeSteps = 0;
    private final static String IVX = "abcdefghijklmnop";
    private final static String SECRET_KEY = "secret1234567890";
    private final static byte[] SALT = "0000111100001111".getBytes();
    private String mMovingPath;
    private boolean mInternal = false;

    private RAMBooster booster;
    private static String TAG = "booster.test";


    private UserPreferences userPreferences;

    private FrameLayout frameLayout;
    private ConstraintLayout constraintLayout;
    private RelativeLayout relativeLayout;

    private boolean isSwitched = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mStorage = new Storage(getApplicationContext());

        userPreferences = new UserPreferences(this);
        booster = new RAMBooster(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        frameLayout = (FrameLayout) findViewById(R.id.fragment_container);
        constraintLayout = (ConstraintLayout) findViewById(R.id.fileList);
        relativeLayout = (RelativeLayout) findViewById(R.id.rl);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mPathView = (TextView) findViewById(R.id.path);
        mMovingLayout = findViewById(R.id.moving_layout);
        mMovingText = (TextView) mMovingLayout.findViewById(R.id.moving_text);

        mMovingLayout.findViewById(R.id.accept_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMovingLayout.setVisibility(View.GONE);
                if (mMovingPath != null) {

                    if (!mCopy) {
                        String toPath = getCurrentPath() + File.separator + mStorage.getFile(mMovingPath).getName();
                        if (!mMovingPath.equals(toPath)) {
                            mStorage.move(mMovingPath, toPath);
                            SnackberView.showSnackbar("Moved", mRecyclerView);
                            showFiles(getCurrentPath());
                        } else {
                            SnackberView.showSnackbar("The file is already here", mRecyclerView);
                        }
                    } else {
                        String toPath = getCurrentPath() + File.separator + "copy " + mStorage.getFile(mMovingPath)
                                .getName();
                        mStorage.copy(mMovingPath, toPath);
                        SnackberView.showSnackbar("Copied", mRecyclerView);
                        showFiles(getCurrentPath());
                    }
                    mMovingPath = null;
                }
            }
        });

        mMovingLayout.findViewById(R.id.decline_move).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMovingLayout.setVisibility(View.GONE);
                mMovingPath = null;
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mFilesAdapter = new FolderAdapter(getApplicationContext());
        mFilesAdapter.setListener(this);
        mRecyclerView.setAdapter(mFilesAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        btnNewFile =(Button) findViewById(R.id.btnNewfile);
        btnNewFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItemsDialog.newInstance().show(getFragmentManager(), "add_items");
            }
        });

        mPathView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPathMenu();
            }
        });

        // load files
        showFiles(mStorage.getExternalStorageDirectory());

        checkPermission();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        MenuItem viewChange =menu.findItem(R.id.switchView);

        if (isSwitched)
        {
            viewChange.setTitle("Grid View");
        }else {
            viewChange.setTitle("List View");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.newFile:
                AddItemsDialog.newInstance().show(getFragmentManager(), "add_items");
                break;
            case R.id.itemHome:
                frameLayout.setVisibility(View.GONE);
                constraintLayout.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.switchView:
                supportInvalidateOptionsMenu();
                isSwitched = mFilesAdapter.toggleItemViewType();
                mRecyclerView.setLayoutManager(isSwitched ? new LinearLayoutManager(this) : new GridLayoutManager(this, 4));
                mFilesAdapter.notifyDataSetChanged();
                break;
        }

        return super.onOptionsItemSelected(item);

    }
    private void showPathMenu() {
        PopupMenu popupmenu = new PopupMenu(this, mPathView);
        MenuInflater inflater = popupmenu.getMenuInflater();
        inflater.inflate(R.menu.path_menu, popupmenu.getMenu());

        popupmenu.getMenu().findItem(R.id.go_internal).setVisible(!mInternal);
        popupmenu.getMenu().findItem(R.id.go_external).setVisible(mInternal);

        popupmenu.show();

        popupmenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.go_up:
                        String previousPath = getPreviousPath();
                        mTreeSteps = 0;
                        showFiles(previousPath);
                        break;
                    case R.id.go_internal:
                        showFiles(mStorage.getInternalFilesDirectory());
                        mInternal = true;
                        break;
                    case R.id.go_external:
                        showFiles(mStorage.getExternalStorageDirectory());
                        mInternal = false;
                        break;
                }
                return true;
            }
        });
    }

    private void showFiles(String path) {
        mPathView.setText(path);
        List<File> files = mStorage.getFiles(path);
        if (files != null) {
            Collections.sort(files, OrderType.NAME.getComparator());
        }
        mFilesAdapter.setFiles(files);
        mFilesAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(File file) {
        if (file.isDirectory()) {
            mTreeSteps++;
            String path = file.getAbsolutePath();
            showFiles(path);
        } else {

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String mimeType =  MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExt(file.getAbsolutePath()));
                Uri apkURI = FileProvider.getUriForFile(
                        this,
                        getApplicationContext()
                                .getPackageName() + ".provider", file);
                intent.setDataAndType(apkURI, mimeType);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                if (mStorage.getSize(file, SizeUnit.KB) > 500) {
                    SnackberView.showSnackbar("The file is too big for preview", mRecyclerView);
                    return;
                }
                Intent intent = new Intent(this, ViewTextActivity.class);
                intent.putExtra(ViewTextActivity.EXTRA_FILE_NAME, file.getName());
                intent.putExtra(ViewTextActivity.EXTRA_FILE_PATH, file.getAbsolutePath());
                startActivity(intent);
            }

        }
    }

    @Override
    public void onLongClick(File file) {
        UpdateItemDialog.newInstance(file.getAbsolutePath()).show(getFragmentManager(), "update_item");
    }

    @Override
    public void onBackPressed() {
        if (mTreeSteps > 0) {
            String path = getPreviousPath();
            mTreeSteps--;
            showFiles(path);
            return;
        }
        super.onBackPressed();
    }

    private String getCurrentPath() {
        return mPathView.getText().toString();
    }

    private String getPreviousPath() {
        String path = getCurrentPath();
        int lastIndexOf = path.lastIndexOf(File.separator);
        if (lastIndexOf < 0) {
            SnackberView.showSnackbar("Can't go anymore", mRecyclerView);
            return getCurrentPath();
        }
        return path.substring(0, lastIndexOf);
    }

    @Override
    public void onOptionClick(int which, String path) {
        switch (which) {
            case R.id.new_file:
                NewTextFileDialog.newInstance().show(getFragmentManager(), "new_file_dialog");
                break;
            case R.id.new_folder:
                NewFolderDialog.newInstance().show(getFragmentManager(), "new_folder_dialog");
                break;
            case R.id.delete:
                ConfirmDeleteDialog.newInstance(path).show(getFragmentManager(), "confirm_delete");
                break;
            case R.id.rename:
                RenameDialog.newInstance(path).show(getFragmentManager(), "rename");
                break;
            case R.id.move:
                mMovingText.setText(getString(R.string.moving_file, mStorage.getFile(path).getName()));
                mMovingPath = path;
                mCopy = false;
                mMovingLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.copy:
                mMovingText.setText(getString(R.string.copy_file, mStorage.getFile(path).getName()));
                mMovingPath = path;
                mCopy = true;
                mMovingLayout.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onNewFolder(String name) {
        String currentPath = getCurrentPath();
        String folderPath = currentPath + File.separator + name;
        boolean created = mStorage.createDirectory(folderPath);
        if (created) {
            showFiles(currentPath);
            SnackberView.showSnackbar("New folder created: " + name, mRecyclerView);
        } else {
            SnackberView.showSnackbar("Failed create folder: " + name, mRecyclerView);
        }
    }

    @Override
    public void onNewFile(String name, String content) {
        String currentPath = getCurrentPath();
        String folderPath = currentPath + File.separator + name;
        mStorage.createFile(folderPath, content);
        showFiles(currentPath);
        SnackberView.showSnackbar("New file created: " + name, mRecyclerView);
    }

    @Override
    public void onConfirmDelete(String path) {
        if (mStorage.getFile(path).isDirectory()) {
            mStorage.deleteDirectory(path);
            SnackberView.showSnackbar("Folder was deleted", mRecyclerView);
        } else {
            mStorage.deleteFile(path);
            SnackberView.showSnackbar("File was deleted", mRecyclerView);
        }
        showFiles(getCurrentPath());
    }

    @Override
    public void onRename(String fromPath, String toPath) {
        mStorage.rename(fromPath, toPath);
        showFiles(getCurrentPath());
        SnackberView.showSnackbar("Renamed", mRecyclerView);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            showFiles(mStorage.getExternalStorageDirectory());
        } else {
            finish();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        Fragment fragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_memory) {
            /*Intent intent = new Intent(getApplicationContext(),MemoryActivity.class);
            startActivity(intent);*/
            constraintLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            fragment = new MemoryFragment();

        } else if (id == R.id.nav_ram) {
            /*Intent intent = new Intent(getApplicationContext(),RamActivity.class);
            startActivity(intent);*/
            constraintLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            fragment = new RamFragment();

        }else if(id == R.id.nav_battery)
        {
            constraintLayout.setVisibility(View.GONE);
            relativeLayout.setVisibility(View.GONE);
            frameLayout.setVisibility(View.VISIBLE);

            fragment = new BatteryFragment();
        }
        else if (id == R.id.nav_junk) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        if(fragment !=null)
        {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction ft = fragmentManager.beginTransaction();

            ft.replace(R.id.fragment_container,fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}