package com.example.homework6_1_2;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.math.BigDecimal;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    private ArrayList<Integer> deletedItems;

    private SharedPreferences sharedPref;
    private static final String TEXT = "text";
    List<Map<String, String>> values = new ArrayList<>();
    SwipeRefreshLayout swipeLayout;
    SimpleAdapter listContentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView list = findViewById(R.id.list);

        sharedPref = getSharedPreferences("text", MODE_PRIVATE);

        if (!(sharedPref.contains(TEXT))) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(TEXT, getString(R.string.large_text));
            editor.apply();

        }

        prepareContent();

        listContentAdapter = createAdapter(values);

        list.setAdapter(listContentAdapter);

        AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                deletedItems.add(position);

                values.remove(position);
                SimpleAdapter adapter = (SimpleAdapter) parent.getAdapter();
                adapter.notifyDataSetChanged();
            }
        };

        list.setOnItemClickListener(listener);

        try{
            deletedItems = savedInstanceState.getIntegerArrayList("deletedItems");
            for (Integer pos: deletedItems){
                int posInt = pos.intValue();
                values.remove(posInt);
                listContentAdapter.notifyDataSetChanged();
            }
        } catch (NullPointerException e){
            deletedItems = new ArrayList<Integer>();
        }



        swipeLayout=findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                prepareContent();
                listContentAdapter.notifyDataSetChanged();

                swipeLayout.setRefreshing(false);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putIntegerArrayList("deletedItems", deletedItems);
    }

    @NonNull
    private SimpleAdapter createAdapter(List<Map<String, String>> values) {
        return new SimpleAdapter(this,
                values,
                R.layout.list_item,
                new String[]{"name","text"},
                new int[]{R.id.name,R.id.text});
    }

    private void prepareContent(){
        values.clear();
        String[] arrayContent = sharedPref.getString(TEXT, "").split("\n\n");
        for (int i = 0; i < arrayContent.length; i++){
            Map<String, String> value = new HashMap<>();
            value.put("name", String.valueOf(arrayContent[i].length()));
            value.put("text", arrayContent[i]);
            values.add(value);
        }
    }


}
