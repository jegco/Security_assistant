package com.example.jorgecaro.info_usuario_jorge_caro.Contacts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jorgecaro.info_usuario_jorge_caro.R;

import java.util.List;

/**
 * Created by jorge caro on 10/22/2017.
 */

public class ContactAdapter extends BaseAdapter{
    private final Context context;
    private final List<Contact> contacts;

    public ContactAdapter(Context context, List<Contact> contactos) {
        this.context = context;
        this.contacts = contactos;
    }


    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null)
            row = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        holder.texto1 = (TextView) row.findViewById(R.id.text1);
        holder.texto2 = (TextView) row.findViewById(R.id.text2);
        holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);

        Contact persona = contacts.get(position);

        holder.texto1.setText(persona.getName());
        holder.texto2.setText(persona.getNumber());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    Toast.makeText(context, "Se llamara a este numero en caso de emergencia", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(context, "Ya no e llamara a este numero en caso de emergencia", Toast.LENGTH_SHORT).show();
            }
        });

        return row;
    }

    private class ViewHolder {
        public TextView texto1;
        public TextView texto2;
        public CheckBox checkBox;
    }
}
