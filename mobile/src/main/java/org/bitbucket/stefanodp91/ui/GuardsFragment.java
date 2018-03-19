package org.bitbucket.stefanodp91.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.blueobject.app.alive.Global;
import com.blueobject.app.alive.GuardEditActivity;
import com.blueobject.app.alive.R;
import com.blueobject.app.alive.adapter.GuardListAdapter;
import com.blueobject.app.alive.helper.UserModel;
import com.blueobject.app.alive.service.OnlyProService;

import org.bitbucket.stefanodp91.model.Page;

import java.util.ArrayList;


public class GuardsFragment extends Fragment {
    private static final String TAG = GuardsFragment.class.getSimpleName();
    protected static final String ARG_KEY = "key";

    public int PICK_CONTACT_REQUEST = 2100;

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private Page mPage;

    private Uri uriContact;
    private String contactID;     // contacts unique ID
    UserModel guard;

    String contactid ="";

    public GuardListAdapter adapter;

    public static GuardsFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        GuardsFragment f = new GuardsFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args != null) {
            mKey = args.getString(ARG_KEY);
         //   mPage = mCallbacks.onGetPage(mKey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_guards_text, container, false);

        RecyclerView list = (RecyclerView) v.findViewById(R.id.guards);
        list.setAdapter(new GuardListAdapter(activity, new ArrayList<UserModel>()));
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        list.setLayoutManager(llm);

        adapter = (GuardListAdapter) list.getAdapter();
        adapter.data.clear();
        adapter.data.addAll(Global.user.guards);
        adapter.notifyDataSetChanged();

        LinearLayout fab = (LinearLayout) v.findViewById(R.id.addnew);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Global.isPro()) {
                    //Toast.makeText(Global.appContext, Global.appContext.getResources().getString(R.string.onlypro), Toast.LENGTH_LONG).show();
                    Global.appContext.startService(new Intent(Global.appContext, OnlyProService.class));
                } else {
                    Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                    pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                    startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
                }
            }
        });

        return v;
    }

    Activity activity;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        /*
        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }
        mCallbacks = (PageFragmentCallbacks) activity;
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.data.clear();
        adapter.data.addAll(Global.user.guards);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.data.clear();
        adapter.data.addAll(Global.user.guards);
        adapter.notifyDataSetChanged();
    }

    protected void setJSONType(String JSONType) {}

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
    }

    protected void setHint(String hint) {}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            String email = "";
            String name = "";
            String normalized = "";

            uriContact = data.getData();

            Cursor cursorID = activity.getContentResolver().query(uriContact, new String[]{ContactsContract.Contacts._ID}, null, null, null);
            if (cursorID.moveToFirst()) {
                contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
            }
            cursorID.close();


            String[] PROJECTION = new String[] {
                    ContactsContract.RawContacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID,
                    ContactsContract.CommonDataKinds.Email.DATA,
                    ContactsContract.CommonDataKinds.Photo.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER,
                    ContactsContract.CommonDataKinds.Email.ADDRESS,
                    ContactsContract.CommonDataKinds.Email.DATA
            };


            Cursor cursor = activity.getContentResolver().query(uriContact, null, null, null, null);
            if (cursor.moveToFirst()) {
                int contid = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int normIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);

                contactid = cursor.getString(contid);
                name = cursor.getString(nameIndex);
                normalized = cursor.getString(normIndex);

                ///////////////////////////////////////////////////////////

                Cursor emails = activity.getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = " + contactid, null, null);
                if(emails != null && emails.moveToFirst()) {
                    email = emails.getString(emails.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    emails.close();
                }

                ///////////////////////////////////////////////////////////////////////////

                guard = new UserModel();
                guard.id = Global.user.guards.size()+1;
                guard.name = name.trim();
                guard.phone = normalized.trim();
                guard.avatarid = new Long(contactID);
                guard.email = email.trim();

                guard.enabled = false;

                boolean detected = false;
                for (UserModel g : Global.user.guards) {
                    if (g.email.equals(email.trim())) {
                        detected = true;
                    }
                }

                if(detected) {
                    Toast.makeText(activity, getResources().getString(R.string.alreadyguard), Toast.LENGTH_LONG).show();
                    return;
                }

                // van egy guard a telefonkönyből.
                // meg kellene nézni hogy ez a guard létezik -e már.
                // ha létezik, nincs mit szerkeszteni rajta.
                // kapunk egy telefon számot, és esetleg jön egy email cím hozzá.

                Log.e("onActivityResult()", normalized + " | " + name + " | " + email);
                new Global.CheckGuard(activity, name.trim(), normalized.trim(), email.trim(), guard, adapter).execute();

            }
            cursor.close();

        }
    }

}

