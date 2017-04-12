package com.accipio.tutorme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v7.widget.SwitchCompat;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.MenuItem;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.NumberPicker;
import android.widget.EditText;
import android.view.LayoutInflater;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class BrowseActivity extends AppCompatActivity implements OnNavigationItemSelectedListener {

    DrawerLayout drawer;
    boolean admin;

    public RecyclerView recycler;
    public TutorsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_browse);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        admin = false;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        recycler = (RecyclerView) findViewById(R.id.browse_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(layoutManager);
        recycler.setHasFixedSize(false);

        handleCheckBox();

        // This is the functionality to change the list between the admin and basic user.

            adapter = populateList(recycler);
            recycler.setAdapter(adapter);



        setupNavigationDrawer();
    }

    public void popDialog(View view){
        showDialog(adapter,recycler);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        goToSelection(id);
        return true;
    }

    private void goToSelection(int id) {
        Intent intent = null;
        switch (id) {
            case R.id.drawer_home:
                // Browse activity, no page change
                toggleMenu(getCurrentFocus());
                return;
            case R.id.drawer_settings:
                intent = new Intent(this, SetupActivity.class);
                break;
            case R.id.drawer_messages:
                intent = new Intent(this, MessagesActivity.class);
                break;
            case R.id.drawer_about:
                // TODO: replace with about page
                return;
            case R.id.drawer_policy:
                // TODO: replace with privacy policy page
                return;
            case R.id.drawer_logout:
                // TODO: remove after demo
                LoginManager.getInstance().logOut();
                intent = new Intent(this, MainActivity.class);
                break;
            default:
                break;
        }
        drawer.closeDrawer(Gravity.LEFT, false);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private TutorsAdapter populateList(RecyclerView recycler) {
        ArrayList<Tutor> tutors = new ArrayList<>();
        // Test data

        JSONParser jsonParser = new JSONParser();
        List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
        args.add(new Pair("",""));
        ArrayList<ArrayList<String>> tutorsList = new ArrayList<ArrayList<String>>();
        tutorsList = jsonParser.request("http://ec2-54-245-142-221.us-west-2.compute.amazonaws.com/getAllTutors.php",args, "GET", "getTutors" );

        boolean equal = false;
        for (ArrayList<String> innerList : tutorsList){
            //testing if adding courses, not new tutor
            for (Tutor currentTutors: tutors){
                System.out.println(currentTutors.getId() + " = " + innerList.get(0));
                if (currentTutors.getId().equals(innerList.get(0))){
                    equal = true;
                    System.out.println("same tutor, adding courses");
                    currentTutors.addCourse(innerList.get(3));
                }
                System.out.println("equal is " + equal);
            }
            ///////////////
            if (!equal) {
                tutors.add(new Tutor(innerList.get(0), innerList.get(1), innerList.get(2), new ArrayList<String>(Arrays.asList(innerList.get(3))), new Float(innerList.get(4)), new Integer(innerList.get(5)), innerList.get(6)));
                System.out.println("Added tutor from db -> " +  innerList.get(0) );
            }
            equal = false;


        }


            TutorsAdapter adapter = new TutorsAdapter(tutors , admin);
            return adapter;

    }



    public class BandAidBro implements View.OnClickListener{

        @Override
        public void onClick(View view) {

        }
    }

    private void handleCheckBox() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isTutor = prefs.getBoolean(String.valueOf("isTutor"), false);
        boolean isAdmin = prefs.getBoolean(String.valueOf("isAdmin"), false);
        if(isAdmin){
            admin = true;
        }


        if (isTutor) {
            final SwitchCompat status = (SwitchCompat) findViewById(R.id.status);
            status.setVisibility(View.VISIBLE);
            status.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    String s = isChecked ? "Available" : "Unavailable";
                    status.setText(s);

                    JSONParser jsonParser = new JSONParser();
                    String tutorid1 = ((TutorMeApplication) BrowseActivity.this.getApplication()).getID();
                    List<Pair<String, String>> para = new ArrayList<Pair<String, String>>();
                    para.add(new Pair("tutor_id", tutorid1));
                    if (s.equals("Available")){
                        para.add(new Pair("status", '1'));
                    }
                    else if (s.equals("Unavailable")){
                        para.add(new Pair("status", '0'));

                    }
                    jsonParser.request("http://ec2-54-245-142-221.us-west-2.compute.amazonaws.com/update_Status.php", para, "POST", "updateStatus");


                }
            });
        }
    }

    public void toggleMenu(View view) {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        } else {
            drawer.openDrawer(Gravity.LEFT);
        }
    }

    private void setupNavigationDrawer() {
        NavigationView navView = (NavigationView) findViewById(R.id.menu);
        navView.setNavigationItemSelectedListener(this);

        View hView = navView.getHeaderView(0);

        String fname = ((TutorMeApplication) BrowseActivity.this.getApplication()).getFirstName();
        String lname = ((TutorMeApplication) BrowseActivity.this.getApplication()).getLastName();
        TextView name = (TextView) hView.findViewById(R.id.header_name);
        name.setText(String.format("%s %s", fname, lname));

        Bitmap picture = ((TutorMeApplication) BrowseActivity.this.getApplication()).getImage();
        CircleImageView image = (CircleImageView) hView.findViewById(R.id.header_image);
        image.setImageBitmap(picture);

        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //String email = prefs.getString("email", "");
        String email = ((TutorMeApplication) BrowseActivity.this.getApplication()).getEmail();
        TextView emailView = (TextView) hView.findViewById(R.id.header_email);
        emailView.setText(email);
    }

    public void showDialog(final TutorsAdapter adapter, final RecyclerView recycler) {
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.dialog_layout, (ViewGroup)findViewById(R.id.dialog_layout));

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(layout);
        builder.setTitle(" Search Options");
        builder.setCancelable(true);

        final TextView seekText = (TextView) layout.findViewById(R.id.maxrate);
        final SeekBar seek = (SeekBar)layout.findViewById(R.id.seekbar);
        final EditText input = (EditText) layout.findViewById(R.id.searchCourse);
        final CheckBox checkBox = (CheckBox) layout.findViewById(R.id.checkbox_id);

        final TextView tv = (TextView) layout.findViewById(R.id.minrating);
        final NumberPicker np = (NumberPicker) layout.findViewById(R.id.np);
        np.setMinValue(1);
        np.setMaxValue(5);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                tv.setText("Minimum Rating: " + newVal);
            }
        });

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                seekText.setText("Maximum Rate: $" + progress + "/hour");
            }

            public void onStartTrackingTouch(SeekBar arg0) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        builder.setPositiveButton(
                "Okay",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id ) {
                        String checked = (checkBox.isChecked()) ? "1" : "0";
                        int rating = np.getValue();
                        int rate = seek.getProgress();
                        rate = (rate <=0) ? 80 : rate;
                        String mText = input.getText().toString().toUpperCase();
                        mText = (mText.equals("")) ? "NONE" : mText;

                        String filterStringFinal = "rate_" + rate + "-";
                        filterStringFinal = filterStringFinal + "rating_" + rating + "-";
                        filterStringFinal = filterStringFinal + mText + "-" + checked;

                        adapter.getFilter().filter(filterStringFinal);
                    }
                });

        builder.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog yourDialog = builder.create();
        yourDialog.show();
    }
}
