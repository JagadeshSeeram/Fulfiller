package com.biglynx.fulfiller.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.Vehicles;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.network.NetworkOperationListener;
import com.biglynx.fulfiller.network.NetworkResponse;
import com.biglynx.fulfiller.utils.AlertDialogManager;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

/**
 * Copyright (c) 206 BigLynx
 * <p/>
 * All Rights reserved.
 */

/*
* @author Ramakrishna on 8/16/2016
* @version 1.0
*
*/

public class AddVehicle extends AppCompatActivity implements View.OnClickListener, NetworkOperationListener {

    ImageView icon_back;
    TextView companyname_tv;
    TextView vehicle_type_ev;
    EditText vehicle_year_ev;
    TextView vehilceinsur_ev;
    EditText licence_ev;
    TextView vehilcereg_ev;
    boolean gallerys = false, cameras = false, insurence = false, registratoin = false;
    static String lineEnd = "\r\n", doctorId, oldnew, responseString;
    static String twoHyphens = "--", insurence_code, registration_code;
    String boundary = "09853945803950934509305930259";
    Uri imageUri = null;
    private static final int REQUEST_ACTIVITY_CAMERA = 5;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    //private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
    private String mCurrentPhotoPath;
    boolean camera = false, gallery = false;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    TextView submit_tv;
    AlertDialog.Builder alertadd;
    AlertDialog ad = null;
    private ImageView vehiceTypeImage;
    private ImageView insurenceAttachmentImage;
    private ImageView regAttachmentImage;
    private ImageView vehilcereg_ev_img;
    private ImageView vehilceinsur_ev_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addvehicle);

        initViews();
    }

    private void initViews() {
        companyname_tv = (TextView) findViewById(R.id.companyname_tv);
        companyname_tv.setText(getString(R.string.addvehicle));
        icon_back = (ImageView) findViewById(R.id.icon_back);
        submit_tv = (TextView) findViewById(R.id.submit_tv);
        vehicle_type_ev = (TextView) findViewById(R.id.vehicle_type_ev);
        vehicle_year_ev = (EditText) findViewById(R.id.vehicle_year_ev);
        vehilceinsur_ev = (TextView) findViewById(R.id.vehilceinsur_ev);
        vehilcereg_ev_img = (ImageView) findViewById(R.id.vehilcereg_ev_img);
        vehilceinsur_ev_img = (ImageView) findViewById(R.id.vehilceinsur_ev_img);
        licence_ev = (EditText) findViewById(R.id.licence_ev);
        vehilcereg_ev = (TextView) findViewById(R.id.vehilcereg_ev);
        vehiceTypeImage = (ImageView) findViewById(R.id.iv_vehicleType_arrow);
        insurenceAttachmentImage = (ImageView) findViewById(R.id.iv_insurence_attachment);
        regAttachmentImage = (ImageView) findViewById(R.id.iv_registration_attachment);
        vehiceTypeImage.setOnClickListener(this);
        insurenceAttachmentImage.setOnClickListener(this);
        regAttachmentImage.setOnClickListener(this);

        icon_back.setVisibility(View.VISIBLE);
        icon_back.setOnClickListener(this);
        submit_tv.setOnClickListener(this);
        vehicle_type_ev.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.icon_back:
                finish();
                break;

            case R.id.submit_tv:

                if (vehicle_type_ev.getText().toString().equals("") ||
                        vehicle_year_ev.getText().toString().equals("") ||
                        vehilcereg_ev.getText().toString().equals("") ||
                        vehilceinsur_ev.getText().toString().equals("") ||
                        licence_ev.getText().toString().equals("")) {

                    AlertDialogManager.showAlertOnly(this, "Fulfiller", "All Fields are mandatory", "Ok");

                } else {
                    HashMap<String, String> addvehilce = new HashMap<>();
                    addvehilce.put("VehicleType", vehicle_type_ev.getText().toString());
                    addvehilce.put("VehicleYear", vehicle_year_ev.getText().toString());
                    addvehilce.put("LicencePlateNumber", licence_ev.getText().toString());
                    addvehilce.put("VehicleInsurance", vehilceinsur_ev.getText().toString());
                    addvehilce.put("VehicleRegistration", vehilcereg_ev.getText().toString());

                    // HttpAdapter.addVehicle(this,"addvehicle",addvehilce.toString());
                    callService(addvehilce);
                }
                break;
            case R.id.iv_vehicleType_arrow:
            case R.id.vehicle_type_ev:
                startActivityForResult(new Intent(AddVehicle.this, VehicleType.class), 10);
                break;
            case R.id.iv_insurence_attachment:
                insurence = true;
                registratoin = false;

                // alert("Insurance");
                // TODO Auto-generated method stub
                alertadd = new AlertDialog.Builder(
                        AddVehicle.this);

                LayoutInflater factory = LayoutInflater.from(AddVehicle.this);
                final View view = factory.inflate(R.layout.addvehicle_dia, null);
                final TextView type_tv = (TextView) view.findViewById(R.id.type_tv);
                final TextView gallery_tv = (TextView) view.findViewById(R.id.gallery_tv);
                final TextView takephoto_tv = (TextView) view.findViewById(R.id.takephoto_tv);
                TextView cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
                TextView ok_tv = (TextView) view.findViewById(R.id.ok_tv);
                ImageView camera_imv = (ImageView) view.findViewById(R.id.camera_imv);
                ImageView gallery_Imv = (ImageView) view.findViewById(R.id.gallery_Imv);
                type_tv.setText("Add Insurance Document");
                alertadd.setView(view);
                ad = alertadd.create();


                cancle_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertadd.setCancelable(true);
                        ad.cancel();

                    }
                });

                ok_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (gallerys) {
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            // Start the Intent
                            startActivityForResult(galleryIntent, 1);
                        } else {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, 0);
                 /*  //  selectphoto_LI.setVisibility(View.VISIBLE);
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                       camera = true;
                       gallery = false;
                       requestPermissions(PERMISSIONS_STORAGE, 10);
                   } else {
                       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       startActivityForResult(intent, 0);
                   }*/
                        }
                    }
                });

                gallery_Imv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gallery_tv.setTextColor(Color.parseColor("#EC932F"));
                        takephoto_tv.setTextColor(Color.parseColor("#868686"));
                        gallerys = true;
                        cameras = false;
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, 1);
                    }
                });

                camera_imv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gallery_tv.setTextColor(Color.parseColor("#868686"));
                        takephoto_tv.setTextColor(Color.parseColor("#EC932F"));
                        gallerys = false;
                        cameras = true;
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, 0);
                    }
                });

                ad.show();
                break;
            case R.id.iv_registration_attachment:
                insurence = false;
                registratoin = true;
                alert("Registratoin");
                break;
        }
    }

    private void callService(HashMap<String, String> hashMap) {

        if (!Common.isNetworkAvailable(AddVehicle.this)) {
            AppUtil.toast(AddVehicle.this, getString(R.string.check_interent_connection));
            return;
        }

        Common.showDialog(AddVehicle.this);
        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        apiWrapper.addVehicleCall(AppPreferences.getInstance(AddVehicle.this).getSignInResult().optString("AuthNToken"),
                hashMap, new Callback<Vehicles>() {
                    @Override
                    public void onResponse(Call<Vehicles> call, Response<Vehicles> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            AppUtil.toast(AddVehicle.this, getString(R.string.vehicle_added));
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("vehicle_added", true);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        } else {
                            try {
                                AppUtil.parseErrorMessage(AddVehicle.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(AddVehicle.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<Vehicles> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(AddVehicle.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (camera) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        /**
                         Here REQUEST_IMAGE is the unique integer value you can pass it any integer
                         **/
                        // start camera activity
                        startActivityForResult(intent, 0);
                    } else {

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        // Start the Intent
                        startActivityForResult(galleryIntent, 1);

                    }
                } else {


                }
                return;
            }
            case 9: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            // get
            Bitmap bitMap = (Bitmap) extras.get("data");

            try {
                createImageFile(bitMap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == 10) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                vehicle_type_ev.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        } else if (data != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = decodeBitmap(uri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            // Get the cursor
            Cursor cursor = getContentResolver().query(uri,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            mCurrentPhotoPath = cursor.getString(columnIndex);

            new SendImageResult().execute(mCurrentPhotoPath);
            cursor.close();
        }

    }

    public Bitmap decodeBitmap(Uri selectedImage) throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        final int REQUIRED_SIZE = 100;

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    private void createImageFile(Bitmap bitmap) throws IOException {
        // Create an image file name
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, bytes);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        FileOutputStream stream = new FileOutputStream(image);
        stream.write(bytes.toByteArray());
        stream.close();
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        mCurrentPhotoPath = image.getAbsolutePath();

        new SendImageResult().execute(mCurrentPhotoPath);
    }

    public void alert(String text) {

        // TODO Auto-generated method stub
        alertadd = new AlertDialog.Builder(
                AddVehicle.this);

        LayoutInflater factory = LayoutInflater.from(AddVehicle.this);
        final View view = factory.inflate(R.layout.addvehicle_dia, null);
        final TextView type_tv = (TextView) view.findViewById(R.id.type_tv);
        final TextView gallery_tv = (TextView) view.findViewById(R.id.gallery_tv);
        final TextView takephoto_tv = (TextView) view.findViewById(R.id.takephoto_tv);
        TextView cancle_tv = (TextView) view.findViewById(R.id.cancle_tv);
        TextView ok_tv = (TextView) view.findViewById(R.id.ok_tv);
        ImageView camera_imv = (ImageView) view.findViewById(R.id.camera_imv);
        ImageView gallery_Imv = (ImageView) view.findViewById(R.id.gallery_Imv);
        type_tv.setText("Add " + text + " Document");
        alertadd.setView(view);
        ad = alertadd.create();


        cancle_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.cancel();
            }
        });

        ok_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gallerys) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    // Start the Intent
                    startActivityForResult(galleryIntent, 1);
                } else {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
                 /*  //  selectphoto_LI.setVisibility(View.VISIBLE);
                   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                       camera = true;
                       gallery = false;
                       requestPermissions(PERMISSIONS_STORAGE, 10);
                   } else {
                       Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                       startActivityForResult(intent, 0);
                   }*/
                }
            }
        });

        gallery_Imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_tv.setTextColor(Color.parseColor("#EC932F"));
                takephoto_tv.setTextColor(Color.parseColor("#868686"));
                gallerys = true;
                cameras = false;
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, 1);
            }
        });

        camera_imv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gallery_tv.setTextColor(Color.parseColor("#868686"));
                takephoto_tv.setTextColor(Color.parseColor("#EC932F"));
                gallerys = false;
                cameras = true;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
            }
        });

        ad.show();


    }


    public void uploadUserPhoto(String filepath) {

        try {
            Log.d("file path", "" + filepath);

            File file = new File(filepath);
            HttpPost httppost = new HttpPost("https://www.eyece.com/Services/Api/FileSystem/UploadPublic?extension=png");
            String boundary = "----" + System.currentTimeMillis() + "----";
            // httppost.setHeader("Content-type", "multipart/form-data; boundary=" + boundary);
            org.apache.http.entity.mime.MultipartEntity multipartEntity = new org.apache.http.entity.mime.MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            // httppost.containsHeader(lineEnd + twoHyphens + boundary + lineEnd);
            // multipartEntity.addPart("filepath", new StringBody(""));
            //  httppost.containsHeader(lineEnd + twoHyphens + boundary + lineEnd);
            multipartEntity.addPart("image", new FileBody(file, "image/jpg"));
            //  httppost.setHeader("Accept", "application/json");
            // httppost.containsHeader(lineEnd);
            httppost.setEntity(multipartEntity);
            //Log.d("entity",""+multipartEntity.getContent());
            HttpParams params = new BasicHttpParams();

            // params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
            //                System.out.println("executing request " + multipartEntity.getContent());
            HttpClient httpClient = new DefaultHttpClient();
            try {
                httpClient.execute(httppost, new PhotoUploadResponseHandler());
            }catch (Exception e){
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e("data", e.getLocalizedMessage(), e);
        }
    }

    @Override
    public void operationCompleted(NetworkResponse networkResponse) {

    }


    private class PhotoUploadResponseHandler implements ResponseHandler<Object> {

        @Override
        public Object handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {
            //10-08 03:19:13.966: E/res(27552): File Uploaded Successfully...
            // Log.e("jsonObject res", ""+response);
            HttpEntity r_entity = response.getEntity();
            responseString = EntityUtils.toString(r_entity);
            Log.e("jsonObject res", "" + responseString);


            return null;
        }
    }

    public class SendImageResult extends AsyncTask<String, String, String> {
        URL url = null;
        String responce = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.showDialog(AddVehicle.this);
        }

        @Override
        protected String doInBackground(String... arg0) {
            uploadUserPhoto(arg0[0]);

            return responce;
        }

        @Override
        protected void onPostExecute(String results) {
            super.onPostExecute(results);
            Common.disMissDialog();
            //  finish();
            try {
                if (responseString == null || responseString.length() == 0) {
                    AppUtil.toast(AddVehicle.this, OOPS_SOMETHING_WENT_WRONG);
                    return;
                }
                JSONArray result = new JSONArray(responseString);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);
                    if (!registratoin) {
                        vehilceinsur_ev.setText(jsonObject.getString("UniqueId"));
                        Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                        vehilceinsur_ev_img.setVisibility(View.VISIBLE);
                        vehilceinsur_ev_img.setImageBitmap(myBitmap);
                    } else {
                        Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                        vehilcereg_ev_img.setVisibility(View.VISIBLE);
                        vehilcereg_ev_img.setImageBitmap(myBitmap);
                        vehilcereg_ev.setText(jsonObject.getString("UniqueId"));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ad.cancel();
        }
    }
}
