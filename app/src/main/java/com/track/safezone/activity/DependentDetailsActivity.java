package com.track.safezone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.track.safezone.R;
import com.track.safezone.beans.DependentDetails;
import com.track.safezone.beans.User;

import java.util.ArrayList;

public class DependentDetailsActivity extends AppCompatActivity {

    private User userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dependent_details);
    }

    public void proceedToLocation(View view) {
        EditText mDependentDetails1FirstName = findViewById(R.id.input_dependent1_firstName);
        EditText mDependentDetials1PhoneNumber = findViewById(R.id.input_dependent1_phoneNumber);
        EditText mDependentDetials2FirstName = findViewById(R.id.input_dependent2_firstName);
        EditText mDependentDetails2PhoneNumber = findViewById(R.id.input_dependent2_phoneNumber);

        DependentDetails dependentDetails1 = new DependentDetails(mDependentDetails1FirstName.getText().toString(),mDependentDetials1PhoneNumber.getText().toString());
        DependentDetails dependentDetails2 = new DependentDetails(mDependentDetials2FirstName.getText().toString(),mDependentDetails2PhoneNumber.getText().toString());

        ArrayList<DependentDetails> dependentDetails = new ArrayList<>();
        dependentDetails.add(dependentDetails1);
        dependentDetails.add(dependentDetails2);

        Bundle extras = getIntent().getExtras();
        this.userData = (User) extras.get("user");
        userData.setDependentDetails(dependentDetails);

        Intent i = new Intent(this, LocationActivity.class);
        i.putExtra("user", userData);
        startActivity(i);
    }
}