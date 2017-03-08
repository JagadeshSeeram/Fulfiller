package com.biglynx.fulfiller.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.adapter.BroadCast_Ful_Adapter;
import com.biglynx.fulfiller.adapter.Broadcast_Adapter;
import com.biglynx.fulfiller.adapter.RecentSearch_Adapter;
import com.biglynx.fulfiller.app.MyApplication;
import com.biglynx.fulfiller.database.DBHelper;
import com.biglynx.fulfiller.models.BroadCast;
import com.biglynx.fulfiller.models.FulfillersDTO;
import com.biglynx.fulfiller.models.RecentSearch;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.services.PlaceJSONParser;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BroadCastFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnCameraIdleListener, PlaceSelectionListener, View.OnClickListener, GoogleMap.OnMarkerClickListener,
        AdapterView.OnItemClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    int currentDistance = 5;
    int strokeColor = 0xff88C0D3; //red outline
    int shadeColor = 0x9988C0D3; //opaque red fill
    private Circle mCircle;
    private LatLng centerLatLng;
    private boolean firstTime;
    private FullFillerApiWrapper apiWrapper;
    FrameLayout maps_view;
    LinearLayout miles_LI, viewpager_LI, pager_indicator, bac_dim_layout, listview_LI, recent_search_LI;
    TextView current_miles_tv, fiften_miles_tv, ten_miles_tv, five_miles_tv, two_miles_tv, companyname_tv, pickup_loc_tv, locationtype_tv, current_loc_tv,title_tv;
    ImageView current_location_tv, companylogo_imv;
    PlaceAutocompleteFragment search_ev;
    boolean search_loc = false, locaton_show = false;
    private static final int PERMISSION_REQUEST_CODE_LOCATION = 1;
    Marker previousMarker;
    HashMap<String, Integer> markervalues = new HashMap<>();

    ArrayList<Marker> shownMarkersList = new ArrayList<Marker>();
    private ViewPager fulfiller_viewPager;
    BroadCast_Ful_Adapter broadCastFulAdapter;
    private int dotsCount;
    private ImageView[] dots;

    String usermarkerId = "null";
    ListView recentlist_lv, listview_lv;
    DBHelper dbHelper;
    RecentSearch_Adapter recentSearchAdapter;
    AutoCompleteTextView atvPlaces;
    TextView autocomplte_places;
    FrameLayout searchbar_FL;
    PlacesTask placesTask;
    ParserTask parserTask;
    List<HashMap<String, String>> googlePlacesresult;
    private ImageView search_back, cancel_button, search_imv, maps_icon, list_icon, refresh_iv;
    ArrayList<BroadCast> broadCastList;
    private String currentAddress;
    private Broadcast_Adapter broadcastAdapter;
    private GeocodeAsyncTask asyncTask;
    private Animation animation_slide_down;
    private Animation animation_slide_up;
    private View v;
    private CircleIndicator circularIndicator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
//        View v = inflater.inflate(R.layout.broadcast, container, false);
        try {
            v = inflater.inflate(R.layout.broadcast, container, false);
        } catch (InflateException e) {
//         map is already there, just return view as it is
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        initViews(v);
        animation_slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slid_down);
        animation_slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slid_up);

        animation_slide_down.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                viewpager_LI.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.googlemaps);
        mapFragment.getMapAsync(this);


        apiWrapper = new FullFillerApiWrapper();
        if (Common.isNetworkAvailable(MyApplication.getInstance())) {
            callService();
        } else
            AppUtil.toast(getActivity(), "Network Disconnected. Please check...");

        return v;
    }

    private void initViews(View v) {
        googlePlacesresult = new ArrayList<>();
        apiWrapper = new FullFillerApiWrapper();
        Log.d("onCreate", "onCreate()");
        dbHelper = new DBHelper(getActivity());
        title_tv = (TextView) v.findViewById(R.id.companyname_tv);
        title_tv.setVisibility(View.VISIBLE);
        title_tv.setText("Broadcast");
        search_ev = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.search_place_ev);
        search_ev.getView().setVisibility(View.GONE);

        search_ev.setOnPlaceSelectedListener(this);
        listview_lv = (ListView) v.findViewById(R.id.listview_lv);

        circularIndicator = (CircleIndicator) v.findViewById(R.id.indicator);

        maps_view = (FrameLayout) v.findViewById(R.id.maps_view);
        listview_LI = (LinearLayout) v.findViewById(R.id.listview_LI);
        recent_search_LI = (LinearLayout) v.findViewById(R.id.recent_search_LI);
        refresh_iv = (ImageView) v.findViewById(R.id.refresh_iv);

        current_miles_tv = (TextView) v.findViewById(R.id.current_miles_tv);
        fiften_miles_tv = (TextView) v.findViewById(R.id.fiften_miles_tv);
        ten_miles_tv = (TextView) v.findViewById(R.id.ten_miles_tv);
        five_miles_tv = (TextView) v.findViewById(R.id.five_miles_tv);
        two_miles_tv = (TextView) v.findViewById(R.id.two_miles_tv);
        current_location_tv = (ImageView) v.findViewById(R.id.current_location_tv);
        miles_LI = (LinearLayout) v.findViewById(R.id.miles_LI);
        recentlist_lv = (ListView) v.findViewById(R.id.recentlist_lv);
        bac_dim_layout = (LinearLayout) v.findViewById(R.id.bac_dim_layout);

        current_loc_tv = (TextView) v.findViewById(R.id.current_loc_tv);

        search_back = (ImageView) v.findViewById(R.id.search_back);
        cancel_button = (ImageView) v.findViewById(R.id.cancel_button);
        maps_icon = (ImageView) v.findViewById(R.id.icon_back);
        list_icon = (ImageView) v.findViewById(R.id.listview_imv);
        maps_icon.setVisibility(View.VISIBLE);
        list_icon.setVisibility(View.VISIBLE);
        //fulfillments viewpager items
        fulfiller_viewPager = (ViewPager) v.findViewById(R.id.fulfiller_vp);
        viewpager_LI = (LinearLayout) v.findViewById(R.id.viewpager_LI);
        companyname_tv = (TextView) v.findViewById(R.id.companyName_tv);
        pickup_loc_tv = (TextView) v.findViewById(R.id.pickup_loc_tv);
        locationtype_tv = (TextView) v.findViewById(R.id.locationtype_tv);
        companylogo_imv = (ImageView) v.findViewById(R.id.companylogo_imv);
        //pager_indicator = (LinearLayout) v.findViewById(R.id.viewPagerCountDots);
        search_imv = (ImageView) v.findViewById(R.id.search_imv);
        current_miles_tv.setOnClickListener(this);
        fiften_miles_tv.setOnClickListener(this);
        ten_miles_tv.setOnClickListener(this);
        two_miles_tv.setOnClickListener(this);
        miles_LI.setOnClickListener(this);
        five_miles_tv.setOnClickListener(this);
        search_back.setOnClickListener(this);
        cancel_button.setOnClickListener(this);
        current_location_tv.setOnClickListener(this);
        list_icon.setOnClickListener(this);
        maps_icon.setOnClickListener(this);
        current_loc_tv.setOnClickListener(this);
        refresh_iv.setOnClickListener(this);
        // list_icon.setImageResource(R.drawable.sortings);
        maps_icon.setImageResource(R.drawable.ic_map_vew_n);
        listview_LI.setVisibility(View.GONE);
        atvPlaces = (AutoCompleteTextView) v.findViewById(R.id.atv_places);
        searchbar_FL = (FrameLayout) v.findViewById(R.id.searchbar_FL);
        autocomplte_places = (TextView) v.findViewById(R.id.autocomplte_places);
        atvPlaces.setThreshold(1);
        broadCastList = new ArrayList<>();
        LinearLayout retailerInfo = (LinearLayout) v.findViewById(R.id.retailerInfo);
        retailerInfo.setOnTouchListener(new RelativeLayoutTouchListener(getActivity()));

        search_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar_FL.setVisibility(View.VISIBLE);
                atvPlaces.setFocusable(true);
                atvPlaces.setText("");
            }
        });
        autocomplte_places.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar_FL.setVisibility(View.VISIBLE);
                atvPlaces.setFocusable(true);
                atvPlaces.setText("");

            }
        });
        bac_dim_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    // atvPlaces.setY(atvPlaces.getY()-10);
                    InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
                    /*search_back.setVisibility(View.GONE);
                    cancel_button.setVisibility(View.GONE);*/
                    recent_search_LI.setVisibility(View.GONE);
                    bac_dim_layout.setVisibility(View.GONE);
                    recentlist_lv.setVisibility(View.GONE);
                    searchbar_FL.setVisibility(View.GONE);


                }
                return true; // return is important...
            }
        });


        atvPlaces.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
                //atvPlaces.setY(atvPlaces.getY()+30);
                atvPlaces.clearFocus();
                bac_dim_layout.setVisibility(View.GONE);
                recentlist_lv.setVisibility(View.GONE);
                searchbar_FL.setVisibility(View.GONE);

                //Toast.makeText(getActivity(), " selected "+googlePlacesresult.get(pos).get("description"), Toast.LENGTH_LONG).show();
                Geocoder coder = new Geocoder(getActivity());
                try {
                    ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(googlePlacesresult.get(pos).get("description"), 50);
                    for (Address add : adresses) {
                        // if (statement) {//Controls to ensure it is right address such as country etc.
                        double longitude = add.getLongitude();
                        double latitude = add.getLatitude();
                        Log.d(" lat, lang", "" + latitude + "," + longitude);
                        // }
                        gMap.clear();
                        // Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                        autocomplte_places.setText(googlePlacesresult.get(pos).get("description"));
                        LatLng position = new LatLng(latitude, longitude);
                        centerLatLng = position;
                        drawCircle(position);
                        dbHelper.insertContact(new RecentSearch("1", googlePlacesresult.get(pos).get("description"), latitude, longitude));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        atvPlaces.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d("onText changed: ", "" + s);
                /* search_back.setVisibility(View.VISIBLE);
                cancel_button.setVisibility(View.VISIBLE);*/

                if (searchbar_FL.getVisibility() == View.VISIBLE) {
                    if (s.length() > 0) {

                        bac_dim_layout.setVisibility(View.VISIBLE);
                        // atvPlaces.setFocusable(false);
                        recentlist_lv.setVisibility(View.GONE);
                        placesTask = new PlacesTask();
                        placesTask.execute(s.toString());

                    } else {

                        if (dbHelper.getAllSearchh().size() > 0) {
                            recentSearchAdapter = new RecentSearch_Adapter(getActivity(), dbHelper.getAllSearchh());
                            recentlist_lv.setAdapter(recentSearchAdapter);
                            recent_search_LI.setVisibility(View.VISIBLE);
                            recentlist_lv.setVisibility(View.VISIBLE);
                            bac_dim_layout.setVisibility(View.VISIBLE);
                            Common.setListViewHeightBasedOnChildren(recentlist_lv);

                        }
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub
                Log.d("before text changed", "text : " + s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

                Log.d("before text changed", "text : " + s.length() + atvPlaces.hasFocus());
            }
        });

        recentlist_lv.setOnItemClickListener(this);


    }

    private void callService() {
        Common.showDialog(getActivity());
        apiWrapper.broadCastCall(AppPreferences.getInstance(getActivity()).getSignInResult() != null ?
                        AppPreferences.getInstance(getActivity()).getSignInResult().optString("AuthNToken") : "",
                new Callback<ArrayList<BroadCast>>() {
                    @Override
                    public void onResponse(Call<ArrayList<BroadCast>> call, Response<ArrayList<BroadCast>> response) {
                        if (response.isSuccessful()) {
                            broadCastList = response.body();
                            checkTheExpressedFullfilments();
                            drawCircle(centerLatLng);
                            broadcastAdapter = new Broadcast_Adapter(getActivity(), broadCastList);
                            listview_lv.setAdapter(broadcastAdapter);
                        } else {
                            try {
                                AppUtil.parseErrorMessage(getActivity(), response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(getActivity(), getString(R.string.OOPS));
                                e.printStackTrace();
                            }
                            AppUtil.CheckErrorCode(getActivity(), response.code());
                        }
                        Common.disMissDialog();
                    }

                    @Override
                    public void onFailure(Call<ArrayList<BroadCast>> call, Throwable t) {
                        Common.disMissDialog();
                        Log.e("BroadCastFrag", "BroadCast :: " + t.getMessage());
                        AppUtil.toast(getActivity(), getString(R.string.OOPS));
                    }
                });
    }

    private void checkTheExpressedFullfilments() {
        if (MyApplication.getInstance().dasBoardListCall != null && MyApplication.getInstance().dasBoardListCall.size() > 0) {
            if (broadCastList != null) {
                for (BroadCast broadCast : broadCastList) {
                    if (broadCast.Fulfillments != null && broadCast.Fulfillments.size() > 0) {
                        boolean isExpressed = true;
                        for (FulfillersDTO fulfillersDTO : broadCast.Fulfillments) {
                            for (FulfillersDTO homeFulFiller : MyApplication.getInstance().dasBoardListCall) {
                                if (fulfillersDTO.FulfillmentId.equals(homeFulFiller.FulfillmentId) && !homeFulFiller.Status.equalsIgnoreCase("Expressed")) {
                                    isExpressed = false;
                                    break;
                                }
                            }
                            if (!isExpressed) {
                                break;
                            }
                        }
                        broadCast.isExpressed = isExpressed;
                    }
                }
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can addMarker markers or lines, addMarker listeners or move the camera. In this case,
     * we just addMarker a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        //Initialize Google Play Services
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        } else {
            buildGoogleApiClient();
        }
        if (mCurrentLastLocation != null) {
            //move map camera
            LatLng latLng = new LatLng(mCurrentLastLocation.getLatitude(), mCurrentLastLocation.getLongitude());
            drawCircle(latLng);
            addMarker(mCurrentLastLocation);
            getAddress(mCurrentLastLocation, true);
        } else {
            gMap.resetMinMaxZoomPreference();
            gMap.clear();
        }
        gMap.setOnCameraIdleListener(this);
        gMap.setOnMarkerClickListener(this);
        gMap.setOnMapClickListener(this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }


        //move map camera
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d("TAG- CURRENT", latLng.latitude + " -- " + latLng.longitude);
        Log.d("TAG- CURRENT", firstTime + "");
        if (!firstTime) {
            drawCircle(latLng);
            firstTime = true;
        }
        addMarker(mCurrentLastLocation);
        getAddress(mCurrentLastLocation, false);
    }


    private void addMarker(Location location) {
        if (location == null) {
            return;
        }
        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.location_ics));
        mCurrLocationMarker = gMap.addMarker(markerOptions);
    }

    private void drawCircle(LatLng latLng) {
        if (gMap == null) {
            return;
        }
        if (latLng == null)
            return;
        if (latLng.longitude == 0 || latLng.latitude == 0) {
            return;
        }
        Log.d("TAG- DRAWN", "DRAWN");
        double radiusInMeters = currentDistance * 1.609 * 1000;
        centerLatLng = latLng;
        CircleOptions circleOptions = new CircleOptions().center(latLng).radius(radiusInMeters).
                fillColor(shadeColor).strokeColor(strokeColor).
                strokeWidth(1);
        if (circleOptions != null) {
//            gMap.clear();
            if (mCircle != null && mCircle.isVisible()) {
                mCircle.remove();
            }
            gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            gMap.animateCamera(CameraUpdateFactory.zoomTo(getZoomLevel(circleOptions)));
            mCircle = gMap.addCircle(circleOptions);
            mCircle.setCenter(latLng);
        }

        if (broadCastList != null)
            setRetailerInfo();
    }

    public int getZoomLevel(CircleOptions circle) {
        int zoomLevel = 0;
        if (circle != null) {
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted. Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        gMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other permissions this app might request.
            // You can addMarker here other case statements according to your requirement.
        }
    }

    @Override
    public void onCameraIdle() {
        if (gMap == null) {
            return;
        }
//        if (!firstTime) {
//            return;
//        }
        LatLng targetLatLng = gMap.getCameraPosition().target;
        if (!compareLatLng(targetLatLng, centerLatLng)) {
            Log.d("TAG- CAMEAR", targetLatLng.latitude + " -- " + targetLatLng.longitude);
            addMarker(mCurrentLastLocation);
            drawCircle(targetLatLng);
            centerLatLng = targetLatLng;
            if (broadCastList != null)
                setRetailerInfo();
        }
    }

    private boolean nullCheck(LatLng first) {
        if (first == null || first.latitude == 0 || first.longitude == 0) {
            return true;
        }
        return false;
    }

    private boolean compareLatLng(LatLng first, LatLng second) {
        if (first == null || second == null) {
            return false;
        }
        Location selected_location = new Location("locationA");
        selected_location.setLatitude(first.latitude);
        selected_location.setLongitude(first.longitude);
        Location near_locations = new Location("locationB");
        near_locations.setLatitude(second.latitude);
        near_locations.setLongitude(second.longitude);
        if (near_locations.distanceTo(selected_location) < 100) {
            return true;
        }
        return false;
    }

    @Override
    public void onPlaceSelected(Place place) {
        if (gMap == null)
            return;
        search_loc = true;
        gMap.clear();
        search_ev.setText(place.getAddress());
        LatLng position = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
        centerLatLng = position;
        drawCircle(position);
        dbHelper.insertContact(new RecentSearch("1", place.getAddress().toString(), place.getLatLng().latitude,
                place.getLatLng().longitude));
    }

    @Override
    public void onError(Status status) {

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            //current_miles_tv,fiften_miles_tv,ten_miles_tv,five_miles_tv,two_miles_tv

            case R.id.search_back:
                //atvPlaces.setY(atvPlaces.getY()+30);
                bac_dim_layout.setVisibility(View.GONE);
                recentlist_lv.setVisibility(View.GONE);
                searchbar_FL.setVisibility(View.GONE);
                break;

            case R.id.cancel_button:
                atvPlaces.setText("");
                break;

            case R.id.current_miles_tv:
                if (miles_LI.isShown()) {
                    miles_LI.setVisibility(View.GONE);
                } else {
                    miles_LI.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.current_location_tv:
                search_loc = false;
                if (mCurrentLastLocation != null) {
                    //move map camera
                    LatLng latLng = new LatLng(mCurrentLastLocation.getLatitude(), mCurrentLastLocation.getLongitude());
                    Log.d("TAG- CURRENT", latLng.latitude + " -- " + latLng.longitude);
                    Log.d("TAG- CURRENT", firstTime + "");
                    drawCircle(latLng);
                    addMarker(mCurrentLastLocation);
                    getAddress(mCurrentLastLocation, true);
                }
                break;
            case R.id.current_loc_tv:
                search_loc = true;
                InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
                //atvPlaces.setY(atvPlaces.getY()+30);
                bac_dim_layout.setVisibility(View.GONE);
                recentlist_lv.setVisibility(View.GONE);
                searchbar_FL.setVisibility(View.GONE);
                recent_search_LI.setVisibility(View.GONE);
                if (mCurrentLastLocation != null) {
                    //move map camera
                    LatLng latLng = new LatLng(mCurrentLastLocation.getLatitude(), mCurrentLastLocation.getLongitude());
                    Log.d("TAG- CURRENT", latLng.latitude + " -- " + latLng.longitude);
                    Log.d("TAG- CURRENT", firstTime + "");
                    drawCircle(latLng);
                    addMarker(mCurrentLastLocation);
                    getAddress(mCurrentLastLocation, true);
                }
                break;
            case R.id.fiften_miles_tv:
                current_miles_tv.setText("15 \n Miles");
                currentDistance = 15;
                miles_LI.setVisibility(View.GONE);
                drawCircle(centerLatLng);
                break;

            case R.id.ten_miles_tv:
                current_miles_tv.setText("10 \n Miles");
                currentDistance = 10;
                miles_LI.setVisibility(View.GONE);
                drawCircle(centerLatLng);
                break;

            case R.id.five_miles_tv:
                current_miles_tv.setText("5 \n Miles");
                currentDistance = 5;
                miles_LI.setVisibility(View.GONE);
                drawCircle(centerLatLng);
                break;

            case R.id.two_miles_tv:
                current_miles_tv.setText("2 \n Miles");
                currentDistance = 2;
                miles_LI.setVisibility(View.GONE);
                drawCircle(centerLatLng);
                break;
            case R.id.icon_back:
                maps_view.setVisibility(View.VISIBLE);
                listview_LI.setVisibility(View.GONE);
                break;
            case R.id.listview_imv:
                maps_view.setVisibility(View.GONE);
                listview_LI.setVisibility(View.VISIBLE);
                break;
            case R.id.refresh_iv:
                callService();
                break;
        }

    }

    // To animate view slide out from top to bottom
    public void slideToBottom(View view) {
        view.startAnimation(animation_slide_down);
        view.setVisibility(View.GONE);
    }

    // To animate view slide out from bottom to to
    public void slideToTop(View view) {
        view.startAnimation(animation_slide_up);
        view.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (usermarkerId.equals(marker.getId())) {
            return true;
        } else {
            if (markervalues == null || markervalues.size() == 0)
                return true;
            else {
                //delete previous marker
                if (previousMarker != null) {
                    setUpMap(broadCastList.get(markervalues.get(previousMarker.getId())), R.drawable.marks_bgs, markervalues.get(previousMarker.getId()));
                    previousMarker.remove();
                }
                setUpMap(broadCastList.get(markervalues.get(marker.getId())), R.drawable.marks_bg_orgs, markervalues.get(marker.getId()));

                //bottom viewpager setup

                if (AppUtil.ifNotEmpty(broadCastList.get(markervalues.get(marker.getId())).CompanyLogo)) {
                    Picasso.with(getActivity()).load(broadCastList.get(markervalues.get(marker.getId())).CompanyLogo).into(companylogo_imv);
                }

                companyname_tv.setText(broadCastList.get(markervalues.get(marker.getId())).BusinessLegalName);
                pickup_loc_tv.setText(broadCastList.get(markervalues.get(marker.getId())).RetailerLocationAddress.RetailerLocationAddress.AddressLine1 + ", " +
                        broadCastList.get(markervalues.get(marker.getId())).RetailerLocationAddress.RetailerLocationAddress.City + ", " +
                        broadCastList.get(markervalues.get(marker.getId())).RetailerLocationAddress.RetailerLocationAddress.State+", "+
                        broadCastList.get(markervalues.get(marker.getId())).RetailerLocationAddress.RetailerLocationAddress.CountryName);

                locationtype_tv.setText(broadCastList.get(markervalues.get(marker.getId())).RetailerLocationAddress.LocationType);
                //viewpager setup
                broadCastFulAdapter = new BroadCast_Ful_Adapter(getContext(), broadCastList.get(markervalues.get(marker.getId())), broadCastList.get(markervalues.get(marker.getId())).Fulfillments);
                fulfiller_viewPager.setAdapter(broadCastFulAdapter);
                circularIndicator.setViewPager(fulfiller_viewPager);
                if (!viewpager_LI.isShown()) {
                    slideToTop(viewpager_LI);
                }
                marker.remove();
                previousMarker = marker; //Now the clicked marker becomes previousMarker
                //setUiPageViewController();
                return false;
            }
        }
    }

    private void setUpMap(BroadCast broadCast, int marks_bgs, int i) {
        if (broadCast.isExpressed) {
            return;
        }
        View marker = ((LayoutInflater) MyApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
        TextView numTxt = (TextView) marker.findViewById(R.id.name_tv);
        TextView fulfillments = (TextView) marker.findViewById(R.id.fulfillments_tv);
        ImageView background_img = (ImageView) marker.findViewById(R.id.background_img);
        background_img.setBackgroundResource(marks_bgs);
        numTxt.setText(broadCast.BusinessLegalName);
        fulfillments.setText(broadCast.Fulfillments.size() + " Fulfillments");

        Marker marker1 = gMap.addMarker(new MarkerOptions()
                .position(new LatLng(Double.parseDouble(broadCast.RetailerLocationAddress.RetailerLocationAddress.Latitude),
                        Double.parseDouble(broadCast.RetailerLocationAddress.RetailerLocationAddress.Longitude)))
                .icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(MyApplication.getInstance(), marker))));
        //Log.d("marker values"+marker1.getId(),""+broadCast.RetailerId);
        markervalues.put(marker1.getId(), i);
        shownMarkersList.add(marker1);
        Log.d("markers id", "" + marker1.getId());
    }

    // Convert a view to bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        //context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;

    }

    private void setUiPageViewController() {
        pager_indicator.removeAllViews();
        dotsCount = 0;
        dotsCount = broadCastFulAdapter.getCount();
        Log.d("count", "" + broadCastFulAdapter.getCount());
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(getActivity());
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (gMap == null)
            return;
        search_loc = true;
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(atvPlaces.getWindowToken(), 0);
        //atvPlaces.setY(atvPlaces.getY()+30);
        bac_dim_layout.setVisibility(View.GONE);
        recentlist_lv.setVisibility(View.GONE);
        searchbar_FL.setVisibility(View.GONE);
        recent_search_LI.setVisibility(View.GONE);
        //  atvPlaces.setY(atvPlaces.getY()+30);


        gMap.clear();
        // Place place = PlaceAutocomplete.getPlace(getActivity(), data);
        autocomplte_places.setText(dbHelper.getAllSearchh().get(position).place);
        LatLng positions = new LatLng(dbHelper.getAllSearchh().get(position).lat, dbHelper.getAllSearchh().get(position).lang);
        centerLatLng = positions;
        drawCircle(positions);

    }

    public void setRetailerInfo() {
        if (broadCastList != null && broadCastList.size() > 0) {
            for (Marker marker : shownMarkersList) {
                marker.remove();
            }
            shownMarkersList.clear();
            for (int i = 0; i < broadCastList.size(); i++) {
                BroadCast broadCast = broadCastList.get(i);
                //checking distance is greater than selceted miles
                if (centerLatLng != null) {
                    int dis = (int) distanceCalculate(broadCast);
                    Log.d("distance is", "" + dis + "," + currentDistance * 1.609 * 1000);
                    if (dis <= currentDistance * 1.609 * 1000) {
                        setUpMap(broadCast, R.drawable.marks_bgs, i);
                    }
                }
            }
        }
    }

    private double distanceCalculate(BroadCast broadCast) {
        double distance = 0;
        Location selected_location = new Location("locationA");
        selected_location.setLatitude(Double.parseDouble(broadCast.RetailerLocationAddress.RetailerLocationAddress.Latitude));
        selected_location.setLongitude(Double.parseDouble(broadCast.RetailerLocationAddress.RetailerLocationAddress.Longitude));
        Location near_locations = new Location("locationB");
        near_locations.setLatitude(centerLatLng.latitude);
        near_locations.setLongitude(centerLatLng.longitude);
        distance = selected_location.distanceTo(near_locations);

        return distance;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        if (isVisible()) {
            if (viewpager_LI.isShown())
                viewpager_LI.setVisibility(View.GONE);
        }
    }

    // Fetches all places from GooglePlaces AutoComplete Web Service
    private class PlacesTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... place) {
            // For storing data from web service
            String data = "";

            // Obtain browser key from https://code.google.com/apis/console
            String key = "key=AIzaSyADZn_fB01NotqDI_rjxkBMe9_NfMXNrEw";


            String input = "";

            try {
                input = "input=" + URLEncoder.encode(place[0], "utf-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }

            // place type to be searched
            String types = "types=geocode";

            // Sensor enabled
            String sensor = "sensor=false";

            // Building the parameters to the web service
            String parameters = input + "&" + types + "&" + sensor + "&" + key;

            // Output format
            String output = "json";

            // Building the url to the web service
            String url = "https://maps.googleapis.com/maps/api/place/autocomplete/" + output + "?" + parameters;
            Log.d("url Task", url);

            try {
                // Fetching the data from we service
                data = downloadUrl(url);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Creating ParserTask
            parserTask = new ParserTask();

            // Starting Parsing the JSON string returned by Web Service
            parserTask.execute(result);
            /*search_back.setVisibility(View.VISIBLE);
            cancel_button.setVisibility(View.VISIBLE);*/
        }
    }

/**
 * A class to parse the Google Places in JSON format
 */

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {

        JSONObject jObject;

        @Override
        protected List<HashMap<String, String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;

            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try {
                jObject = new JSONObject(jsonData[0]);

                // Getting the parsed data as a List construct
                places = placeJsonParser.parse(jObject);

            } catch (Exception e) {
                Log.d("Exception", e.toString());
            }
            return places;
        }

        @Override
        protected void onPostExecute(List<HashMap<String, String>> result) {
            if (googlePlacesresult != null && googlePlacesresult.size() > 0)
                googlePlacesresult.clear();

            String[] from = new String[]{"description"};
            int[] to = new int[]{android.R.id.text1};

            if (result != null) {
                // Creating a SimpleAdapter for the AutoCompleteTextView
                SimpleAdapter adapter = new SimpleAdapter(getContext(), result, R.layout.dropdown, from, to);

                // Setting the adapter
                atvPlaces.setAdapter(adapter);
                googlePlacesresult.addAll(result);
            }
        }
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Error", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        Log.d("data", "" + data);
        return data;
    }

    private void getAddress(Location location, boolean isCurrent) {
        if (asyncTask != null) {
            asyncTask.cancel(true);
        }
        asyncTask = new GeocodeAsyncTask(location, isCurrent);
        asyncTask.execute();
    }

    class GeocodeAsyncTask extends AsyncTask<Void, Void, Address> {

        private Location location;
        private boolean isCurrent;

        public GeocodeAsyncTask(Location location, boolean isCurrent) {
            this.location = location;
            this.isCurrent = isCurrent;
        }


        @Override
        protected Address doInBackground(Void... none) {
            Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            } catch (IOException ioException) {
            } catch (IllegalArgumentException illegalArgumentException) {
            }
            if (addresses != null && addresses.size() > 0)
                return addresses.get(0);
            return null;
        }

        protected void onPostExecute(Address address) {
            if (address != null) {
                if (isCurrent) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    centerLatLng = latLng;
                }
                String addressLine1 = address.getAddressLine(0);
                String city = address.getLocality();
                currentAddress = addressLine1 + "," + city;
                autocomplte_places.setText(currentAddress);
                current_loc_tv.setText(currentAddress);
                locaton_show = true;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //autocompleteFragment.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 5) {
            if (resultCode == -1) {
                if (gMap == null)
                    return;
                search_loc = true;
                gMap.clear();
                Place place = PlaceAutocomplete.getPlace(getActivity(), data);
                search_ev.setText(place.getAddress());
                LatLng position = new LatLng(place.getLatLng().latitude, place.getLatLng().longitude);
                centerLatLng = position;
                drawCircle(position);

            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getActivity(), data);
                Log.i("Place error ", status.getStatusMessage());
            } else if (requestCode == 0) {

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        maps_view.setVisibility(View.VISIBLE);
        listview_LI.setVisibility(View.GONE);
    }

    public class RelativeLayoutTouchListener implements View.OnTouchListener {

        private Activity activity;
        static final int MIN_DISTANCE = 100;// TODO change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
        private float downX, downY, upX, upY;

        public RelativeLayoutTouchListener(FragmentActivity activity) {
            this.activity = activity;
        }


        public void onRightToLeftSwipe() {
        }

        public void onLeftToRightSwipe() {
        }

        public void onTopToBottomSwipe() {
            if (viewpager_LI.isShown())
                slideToBottom(viewpager_LI);
        }

        public void onBottomToTopSwipe() {

        }

        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {
                        // return false; // We don't consume the event
                    }

                    // swipe vertical?
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            return true;
                        }
                    } else {
                        // return false; // We don't consume the event
                    }

                    return false; // no swipe horizontally and no swipe vertically
                }// case MotionEvent.ACTION_UP:
            }
            return false;
        }

    }

    private static final double ASSUMED_INIT_LATLNG_DIFF = 1.0;
    private static final float ACCURACY = 0.01f;

    public LatLngBounds boundsWithCenterAndLatLngDistance(LatLng center, double latDistanceInMeters, double lngDistanceInMeters) {
        latDistanceInMeters /= 2;
        lngDistanceInMeters /= 2;
        LatLngBounds.Builder builder = LatLngBounds.builder();
        float[] distance = new float[1];
        {
            boolean foundMax = false;
            double foundMinLngDiff = 0;
            double assumedLngDiff = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude, center.longitude + assumedLngDiff, distance);
                double distanceDiff = distance[0] - lngDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLngDiff = assumedLngDiff;
                        assumedLngDiff *= 2;
                    } else {
                        double tmp = assumedLngDiff;
                        assumedLngDiff += (assumedLngDiff - foundMinLngDiff) / 2;
                        foundMinLngDiff = tmp;
                    }
                } else {
                    assumedLngDiff -= (assumedLngDiff - foundMinLngDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - lngDistanceInMeters) > lngDistanceInMeters * ACCURACY);
            LatLng east = new LatLng(center.latitude, center.longitude + assumedLngDiff);
            builder.include(east);
            LatLng west = new LatLng(center.latitude, center.longitude - assumedLngDiff);
            builder.include(west);
        }
        {
            boolean foundMax = false;
            double foundMinLatDiff = 0;
            double assumedLatDiffNorth = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude + assumedLatDiffNorth, center.longitude, distance);
                double distanceDiff = distance[0] - latDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLatDiff = assumedLatDiffNorth;
                        assumedLatDiffNorth *= 2;
                    } else {
                        double tmp = assumedLatDiffNorth;
                        assumedLatDiffNorth += (assumedLatDiffNorth - foundMinLatDiff) / 2;
                        foundMinLatDiff = tmp;
                    }
                } else {
                    assumedLatDiffNorth -= (assumedLatDiffNorth - foundMinLatDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
            LatLng north = new LatLng(center.latitude + assumedLatDiffNorth, center.longitude);
            builder.include(north);
        }
        {
            boolean foundMax = false;
            double foundMinLatDiff = 0;
            double assumedLatDiffSouth = ASSUMED_INIT_LATLNG_DIFF;
            do {
                Location.distanceBetween(center.latitude, center.longitude, center.latitude - assumedLatDiffSouth, center.longitude, distance);
                double distanceDiff = distance[0] - latDistanceInMeters;
                if (distanceDiff < 0) {
                    if (!foundMax) {
                        foundMinLatDiff = assumedLatDiffSouth;
                        assumedLatDiffSouth *= 2;
                    } else {
                        double tmp = assumedLatDiffSouth;
                        assumedLatDiffSouth += (assumedLatDiffSouth - foundMinLatDiff) / 2;
                        foundMinLatDiff = tmp;
                    }
                } else {
                    assumedLatDiffSouth -= (assumedLatDiffSouth - foundMinLatDiff) / 2;
                    foundMax = true;
                }
            } while (Math.abs(distance[0] - latDistanceInMeters) > latDistanceInMeters * ACCURACY);
            LatLng south = new LatLng(center.latitude - assumedLatDiffSouth, center.longitude);
            builder.include(south);
        }
        return builder.build();
    }
}
