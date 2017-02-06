package com.biglynx.fulfiller.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.SignInResult;
import com.biglynx.fulfiller.network.FullFillerApiWrapper;
import com.biglynx.fulfiller.utils.AlertDialogManager;
import com.biglynx.fulfiller.utils.AppPreferences;
import com.biglynx.fulfiller.utils.AppUtil;
import com.biglynx.fulfiller.utils.Common;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.biglynx.fulfiller.utils.Constants.OOPS_SOMETHING_WENT_WRONG;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CAMERA = 0;
    private static final int GALLERY = 1;
    private TextView title_tv, done_tv;
    private ImageView icon_back;
    private LinearLayout contactPerson_LI, camera_LI, gallery_LI;
    private TextView fullName_tv, phoneNumber_tv;
    private EditText fullName_ev, contactPerson_ev, email_ev, address_ev, city_ev, phoneNumber_ev;
    private String rollType;
    private CircularImageView profilePIc_iv;
    private String mCurrentPhotoPath;
    private static String responseString;
    private String blobId;
    private Spinner stateSpinner;
    private ArrayList<String> stateSpinnerList;
    private RelativeLayout spinner_LI;
    private String state;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        initViews();
    }

    private void initViews() {
        title_tv = (TextView) findViewById(R.id.companyname_tv);
        done_tv = (TextView) findViewById(R.id.done_tv);
        icon_back = (ImageView) findViewById(R.id.icon_back);
        title_tv.setText(getString(R.string.edit_profile));
        icon_back.setVisibility(View.VISIBLE);
        done_tv.setVisibility(View.VISIBLE);
        contactPerson_LI = (LinearLayout) findViewById(R.id.contactPerson_LI);
        contactPerson_ev = (EditText) findViewById(R.id.contachPerson_ev);
        fullName_ev = (EditText) findViewById(R.id.fullname_ev);
        fullName_tv = (TextView) findViewById(R.id.fullName_tv);
        email_ev = (EditText) findViewById(R.id.email_ev);
        address_ev = (EditText) findViewById(R.id.address_ev);
        city_ev = (EditText) findViewById(R.id.city_ev);
        phoneNumber_ev = (EditText) findViewById(R.id.mobile_ev);
        phoneNumber_tv = (TextView) findViewById(R.id.mobile_tv);
        profilePIc_iv = (CircularImageView) findViewById(R.id.profilePic_iv);
        camera_LI = (LinearLayout) findViewById(R.id.takePhoto_LI);
        gallery_LI = (LinearLayout) findViewById(R.id.gallery_LI);
        stateSpinner = (Spinner) findViewById(R.id.spinner_satte);
        spinner_LI = (RelativeLayout) findViewById(R.id.layout_spinner_state);
        stateSpinnerList = new ArrayList<>();
        for (String state : getResources().getStringArray(R.array.statesList)) {
            stateSpinnerList.add(state);
        }

        stateSpinner.setAdapter(getSpinnerAdapter(stateSpinnerList));

        buildUI();

        icon_back.setOnClickListener(this);
        done_tv.setOnClickListener(this);
        camera_LI.setOnClickListener(this);
        gallery_LI.setOnClickListener(this);
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                state = stateSpinnerList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public ArrayAdapter getSpinnerAdapter(ArrayList<String> list) {
        return new ArrayAdapter(getApplicationContext(),
                R.layout.view_state_spinner_item, list);
    }

    private void buildUI() {

        JSONObject signInResult = AppPreferences.getInstance(EditProfileActivity.this).getSignInResult();
        state = signInResult.optString("State");

        if (signInResult.optString("Role").equals("DeliveryPerson"))
            rollType = "editprofileperson";
        else
            rollType = "editprofilepartner";

        if (rollType.equals("editprofilepartner")) {
            contactPerson_LI.setVisibility(View.VISIBLE);
            fullName_tv.setText(getString(R.string.business_name));
            fullName_ev.setEnabled(false);
            fullName_ev.setClickable(false);
            fullName_ev.setFocusable(false);
            fullName_ev.setFocusableInTouchMode(false);
            fullName_ev.setText(signInResult.optString("BusinessLegalName"));
            contactPerson_ev.setText(signInResult.optString("FirstName"));
            phoneNumber_tv.setText(getString(R.string.phonenumber));
        } else {
            contactPerson_LI.setVisibility(View.GONE);
            fullName_tv.setText(getString(R.string.full_name));
            fullName_ev.setEnabled(true);
            fullName_ev.setClickable(true);
            fullName_ev.setFocusable(true);
            fullName_ev.setFocusableInTouchMode(true);
            fullName_ev.setText(signInResult.optString("FirstName"));
            phoneNumber_tv.setText(getString(R.string.mobile));
        }
        email_ev.setText(signInResult.optString("Email"));

        //if (AppUtil.ifNotEmpty(signInResult.optString("Phone")))
        phoneNumber_ev.setText(signInResult.optString("Phone"));
        if (AppUtil.ifNotEmpty(signInResult.optString("AddressLine1")))
            address_ev.setText(signInResult.optString("AddressLine1"));
        if (AppUtil.ifNotEmpty(signInResult.optString("City")))
            city_ev.setText(signInResult.optString("City"));
        if (AppUtil.ifNotEmpty(state)) {
            for (int i = 0; i < stateSpinnerList.size(); i++) {
                if (state.toLowerCase().equals(stateSpinnerList.get(i).toLowerCase()))
                    stateSpinner.setSelection(i);
            }
        }
        if (AppUtil.ifNotEmpty(AppPreferences.getInstance(EditProfileActivity.this).getSignInResult().optString("CompanyLogo")))
            Picasso.with(EditProfileActivity.this).load(AppPreferences.getInstance(EditProfileActivity.this).getSignInResult().optString("CompanyLogo"))
                    .error((int) R.drawable.com_facebook_profile_picture_blank_square).into(this.profilePIc_iv);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.icon_back:
                finish();
                break;
            case R.id.done_tv:
                if (checkAllMandatoryFields()) {
                    HashMap<String, Object> hashMap = new HashMap<>();

                    if (contactPerson_LI.isShown()) {
                        hashMap.put("BusinessLegalName", fullName_ev.getText().toString());
                        hashMap.put("FirstName", contactPerson_ev.getText().toString());
                    } else {
                        hashMap.put("BusinessLegalName", "");
                        hashMap.put("FirstName", fullName_ev.getText().toString());
                    }
                    hashMap.put("Email", TextUtils.isEmpty(email_ev.getText().toString()) ? "" : email_ev.getText().toString());
                    hashMap.put("AddressLine1", address_ev.getText().toString());
                    hashMap.put("City", city_ev.getText().toString());
                    hashMap.put("State", stateSpinner.getSelectedItem().toString());
                    hashMap.put("Country", getString(R.string.united_states_of_america));
                    hashMap.put("Phone", phoneNumber_ev.getText().toString());
                    hashMap.put("BlobId", blobId);

                    callService(hashMap);
                } else {
                    AlertDialogManager.showAlertOnly(EditProfileActivity.this, getString(R.string.edit_profile),
                            "All fields are mandatory", "OK");
                }
                break;
            case R.id.takePhoto_LI:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA);
                break;
            case R.id.gallery_LI:
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // Start the Intent
                startActivityForResult(galleryIntent, GALLERY);
                break;
        }
    }

    private void callService(HashMap<String, Object> hashMap) {

        if (!Common.isNetworkAvailable(EditProfileActivity.this)) {
            AppUtil.toast(EditProfileActivity.this, getString(R.string.check_interent_connection));
            return;
        }

        Common.showDialog(EditProfileActivity.this);
        FullFillerApiWrapper apiWrapper = new FullFillerApiWrapper();
        apiWrapper.editProfileInSettings(AppPreferences.getInstance(EditProfileActivity.this).getSignInResult().optString("AuthNToken"),
                rollType, hashMap, new Callback<SignInResult>() {
                    @Override
                    public void onResponse(Call<SignInResult> call, Response<SignInResult> response) {
                        Common.disMissDialog();
                        if (response.isSuccessful()) {
                            SignInResult signInResult = response.body();
                            if (signInResult != null) {
                                AppPreferences.getInstance(EditProfileActivity.this).setSignInResult(signInResult);
                            }
                            buildUI();
                        } else {
                            try {
                                AppUtil.parseErrorMessage(EditProfileActivity.this, response.errorBody().string());
                            } catch (IOException e) {
                                AppUtil.toast(EditProfileActivity.this, OOPS_SOMETHING_WENT_WRONG);
                                e.printStackTrace();
                            }

                            AppUtil.CheckErrorCode(EditProfileActivity.this, response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<SignInResult> call, Throwable t) {
                        Common.disMissDialog();
                        AppUtil.toast(EditProfileActivity.this, OOPS_SOMETHING_WENT_WRONG);
                    }
                });
    }

    private boolean checkAllMandatoryFields() {
        if (!TextUtils.isEmpty(fullName_ev.getText().toString().trim())
                /*&& (!TextUtils.isEmpty(email_ev.getText().toString().trim()))*/
                && (!TextUtils.isEmpty(phoneNumber_ev.getText().toString().trim()))
                && (!TextUtils.isEmpty(address_ev.getText().toString().trim()))
                && (!TextUtils.isEmpty(city_ev.getText().toString().trim()))
                && (!TextUtils.isEmpty(stateSpinner.getSelectedItem().toString().trim()))) {
            if (contactPerson_LI.isShown()) {
                if (!TextUtils.isEmpty(contactPerson_ev.getText().toString().trim()))
                    return true;
                else
                    return false;
            } else
                return true;

        } else
            return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA && resultCode == Activity.RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            // get
            Bitmap bitMap = (Bitmap) extras.get("data");

            try {
                createImageFile(bitMap);
            } catch (IOException e) {
                e.printStackTrace();
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

    public class SendImageResult extends AsyncTask<String, String, String> {
        URL url = null;
        String responce = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Common.showDialog(EditProfileActivity.this);
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
                JSONArray result = new JSONArray(responseString);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject jsonObject = result.getJSONObject(i);

                    blobId = jsonObject.getString("UniqueId");
                    Bitmap myBitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    profilePIc_iv.setImageBitmap(myBitmap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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
            httpClient.execute(httppost, new PhotoUploadResponseHandler());

        } catch (Exception e) {
            Log.e("data", e.getLocalizedMessage(), e);
        }
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
}
