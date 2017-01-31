package com.biglynx.fulfiller.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.biglynx.fulfiller.R;
import com.biglynx.fulfiller.models.Orders;
import com.biglynx.fulfiller.ui.StartDelivery;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.biglynx.fulfiller.utils.AppUtil.context;

/**
 * Created by Biglynx on 7/22/2016.
 */
public class StartDeliveryAdapter extends BaseAdapter {
    private ArrayAdapter<String> myAdapter;
    private PhoneNumberUtil phoneNumberUtil;
    android.content.Context Context;
    List<Orders> ordersList;
    LayoutInflater inflater;
    SimpleDateFormat simpleDateFormat;
    private boolean spinnerSelectionViaCode = false;
    private boolean userSelected = false;


    public StartDeliveryAdapter(Context mContext) {
        Context = mContext;
        inflater = LayoutInflater.from(this.Context);
        ordersList = new ArrayList<>();
        phoneNumberUtil = PhoneNumberUtil.getInstance();
    }

    @Override
    public int getCount() {
        return ordersList.size();
    }

    @Override
    public Object getItem(int position) {
        return ordersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final MyViewHolder mViewHolder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.startdelivery_ord_items, parent, false);
            mViewHolder = new MyViewHolder(convertView);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (MyViewHolder) convertView.getTag();
        }
        mViewHolder.name_tv.setText(ordersList.get(position).CustomerFirstName);
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(ordersList.get(position).CustomerPhone, "US");
            String usFormatNUmber = phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
            String finalNumber = usFormatNUmber.replace(" ", "-");
            mViewHolder.phoneno_tv.setText(finalNumber);

        } catch (NumberParseException e) {
            e.printStackTrace();
            mViewHolder.phoneno_tv.setText(ordersList.get(position).CustomerPhone);
        }
        mViewHolder.address_tv.setText(ordersList.get(position).OrderAddress.AddressLine1 + ", " +
                ordersList.get(position).OrderAddress.City + ", " +
                ordersList.get(position).OrderAddress.State + " " +
                ordersList.get(position).OrderAddress.CountryName);
        mViewHolder.copy_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                   /* android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context
                            .getSystemService(context.CLIPBOARD_SERVICE);
                    clipboard.setText("Confirmation code: " + interest.Fulfillments.FulfillerInterests.ConfirmationCode + "\nFulfillment Id : " +
                            interest.Fulfillments.FulfillmentId);*/
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                            Context.getSystemService(context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData
                            .newPlainText("Details", mViewHolder.address_tv.getText().toString().trim());
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(Context,"Copied to Clipboard!!",Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mViewHolder.openinmap_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        if (ordersList.get(position).DeliveryStatusId == 4) {
            mViewHolder.spinner.setSelection(2);
            mViewHolder.time_tv.setText("");
        } else if (ordersList.get(position).DeliveryStatusId == 5) {
            mViewHolder.spinner.setSelection(1);
        } else {
            mViewHolder.spinner.setSelection(0);
            mViewHolder.time_tv.setText("");
        }


        // attaching data adapter to spinner
        /*spinnerOrdersList=new ArrayList<>();
        spinnerOrdersList.add(new SpinnerOrders("Not Started",position));
        spinnerOrdersList.add(new SpinnerOrders("Started",position));
        spinnerOrdersList.add(new SpinnerOrders("Delivered",position));
        spinnerOrdersList.add(new SpinnerOrders("Completed",position));
        Log.d("inserting position",""+position);
        mViewHolder.spinner.setTag(position);
        OrderSpinner orderSpinner= new OrderSpinner(Context,spinnerOrdersList,position);*/

        mViewHolder.spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelected = true;
                return false;
            }
        });
        mViewHolder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int adapterPosition, long id) {
/*                String item = parent..get(position).name;
               // int pos= (Integer) view.getTag();
                TextView tv=(TextView)view.findViewById(R.id.pos_tv);
                Log.d("get position",tv.getText().toString()+""+spinnerOrdersList.get(position).id);*/
                if (userSelected) {
                    userSelected = false;
                    Log.d("Slected Type ", " :: " + parent.getAdapter().getItem(adapterPosition));
                    StartDelivery startDelivery = (StartDelivery) Context;
                    if (parent.getAdapter().getItem(adapterPosition).equals("Started"))
                        startDelivery.callUpdateDeliveryStatusAPi(4, ordersList.get(position).OrderId);
                    else if (parent.getAdapter().getItem(adapterPosition).equals("Delivered"))
                        startDelivery.callUpdateDeliverySignLayout(ordersList.get(position).OrderId);
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return convertView;
    }

    public void setItemsList(List<Orders> itemsList) {
        if (ordersList != null)
            ordersList.clear();
        ordersList.addAll(itemsList);
        notifyDataSetChanged();
    }

    private class MyViewHolder {
        TextView name_tv, phoneno_tv, address_tv, copy_tv, openinmap_tv, time_tv;
        Spinner spinner;
        RelativeLayout spinnerLayout;

        public MyViewHolder(View item) {
            name_tv = (TextView) item.findViewById(R.id.name_tv);
            phoneno_tv = (TextView) item.findViewById(R.id.phoneno_tv);
            address_tv = (TextView) item.findViewById(R.id.address_tv);
            copy_tv = (TextView) item.findViewById(R.id.copy_tv);
            time_tv = (TextView) item.findViewById(R.id.time_tv);
            openinmap_tv = (TextView) item.findViewById(R.id.openinmap_tv);
            spinner = (Spinner) item.findViewById(R.id.spinner);
            spinnerLayout = (RelativeLayout) item.findViewById(R.id.layout_spinner_status);

            myAdapter = new ArrayAdapter(Context, R.layout.view_status_spinner);
            myAdapter.add("Not Started");
            myAdapter.add("Delivered");
            myAdapter.add("Started");
            spinner.setAdapter(myAdapter);
        }

    }
}
