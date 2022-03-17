package com.execmodule.trustchain_voice_search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.speech.RecognizerIntent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

    static final int check = 1111;
    private ListView listview;
    private Context context;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    private View.OnClickListener buttOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak up!");
            startActivityForResult(i, check);

        }
    };

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case check: {
                if (resultCode == RESULT_OK && null != data) {
                    final ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.i("personal", TextUtils.join(", ", result));
                    listview.setAdapter(new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, result));
                    final JSONObject[] json = new JSONObject[1];
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                json[0] = getJSONObjectFromURL("https://api.duckduckgo.com/?q=" + "some text" + "&format=json");
                                JSONArray relatedTopics = json[0].getJSONArray("RelatedTopics");
                                ArrayList<String> listdata = new ArrayList<String>();
                                if (relatedTopics != null) {
                                    for (int i=0;i<relatedTopics.length();i++){
                                        JSONObject jsonObject = relatedTopics.getJSONObject(i);
                                        if(jsonObject.has("Text")) {
                                            Log.i("jsonObject", String.valueOf(jsonObject));
                                            listdata.add(jsonObject.getString("Text"));
                                        }
                                    }
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            listview.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, listdata));
                                        }
                                    });
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                }
                break;
            }

        }

    }

    @SuppressLint({"ResourceType", "SetTextI18n"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RelativeLayout mylayout = new RelativeLayout(container.getContext());
        context = container.getContext();

        EditText mytext = new EditText(container.getContext());
        mytext.setId(4);

        mytext.setEms(45);
        mytext.setInputType(R.id.text);
        mytext.setHint("Record to perform query");
        mytext.setTextSize(20);
        Test tmpObj = new Test("Record");
        mylayout.addView(mytext, 0);

        Button mybutton = new Button(container.getContext());
        mybutton.setText(tmpObj.getString());
        mybutton.setId(0);
        mybutton.setOnClickListener(buttOnClickListener);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, mytext.getId());
        mylayout.addView(mybutton, lp);

        listview = new ListView(container.getContext());
        listview.setId(1);
        String[] stringArray = new String[]{"Search Results will appear here"};
        listview.setAdapter(new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, stringArray));
        RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp2.addRule(RelativeLayout.BELOW, mytext.getId());
        mylayout.addView(listview, lp2);

        View view = (View) mylayout;
        return view;
    }
}
