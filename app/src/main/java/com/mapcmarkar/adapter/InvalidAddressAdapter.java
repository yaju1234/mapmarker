package com.mapcmarkar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mapcmarkar.R;
import com.mapcmarkar.model.InvalidAddress;

import java.util.ArrayList;

/**
 * Created by root on 15/11/17.
 */

public class InvalidAddressAdapter extends BaseAdapter {
    Context context;
    ArrayList<InvalidAddress> invalidAddressList=new ArrayList<>();

    LayoutInflater inflater;
    public InvalidAddressAdapter(Context applicationContext, ArrayList<InvalidAddress> invalidAddressList) {
        this.context = applicationContext;
        this.invalidAddressList = invalidAddressList;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return invalidAddressList.size();
    }

    @Override
    public Object getItem(int i) {
        return invalidAddressList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return invalidAddressList.hashCode();
    }

   /* @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.item_invalid_address, null);
        TextView txtName = (TextView) view.findViewById(R.id.txtName);
        TextView txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        txtName.setText(invalidAddressList.get(i).getSellername());
        txtAddress.setText(invalidAddressList.get(i).getAddress());


        return view;
    }*/
   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
       MyViewHolder mViewHolder;

       if (convertView == null) {
           convertView = inflater.inflate(R.layout.item_invalid_address, parent, false);
           mViewHolder = new MyViewHolder(convertView);
           convertView.setTag(mViewHolder);
       } else {
           mViewHolder = (MyViewHolder) convertView.getTag();
       }


       mViewHolder.txtName.setText(invalidAddressList.get(position).getSellername());
       mViewHolder.txtAddress.setText(invalidAddressList.get(position).getAddress());

       return convertView;
   }

    private class MyViewHolder {
        TextView txtName,txtAddress;

        public MyViewHolder(View item) {
             txtName = (TextView) item.findViewById(R.id.txtName);
             txtAddress = (TextView) item.findViewById(R.id.txtAddress);
        }
    }

}
