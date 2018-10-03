package com.example.shourav.watchover.Fragment;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.shourav.watchover.Adapter.MemoryAdapter;
import com.example.shourav.watchover.Adapter.RamAdapter;
import com.example.shourav.watchover.Pojo.Memory;
import com.example.shourav.watchover.Pojo.Ram;
import com.example.shourav.watchover.R;
import com.github.mikephil.charting.charts.PieChart;
import com.ram.speed.booster.RAMBooster;
import com.ram.speed.booster.interfaces.CleanListener;
import com.ram.speed.booster.interfaces.ScanListener;
import com.ram.speed.booster.utils.ProcessInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class RamFragment extends Fragment {

    private static String TAG = "booster.test";
    RAMBooster booster;

    public TextView tvRam,tvUsedRam,tvAvailRam;
    public PieChart pieChart;
    public Button btnClear;
    public ListView ram;
    public List<String> process;
    public RecyclerView recyclerView;


    public float tRam,aRam,usedRam;

    public Memory memory;
    public  List<Ram> listProcess = new ArrayList<Ram>();


    public RamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_ram, container, false);

        btnClear = (Button) v.findViewById(R.id.btnClear);

        recyclerView = (RecyclerView) v.findViewById(R.id.processList);
        ram= (ListView) v.findViewById(R.id.ram);


        if (booster==null)
            booster=null;
        booster = new RAMBooster(getContext());
        booster.setDebug(true);
        booster.setScanListener(new ScanListener() {
            @Override
            public void onStarted() {
                Log.d(TAG, "Scan started");
            }

            @Override
            public void onFinished(long availableRam, long totalRam, List<ProcessInfo> appsToClean) {

                aRam = availableRam;
                tRam = totalRam;
                usedRam = totalRam-availableRam;



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<Memory> memoryDataSource = new ArrayList<Memory>();

                        Memory ramInfo = new Memory("Ram Information",String.valueOf(usedRam),String.valueOf(aRam),String.valueOf(tRam));
                        memoryDataSource.add(ramInfo);
                        MemoryAdapter memoryAdapter = new MemoryAdapter(getContext(), memoryDataSource);
                        ram.setAdapter(memoryAdapter);

                    }
                });



                Log.e(TAG,"Ram : "+aRam);

                Log.d(TAG, String.format(Locale.US,
                        "Scan finished, available RAM: %dMB, total RAM: %dMB",
                        availableRam,totalRam));

                for (ProcessInfo info:appsToClean) {

                    Context context = getContext();
                    String packageName = info.getProcessName();
                    PackageManager packageManager = context.getPackageManager();
                    ApplicationInfo applicationInfo = null;
                    try {
                        applicationInfo = packageManager.getApplicationInfo(packageName, 0);
                    } catch (final PackageManager.NameNotFoundException e) {
                    }
                    final String title = (String) ((applicationInfo != null) ? packageManager.getApplicationLabel(applicationInfo) : "???");
                    Drawable appIcon = applicationInfo.loadIcon(packageManager);

                    listProcess.add(new Ram(appIcon,title));
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        RamAdapter recyclerViewAdapter = new RamAdapter(getContext(),(List<Ram>) listProcess);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
                        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                        recyclerView.setLayoutManager(layoutManager);
                        recyclerView.setAdapter(recyclerViewAdapter);
                    }
                });
                booster.startClean();




            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try{
                    booster.setCleanListener(new CleanListener() {
                        @Override
                        public void onStarted() {
                            Log.d(TAG, "Clean started");
                        }


                        @Override
                        public void onFinished(long availableRam, long totalRam) {

                            aRam = availableRam;
                            tRam = totalRam;
                            usedRam = totalRam-availableRam;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    List<Memory> memoryDataSource = new ArrayList<Memory>();

                                    Memory ramInfo = new Memory("Ram Information",String.valueOf(usedRam),String.valueOf(aRam),String.valueOf(tRam));
                                    memoryDataSource.add(ramInfo);
                                    MemoryAdapter memoryAdapter = new MemoryAdapter(getContext(), memoryDataSource);
                                    ram.setAdapter(memoryAdapter);
                                    recyclerView.setAdapter(null);
//call the invalidate()

                                }
                            });


                            Log.d(TAG, String.format(Locale.US,
                                    "Clean finished, available RAM: %dMB, total RAM: %dMB",
                                    availableRam,totalRam));
                            booster = null;

                        }

                    });
                    booster.startScan(true);

                }catch (Exception e)
                {
                    System.out.println("Error " + e.getMessage());
                }
            }
        });


        booster.startScan(true);

        return v;
    }

}
