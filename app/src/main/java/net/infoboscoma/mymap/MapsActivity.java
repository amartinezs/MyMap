package net.infoboscoma.mymap;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ToggleButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements AdapterView.OnItemClickListener {
    private static final LatLng INS_BOSC_DE_LA_COMA = new LatLng(42.1727,2.47631);
    private static final String A = "a";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    //private ToggleButton btnAnimacio;
    private Button btn_Buscar;
    private EditText editText_Ciutat;
    public static ArrayList<LocationInfo> locateData;
    //private Spinner changeMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        locateData = new ArrayList<LocationInfo>();
        //btnAnimacio = (ToggleButton) findViewById(R.id.btnAnimacio);

        //changeMap = (Spinner) findViewById(R.id.spinner_Map);

        //changeMap.setOnItemClickListener(this);

        btn_Buscar = (Button) findViewById(R.id.btn_buscar);
        editText_Ciutat = (EditText) findViewById(R.id.editText_Ciutat);


        btn_Buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                new Downloader_Thread().execute(editText_Ciutat.getText().toString());
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            /*if (mMap != null) {
                //setUpMap();
            }*/
        }
    }


    private void setupGUI(){
        //Spinner cmTipusMapa = (Spinner) findViewById(R.id.spinner_Map);
        //cmTipusMapa.setOnItemClickListener(this);

        //btnCentrar = (Button) findViewById(R.id.btn_centrar);
        //btnAnimacio = (ToggleButton) findViewById(R.id.btnAnimacio);
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.setMyLocationEnabled(true);

        mMap.addMarker(new MarkerOptions()
                        .position(INS_BOSC_DE_LA_COMA)
                        .title("Ins Bosc de la coma")
                        .snippet("Estudis: Eso, Batxillerat, Cicles Formatius i CAS")
        );
    }

    /*private void clickCentrar(){
        if(btnAnimacio.isChecked()){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(INS_BOSC_DE_LA_COMA,15),2000,null);
        } else{
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(INS_BOSC_DE_LA_COMA,15));
        }
    }*/

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       // String tipus = (String) parent.getItemAtPosition(position);

        if(position == 0){
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else if(position == 1){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }
        else if (position == 2){
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (position == 3){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }
    }

    /**
     * No implementada
     * */
    private PolylineOptions polygon(){

        PolylineOptions rectOptions =
                new PolylineOptions()
                .add(new LatLng(42.1627, 2.46631))
                .add(new LatLng(42.1627, 2.46631))
                .add(new LatLng(42.1627, 2.46631))
                .add(new LatLng(42.1627, 2.46631))
                .add(new LatLng(42.1627, 2.46631));

        return rectOptions;
    }

    private void makeMark(LatLng pos, String titol, String desc){

        mMap.addMarker(new MarkerOptions()
                        .position(pos)
                        .title(titol)
                        .snippet(desc)
        );



    }


    private void refreshMap_PrinceOfBelair(){
        mMap.clear();
        if(locateData != null)
        {
            for(int i = 0; i < locateData.size(); i++){
                LocationInfo li = locateData.get(i);
                makeMark(new LatLng(li.getLatitude(), li.getLongitude()), li.getName(), li.getCity());
            }
        }


    }


    class Downloader_Thread extends AsyncTask<String,Void, ArrayList<LocationInfo>> {


        private ArrayList<LocationInfo> locationData;

        private final String URL_DATA = "http://www.infobosccoma.net/pmdm/pois.php";

        @Override
        protected ArrayList<LocationInfo> doInBackground(String... params) {
            locationData = new ArrayList<LocationInfo>();

            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httppostreq = new HttpPost(URL_DATA);
            HttpResponse httpresponse = null;

            try {
                LocationInfo li = new LocationInfo();
                li.setCity(params[0]);
                httppostreq.setEntity(li);
                httpresponse = httpclient.execute(httppostreq);
                String responseText = EntityUtils.toString(httpresponse.getEntity());
                locationData = makeJSON_Derulo(responseText);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return locationData;
        }


        public ArrayList<LocationInfo> makeJSON_Derulo(String responseText){
            Gson converter_wiggle = new Gson();

            return converter_wiggle.fromJson(responseText, new TypeToken<ArrayList<LocationInfo>>(){}.getType());
        }

        @Override
        protected void onPostExecute(ArrayList<LocationInfo> locations){
            locateData = locations;
            refreshMap_PrinceOfBelair();
        }




    }






}
