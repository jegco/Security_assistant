package com.example.jorgecaro.info_usuario_jorge_caro.Fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jorgecaro.info_usuario_jorge_caro.Contacts.Contact;
import com.example.jorgecaro.info_usuario_jorge_caro.Contacts.ContactAdapter;
import com.example.jorgecaro.info_usuario_jorge_caro.R;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzagz.runOnUiThread;


public class ContactFragment extends Fragment {

    private View view;
    private ListView mListView;
    private ProgressDialog pDialog;
    private android.os.Handler updateBarHandler;

    ArrayList<Contact> contactList;

    Cursor cursor;
    int counter=0;
    private static final int REQUEST_CODE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_contact, container, false);
        contactList = new ArrayList<Contact>();
        final String email = getArguments().getString("email");
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Leyendo");
        pDialog.setCancelable(false);
        pDialog.show();

        mListView = (ListView) view.findViewById(R.id.listView);
        updateBarHandler = new android.os.Handler();


        new Thread(new Runnable() {

            @Override
            public void run() {
                getContacts();
            }
        }).start();

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainFragment mainFragment = new MainFragment();
                Bundle arg = new Bundle();
                arg.putString("email", email);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, mainFragment).commit();
            }
        });
        return view;
    }

    public void getContacts() {
        String phoneNumber = null;
        String email= null;
        Uri CONTENT_URI= ContactsContract.Contacts.CONTENT_URI;
        String _ID = ContactsContract.Contacts._ID;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER= ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String Phone_CONTAC_ID =ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String NUMBER =ContactsContract.CommonDataKinds.Phone.NUMBER;
        Uri emailCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String emailCONTAC_ID =ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
        String data =ContactsContract.CommonDataKinds.Phone.DATA;

        StringBuffer output;

        ContentResolver contentResolver = getActivity().getContentResolver();

        cursor = contentResolver.query(CONTENT_URI,null,null,null,null);


        if(cursor.getCount()>0) {

            counter = 0;
            while (cursor.moveToNext()) {
                String nombre = "";
                String numero = "";
                String correo = "";

                updateBarHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.setMessage("Leyendo :" + counter++ + "/" + cursor.getCount());
                    }
                });
                String contact_id = cursor.getString(cursor.getColumnIndex(_ID));
                String name = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
                int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

                if (hasPhoneNumber > 0) {
                    nombre = name;
                    Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTAC_ID + "=?", new String[]{contact_id}, null);


                    while (phoneCursor.moveToNext()) {

                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                        numero = phoneNumber;

                    }
                    phoneCursor.close();
                    Cursor emailCursor = contentResolver.query(emailCONTENT_URI, null, Phone_CONTAC_ID + "=?", new String[]{contact_id}, null);

                    while (emailCursor.moveToNext()) {

                        email = emailCursor.getString(emailCursor.getColumnIndex(data));
                        correo = email;

                    }
                    emailCursor.close();

                }


                contactList.add(new Contact(nombre, numero, correo));
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ContactAdapter ad = new ContactAdapter(getActivity(), contactList);
                    mListView.setAdapter(ad);

                }
            });

            updateBarHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    pDialog.cancel();
                }
            }, 500);




        }

    }
}
