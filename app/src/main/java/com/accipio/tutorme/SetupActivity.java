package com.accipio.tutorme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class SetupActivity extends AppCompatActivity implements View.OnClickListener, OnNavigationItemSelectedListener {

    private TextView info;

    private boolean isTutor;

    private boolean isAdmin;

    private int[] ids = {R.id.email, R.id.toLearn, R.id.toTeach, R.id.description, R.id.rate};

    private DrawerLayout drawer;

    public CheckBox adminBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        setContentView(R.layout.activity_setup);

        drawer = (DrawerLayout) findViewById(R.id.drawer_setup_layout);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        View mActionBarView = getLayoutInflater().inflate(R.layout.action_bar, null);
        actionBar.setCustomView(mActionBarView);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

//        int resourceId = getResources().getIdentifier("actionbar_bg", "drawable", getPackageName());
//        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(resourceId));

        info = (TextView)findViewById(R.id.info);
        findViewById(R.id.go).setOnClickListener(this);

        showName();

        setupCheckBox();

        restoreInfo();

        setupBubbleTextViews();

        setupNavigationDrawer();

        adminBox = (CheckBox) findViewById(R.id.checkBox2);
        adminBox.setOnClickListener(new BandaidSolution());
    }

    public class BandaidSolution implements View.OnClickListener{

        public BandaidSolution(){

        }

        @Override
        public void onClick(View view) {
            if(isAdmin){
                isAdmin = false;
            }
            else{
                isAdmin = true;
            }
        }
    }

    private void showName() {
        String firstName = ((TutorMeApplication) SetupActivity.this.getApplication()).getFirstName();
        String greeting = String.format("Hello, %s!", firstName);
        info.setText(greeting);
    }

    private void setupCheckBox() {
        final CheckBox box = (CheckBox)findViewById(R.id.checkBox);
        box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                   @Override
                   public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                       int visibility = isChecked ? View.VISIBLE : View.INVISIBLE;
                       findViewById(R.id.description).setVisibility(visibility);
                       findViewById(R.id.toTeach).setVisibility(visibility);
                       findViewById(R.id.teachCoursesInfo).setVisibility(visibility);
                       findViewById(R.id.rate).setVisibility(visibility);

                       isTutor = isChecked;
                   }
               }
        );
    }

    private void setupBubbleTextViews() {
        BubbleTextView toLearn = (BubbleTextView) findViewById(R.id.toLearn);
        BubbleTextView toTeach = (BubbleTextView) findViewById(R.id.toTeach);
        BubbleTextView[] bubbles = {toLearn, toTeach};

        for (BubbleTextView bubble : bubbles) {
            /* Ensures that predictive text is off, sets to uppercase */
            bubble.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

            String[] item = getResources().getStringArray(R.array.course);
            //TODO: also populate from items in database?

            bubble.setAdapter(new ArrayAdapter(this,
                    android.R.layout.simple_dropdown_item_1line, item));
            bubble.setTokenizer(new SpaceTokenizer());
        }
        toLearn.setBubbles();
        toTeach.setBubbles();
    }

    public void onClick(View view) {
        if (view.getId() == R.id.go) {
            saveInfo();

            Intent intent = new Intent(this, BrowseActivity.class);
            startActivity(intent);
        }
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
                intent = new Intent(this, BrowseActivity.class);
                break;
            case R.id.drawer_settings:
                // Browse activity, no page change
                toggleMenu(getCurrentFocus());
                return;
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

    public void toggleMenu(View view) {
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        }
        else {
            drawer.openDrawer(Gravity.LEFT);
        }
    }

    private void setupNavigationDrawer() {
        NavigationView navView = (NavigationView) findViewById(R.id.menu);
        navView.setNavigationItemSelectedListener(this);

        View hView =  navView.getHeaderView(0);

        String fname = ((TutorMeApplication) SetupActivity.this.getApplication()).getFirstName();
        String lname = ((TutorMeApplication) SetupActivity.this.getApplication()).getLastName();
        TextView name = (TextView)hView.findViewById(R.id.header_name);
        name.setText(String.format("%s %s", fname, lname));

        Bitmap picture = ((TutorMeApplication) SetupActivity.this.getApplication()).getImage();
        CircleImageView image = (CircleImageView)hView.findViewById(R.id.header_image);
        image.setImageBitmap(picture);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String email = prefs.getString("email", "");
        TextView emailView = (TextView)hView.findViewById(R.id.header_email);
        emailView.setText(email);
    }

    private void restoreInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        for (int id : ids) {
            String text = prefs.getString(String.valueOf(id), "");
            EditText editText = (EditText) findViewById(id);
            if (text != null && !text.isEmpty()) {
                editText.setText(text);
            }
        }

        if (prefs.getBoolean(String.valueOf(isTutor), false)) {
            CheckBox box = (CheckBox)findViewById(R.id.checkBox);
            box.setChecked(true);
        }
    }

    private void saveInfo() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SetupActivity.this);
        SharedPreferences.Editor editor = prefs.edit();



        for (int id : ids) {
            EditText editText = (EditText) findViewById(id);
            String text = editText.getText().toString();
            editor.putString(String.valueOf(id), text);
            System.out.println(String.valueOf(id) + " and " + text);

        }



        if (isTutor) {

            JSONParser jsonParser = new JSONParser();
            List<Pair<String, String>> args = new ArrayList<Pair<String, String>>();
            String tutorid = ((TutorMeApplication) SetupActivity.this.getApplication()).getID();

            EditText editText = (EditText) findViewById(R.id.description);
            String textDescription = editText.getText().toString().replace("\n", "");
            EditText editText1 = (EditText) findViewById(R.id.rate);
            String textPrice = editText1.getText().toString();




            System.out.println(textDescription + " and THIS IS " + textPrice);

            args.add(new Pair("tutor_id", tutorid ));
            args.add(new Pair("tutor_price", textPrice));
            args.add(new Pair("tutor_bio", textDescription));

            jsonParser.request("http://ec2-54-245-142-221.us-west-2.compute.amazonaws.com/create_Tutor.php", args, "POST", "createTutor");

            EditText editText2 = (EditText) findViewById(R.id.toTeach);
            String textCourses = editText2.getText().toString().replace("\n","");
            String[] courses = textCourses.split(" ");
            List<Pair<String, String>> arguments = new ArrayList<Pair<String, String>>();
            arguments.add(new Pair("tutor_id", tutorid));
            for (String course : courses){
                arguments.add(new Pair("course_name", course));
            }
            jsonParser.request("http://ec2-54-245-142-221.us-west-2.compute.amazonaws.com/createTutorCourse.php", arguments, "POST", "createTutorCourse");




        }




        //TODO: take this out after log out is removed
        String email = ((EditText) findViewById(R.id.email)).getText().toString();
        ((TutorMeApplication) SetupActivity.this.getApplication()).setEmail(email);
        ((TutorMeApplication) SetupActivity.this.getApplication()).setTutor(isTutor);
        editor.putBoolean("isTutor", isTutor);
        editor.putBoolean("isAdmin", isAdmin);




        editor.apply();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
