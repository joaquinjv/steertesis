package com.unlp.tesis.steer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unlp.tesis.steer.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FollowersActivity extends AppCompatActivity {

    public class UserListItem {
        boolean checked;
        String ItemString;
        String userId;
        UserListItem(String t, boolean b, String uid){
            ItemString = t;
            checked = b;
            userId = uid;
        }

        public boolean isChecked(){
            return checked;
        }
    }

    static class ViewHolder {
        CheckBox checkBox;
        TextView text;
        TextView hiddenText;
    }

    public class UserListAdapter extends BaseAdapter {

        private Context context;
        private List<UserListItem> list;

        UserListAdapter(Context c, List<UserListItem> l) {
            context = c;
            list = l;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public boolean isChecked(int position) {
            return list.get(position).checked;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = convertView;

            // reuse views
            ViewHolder viewHolder = new ViewHolder();
            if (rowView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                rowView = inflater.inflate(R.layout.activity_followers_rows, null);

                viewHolder.checkBox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
                viewHolder.text = (TextView) rowView.findViewById(R.id.rowTextView);
                viewHolder.hiddenText = (TextView) rowView.findViewById(R.id.rowTextViewHidden);
                rowView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) rowView.getTag();
            }

            viewHolder.checkBox.setChecked(list.get(position).checked);

            final String itemStr = list.get(position).ItemString;
            final String itemStrHidden = list.get(position).userId;
            viewHolder.text.setText(itemStr);
            viewHolder.hiddenText.setText(itemStrHidden);

            viewHolder.checkBox.setTag(position);

            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean newState = !list.get(position).isChecked();
                    list.get(position).checked = newState;
                    Toast.makeText(getApplicationContext(),
                            itemStr + "setOnClickListener\nchecked: " + newState,
                            Toast.LENGTH_LONG).show();
                }
            });

//            viewHolder.text.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                    boolean newState = !list.get(position).isChecked();
//                    list.get(position).checked = newState;
//                    Toast.makeText(FollowersActivity.this,
//                            itemStr + "setOnClickListener\nchecked: " + newState,
//                            Toast.LENGTH_LONG).show();
//                }
//            });

            viewHolder.checkBox.setChecked(isChecked(position));

            return rowView;
        }
    }


    private DatabaseReference mDatabase;
    private static FirebaseUser user;
    List<UserListItem> items;
    UserListAdapter itemsListAdapter;
    ListView listView;
    long count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        listView = (ListView)findViewById(R.id.list_followers);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        items = new ArrayList<UserListItem>();
        mDatabase.child("users").child(user.getUid()).child("friends").addValueEventListener(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    count = dataSnapshot.getChildrenCount();
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        mDatabase.child("users").child(ds.getKey()).addValueEventListener(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value
                                        User friend = dataSnapshot.getValue(User.class);
                                        if (friend != null) {
                                            items.add(new UserListItem(friend.getEmail(), false, dataSnapshot.getKey()));
                                            count = count - 1;
                                            if (count == 0) {
                                                itemsListAdapter = new UserListAdapter(FollowersActivity.this, items);
                                                listView.setAdapter(itemsListAdapter);
                                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view,
                                                                            int position, long id) {
                                                        Toast.makeText(FollowersActivity.this,
                                                                ((UserListItem)(parent.getItemAtPosition(position))).ItemString,
                                                                Toast.LENGTH_LONG).show();
                                                    }});
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                }
                        );
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            }
        );
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabFollowersOk);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Map<String, Object> childUpdates = new HashMap<>();

                int cnt = itemsListAdapter.getCount();
                for (int i=0; i<cnt; i++){
                    if(itemsListAdapter.isChecked(i)){
                        UserListItem userFollower = (UserListItem)itemsListAdapter.getItem(i);
                        childUpdates.put("/"+userFollower.userId+"/followed/" + user.getUid(), true);
                    }
                }

                mDatabase.child("users").updateChildren(childUpdates);

                FollowersActivity.this.finish();
                Snackbar.make(view, "Se presionó el FAB", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }

}
