package com.example.acer.appusagetracker.usagetracker.barchart;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.replicator.Replication;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.example.acer.appusagetracker.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import static com.example.acer.appusagetracker.MainActivity.database;
import static com.example.acer.appusagetracker.MainActivity.flag;

public class
BarChartView extends Fragment implements Replication.ChangeListener {
    public static int i = 0;
    public static String TAG = "GrocerySync";
    public static ArrayList<String> ydata = new ArrayList<String>();
    public static ArrayList<Float> xdata = new ArrayList<Float>();
    public static ArrayList<String> myDocId = new ArrayList<String>();
    public static ArrayList<Float> cpyf = new ArrayList<Float>();
    public static PieChart pieChart;

    @Override
    public void onStop() {super.onStop();}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bar_chart, container, false);
    }
    @Override
    public void onPause() {super.onPause();}

    @Override
    public void onViewCreated(View rootView, Bundle savedInstanceState) {
        pieChart = (PieChart) rootView.findViewById(R.id.idPieChart);

        pieChart.setRotationEnabled(true);
        pieChart.setHoleRadius(55f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setTransparentCircleAlpha(0);
        pieChart.setCenterText("APP USAGE");
        pieChart.setCenterTextSize(20);
    }
    public static void addDataSet() {
        float others = 0; int flag2=0;
        ArrayList<PieEntry> xEntrys = new ArrayList<>();
        ArrayList<String> yEntrys = new ArrayList<>();
        for (int i = 0; i < xdata.size(); i++) {
            if (xdata.get(i) < 5.0) {
                others += xdata.get(i);
                xdata.remove(i);
                ydata.remove(i);
            }
        }
        Log.e("test", "size" + xdata.size());
        for (int i = 0; i < ydata.size(); i++) {
            String value;
            value = ydata.get(i);
            for (int j = i + 1; j < ydata.size(); j++) {
                if (value.equals(ydata.get(j))) {
                    ydata.remove(j);
                    xdata.remove(j);
                }
            }
        }
        for (int i = 0; i < xdata.size(); i++) {
            Log.e("test", "percentage" + xdata.get(i));
            Log.e("test", "application name" + ydata.get(i));
            String str = ydata.get(i);
            float x = xdata.get(i);
            if(flag==0) {
                myDocId.add(createDocument(xdata.get(i), ydata.get(i)));
                flag++;
            }
            else
            {
                Log.e("updation","updation");
                for(int j=0;j<myDocId.size();j++) {
                    Document doc = database.getDocument(myDocId.get(j));
                    // We can directly access properties from the document object:
                    String percent=(String)doc.getProperty("percent");
                    String owner = (String) doc.getProperty("Applicationn name");
                    Log.e("String","String obtained"+owner);
                    if(owner.equals(ydata.get(i))){
                        flag2=1;
                        x=x+100;
                        String xs=x+"%";
                        Log.e("for checking percent 1"+xs,"percent 2"+percent);
                        if(!(percent.equals(x+"%")))
                        {
                        Document doc1 = database.getDocument(myDocId.get(j));
                        Map<String, Object> properties1 = new HashMap<String, Object>();
                        properties1.putAll(doc1.getProperties());
                        properties1.put("percent", x + "%");
                        properties1.put("Applicationn name", str);
                        try {
                            doc.putProperties(properties1);
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }
                     }
                     else{
                            continue;
                        }
                    }
                }
                if(flag2==0){
                    Log.e("not matching excitsing","adding"+ydata.get(i));
                    myDocId.add(createDocument(xdata.get(i), ydata.get(i)));

                }
                flag2=0;

            }
            Log.e("test", "index" + i);
            xEntrys.add(new PieEntry(xdata.get(i), ydata.get(i)));


        }

        for (int i = 0; i < ydata.size(); i++) {
            yEntrys.add(ydata.get(i));

        }

        PieDataSet pieDataSet = new PieDataSet(xEntrys, " ");
        pieDataSet.setSliceSpace(4);
        pieDataSet.setValueTextSize(10);

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
        colors.add(Color.CYAN);
        colors.add(Color.BLUE);
        colors.add(Color.MAGENTA);
        colors.add(Color.YELLOW);

        pieDataSet.setColors(colors);
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


    @Override
    public void changed(Replication.ChangeEvent event) {

        Replication replication = event.getSource();
        com.couchbase.lite.util.Log.d(TAG, "Replication : " + replication + " changed.");
        if (!replication.isRunning()) {
            String msg = String.format("Replicator %s not running", replication);
            com.couchbase.lite.util.Log.d(TAG, msg);

        } else {
            int processed = replication.getCompletedChangesCount();
            int total = replication.getChangesCount();
            String msg = String.format("Replicator processed %d / %d", processed, total);
            com.couchbase.lite.util.Log.d(TAG, msg);
        }
    }
    private static String createDocument(float x, String s) {
            Log.e("test", "entered document creation");
            Document document = database.createDocument();
            String documentId = document.getId();
            Log.e("oru valya test", "document id:" + document.getId());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("percent", x + "%");
            map.put("Applicationn name", s);
             try {
                // Save the properties to the document
                document.putProperties(map);
            } catch (CouchbaseLiteException e) {
                Log.e(TAG, "Error putting", e);
            }
            return documentId;

    }

}
